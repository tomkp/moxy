package com.tomkp.moxy;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RunWith(MockitoJUnitRunner.class)
public class MoxyRequestHandlerTest {


    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;

    @Test
    public void x() {

        TestSession testSession = new TestSession("/");
        MoxyRequestHandler handler = new MoxyRequestHandler(testSession);
        handler.process(request, response);
    }

}
