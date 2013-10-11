package com.tomkp.moxy;

import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.filenames.FilenameGenerator;
import com.tomkp.moxy.profile.Profile;
import com.tomkp.moxy.profile.ProfileRequestHandler;

import java.lang.reflect.Method;

public class MoxyTestRunner {

    private MoxyHttpServer moxyHttpServer;


    public MoxyTestRunner(MoxyHttpServer moxyHttpServer) {
        this.moxyHttpServer = moxyHttpServer;
    }


    public void initialise(Class<?> testClass, Method method) {
        Moxy moxy = method.getAnnotation(Moxy.class);
        if (moxy != null) {
            String path = testClass.getResource(".").getPath();

            Profile profile = new Profile(path)
                    .setContentTypes(moxy.contentType())
                    .setCookies(moxy.cookie())
                    .setFiles(moxy.file())
                    .setIndexed(moxy.indexed())
                    .setProxy(moxy.proxy())
                    .setResponses(moxy.response())
                    .setStatusCodes(moxy.statusCode())
                    .setReplacements(moxy.replace())
                    .setFilenameGenerator(getFilenameGenerator(moxy.filenameGenerator()));
                    ;

            int port = moxy.port();
            RequestHandler handler = new ProfileRequestHandler(profile);
            moxyHttpServer.start(port, handler);

        }
    }




    public void end() {
        moxyHttpServer.stop();
    }


    private FilenameGenerator getFilenameGenerator(Class<? extends FilenameGenerator> aClass) {
        try {
            return aClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("unable to instantiate new instance of '" + aClass + "'", e);
        }
    }
}
