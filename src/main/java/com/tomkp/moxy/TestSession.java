package com.tomkp.moxy;

import com.tomkp.moxy.annotations.Moxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
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

    private List<Moxy> moxies = new ArrayList<Moxy>();

    private int index = 0;
    private Map<String,String> replacementsMap;


    public TestSession(String path) {
        this.path = path;
    }

    public void increment() {
        index++;
    }


    public String getPath() {
        return path;
    }


    public void add(Moxy moxy) {
        moxies.add(moxy);
    }


    public boolean isEmpty() {
        return moxies.isEmpty();
    }



    public boolean useStaticResponse() {
        return getResponseCount() > index;
    }


    public int getPort() {
        int port = DEFAULT_PORT;
        for (Moxy moxy : moxies) {
            if (moxy.port() != 0) {
                port = moxy.port();
                break;
            }
        }
        return port;
    }


    public int getResponseCount() {
        String[] responses = getResponses();
        return responses.length;
    }


    public String getResponse() {
        String[] responses = getResponses();
        return responses[index];
    }


    public String getFilename() {
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


    public boolean useProxiedResponse() {
        String proxy = getProxy();
        return !proxy.isEmpty();
    }


    public String getProxy() {
        String proxy = "";
        for (Moxy moxy : moxies) {
            proxy = moxy.proxy();
            if (proxy.isEmpty()) {
                break;
            }
        }
        return proxy;
    }



    public Map<String, String> getReplacements() {
        return replacementsMap;
    }



    public boolean shouldSaveResponse() {
        return (useProxiedResponse() && (getFileCount() > 0 || getIndexed()));
    }


    public void validate() {
        int fileCount = getFileCount();
        int responseCount = getResponseCount();

        if (responseCount > 0 && fileCount > 0) {
            throw new MoxyException("You must annotate your test with either 'responses' or 'files', but not both");
        }

        buildReplacementsMap();

    }



    public boolean useRelativeFile() {
        return hasFiles() && getFilename().startsWith("/");
    }

    public boolean useAbsoluteFile() {
        return hasFiles();
    }

    //.....


    private Map<String, String> buildReplacementsMap() {
        replacementsMap = new HashMap<String, String>();
        for (Moxy moxy : moxies) {
            String[] replacementList = moxy.replace();
            if (replacementList.length > 0) {
                if (replacementList.length % 2 != 0) {
                    throw new MoxyException("replace must consist of pairs of values, something to replace 'from' and 'to'");
                }
                for (int i = 0; i < replacementList.length; i += 2) {
                    String replaceThis = replacementList[i];
                    String withThat = replacementList[i + 1];
                    replacementsMap.put(replaceThis, withThat);
                }
                break;
            }
        }
        return replacementsMap;
    }



    private int getFileCount() {
        String[] files = getFiles();
        return files.length;
    }


    private boolean getIndexed() {
        boolean indexed = false;
        for (Moxy moxy : moxies) {
            indexed = moxy.indexed();
            if (indexed) {
                break;
            }
        }
        return indexed;
    }


    private int[] getStatusCodes() {
        int[] statusCodes = {};
        for (Moxy moxy : moxies) {
            statusCodes = moxy.statusCode();
            if (statusCodes.length > 0) {
                break;
            }
        }
        return statusCodes;
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


    private String[] getContentTypes() {
        String[] contentTypes = {};
        for (Moxy moxy : moxies) {
            contentTypes = moxy.contentType();
            if (contentTypes.length > 0) {
                break;
            }
        }
        return contentTypes;
    }


    private String[] getCookiesArray() {
        String[] cookies = {};
        for (Moxy moxy : moxies) {
            cookies = moxy.cookie();
            if (cookies.length > 0) {
                break;
            }
        }
        return cookies;
    }


    private String[] getFiles() {
        String[] files = {};
        for (Moxy moxy : moxies) {
            files = moxy.file();
            if (files.length > 0) {
                break;
            }
        }
        return files;
    }


    private String[] getResponses() {
        String[] responses = {};
        for (Moxy moxy : moxies) {
            responses = moxy.response();
            if (responses.length > 0) {
                break;
            }
        }
        return responses;
    }


    private boolean hasFiles() {
        return getFileCount() > index || getIndexed();
    }

}
