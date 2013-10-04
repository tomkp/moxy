package com.tomkp.moxy.responses;

import com.google.common.io.Files;
import com.tomkp.moxy.TestSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class AbsoluteFileResponse  implements Response {

    private static final Logger LOG = LoggerFactory.getLogger(AbsoluteFileResponse.class);



    public InputStream getResponse(TestSession testSession, HttpServletRequest request) throws IOException {
        InputStream inputStream;
        // ABSOLUTE FILES
        String filename = testSession.getFilename();
        File file = new File(testSession.getPath(), filename);
        inputStream = Files.newInputStreamSupplier(file).getInput();

        return inputStream;
    }


}
