package com.tomkp.moxy.junit;

import com.tomkp.moxy.HttpServer;
import com.tomkp.moxy.MoxyData;
import com.tomkp.moxy.MoxyRequestHandler;
import com.tomkp.moxy.RequestProxy;
import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.jetty.EmbeddedJetty;
import com.tomkp.moxy.writers.HttpResponseWriter;
import com.tomkp.moxy.writers.ResponseWriter;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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

        MoxyData moxyData = createMoxyData(method);

        if (!moxyData.isEmpty()) {

            int port = moxyData.getPort();

            String path = testClass.getResource(".").getPath();

            HttpResponseWriter httpResponseWriter = new HttpResponseWriter();

            ResponseWriter responseWriter = new ResponseWriter(httpResponseWriter);
            RequestProxy proxyRequest = new RequestProxy(httpResponseWriter);

            MoxyRequestHandler handler = new MoxyRequestHandler(
                    proxyRequest,
                    responseWriter,
                    path,
                    moxyData);

            HttpServer httpServer = new EmbeddedJetty();
            httpServer.start(port, handler);
            super.runChild(method, notifier);
            httpServer.stop();

        }
        long end = System.currentTimeMillis();
        LOG.info("" + (end - start) + "ms");
    }


    private MoxyData createMoxyData(FrameworkMethod method) {
        List<Moxy> moxies = new ArrayList<Moxy>();
        Moxy moxyMethodAnnotation = method.getAnnotation(Moxy.class);
        if (moxyMethodAnnotation != null) {
            moxies.add(moxyMethodAnnotation);
        }
        addParentMoxies(moxies, testClass);

        return new MoxyData(moxies);
    }


    private void addParentMoxies(List<Moxy> moxies, Class<?> claz) {
        Moxy moxy = claz.getAnnotation(Moxy.class);
        if (moxy != null) {
            moxies.add(moxy);
        }
        Class<?> superclass = claz.getSuperclass();
        if (superclass != null) {
            addParentMoxies(moxies, superclass);
        }
    }


}