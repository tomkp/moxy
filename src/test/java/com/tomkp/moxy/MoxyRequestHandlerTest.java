package com.tomkp.moxy;


import com.tomkp.moxy.writers.HttpResponseWriter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RunWith(MockitoJUnitRunner.class)
public class MoxyRequestHandlerTest {


    //@Mock private RequestProxy requestProxy;
    @Mock private HttpResponseWriter writer;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;

    @Test
    public void x() {

        MoxyData moxyData = new MoxyData();
        //MoxyRequestHandler handler = new MoxyRequestHandler(requestProxy, writer, "/", moxyData);
        MoxyRequestHandler handler = new MoxyRequestHandler(writer, "/", moxyData);
        handler.process(request, response);
    }

}
