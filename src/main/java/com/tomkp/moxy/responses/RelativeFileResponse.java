package com.tomkp.moxy.responses;

import com.tomkp.moxy.TestSession;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

public class RelativeFileResponse implements Response {


    @Override
    public InputStream getResponse(TestSession testSession, HttpServletRequest request) throws IOException {
        String filename = testSession.getFilename(request);
        return this.getClass().getResourceAsStream(filename);
    }

}
