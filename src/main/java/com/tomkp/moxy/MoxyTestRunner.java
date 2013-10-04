package com.tomkp.moxy;

import java.lang.reflect.Method;

public class MoxyTestRunner {

    private MoxyHttpServer moxyHttpServer;
    private TestSessionFactory testSessionFactory;


    public MoxyTestRunner(MoxyHttpServer moxyHttpServer, TestSessionFactory testSessionFactory) {
        this.moxyHttpServer = moxyHttpServer;
        this.testSessionFactory = testSessionFactory;
    }


    public void initialise(Class<?> testClass, Method method) {

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
