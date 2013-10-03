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


    public MoxyRequestHandler(TestSession testSession) {
        this.testSession = testSession;
        Requests.reset();
    }


    public void process(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        Requests.capture(httpServletRequest);

        try {

            configureHttpHeaders(httpServletResponse);

            InputStream inputStream = null;

            if (testSession.useProxiedResponse()) {

                // PROXY REQUESTS

                inputStream = returnProxiedResponse(httpServletRequest);


            } else if (testSession.useStaticResponse()) {

                // RETURN STATIC RESPONSES

                inputStream = returnStaticResponse();


            } else if (testSession.useRelativeFile()) {

                // RETURN RESPONSES FROM FILES

                inputStream = returnRelativeFileResponse();

            } else if (testSession.useAbsoluteFile()) {

                // RETURN RESPONSES FROM FILES

                inputStream = returnAbsoluteFileResponse();
            }


            if (inputStream != null) {

                // APPLY REPLACEMENTS

                Map<String, String> template = testSession.getReplacements();
                InputSupplier<? extends InputStream> inputSupplier = replace(template, inputStream);
                ByteStreams.copy(inputSupplier.getInput(), httpServletResponse.getOutputStream());


                if (testSession.shouldSaveResponse()) {

                    // SAVE RESPONSES TO FILE

                    String filename = testSession.getFilename();

                    File file = new File(testSession.getPath(), filename);
                    if (!file.exists()) {
                        Files.createParentDirs(file);
                        boolean created = file.createNewFile();
                        LOG.info("file '{}' created '{}'", file, created);
                    }
                    ByteStreams.copy(inputSupplier, new FileOutputStream(file));
                }
            }

            testSession.increment();

        } catch (Exception e) {
            throw new MoxyException("error processing request", e);
        }
    }


    private InputStream returnRelativeFileResponse() throws IOException {
        InputStream inputStream;
        String filename = testSession.getFilename();

        // RELATIVE FILES
        inputStream = this.getClass().getResourceAsStream(filename);

        return inputStream;
    }


    private InputStream returnAbsoluteFileResponse() throws IOException {
        InputStream inputStream;
        // ABSOLUTE FILES
        String filename = testSession.getFilename();
        File file = new File(testSession.getPath(), filename);
        inputStream = Files.newInputStreamSupplier(file).getInput();

        return inputStream;
    }


    private InputStream returnStaticResponse() {
        InputStream inputStream;
        String response = testSession.getResponse();

        inputStream = new ByteArrayInputStream(response.getBytes(Charset.forName("UTF-8")));
        return inputStream;
    }

    private InputStream returnProxiedResponse(HttpServletRequest httpServletRequest) throws IOException {
        InputStream inputStream;
        String proxy = testSession.getProxy();

        // generate the correct url to proxy to
        URL url = createProxyUrl(httpServletRequest, proxy);

        // perform http GET / POST / PUT / DELETE
        String method = httpServletRequest.getMethod();

        if (method.equalsIgnoreCase("GET")) {
            // HTTP GET
            inputStream = Resources.newInputStreamSupplier(url).getInput();
        } else {
            // HTTP POST / DELETE / PUT
            byte[] requestBytes = ByteStreams.toByteArray(httpServletRequest.getInputStream());
            byte[] responseBytes = write(url, requestBytes, method);
            inputStream = ByteStreams.newInputStreamSupplier(responseBytes).getInput();
        }
        return inputStream;
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


    private byte[] write(URL url, byte[] body, String method) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod(method);
        InputSupplier<ByteArrayInputStream> inputSupplier = ByteStreams.newInputStreamSupplier(body);
        ByteStreams.copy(inputSupplier, httpURLConnection.getOutputStream());
        return ByteStreams.toByteArray(httpURLConnection.getInputStream());
    }


    private InputSupplier<? extends InputStream> replace(Map<String, String> replacementMap, InputStream inputStream) throws IOException {
        LOG.info("replace: '{}'", replacementMap);
        String str = CharStreams.toString(new InputStreamReader(inputStream, Charsets.UTF_8));
        for (String from : replacementMap.keySet()) {
            String to = replacementMap.get(from);
            LOG.info("replace '" + from + "' with '" + to + "'");
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
