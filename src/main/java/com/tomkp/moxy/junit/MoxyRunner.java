package com.tomkp.moxy.junit;

import com.tomkp.moxy.*;
import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.jetty.EmbeddedJetty;
import com.tomkp.moxy.writers.AbsoluteFileResponseWriter;
import com.tomkp.moxy.writers.RelativeFileResponseWriter;
import com.tomkp.moxy.writers.Utf8StringResponseWriter;
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


            FilenameGenerator filenameGenerator = new FilenameGenerator();
            ResponseWriter responseWriter = new ResponseWriter();
            RequestProxy proxyRequest = new RequestProxy(responseWriter);

            String path = testClass.getResource(".").getPath();


            MoxyRequestHandler handler = new MoxyRequestHandler(
                    filenameGenerator,
                    proxyRequest,
                    new RelativeFileResponseWriter(responseWriter),
                    new AbsoluteFileResponseWriter(responseWriter),
                    new Utf8StringResponseWriter(responseWriter),
                    path,
                    moxies);



            HttpServer httpServer = new EmbeddedJetty();

            httpServer.start(port, handler);

            super.runChild(method, notifier);

            httpServer.stop();

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