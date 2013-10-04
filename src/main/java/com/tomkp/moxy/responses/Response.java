package com.tomkp.moxy.responses;

import com.tomkp.moxy.TestSession;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

public interface Response {

    InputStream getResponse(TestSession testSession, HttpServletRequest request) throws IOException;

}
