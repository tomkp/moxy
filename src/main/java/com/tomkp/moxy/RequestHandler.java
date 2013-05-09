package com.tomkp.moxy;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.readers.AbsoluteFileReader;
import com.tomkp.moxy.readers.RelativeFileReader;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpCookie;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RequestHandler extends AbstractHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RequestHandler.class);

    public static final int DEFAULT_STATUS = 200;
    private static final String DEFAULT_CONTENT_TYPE = "text/plain";

    private final FilenameGenerator filenameGenerator;
    private final RequestProxy proxyRequest;

    private final ResponseWriter responseWriter;
    private final RelativeFileReader relativeFileReader;
    private final AbsoluteFileReader absoluteFileReader;
    private final com.tomkp.moxy.readers.StringReader stringReader;


    private List<Moxy> moxies;
    private Class<?> testClass;
    private int index = 0;


    public RequestHandler(FilenameGenerator filenameGenerator,
                          RequestProxy proxyRequest,
                          Class<?> testClass,
                          List<Moxy> moxies) {
        this.filenameGenerator = filenameGenerator;
        this.proxyRequest = proxyRequest;
        this.testClass = testClass;
        this.moxies = moxies;
        responseWriter = new ResponseWriter();
        relativeFileReader = new RelativeFileReader();
        absoluteFileReader = new AbsoluteFileReader();
        stringReader = new com.tomkp.moxy.readers.StringReader();

        Requests.reset();
    }


    @Override
    public void handle(String path, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {

        String method = httpServletRequest.getMethod();

        LOG.info("handle request, method '{}', path '{}'", method, path);

        Requests.capture(httpServletRequest);

        try {

            MoxyData moxyData = new MoxyData(moxies);

            int[] statusCodes = moxyData.getStatusCodes();

            int statusCode = getStatusCode(statusCodes);
            List<Cookie> httpCookies = getCookies(moxyData.getCookies());

            httpServletResponse.setStatus(statusCode);
            for (Cookie httpCookie : httpCookies) {
                httpServletResponse.addCookie(httpCookie);
            }
            httpServletResponse.setContentType(getContentType(moxyData.getContentTypes()));

            String[] files = moxyData.getFiles();
            String[] responses = moxyData.getResponses();
            boolean indexed = moxyData.getIndexed();

            int fileCount = files.length;

            if (responses.length > 0 && fileCount > 0) {
                throw new IOException("You must annotate your test with either 'responses' or 'files', but not both");
            }

            String proxy = moxyData.getProxy();


            if (!proxy.isEmpty()) {

                InputStream inputStream = proxyRequest.proxyRequest(httpServletRequest, httpServletResponse, proxy);

                // record the reponse to a file
                if (fileCount > 0 || indexed)  {

                    String filename = filenameGenerator.generateFilename(files, indexed, index);

                    saveResponseToFile(testClass, filename, inputStream);
                }


            } else {

                LOG.info("current index {}, indexed {}", index, indexed);
                LOG.info("{} files, {} static responses", fileCount, responses.length);

                if (responses.length > index) {

                    // write response body using annotation value
                    String response = responses[index];
                    writeStringToResponse(httpServletResponse, response);

                } else if (fileCount > index || indexed) {

                    // write response using file contents
                    String filename = filenameGenerator.generateFilename(files, indexed, index);

                    if (filename.startsWith("/")) {
                        writeAbsoluteFileToResponse(httpServletResponse, filename);
                    } else {
                        URL resource = testClass.getResource(".");
                        writeRelativeFileToResponse(httpServletResponse, resource.getPath(), filename);
                    }
                }
            }

            index++;

        } finally {
            request.setHandled(true);
        }
    }




    // response writers

    private void writeRelativeFileToResponse(HttpServletResponse httpServletResponse, String resourcePath, String filename) throws IOException {
        InputStream inputStream = relativeFileReader.readRelativeFile(resourcePath, filename);
        responseWriter.writeResponse(httpServletResponse, inputStream);
    }



    private void writeAbsoluteFileToResponse(HttpServletResponse httpServletResponse, String filename) throws IOException {
        InputStream inputStream = absoluteFileReader.readAbsoluteFile(testClass, filename);
        responseWriter.writeResponse(httpServletResponse, inputStream);
    }



    private void writeStringToResponse(HttpServletResponse httpServletResponse, String response) throws IOException {
        InputStream inputStream = stringReader.readString(response);
        responseWriter.writeResponse(httpServletResponse, inputStream);
    }


    // capture

    private void saveResponseToFile(Class<?> testClass, String filename, InputStream inputStream) throws IOException {
        URL resource = testClass.getResource(".");
        File file = new File(resource.getPath(), filename);
        if (!file.exists()) {
            Files.createParentDirs(file);
            boolean created = file.createNewFile();
            LOG.info("file '{}' created '{}'", file, created);
        }

        ByteStreams.copy(inputStream, new FileOutputStream(file));
    }


    //....






    private int getStatusCode(int[] statusCodes) {
        int statusCode = DEFAULT_STATUS;
        if (statusCodes != null && statusCodes.length > 1) {
            statusCode = statusCodes[index];
        } else if (statusCodes != null && statusCodes.length == 1) {
            statusCode = statusCodes[0];
        }
        return statusCode;
    }


    private String getContentType(String[] contentTypes) {
        String contentType;
        if (contentTypes != null && contentTypes.length > 1) {
            contentType = contentTypes[index];
        } else if (contentTypes != null && contentTypes.length == 1) {
            contentType = contentTypes[0];
        } else {
            contentType = DEFAULT_CONTENT_TYPE;
        }
        LOG.info("contentType: '{}'", contentType);
        return contentType;
    }


    private List<Cookie> getCookies(String[] cookies) {
        List<Cookie> httpCookies = new ArrayList<Cookie>();
        if (cookies != null && cookies.length > 1) {
            String cookie = cookies[index];
            LOG.info("cookie: '{}'", cookie);
            httpCookies = createCookies(cookie);
        } else if (cookies != null && cookies.length == 1) {
            String cookie = cookies[0];
            LOG.info("cookie: '{}'", cookie);
            httpCookies = createCookies(cookie);
        }
        return httpCookies;
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
