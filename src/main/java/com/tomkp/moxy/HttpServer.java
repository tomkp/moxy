package com.tomkp.moxy;

public interface HttpServer {


    void start(int port, MoxyRequestHandler handler);

    void stop();

}
