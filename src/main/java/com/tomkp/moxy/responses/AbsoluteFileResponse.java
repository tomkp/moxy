package com.tomkp.moxy.responses;

import com.google.common.io.Files;
import com.tomkp.moxy.TestSession;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class AbsoluteFileResponse  implements Response {

    @Override
    public InputStream getResponse(TestSession testSession, HttpServletRequest request) throws IOException {
        // ABSOLUTE FILES
        String filename = testSession.getFilename(request);
        File file = new File(testSession.getPath(), filename);
        return Files.newInputStreamSupplier(file).getInput();
    }

}
