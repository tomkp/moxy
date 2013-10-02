package com.tomkp.moxy;

import com.google.common.base.Charsets;
import com.google.common.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class MoxyRequestHandler {


    private static final Logger LOG = LoggerFactory.getLogger(MoxyRequestHandler.class);


    private final TestSession testSession;
    private final String path;



    public MoxyRequestHandler(String path,
                              TestSession testSession) {
        this.path = path;
        this.testSession = testSession;

        Requests.reset();
    }


    public void process(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        Requests.capture(httpServletRequest);

        try {

            configureHttpHeaders(httpServletResponse);

            if (testSession.hasProxy()) {

                proxyRequest(httpServletRequest, httpServletResponse);

            } else if (testSession.hasResponses()) {

                String response = testSession.getResponse();

                InputStream inputStream = new ByteArrayInputStream(response.getBytes(Charset.forName("UTF-8")));

                Map<String, String> template = testSession.getReplacements();

                InputSupplier<? extends InputStream> inputSupplier = replace(template, inputStream);

                ByteStreams.copy(inputSupplier.getInput(), httpServletResponse.getOutputStream());

            } else if (testSession.hasFiles()) {

                // write response using file contents
                String filename = testSession.getFilename();
                if (filename.startsWith("/")) {

                    InputStream inputStream = this.getClass().getResourceAsStream(filename);

                    Map<String, String> template = testSession.getReplacements();

                    InputSupplier<? extends InputStream> inputSupplier = replace(template, inputStream);

                    ByteStreams.copy(inputSupplier.getInput(), httpServletResponse.getOutputStream());

                } else {

                    File file = new File(path, filename);
                    InputSupplier<? extends InputStream> inputSupplier = Files.newInputStreamSupplier(file);

                    Map<String, String> template = testSession.getReplacements();

                    inputSupplier = replace(template, inputSupplier.getInput());

                    ByteStreams.copy(inputSupplier.getInput(), httpServletResponse.getOutputStream());

                }
            }
            testSession.increment();

        } catch (Exception e) {
            throw new MoxyException("error processing request", e);
        }
    }





    private void configureHttpHeaders(HttpServletResponse httpServletResponse) {
        int statusCode = testSession.getStatusCode();
        List<Cookie> httpCookies = testSession.getCookies();
        String contentType = testSession.getContentType();

        httpServletResponse.setStatus(statusCode);
        for (Cookie httpCookie : httpCookies) {
            httpServletResponse.addCookie(httpCookie);
        }
        httpServletResponse.setContentType(contentType);
    }



    private void proxyRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        String proxy = testSession.getProxy();
        boolean indexed = testSession.getIndexed();
        int fileCount = testSession.getFileCount();

        // generate the correct url to proxy to
        URL url = createProxyUrl(httpServletRequest, proxy);

        // perform http GET / POST / PUT / DELETE
        InputSupplier<? extends InputStream> inputSupplier;
        String method = httpServletRequest.getMethod();

        if (method.equalsIgnoreCase("GET")) {
            inputSupplier = Resources.newInputStreamSupplier(url);
        } else {
            byte[] requestBytes = ByteStreams.toByteArray(httpServletRequest.getInputStream());
            byte[] responseBytes = write(url, requestBytes, method);
            inputSupplier = ByteStreams.newInputStreamSupplier(responseBytes);
        }

        Map<String, String> template = testSession.getReplacements();

        inputSupplier = replace(template, inputSupplier.getInput());

        ByteStreams.copy(inputSupplier, httpServletResponse.getOutputStream());


        // record the response to a file
        if (fileCount > 0 || indexed)  {
            String filename = testSession.getFilename();

            File file = new File(path, filename);
            if (!file.exists()) {
                Files.createParentDirs(file);
                boolean created = file.createNewFile();
                LOG.info("file '{}' created '{}'", file, created);
            }
            ByteStreams.copy(inputSupplier, new FileOutputStream(file));
        }
    }




    private byte[] write(URL url, byte[] body, String method) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod(method);
        InputSupplier<ByteArrayInputStream> inputSupplier = ByteStreams.newInputStreamSupplier(body);
        ByteStreams.copy(inputSupplier, httpURLConnection.getOutputStream());
        return ByteStreams.toByteArray(httpURLConnection.getInputStream());
    }



    private InputSupplier<? extends InputStream> replace(Map<String, String> template, InputStream inputStream) throws IOException {
        LOG.info("replace: '{}'", template);
        String str = CharStreams.toString(new InputStreamReader(inputStream, Charsets.UTF_8));
        for (String from : template.keySet()) {
            String to = template.get(from);
            System.out.println("replace '" + from + "' with '" + to + "'");
            str = str.replaceAll(from, to);
        }
        return ByteStreams.newInputStreamSupplier(str.getBytes("UTF-8"));
    }


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
