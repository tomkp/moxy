package com.tomkp.moxy;

import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.profile.Profile;
import com.tomkp.moxy.profile.ProfileRequestHandler;

import java.lang.reflect.Method;

public class MoxyTestRunner {

    private MoxyHttpServer moxyHttpServer;
    private TestSessionFactory testSessionFactory;


    public MoxyTestRunner(MoxyHttpServer moxyHttpServer, TestSessionFactory testSessionFactory) {
        this.moxyHttpServer = moxyHttpServer;
        this.testSessionFactory = testSessionFactory;
    }


    public void initialise(Class<?> testClass, Method method) {
        Moxy moxy = method.getAnnotation(Moxy.class);
        if (moxy != null) {
            String path = testClass.getResource(".").getPath();
            /*TestSession testSession = testSessionFactory.createTestSession(moxy, path);
            if (testSession != null) {
                int port = testSession.getPort();
                MoxyRequestHandler handler = new MoxyRequestHandler(testSession);
                moxyHttpServer.start(port, handler);
            }*/

            Profile profile = new Profile(path)
                    .setContentTypes(moxy.contentType())
                    .setCookies(moxy.cookie())
                    .setFiles(moxy.file())
                    .setIndexed(moxy.indexed())
                    //.setPort(moxy.port())
                    .setProxy(moxy.proxy())
                    .setResponses(moxy.response())
                    .setStatusCodes(moxy.statusCode())
                    .setReplacements(moxy.replace());

            int port = moxy.port();
            RequestHandler handler = new ProfileRequestHandler(profile);
            moxyHttpServer.start(port, handler);

        }
    }


    public void end() {
        moxyHttpServer.stop();
    }

}
