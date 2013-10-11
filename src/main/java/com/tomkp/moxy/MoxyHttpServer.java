package com.tomkp.moxy;

public interface MoxyHttpServer {


    void start(int port, RequestHandler handler);

    void stop();

}
