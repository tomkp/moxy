package com.tomkp.moxy.writers;

import com.tomkp.moxy.ResponseWriter;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class Utf8StringResponseWriter {

    private final ResponseWriter responseWriter;

    public Utf8StringResponseWriter(ResponseWriter responseWriter) {
        this.responseWriter = responseWriter;
    }

    public void writeStringToResponse(HttpServletResponse httpServletResponse, String response) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(response.getBytes(Charset.forName("UTF-8")));
        responseWriter.writeResponse(httpServletResponse, inputStream);
    }

}
