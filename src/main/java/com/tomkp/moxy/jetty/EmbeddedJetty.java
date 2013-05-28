package com.tomkp.moxy.jetty;

import com.tomkp.moxy.HttpServer;
import com.tomkp.moxy.MoxyException;
import com.tomkp.moxy.MoxyRequestHandler;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmbeddedJetty implements HttpServer {

    private static final Logger LOG = LoggerFactory.getLogger(EmbeddedJetty.class);

    private Server server;


    public void start(int port, MoxyRequestHandler handler) {
        LOG.info("start server on port {}", port);

        try {
            server = new Server(port);
            server.setHandler(new JettyRequestHandler(handler));
            server.start();
        } catch (Exception e) {
            throw new MoxyException("error starting server", e);
        }
    }


    public void stop() {
        try {
            if (server != null) {
                server.stop();
            }
        } catch (Exception e) {
            throw new MoxyException("error stopping server", e);
        }
    }
}
