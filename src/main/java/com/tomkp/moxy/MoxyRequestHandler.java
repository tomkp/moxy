package com.tomkp.moxy;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.writers.ResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;

public class MoxyRequestHandler {

    private static final Logger LOG = LoggerFactory.getLogger(MoxyRequestHandler.class);

    private static final int DEFAULT_STATUS = 200;
    private static final String DEFAULT_CONTENT_TYPE = "text/plain";

    private final FilenameGenerator filenameGenerator;
    private final RequestProxy proxyRequest;
    private final ResponseWriter responseWriter;

    private List<Moxy> moxies;
    private String path;
    private int index = 0;


    public MoxyRequestHandler(FilenameGenerator filenameGenerator,
                              RequestProxy proxyRequest,
                              ResponseWriter responseWriter,
                              String path,
                              List<Moxy> moxies) {
        this.filenameGenerator = filenameGenerator;
        this.proxyRequest = proxyRequest;
        this.responseWriter = responseWriter;
        this.path = path;
        this.moxies = moxies;

        Requests.reset();
    }


    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

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
                throw new RuntimeException("You must annotate your test with either 'responses' or 'files', but not both");
            }

            String proxy = moxyData.getProxy();
            if (!proxy.isEmpty()) {

                InputStream inputStream = proxyRequest.proxyRequest(httpServletRequest, httpServletResponse, proxy);

                // record the reponse to a file
                if (fileCount > 0 || indexed)  {

                    String filename = filenameGenerator.generateFilename(files, indexed, index);
                    saveResponseToFile(path, filename, inputStream);
                }
            } else {

                LOG.info("current index {}, indexed {}", index, indexed);
                LOG.info("{} files, {} static responses", fileCount, responses.length);

                if (responses.length > index) {

                    // write response body using annotation value
                    String response = responses[index];
                    responseWriter.writeStringToResponse(httpServletResponse, response);

                } else if (fileCount > index || indexed) {

                    // write response using file contents
                    String filename = filenameGenerator.generateFilename(files, indexed, index);

                    if (filename.startsWith("/")) {
                        responseWriter.writeAbsoluteFileToResponse(httpServletResponse, filename);
                    } else {
                        responseWriter.writeRelativeFileToResponse(httpServletResponse, path, filename);
                    }
                }
            }

            index++;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }





    // capture

    private void saveResponseToFile(String path, String filename, InputStream inputStream) throws IOException {
        File file = new File(path, filename);
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
