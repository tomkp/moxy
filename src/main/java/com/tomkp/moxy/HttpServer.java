package com.tomkp.moxy;

public interface HttpServer {


    public void start(int port, MoxyRequestHandler handler);

    public void stop();

}
