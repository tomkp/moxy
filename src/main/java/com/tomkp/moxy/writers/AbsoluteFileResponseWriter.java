package com.tomkp.moxy.writers;

import com.tomkp.moxy.ResponseWriter;
import com.tomkp.moxy.readers.AbsoluteFileReader;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

public class AbsoluteFileResponseWriter {

    private final ResponseWriter responseWriter;
    private final AbsoluteFileReader absoluteFileReader;

    public AbsoluteFileResponseWriter(ResponseWriter responseWriter, AbsoluteFileReader absoluteFileReader) {
        this.absoluteFileReader = absoluteFileReader;
        this.responseWriter = responseWriter;
    }

    public void writeAbsoluteFileToResponse(HttpServletResponse httpServletResponse, String filename) throws IOException {
        InputStream inputStream = absoluteFileReader.readAbsoluteFile(filename);
        responseWriter.writeResponse(httpServletResponse, inputStream);
    }


}
