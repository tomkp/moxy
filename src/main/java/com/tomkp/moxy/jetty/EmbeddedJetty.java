package com.tomkp.moxy.jetty;

import com.tomkp.moxy.MoxyHttpServer;
import com.tomkp.moxy.RequestHandler;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmbeddedJetty implements MoxyHttpServer {

    private static final Logger LOG = LoggerFactory.getLogger(EmbeddedJetty.class);


    private Server server;


    public void start(int port, RequestHandler handler) {
        LOG.info("start server on port {}", port);

        try {
            server = new Server(port);
            server.setHandler(new JettyRequestHandler(handler));
            server.start();
        } catch (Exception e) {
            throw new RuntimeException("error starting server", e);
        }
    }


    public void stop() {
        try {
            if (server != null) {
                server.stop();
            }
        } catch (Exception e) {
            throw new RuntimeException("error stopping server", e);
        }
    }
}
