package com.tomkp.moxy;

import java.lang.reflect.Method;

public class MoxyTestRunner {

    private MoxyHttpServer moxyHttpServer;


    public MoxyTestRunner(MoxyHttpServer moxyHttpServer) {
        this.moxyHttpServer = moxyHttpServer;
    }


    public void initialise(Class<?> testClass, Method method) {

        MoxyDataFactory moxyDataFactory = new MoxyDataFactory();

        MoxyData moxyData = moxyDataFactory.createMoxyData(testClass, method);

        if (!moxyData.isEmpty()) {

            int port = moxyData.getPort();

            String path = testClass.getResource(".").getPath();

            MoxyRequestHandler handler = new MoxyRequestHandler(path, moxyData);

            moxyHttpServer.start(port, handler);
        }
    }


    public void end() {
        moxyHttpServer.stop();
    }





}
