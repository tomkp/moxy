package com.tomkp.moxy.responses;

import com.tomkp.moxy.TestSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class StaticResponse  implements Response {

    private static final Logger LOG = LoggerFactory.getLogger(StaticResponse.class);



    public InputStream getResponse(TestSession testSession, HttpServletRequest request) throws IOException {
        InputStream inputStream;
        String response = testSession.getResponse();

        inputStream = new ByteArrayInputStream(response.getBytes(Charset.forName("UTF-8")));
        return inputStream;
    }

}
