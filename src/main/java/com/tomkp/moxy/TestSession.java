package com.tomkp.moxy;

import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.filenames.DefaultFilenameGenerator;
import com.tomkp.moxy.responses.AbsoluteFileResponse;
import com.tomkp.moxy.responses.ProxiedResponse;
import com.tomkp.moxy.responses.RelativeFileResponse;
import com.tomkp.moxy.responses.StaticResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestSession {

    private static final Logger LOG = LoggerFactory.getLogger(TestSession.class);

    private static final int DEFAULT_STATUS = 200;
    private static final String DEFAULT_CONTENT_TYPE = "text/plain";
    private static final int DEFAULT_PORT = 9001;

    private final String path;

    private Moxy moxy;

    private int index = 0;

    private Map<String, String> replacementsMap;


    public TestSession(String path, Moxy moxy) {
        this.path = path;
        this.moxy = moxy;
    }

    public void increment() {
        index++;
    }


    public String getPath() {
        return path;
    }


    public boolean useStaticResponse() {
        return getResponseCount() > index;
    }


    public int getResponseCount() {
        String[] responses = getResponses();
        return responses.length;
    }


    public String getResponse() {
        String[] responses = getResponses();
        return responses[index];
    }


    public String getFilename(HttpServletRequest request) {
//        try {
//            if (!moxy.filenameGenerator().equals(DefaultFilenameGenerator.class)) {
//                FilenameGenerator filenameGenerator = moxy.filenameGenerator().newInstance();
//                return filenameGenerator.generate(request);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        String[] files = getFiles();
        boolean indexed = getIndexed();
        String filename;
        if (indexed) {
            filename = files[0];
            filename = filename.replaceAll("\\$", String.valueOf(index + 1));
        } else {
            filename = files[index];
        }
        LOG.info("filename: '{}'", filename);
        return filename;
    }


    public List<Cookie> getCookies() {
        String[] cookies = getCookiesArray();
        List<Cookie> httpCookies = new ArrayList<Cookie>();
        if (cookies.length > 1) {
            String cookie = cookies[index];
            LOG.info("cookie: '{}'", cookie);
            httpCookies = createCookies(cookie);
        } else if (cookies.length == 1) {
            String cookie = cookies[0];
            LOG.info("cookie: '{}'", cookie);
            httpCookies = createCookies(cookie);
        }
        return httpCookies;
    }


    public String getContentType() {
        String[] contentTypes = getContentTypes();
        String contentType;
        if (contentTypes.length > 1) {
            contentType = contentTypes[index];
        } else if (contentTypes.length == 1) {
            contentType = contentTypes[0];
        } else {
            contentType = DEFAULT_CONTENT_TYPE;
        }
        LOG.info("contentType: '{}'", contentType);
        return contentType;
    }


    public int getStatusCode() {
        int[] statusCodes = getStatusCodes();
        int statusCode = DEFAULT_STATUS;
        if (statusCodes.length > 1) {
            statusCode = statusCodes[index];
        } else if (statusCodes.length == 1) {
            statusCode = statusCodes[0];
        }
        return statusCode;
    }


    private boolean useProxiedResponse() {
        String proxy = getProxy();
        return !proxy.isEmpty();
    }


    public Map<String, String> getReplacements() {
        return replacementsMap;
    }


    public boolean shouldSaveResponse() {
        return (useProxiedResponse() && (getFileCount() > 0 || getIndexed() || useFilenameGenerator()));
    }


    private boolean useFilenameGenerator() {
        return (moxy.filenameGenerator() != DefaultFilenameGenerator.class);
    }


    public void validate() {
        int fileCount = getFileCount();
        int responseCount = getResponseCount();

        if (responseCount > 0 && fileCount > 0) {
            throw new RuntimeException("You must annotate your test with either 'responses' or 'files', but not both");
        }

        buildReplacementsMap();

    }


    private boolean useRelativeFile(HttpServletRequest request) {
        return hasFiles() && getFilename(request).startsWith("/");
    }

    private boolean useAbsoluteFile() {
        return hasFiles();
    }

    //.......................................................................................................................................


    public int getPort() {
        int port = DEFAULT_PORT;
        if (moxy.port() != 0) {
            port = moxy.port();
        }
        return port;
    }


    public String getProxy() {
        return moxy.proxy();
    }

    private Map<String, String> buildReplacementsMap() {
        replacementsMap = new HashMap<String, String>();
        String[] replacementList = moxy.replace();
        if (replacementList.length > 0) {
            if (replacementList.length % 2 != 0) {
                throw new RuntimeException("replace must consist of pairs of values, something to replace 'from' and 'to'");
            }
            for (int i = 0; i < replacementList.length; i += 2) {
                String replaceThis = replacementList[i];
                String withThat = replacementList[i + 1];
                replacementsMap.put(replaceThis, withThat);
            }
        }
        return replacementsMap;
    }


    private boolean getIndexed() {
        return moxy.indexed();
    }


    private int[] getStatusCodes() {
        return moxy.statusCode();
    }

    private String[] getContentTypes() {
        return moxy.contentType();
    }


    private String[] getCookiesArray() {
        return moxy.cookie();
    }


    private String[] getFiles() {
       return moxy.file();
    }


    private String[] getResponses() {
        return moxy.response();
    }


    //.......................................................................................................................................


    private int getFileCount() {
        String[] files = getFiles();
        return files.length;
    }


    private List<Cookie> createCookies(String cookieString) {
        List<HttpCookie> httpCookies = HttpCookie.parse(cookieString);
        List<Cookie> cookies = new ArrayList<Cookie>();
        for (HttpCookie httpCookie : httpCookies) {
            Cookie cookie = new Cookie(httpCookie.getName(), httpCookie.getValue());
            cookie.setPath(httpCookie.getPath());
            cookie.setMaxAge((int) httpCookie.getMaxAge());
            cookie.setSecure(httpCookie.getSecure());
            LOG.info("cookie: '{}'", cookie);
            cookies.add(cookie);
        }
        return cookies;
    }


    private boolean hasFiles() {
        return getFileCount() > index || getIndexed() || useFilenameGenerator();
    }


    public InputStream generateResponse(HttpServletRequest httpServletRequest) throws IOException {

        InputStream inputStream = null;

        if (useProxiedResponse()) {

            // PROXY REQUESTS

            inputStream = new ProxiedResponse().getResponse(this, httpServletRequest);

        } else if (useStaticResponse()) {

            // RETURN STATIC RESPONSES

            inputStream = new StaticResponse().getResponse(this, httpServletRequest);

        } else if (useRelativeFile(httpServletRequest)) {

            // RETURN RESPONSES FROM FILES

            inputStream = new RelativeFileResponse().getResponse(this, httpServletRequest);

        } else if (useAbsoluteFile()) {

            // RETURN RESPONSES FROM FILES

            inputStream = new AbsoluteFileResponse().getResponse(this, httpServletRequest);
        }

        return inputStream;
    }
}
