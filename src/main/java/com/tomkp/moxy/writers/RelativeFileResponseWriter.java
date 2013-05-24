package com.tomkp.moxy.writers;

import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.tomkp.moxy.ResponseWriter;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class RelativeFileResponseWriter {

    private final ResponseWriter responseWriter;


    public RelativeFileResponseWriter(ResponseWriter responseWriter) {
        this.responseWriter = responseWriter;
    }

    public void writeRelativeFileToResponse(HttpServletResponse httpServletResponse, String resourcePath, String filename) throws IOException {
        File file = new File(resourcePath, filename);
        InputSupplier<FileInputStream> inputSupplier = Files.newInputStreamSupplier(file);
        InputStream inputStream = inputSupplier.getInput();
        responseWriter.writeResponse(httpServletResponse, inputStream);
    }
}
