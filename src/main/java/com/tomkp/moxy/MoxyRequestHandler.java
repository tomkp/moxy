package com.tomkp.moxy;

import com.tomkp.moxy.writers.ResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;

public class MoxyRequestHandler {

    private static final Logger LOG = LoggerFactory.getLogger(MoxyRequestHandler.class);


    private final FilenameGenerator filenameGenerator;
    private final RequestProxy proxyRequest;
    private final ResponseWriter responseWriter;

    private MoxyData moxyData;
    private String path;
    private int index = 0;


    public MoxyRequestHandler(FilenameGenerator filenameGenerator,
                              RequestProxy proxyRequest,
                              ResponseWriter responseWriter,
                              String path,
                              MoxyData moxyData) {
        this.filenameGenerator = filenameGenerator;
        this.proxyRequest = proxyRequest;
        this.responseWriter = responseWriter;
        this.path = path;
        this.moxyData = moxyData;

        Requests.reset();
    }


    public void process(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        Requests.capture(httpServletRequest);

        try {
            int statusCode = moxyData.getStatusCode(index);

            List<Cookie> httpCookies = moxyData.getCookies(index);

            httpServletResponse.setStatus(statusCode);
            for (Cookie httpCookie : httpCookies) {
                httpServletResponse.addCookie(httpCookie);
            }
            httpServletResponse.setContentType(moxyData.getContentType(index));

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

                // record the response to a file
                if (fileCount > 0 || indexed)  {

                    String filename = filenameGenerator.generateFilename(files, indexed, index);
                    responseWriter.writeResponseToFile(path, filename, inputStream);
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
            throw new RuntimeException("error processing request", e);
        }
    }

}
