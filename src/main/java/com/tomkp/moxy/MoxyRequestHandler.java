package com.tomkp.moxy;

import com.google.common.base.Charsets;
import com.google.common.io.*;
import com.tomkp.moxy.writers.HttpResponseWriter;
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

    //private final RequestProxy proxyRequest;
    //private final ResponseWriter responseWriter;
    private HttpResponseWriter responseWriter;

    private final MoxyData moxyData;
    private final String path;
    private int index = 0;


    public MoxyRequestHandler(//RequestProxy proxyRequest,
                              HttpResponseWriter responseWriter,
                              String path,
                              MoxyData moxyData) {
        //this.proxyRequest = proxyRequest;
        this.responseWriter = responseWriter;
        this.path = path;
        this.moxyData = moxyData;

        Requests.reset();
    }


    public void process(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        Requests.capture(httpServletRequest);

        try {

            configureHttpHeaders(httpServletResponse);

            if (moxyData.hasProxy()) {

//                InputStream inputStream = null;
//                writeProxyRequest(inputStream, httpServletResponse);

                proxyRequest(httpServletRequest, httpServletResponse);

            } else if (moxyData.hasResponses(index)) {

                //writeResponseUsingAnnotationValue(httpServletResponse);

                String response = moxyData.getResponse(index);
                //responseWriter.writeStringToResponse(httpServletResponse, response);

                InputStream inputStream = new ByteArrayInputStream(response.getBytes(Charset.forName("UTF-8")));

                Map<String, String> template = moxyData.getTemplate();

                InputSupplier<? extends InputStream> inputSupplier = replace(template, inputStream);

                ByteStreams.copy(inputSupplier.getInput(), httpServletResponse.getOutputStream());


                //responseWriter.writeResponse(httpServletResponse, inputStream);

            } else if (moxyData.hasFiles(index)) {

                //writeResponseUsingFileContents(httpServletResponse);


                // write response using file contents
                String filename = moxyData.getFilename(index);
                if (filename.startsWith("/")) {
                    //responseWriter.writeAbsoluteFileToResponse(httpServletResponse, filename);

                    InputStream inputStream = this.getClass().getResourceAsStream(filename);

                    //responseWriter.writeResponse(httpServletResponse, inputStream);

                    //InputSupplier<InputStream> inputSupplier = Resources.newInputStreamSupplier(Resources.getResource(filename));

                    Map<String, String> template = moxyData.getTemplate();

                    InputSupplier<? extends InputStream> inputSupplier = replace(template, inputStream);

                    ByteStreams.copy(inputSupplier.getInput(), httpServletResponse.getOutputStream());

                } else {
                    //responseWriter.writeRelativeFileToResponse(httpServletResponse, path, filename);

                    File file = new File(path, filename);
                    InputSupplier<? extends InputStream> inputSupplier = Files.newInputStreamSupplier(file);
                    //InputStream inputStream = inputSupplier.getInput();
                    //responseWriter.writeResponse(httpServletResponse, inputStream);

                    Map<String, String> template = moxyData.getTemplate();

                    inputSupplier = replace(template, inputSupplier.getInput());

                    ByteStreams.copy(inputSupplier.getInput(), httpServletResponse.getOutputStream());

                    //ByteStreams.copy(inputStream, httpServletResponse.getOutputStream());
                }
            }
            index++;

        } catch (Exception e) {
            throw new MoxyException("error processing request", e);
        }
    }





    private void configureHttpHeaders(HttpServletResponse httpServletResponse) {
        int statusCode = moxyData.getStatusCode(index);
        List<Cookie> httpCookies = moxyData.getCookies(index);
        String contentType = moxyData.getContentType(index);

        httpServletResponse.setStatus(statusCode);
        for (Cookie httpCookie : httpCookies) {
            httpServletResponse.addCookie(httpCookie);
        }
        httpServletResponse.setContentType(contentType);
    }



    private void proxyRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        String proxy = moxyData.getProxy();
        boolean indexed = moxyData.getIndexed();
        int fileCount = moxyData.getFileCount();


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
            //ByteStreams.copy(inputSupplier, httpServletResponse.getOutputStream());
            //responseWriter.writeResponse(httpServletResponse, Resources.newInputStreamSupplier(url).getInput());
        }


        Map<String, String> template = moxyData.getTemplate();

        inputSupplier = replace(template, inputSupplier.getInput());

        //InputStream inputStream = inputSupplier.getInput();

        //responseWriter.writeResponse(httpServletResponse, inputSupplier.getInput());

        ByteStreams.copy(inputSupplier, httpServletResponse.getOutputStream());



        // record the response to a file
        if (fileCount > 0 || indexed)  {
            String filename = moxyData.getFilename(index);
            //responseWriter.writeResponseToFile(path, filename, inputStream);

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
        LOG.info("template: '{}'", template);
        String str = CharStreams.toString(new InputStreamReader(inputStream, Charsets.UTF_8));
        for (String from : template.keySet()) {
            String to = template.get(from);
            System.out.println("replace '" + from + "' with '" + to + "'");
            str = str.replaceAll(from, to);
        }
        return ByteStreams.newInputStreamSupplier(str.getBytes("UTF-8"));
        //return inputSupplier;
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
