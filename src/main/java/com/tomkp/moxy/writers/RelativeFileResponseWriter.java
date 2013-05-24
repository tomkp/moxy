package com.tomkp.moxy.writers;

import com.tomkp.moxy.ResponseWriter;
import com.tomkp.moxy.readers.RelativeFileReader;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

public class RelativeFileResponseWriter {

    private final ResponseWriter responseWriter;
    private final RelativeFileReader relativeFileReader;


    public RelativeFileResponseWriter(ResponseWriter responseWriter, RelativeFileReader relativeFileReader) {
        this.responseWriter = responseWriter;
        this.relativeFileReader = relativeFileReader;
    }

    public void writeRelativeFileToResponse(HttpServletResponse httpServletResponse, String resourcePath, String filename) throws IOException {
        InputStream inputStream = relativeFileReader.readRelativeFile(resourcePath, filename);
        responseWriter.writeResponse(httpServletResponse, inputStream);
    }
}
