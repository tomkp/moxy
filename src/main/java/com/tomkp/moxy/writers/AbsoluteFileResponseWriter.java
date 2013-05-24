package com.tomkp.moxy.writers;

import com.tomkp.moxy.ResponseWriter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

public class AbsoluteFileResponseWriter {

    private final ResponseWriter responseWriter;

    public AbsoluteFileResponseWriter(ResponseWriter responseWriter) {
        this.responseWriter = responseWriter;
    }

    public void writeAbsoluteFileToResponse(HttpServletResponse httpServletResponse, String filename) throws IOException {
        InputStream inputStream = this.getClass().getResourceAsStream(filename);
        responseWriter.writeResponse(httpServletResponse, inputStream);
    }


}
