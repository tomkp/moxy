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
import java.net.URL;
import java.nio.charset.Charset;

public class RequestHandler extends AbstractHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RequestHandler.class);

    public static final int DEFAULT_STATUS = 200;

    private Moxy moxy;
    private Class<?> testClass;
    private int index = 0;


    public RequestHandler(Class<?> testClass, Moxy moxy) {
        this.testClass = testClass;
        this.moxy = moxy;
    }


    @Override
    public void handle(String path, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {

        LOG.info("path: '{}'", path);

        setStatus(httpServletResponse);

        try {

            setContentType(httpServletResponse);

            String[] responses = moxy.response();
            String[] files = moxy.file();

            if (responses.length > 0 && files.length > 0) {
                throw new IOException("You must annotate your test with either 'responses' or 'files', but not both");
            }

            String proxy = moxy.proxy();
            if (!proxy.isEmpty()) {

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
                InputSupplier<InputStream> inputSupplier = Resources.newInputStreamSupplier(url);
                ByteStreams.copy(inputSupplier.getInput(), httpServletResponse.getOutputStream());

                if (files.length > 0)  {
                    String filename = files[index];
                    URL resource = testClass.getResource(".");
                    File file = new File(resource.getPath(), filename);
                    if (!file.exists()) {
                        Files.createParentDirs(file);
                        boolean created = file.createNewFile();
                        LOG.info("file '{}' created '{}'", file, created);
                    }
                    ByteStreams.copy(inputSupplier.getInput(), new FileOutputStream(file));
                }

            } else {

                if (responses.length > index) {

                    String response = responses[index];
                    InputStream inputStream = new ByteArrayInputStream(response.getBytes(Charset.forName("UTF-8")));
                    ByteStreams.copy(inputStream, httpServletResponse.getOutputStream());

                } else if (files.length > index) {

                    String filename = files[index];
                    if (filename.startsWith("/")) {
                        InputStream inputStream = this.getClass().getResourceAsStream(filename);
                        ByteStreams.copy(inputStream, httpServletResponse.getOutputStream());
                    } else {
                        URL resource = testClass.getResource(".");
                        File file = new File(resource.getPath(), filename);
                        InputSupplier<FileInputStream> inputSupplier = Files.newInputStreamSupplier(file);
                        FileInputStream inputStream = inputSupplier.getInput();
                        ByteStreams.copy(inputStream, httpServletResponse.getOutputStream());
                    }
                }
            }

            index++;

        } finally {
            request.setHandled(true);
        }
    }


    private void setStatus(HttpServletResponse httpServletResponse) {
        int[] statusCodes = moxy.statusCode();
        if (statusCodes != null && statusCodes.length > 1) {
            httpServletResponse.setStatus(statusCodes[index]);
        } else if (statusCodes != null && statusCodes.length == 1) {
            httpServletResponse.setStatus(statusCodes[0]);
        } else {
            httpServletResponse.setStatus(DEFAULT_STATUS);
        }
    }


    private void setContentType(HttpServletResponse httpServletResponse) {
        String[] contentTypes = moxy.contentType();
        if (contentTypes != null && contentTypes.length > 1) {
            httpServletResponse.setContentType(contentTypes[index]);
        } else if (contentTypes != null && contentTypes.length == 1) {
            httpServletResponse.setContentType(contentTypes[0]);
        } else {
            httpServletResponse.setContentType("text/plain");
        }
    }


    private void addCookie(HttpServletResponse httpServletResponse) {
        String[] cookies = moxy.cookie();
        if (cookies != null && cookies.length > 1) {
            String cookie = cookies[index];
            httpServletResponse.addCookie(createCookie(cookie));
        } else if (cookies != null && cookies.length == 1) {
            String cookie = cookies[0];
            httpServletResponse.addCookie(createCookie(cookie));
        }
    }


    //Set-Cookie: PubAuth1=134240759%2C134240757%2C134240754%2C%2B255084548049850%2C%2B114347059694522%2C%2B0%2C3472674174%2C1060798794%2CtEe9tPJ9pawRIWDHIn47sg; path=/; expires=Thu, 14-Aug-2003 04:19:54 GMT; secure\n" +
    /*
    // Escape regexp special characters (thanks kangax!)
    name = name.replace(/([.*+?^=!:${}()|[\]\/\\])/g, '\\$1');

    var regex = new RegExp('(?:^|;)\\s?' + name + '=(.*?)(?:;|$)','i'),
        match = document.cookie.match(regex);

    return match && unescape(match[1]); // thanks James!
     */
    private Cookie createCookie(String cookieString) {
        return new Cookie();
    }


}
