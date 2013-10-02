package com.tomkp.moxy;

import java.lang.reflect.Method;

public class MoxyTestRunner {

    private MoxyHttpServer moxyHttpServer;


    public MoxyTestRunner(MoxyHttpServer moxyHttpServer) {
        this.moxyHttpServer = moxyHttpServer;
    }


    public void initialise(Class<?> testClass, Method method) {

        TestSessionFactory testSessionFactory = new TestSessionFactory();

        TestSession testSession = testSessionFactory.createTestSession(testClass, method);

        if (!testSession.isEmpty()) {

            int port = testSession.getPort();

            MoxyRequestHandler handler = new MoxyRequestHandler(testSession);

            moxyHttpServer.start(port, handler);
        }
    }


    public void end() {
        moxyHttpServer.stop();
    }

}
