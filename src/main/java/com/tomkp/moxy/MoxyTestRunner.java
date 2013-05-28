package com.tomkp.moxy;

import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.writers.HttpResponseWriter;
import com.tomkp.moxy.writers.ResponseWriter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MoxyTestRunner {


    private HttpServer httpServer;


    public MoxyTestRunner(HttpServer httpServer) {
        this.httpServer = httpServer;
    }


    public void initialise(Class<?> testClass, Method method) {

        MoxyData moxyData = createMoxyData(testClass, method);

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

            httpServer.start(port, handler);
        }
    }


    public void end() {
        httpServer.stop();
    }



    private MoxyData createMoxyData(Class<?> testClass, Method method) {
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
