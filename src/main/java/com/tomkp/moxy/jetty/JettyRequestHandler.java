package com.tomkp.moxy.jetty;

import com.tomkp.moxy.MoxyRequestHandler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JettyRequestHandler extends AbstractHandler {


    private MoxyRequestHandler moxyRequestHandler;


    public JettyRequestHandler(MoxyRequestHandler moxyRequestHandler) {
        this.moxyRequestHandler = moxyRequestHandler;
    }


    @Override
    public void handle(String path, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        try {
            moxyRequestHandler.process(httpServletRequest, httpServletResponse);
        } finally {
            request.setHandled(true);
        }
    }
}

