package com.tomkp.moxy;

import com.google.common.io.ByteStreams;
import com.google.common.io.InputSupplier;
import com.google.common.io.Resources;
import com.tomkp.moxy.writers.HttpResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RequestProxy {


    private static final Logger LOG = LoggerFactory.getLogger(RequestProxy.class);


    private HttpResponseWriter httpResponseWriter;


    public RequestProxy(HttpResponseWriter httpResponseWriter) {
        this.httpResponseWriter = httpResponseWriter;
    }


    public InputStream proxyRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String proxy) throws IOException {

        // generate the correct url to proxy to
        URL url = createProxyUrl(httpServletRequest, proxy);

        // perform http GET / POST / PUT / DELETE
        InputSupplier<? extends InputStream> inputSupplier = executeProxyHttpRequest(httpServletRequest, httpServletResponse, url);

        return inputSupplier.getInput();
    }



    private InputSupplier<? extends InputStream> executeProxyHttpRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, URL url) throws IOException {
        InputSupplier<? extends InputStream> inputSupplier;
        String method = httpServletRequest.getMethod();
        if (method.equalsIgnoreCase("GET")) {
            inputSupplier = httpGet(httpServletResponse, url);
        } else {
            inputSupplier = httpPost(httpServletRequest, httpServletResponse, url);
        }
        return inputSupplier;
    }




    // http methods



    private InputSupplier<? extends InputStream> httpGet(HttpServletResponse httpServletResponse, URL url) throws IOException {
        InputSupplier<? extends InputStream> inputSupplier = Resources.newInputStreamSupplier(url);
        httpResponseWriter.writeResponse(httpServletResponse, inputSupplier.getInput());
        return inputSupplier;
    }


    private InputSupplier<? extends InputStream> httpPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, URL url) throws IOException {
        String method = httpServletRequest.getMethod();
        byte[] requestBytes = ByteStreams.toByteArray(httpServletRequest.getInputStream());
        byte[] responseBytes = write(url, requestBytes, method);
        InputSupplier<? extends InputStream> inputSupplier = ByteStreams.newInputStreamSupplier(responseBytes);
        ByteStreams.copy(inputSupplier, httpServletResponse.getOutputStream());
        return inputSupplier;
    }


    private byte[] write(URL url, byte[] body, String method) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod(method);
        InputSupplier<ByteArrayInputStream> inputSupplier = ByteStreams.newInputStreamSupplier(body);
        ByteStreams.copy(inputSupplier, httpURLConnection.getOutputStream());
        return ByteStreams.toByteArray(httpURLConnection.getInputStream());
    }



    //


    private URL createProxyUrl(HttpServletRequest httpServletRequest, String proxy) throws MalformedURLException {
        String pathInfo = httpServletRequest.getPathInfo();
        LOG.info("pathInfo: '{}'", pathInfo);
        String queryString = httpServletRequest.getQueryString();
        LOG.info("queryString: '{}'", queryString);
        if (queryString == null) {
            queryString = "";
        } else {
            queryString = "?" + queryString;
        }
        URL url = new URL(proxy + pathInfo + queryString);
        LOG.info("proxy to '{}'", url);
        return url;
    }


}
