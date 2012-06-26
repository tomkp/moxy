package com.tomkp.moxy.junit;

import com.tomkp.moxy.annotations.Moxy;
import org.eclipse.jetty.server.Server;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoxyRunner extends BlockJUnit4ClassRunner {


    private static final Logger LOG = LoggerFactory.getLogger(MoxyRunner.class);


    private Class<?> testClass;


    public MoxyRunner(Class<?> testClass) throws Exception {
        super(testClass);
        this.testClass = testClass;
    }


    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        long start = System.currentTimeMillis();
        if (LOG.isInfoEnabled()) LOG.info(testClass + "." + method.getName() + "");
        Moxy moxyMethodAnnotation = method.getAnnotation(Moxy.class);
        if (moxyMethodAnnotation != null) {
            Server server = new Server(moxyMethodAnnotation.port());
            try {
                server.setHandler(new RequestHandler(testClass, moxyMethodAnnotation));
                server.start();
                super.runChild(method, notifier);
                server.stop();
            } catch (Exception e) {
                throw new RuntimeException("error running server", e);
            }
        }
        long end = System.currentTimeMillis();
        LOG.info("" + (end - start) + "ms");
    }


}