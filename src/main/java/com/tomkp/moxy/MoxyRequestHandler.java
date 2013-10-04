package com.tomkp.moxy;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.Map;

public class MoxyRequestHandler {


    private static final Logger LOG = LoggerFactory.getLogger(MoxyRequestHandler.class);


    private final TestSession testSession;


    public MoxyRequestHandler(TestSession testSession) {
        this.testSession = testSession;
        Requests.reset();
    }


    public void process(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        Requests.capture(httpServletRequest);

        try {

            configureHttpHeaders(httpServletResponse);

            InputStream inputStream = testSession.generateResponse(httpServletRequest);

            if (inputStream != null) {

                // APPLY REPLACEMENTS

                InputSupplier<? extends InputStream> inputSupplier = applyReplacements(inputStream);

                // WRITE RESPONSE
                writeResponse(httpServletResponse, inputSupplier);

                if (testSession.shouldSaveResponse()) {

                    // SAVE RESPONSES TO FILE

                    saveResponses(inputSupplier);
                }
            }

            testSession.increment();

        } catch (Exception e) {
            throw new RuntimeException("error processing request", e);
        }
    }


    private void writeResponse(HttpServletResponse httpServletResponse, InputSupplier<? extends InputStream> inputSupplier) throws IOException {
        ByteStreams.copy(inputSupplier.getInput(), httpServletResponse.getOutputStream());
    }


    private void saveResponses(InputSupplier<? extends InputStream> inputSupplier) throws IOException {
        String filename = testSession.getFilename();

        File file = new File(testSession.getPath(), filename);
        if (!file.exists()) {
            Files.createParentDirs(file);
            boolean created = file.createNewFile();
            LOG.info("file '{}' created '{}'", file, created);
        }
        ByteStreams.copy(inputSupplier, new FileOutputStream(file));
    }



    private InputSupplier<? extends InputStream> applyReplacements(InputStream inputStream) throws IOException {
        Map<String, String> template = testSession.getReplacements();
        InputSupplier<? extends InputStream> inputSupplier = replace(template, inputStream);
        return inputSupplier;
    }


    private void configureHttpHeaders(HttpServletResponse httpServletResponse) {
        int statusCode = testSession.getStatusCode();
        List<Cookie> httpCookies = testSession.getCookies();
        String contentType = testSession.getContentType();

        httpServletResponse.setStatus(statusCode);
        for (Cookie httpCookie : httpCookies) {
            httpServletResponse.addCookie(httpCookie);
        }
        httpServletResponse.setContentType(contentType);
    }


    private InputSupplier<? extends InputStream> replace(Map<String, String> replacementMap, InputStream inputStream) throws IOException {
        LOG.info("replace: '{}'", replacementMap);
        String str = CharStreams.toString(new InputStreamReader(inputStream, Charsets.UTF_8));
        for (String from : replacementMap.keySet()) {
            String to = replacementMap.get(from);
            LOG.info("replace '" + from + "' with '" + to + "'");
            str = str.replaceAll(from, to);
        }
        return ByteStreams.newInputStreamSupplier(str.getBytes("UTF-8"));
    }

}
