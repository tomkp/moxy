package com.tomkp.moxy.writers;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;

public class ResponseWriter {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseWriter.class);


    private final HttpResponseWriter httpResponseWriter;


    public ResponseWriter(HttpResponseWriter httpResponseWriter) {
        this.httpResponseWriter = httpResponseWriter;
    }

    public void writeAbsoluteFileToResponse(HttpServletResponse httpServletResponse, String filename) throws IOException {
        InputStream inputStream = this.getClass().getResourceAsStream(filename);
        httpResponseWriter.writeResponse(httpServletResponse, inputStream);
    }


    public void writeRelativeFileToResponse(HttpServletResponse httpServletResponse, String resourcePath, String filename) throws IOException {
        File file = new File(resourcePath, filename);
        InputSupplier<FileInputStream> inputSupplier = Files.newInputStreamSupplier(file);
        InputStream inputStream = inputSupplier.getInput();
        httpResponseWriter.writeResponse(httpServletResponse, inputStream);
    }


    public void writeStringToResponse(HttpServletResponse httpServletResponse, String response) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(response.getBytes(Charset.forName("UTF-8")));
        httpResponseWriter.writeResponse(httpServletResponse, inputStream);
    }


    public void writeResponseToFile(String path, String filename, InputStream inputStream) throws IOException {
        File file = new File(path, filename);
        if (!file.exists()) {
            Files.createParentDirs(file);
            boolean created = file.createNewFile();
            LOG.info("file '{}' created '{}'", file, created);
        }
        ByteStreams.copy(inputStream, new FileOutputStream(file));
    }
}
