package com.tomkp.moxy;

public interface MoxyHttpServer {


    void start(int port, MoxyRequestHandler handler);

    void stop();

}
