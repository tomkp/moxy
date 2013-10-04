package com.tomkp.moxy.responses;

import com.tomkp.moxy.TestSession;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class StaticResponse  implements Response {


    @Override
    public InputStream getResponse(TestSession testSession, HttpServletRequest request) throws IOException {
        String response = testSession.getResponse();
        return new ByteArrayInputStream(response.getBytes(Charset.forName("UTF-8")));
    }

}
