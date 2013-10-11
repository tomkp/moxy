package com.tomkp.moxy.profile;

import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.io.*;
import com.tomkp.moxy.FilenameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Profile {

    private static final Logger LOG = LoggerFactory.getLogger(Profile.class);

    private static final int DEFAULT_STATUS = 200;
    private static final String DEFAULT_CONTENT_TYPE = "text/plain";
    //private static final int DEFAULT_PORT = 9001;

    //private int port = DEFAULT_PORT;
    private String proxy = "";
    private List<String> contentTypes = new ArrayList<>();
    private List<Integer> statusCodes = new ArrayList<>();
    private List<String> cookies = new ArrayList<>();
    private List<String> responses = new ArrayList<>();
    private List<String> files = new ArrayList<>();
    private boolean indexed = false;
    private Map<String, String> replacements = new HashMap<>();
    private FilenameGenerator filenameGenerator;

    private String path;

    private int index = 0;

    public Profile(String path) {
        this.path = path;
    }

    public void increment() {
        index++;
        LOG.info("index '{}' incremented", index);
    }


    public Profile setContentTypes(String[] contentTypes) {
        this.contentTypes = Arrays.asList(contentTypes);
        return this;
    }


    public Profile setCookies(String[] cookies) {
        this.cookies = Arrays.asList(cookies);
        return this;
    }


    public Profile setFiles(String[] files) {
        this.files = Arrays.asList(files);
        return this;
    }


    public Profile setIndexed(boolean indexed) {
        this.indexed = indexed;
        return this;
    }


//    public Profile setPort(int port) {
//        this.port = port;
//        return this;
//    }


    public Profile setProxy(String proxy) {
        this.proxy = proxy;
        return this;
    }


    public Profile setReplacements(String[] replacementList) {
        if (replacementList.length > 0) {
            if (replacementList.length % 2 != 0) {
                throw new RuntimeException("replace must consist of pairs of values, something to replace 'from' and 'to'");
            }
            for (int i = 0; i < replacementList.length; i += 2) {
                String replaceThis = replacementList[i];
                String withThat = replacementList[i + 1];
                replacements.put(replaceThis, withThat);
            }
        }
        return this;
    }


    public Profile setResponses(String[] responses) {
        this.responses = Arrays.asList(responses);
        return this;
    }


    public Profile setStatusCodes(int[] statusCodes) {
        for (int code : statusCodes) {
            this.statusCodes.add(code);
        }
        return this;
    }


    public void setFilenameGenerator(FilenameGenerator filenameGenerator) {
        this.filenameGenerator = filenameGenerator;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                //.add("port", port)
                .add("proxy", proxy)
                .add("contentTypes", contentTypes)
                .add("statusCodes", statusCodes)
                .add("cookies", cookies)
                .add("responses", responses)
                .add("files", files)
                .add("indexed", indexed)
                .add("replacements", replacements)
                .add("filenameGenerator", filenameGenerator)
                .add("path", path)
                .add("index", index)
                .toString();
    }


    public void saveResponses(HttpServletRequest request, InputSupplier inputSupplier) throws Exception {

        if (!proxy.isEmpty() && (!files.isEmpty() || indexed || filenameGenerator != null)) {

            String filename = getFile(request);

            if (filename != null) {
                // todo - save to correct location!
                File file = new File(path, filename);

                if (!file.exists()) {
                    Files.createParentDirs(file);
                    boolean created = file.createNewFile();
                    LOG.info("files '{}' created '{}'", file, created);
                }
                ByteStreams.copy(inputSupplier, new FileOutputStream(file));
            }
        }
    }


    public InputStream generateResponse(HttpServletRequest request) throws IOException {
        InputStream inputStream = null;

        if (!proxy.isEmpty()) {
            // PROXY REQUESTS

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
        } else if (!responses.isEmpty()) {

            inputStream = new ByteArrayInputStream(responses.get(index).getBytes(Charsets.UTF_8));

        } else {

            if (!files.isEmpty()) {

                String filename = getFile(request);

                if (filename != null) {

                    LOG.info("path: '{}', filename: '{}'", path, filename);

                    if (filename.startsWith("/")) {
                        //RELATIVE
                        inputStream = this.getClass().getResourceAsStream(filename);
                    } else {
                        //ABSOLUTE
                        inputStream = Files.newInputStreamSupplier(new File(path, filename)).getInput();
                    }

                }
            }
        }
        return inputStream;
    }


    public InputSupplier<? extends InputStream> applyReplacements(InputStream inputStream) throws IOException {
        return replace(inputStream);
    }


    public void configureHttpHeaders(HttpServletResponse httpServletResponse) {
        int statusCode = getStatusCode();
        httpServletResponse.setStatus(statusCode);

        String contentType = getContentTypes();
        httpServletResponse.setContentType(contentType);

        List<Cookie> httpCookies = getCookies();
        for (Cookie httpCookie : httpCookies) {
            httpServletResponse.addCookie(httpCookie);
        }
    }


    private InputSupplier<? extends InputStream> replace(InputStream inputStream) throws IOException {
        LOG.info("replace: '{}'", replacements);
        String str = CharStreams.toString(new InputStreamReader(inputStream, Charsets.UTF_8));
        for (String from : replacements.keySet()) {
            String to = replacements.get(from);
            LOG.info("replace '" + from + "' with '" + to + "'");
            str = str.replaceAll(from, to);
        }
        return ByteStreams.newInputStreamSupplier(str.getBytes("UTF-8"));
    }


    //..................................................................................................................


    public String getFile(HttpServletRequest request) {

        if (filenameGenerator != null) {

            return filenameGenerator.generate(request);

        } else {
            String filename = null;
            if (files.size() == 1) {
                filename = files.get(0);
            } else if (!files.isEmpty()) {
                filename = files.get(index);
            }

            if (indexed) {
                filename = filename.replaceAll("\\$", String.valueOf(index + 1));
            }
            return filename;
        }
    }


    private List<Cookie> createCookies(String cookieString) {
        List<HttpCookie> httpCookies = HttpCookie.parse(cookieString);
        List<Cookie> cookies = new ArrayList<Cookie>();
        for (HttpCookie httpCookie : httpCookies) {
            Cookie cookie = new Cookie(httpCookie.getName(), httpCookie.getValue());
            cookie.setPath(httpCookie.getPath());
            cookie.setMaxAge((int) httpCookie.getMaxAge());
            cookie.setSecure(httpCookie.getSecure());
            LOG.info("cookies: '{}'", cookie);
            cookies.add(cookie);
        }
        return cookies;
    }


    private List<Cookie> getCookies() {
        List<Cookie> httpCookies = new ArrayList<Cookie>();
        if (cookies.size() > 1) {
            String cookie = cookies.get(index);
            LOG.info("cookies: '{}'", cookie);
            httpCookies = createCookies(cookie);
        } else if (cookies.size() == 1) {
            String cookie = cookies.get(0);
            LOG.info("cookies: '{}'", cookie);
            httpCookies = createCookies(cookie);
        }
        return httpCookies;
    }


    private String getContentTypes() {
        String contentType;
        if (contentTypes.size() > 1) {
            contentType = contentTypes.get(index);
        } else if (contentTypes.size() == 1) {
            contentType = contentTypes.get(0);
        } else {
            contentType = DEFAULT_CONTENT_TYPE;
        }
        LOG.info("contentType: '{}'", contentType);
        return contentType;
    }


    private int getStatusCode() {
        int statusCode = DEFAULT_STATUS;
        if (statusCodes.size() > 1) {
            statusCode = statusCodes.get(index);
        } else if (statusCodes.size() == 1) {
            statusCode = statusCodes.get(0);
        }
        return statusCode;
    }


    //..................................................................................................................


    //proxy

    private byte[] write(URL url, byte[] body, String method) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod(method);
        InputSupplier<ByteArrayInputStream> inputSupplier = ByteStreams.newInputStreamSupplier(body);
        ByteStreams.copy(inputSupplier, httpURLConnection.getOutputStream());
        return ByteStreams.toByteArray(httpURLConnection.getInputStream());
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


    //..................................................................................................................


}
