package com.tomkp.moxy.writers;

import com.google.common.io.ByteStreams;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

public class HttpResponseWriter {


    public void writeResponse(HttpServletResponse httpServletResponse, InputStream inputStream) throws IOException {
        ByteStreams.copy(inputStream, httpServletResponse.getOutputStream());
    }

}