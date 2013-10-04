package com.tomkp.moxy.responses;

import com.google.common.io.ByteStreams;
import com.google.common.io.InputSupplier;
import com.google.common.io.Resources;
import com.tomkp.moxy.TestSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ProxiedResponse  implements Response {

    private static final Logger LOG = LoggerFactory.getLogger(ProxiedResponse.class);


    public InputStream getResponse(TestSession testSession, HttpServletRequest request) throws IOException {
        InputStream inputStream;
        String proxy = testSession.getProxy();

        // generate the correct url to proxy to
        URL url = createProxyUrl(request, proxy);

        // perform http GET / POST / PUT / DELETE
        String method = request.getMethod();

        if (method.equalsIgnoreCase("GET")) {
            // HTTP GET
            inputStream = Resources.newInputStreamSupplier(url).getInput();
        } else {
            // HTTP POST / DELETE / PUT
            byte[] requestBytes = ByteStreams.toByteArray(request.getInputStream());
            byte[] responseBytes = write(url, requestBytes, method);
            inputStream = ByteStreams.newInputStreamSupplier(responseBytes).getInput();
        }
        return inputStream;
    }

    public byte[] write(URL url, byte[] body, String method) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod(method);
        InputSupplier<ByteArrayInputStream> inputSupplier = ByteStreams.newInputStreamSupplier(body);
        ByteStreams.copy(inputSupplier, httpURLConnection.getOutputStream());
        return ByteStreams.toByteArray(httpURLConnection.getInputStream());
    }

    public URL createProxyUrl(HttpServletRequest httpServletRequest, String proxy) throws MalformedURLException {
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
