package com.tomkp.moxy.junit;

import com.tomkp.moxy.RequestHandler;
import com.tomkp.moxy.annotations.Moxy;
import org.eclipse.jetty.server.Server;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MoxyRunner extends BlockJUnit4ClassRunner {


    private static final Logger LOG = LoggerFactory.getLogger(MoxyRunner.class);
    private static final int DEFAULT_PORT = 9001;


    private Class<?> testClass;


    public MoxyRunner(Class<?> testClass) throws Exception {
        super(testClass);
        this.testClass = testClass;
    }


    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        long start = System.currentTimeMillis();
        if (LOG.isInfoEnabled()) LOG.info(testClass + "." + method.getName() + "");

        List<Moxy> moxies = new ArrayList<Moxy>();
        Moxy moxyMethodAnnotation = method.getAnnotation(Moxy.class);
        if (moxyMethodAnnotation != null) {
            moxies.add(moxyMethodAnnotation);
        }
        addMoxies(moxies, testClass);

        LOG.info("moxies: '{}'", moxies);

        if (moxies.size() > 0) {
            int port = selectPort(moxies);
            LOG.info("start server on port {}", port);
            Server server = new Server(port);
            try {
                RequestHandler handler = new RequestHandler(testClass, moxies);
                server.setHandler(handler);
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


    private void addMoxies(List<Moxy> moxies, Class<?> claz) {
        Moxy moxy = claz.getAnnotation(Moxy.class);
        if (moxy != null) {
            moxies.add(moxy);
        }
        Class<?> superclass = claz.getSuperclass();
        if (superclass != null) {
            addMoxies(moxies, superclass);
        }
    }


    private int selectPort(List<Moxy> moxies) {
        int port = DEFAULT_PORT;
        for (Moxy moxy : moxies) {
            if (moxy.port() != 0) {
                port = moxy.port();
                break;
            }
        }
        return port;
    }


}