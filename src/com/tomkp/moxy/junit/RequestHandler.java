package com.tomkp.moxy.junit;

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

            //httpServletResponse.addCookie();

            httpServletResponse.setContentType(moxy.contentType());
            String[] responses = moxy.response();
            String[] files = moxy.file();

            if (responses.length > 0 && files.length > 0) {
                throw new IOException("You must annotate your test with either 'responses' or 'files', but not both");
            }

            String proxy = moxy.proxy();
            if (!proxy.isEmpty()) {

                InputSupplier<InputStream> inputSupplier = Resources.newInputStreamSupplier(new URL(proxy));
                ByteStreams.copy(inputSupplier.getInput(), httpServletResponse.getOutputStream());

                if (files.length > 0)  {
                    String filename = files[index];
                    URL resource = testClass.getResource(".");
                    File file = new File(resource.getPath(), filename);
                    if (!file.exists()) {
                        LOG.info("create file '{}'", file);
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
}
