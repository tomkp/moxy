package com.tomkp.moxy.writers;

import com.tomkp.moxy.ResponseWriter;
import com.tomkp.moxy.readers.Utf8StringReader;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

public class Utf8StringResponseWriter {

    private final ResponseWriter responseWriter;
    private final Utf8StringReader utf8StringReader;

    public Utf8StringResponseWriter(ResponseWriter responseWriter, Utf8StringReader utf8StringReader) {
        this.responseWriter = responseWriter;
        this.utf8StringReader = utf8StringReader;
    }

    public void writeStringToResponse(HttpServletResponse httpServletResponse, String response) throws IOException {
        InputStream inputStream = utf8StringReader.readString(response);
        responseWriter.writeResponse(httpServletResponse, inputStream);
    }

}
