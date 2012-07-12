package com.tomkp.moxy;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.google.common.io.Resources;
import com.tomkp.moxy.annotations.Moxy;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

public class RequestHandler extends AbstractHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RequestHandler.class);

    public static final int DEFAULT_STATUS = 200;
    private static final String DEFAULT_CONTENT_TYPE = "text/plain";


    private Moxy moxy;
    private Class<?> testClass;
    private int index = 0;


    public RequestHandler(Class<?> testClass, Moxy moxy) {
        this.testClass = testClass;
        this.moxy = moxy;
        Requests.reset();
    }


    @Override
    public void handle(String path, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {

        String method = httpServletRequest.getMethod();

        LOG.info("handle request, method '{}', path '{}'", method, path);

        Requests.capture(httpServletRequest);

        try {

            httpServletResponse.setStatus(getStatusCode());

            addCookies(httpServletResponse);

            setContentType(httpServletResponse);

            String[] responses = moxy.response();
            String[] files = moxy.file();

            if (responses.length > 0 && files.length > 0) {
                throw new IOException("You must annotate your test with either 'responses' or 'files', but not both");
            }

            String proxy = moxy.proxy();


            if (!proxy.isEmpty()) {

                // generate the correct url to proxy to
                URL url = createProxyUrl(httpServletRequest, proxy);

                // perform http GET / POST / PUT / DELETE
                InputSupplier<? extends InputStream> inputSupplier = executeProxyHttpRequest(httpServletRequest, httpServletResponse, url);

                // record the reponse to a file
                if (files.length > 0)  {
                    String filename = files[index];
                    saveResponseToFile(inputSupplier, filename);
                }

            } else {

                if (responses.length > index) {

                    // write response body using annotation value
                    String response = responses[index];
                    writeResponse(httpServletResponse, response);

                } else if (files.length > index) {

                    // write response using file contents
                    String filename = files[index];
                    if (filename.startsWith("/")) {
                        writeAbsoluteFileToResponse(httpServletResponse, filename);
                    } else {
                        writeRelativeFileToResponse(httpServletResponse, filename);
                    }
                }
            }

            index++;

        } finally {
            request.setHandled(true);
        }
    }




    // response writers


    private void writeRelativeFileToResponse(HttpServletResponse httpServletResponse, String filename) throws IOException {
        URL resource = testClass.getResource(".");
        File file = new File(resource.getPath(), filename);
        InputSupplier<FileInputStream> inputSupplier = Files.newInputStreamSupplier(file);
        FileInputStream inputStream = inputSupplier.getInput();
        ByteStreams.copy(inputStream, httpServletResponse.getOutputStream());
    }


    private void writeAbsoluteFileToResponse(HttpServletResponse httpServletResponse, String filename) throws IOException {
        InputStream inputStream = this.getClass().getResourceAsStream(filename);
        ByteStreams.copy(inputStream, httpServletResponse.getOutputStream());
    }


    private void writeResponse(HttpServletResponse httpServletResponse, String response) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(response.getBytes(Charset.forName("UTF-8")));
        ByteStreams.copy(inputStream, httpServletResponse.getOutputStream());
    }


    // http methods


    private InputSupplier<? extends InputStream> executeProxyHttpRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, URL url) throws IOException {
        InputSupplier<? extends InputStream> inputSupplier;
        String method = httpServletRequest.getMethod();
        if (method.equalsIgnoreCase("GET")) {
            inputSupplier = httpGet(httpServletResponse, url);
        } else {
            inputSupplier = httpPost(httpServletRequest, httpServletResponse, url);
        }
        return inputSupplier;
    }


    private InputSupplier<? extends InputStream> httpGet(HttpServletResponse httpServletResponse, URL url) throws IOException {
        InputSupplier<? extends InputStream> inputSupplier = Resources.newInputStreamSupplier(url);
        ByteStreams.copy(inputSupplier.getInput(), httpServletResponse.getOutputStream());
        return inputSupplier;
    }


    private InputSupplier<? extends InputStream> httpPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, URL url) throws IOException {
        String method = httpServletRequest.getMethod();
        byte[] requestBytes = ByteStreams.toByteArray(httpServletRequest.getInputStream());
        byte[] responseBytes = write(url, requestBytes, method);
        InputSupplier<? extends InputStream> inputSupplier = ByteStreams.newInputStreamSupplier(responseBytes);
        ByteStreams.copy(inputSupplier, httpServletResponse.getOutputStream());
        return inputSupplier;
    }


    private byte[] write(URL url, byte[] body, String method) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod(method);
        InputSupplier<ByteArrayInputStream> inputSupplier = ByteStreams.newInputStreamSupplier(body);
        ByteStreams.copy(inputSupplier, httpURLConnection.getOutputStream());
        return ByteStreams.toByteArray(httpURLConnection.getInputStream());
    }


    // capture

    private void saveResponseToFile(InputSupplier<? extends InputStream> inputSupplier, String filename) throws IOException {
        URL resource = testClass.getResource(".");
        File file = new File(resource.getPath(), filename);
        if (!file.exists()) {
            Files.createParentDirs(file);
            boolean created = file.createNewFile();
            LOG.info("file '{}' created '{}'", file, created);
        }

        InputStream inputStream = inputSupplier.getInput();
        ByteStreams.copy(inputStream, new FileOutputStream(file));
    }


    //


    private URL createProxyUrl(HttpServletRequest httpServletRequest, String proxy) throws MalformedURLException {
        String pathInfo = httpServletRequest.getPathInfo();
        LOG.info("pathInfo: '{}'", pathInfo);
        String queryString = httpServletRequest.getQueryString();
        LOG.info("queryString: '{}'", queryString);
        if (queryString == null) {
            queryString = "";
        } else {
            queryString = "?" + queryString;
        }
        URL url = new URL(proxy + pathInfo + queryString);
        LOG.info("proxy to '{}'", url);
        return url;
    }


    private int getStatusCode() {
        int[] statusCodes = moxy.statusCode();
        int statusCode = DEFAULT_STATUS;
        if (statusCodes != null && statusCodes.length > 1) {
            statusCode = statusCodes[index];
        } else if (statusCodes != null && statusCodes.length == 1) {
            statusCode = statusCodes[0];
        }
        return statusCode;
    }


    private void setContentType(HttpServletResponse httpServletResponse) {
        String[] contentTypes = moxy.contentType();
        if (contentTypes != null && contentTypes.length > 1) {
            httpServletResponse.setContentType(contentTypes[index]);
        } else if (contentTypes != null && contentTypes.length == 1) {
            httpServletResponse.setContentType(contentTypes[0]);
        } else {
            httpServletResponse.setContentType(DEFAULT_CONTENT_TYPE);
        }
    }


    private void addCookies(HttpServletResponse httpServletResponse) {
        String[] cookies = moxy.cookie();
        if (cookies != null && cookies.length > 1) {
            String cookie = cookies[index];
            LOG.info("cookie: '{}'", cookie);
            List<Cookie> httpCookies = createCookies(cookie);
            for (Cookie httpCookie : httpCookies) {
                httpServletResponse.addCookie(httpCookie);
            }
        } else if (cookies != null && cookies.length == 1) {
            String cookie = cookies[0];
            LOG.info("cookie: '{}'", cookie);
            List<Cookie> httpCookies = createCookies(cookie);
            for (Cookie httpCookie : httpCookies) {
                httpServletResponse.addCookie(httpCookie);
            }
        }
    }


    private List<Cookie> createCookies(String cookieString) {
        List<HttpCookie> httpCookies = HttpCookie.parse(cookieString);
        List<Cookie> cookies = new ArrayList<Cookie>();
        for (HttpCookie httpCookie : httpCookies) {
            Cookie cookie = new Cookie(httpCookie.getName(), httpCookie.getValue());
            cookie.setPath(httpCookie.getPath());
            cookie.setMaxAge((int) httpCookie.getMaxAge());
            cookie.setSecure(httpCookie.getSecure());
            LOG.info("cookie: '{}'", cookie);
            cookies.add(cookie);
        }
        return cookies;
    }


}
