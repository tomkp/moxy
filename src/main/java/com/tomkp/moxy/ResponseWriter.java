package com.tomkp.moxy;

import com.google.common.io.ByteStreams;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

public class ResponseWriter {


    public void writeResponse(HttpServletResponse httpServletResponse, InputStream inputStream) throws IOException {
        ByteStreams.copy(inputStream, httpServletResponse.getOutputStream());
    }

}