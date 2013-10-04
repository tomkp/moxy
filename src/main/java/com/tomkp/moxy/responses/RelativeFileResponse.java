package com.tomkp.moxy.responses;

import com.tomkp.moxy.TestSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

public class RelativeFileResponse implements Response {

    private static final Logger LOG = LoggerFactory.getLogger(RelativeFileResponse.class);


    @Override
    public InputStream getResponse(TestSession testSession, HttpServletRequest request) throws IOException {
        InputStream inputStream;
        String filename = testSession.getFilename();

        // RELATIVE FILES
        inputStream = this.getClass().getResourceAsStream(filename);

        return inputStream;
    }


}
