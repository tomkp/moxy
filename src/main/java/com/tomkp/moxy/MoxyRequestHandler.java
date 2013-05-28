package com.tomkp.moxy;

import com.tomkp.moxy.writers.ResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MoxyRequestHandler {

    private static final Logger LOG = LoggerFactory.getLogger(MoxyRequestHandler.class);

    private final RequestProxy proxyRequest;
    private final ResponseWriter responseWriter;

    private final MoxyData moxyData;
    private final String path;
    private int index = 0;


    public MoxyRequestHandler(RequestProxy proxyRequest,
                              ResponseWriter responseWriter,
                              String path,
                              MoxyData moxyData) {
        this.proxyRequest = proxyRequest;
        this.responseWriter = responseWriter;
        this.path = path;
        this.moxyData = moxyData;

        Requests.reset();
    }


    public void process(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        Requests.capture(httpServletRequest);

        try {

            validateMoxyData();

            int statusCode = moxyData.getStatusCode(index);
            List<Cookie> httpCookies = moxyData.getCookies(index);
            String contentType = moxyData.getContentType(index);

            httpServletResponse.setStatus(statusCode);
            for (Cookie httpCookie : httpCookies) {
                httpServletResponse.addCookie(httpCookie);
            }
            httpServletResponse.setContentType(contentType);


            boolean indexed = moxyData.getIndexed();
            String proxy = moxyData.getProxy();

            if (!proxy.isEmpty()) {

                proxyRequest(httpServletRequest, httpServletResponse);

            } else {

                int fileCount = moxyData.getFileCount();
                int responseCount = moxyData.getResponseCount();

                if (responseCount > index) {

                    writeResponseUsingAnnotationValue(httpServletResponse);

                } else if (fileCount > index || indexed) {

                    writeResponseUsingFileContents(httpServletResponse);

                }
            }
            index++;

        } catch (Exception e) {
            throw new MoxyException("error processing request", e);
        }
    }

    private void validateMoxyData() {
        int fileCount = moxyData.getFileCount();
        int responseCount = moxyData.getResponseCount();

        if (responseCount > 0 && fileCount > 0) {
            throw new MoxyException("You must annotate your test with either 'responses' or 'files', but not both");
        }
    }


    private void proxyRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        String proxy = moxyData.getProxy();
        boolean indexed = moxyData.getIndexed();
        int fileCount = moxyData.getFileCount();

        InputStream inputStream = proxyRequest.proxyRequest(httpServletRequest, httpServletResponse, proxy);

        // record the response to a file
        if (fileCount > 0 || indexed)  {
            String filename = moxyData.getFilename(index);
            responseWriter.writeResponseToFile(path, filename, inputStream);
        }
    }


    private void writeResponseUsingFileContents(HttpServletResponse httpServletResponse) throws IOException {
        // write response using file contents
        String filename = moxyData.getFilename(index);
        if (filename.startsWith("/")) {
            responseWriter.writeAbsoluteFileToResponse(httpServletResponse, filename);
        } else {
            responseWriter.writeRelativeFileToResponse(httpServletResponse, path, filename);
        }
    }


    private void writeResponseUsingAnnotationValue(HttpServletResponse httpServletResponse) throws IOException {
        // write response body using annotation value
        String response = moxyData.getResponse(index);
        responseWriter.writeStringToResponse(httpServletResponse, response);
    }


}
