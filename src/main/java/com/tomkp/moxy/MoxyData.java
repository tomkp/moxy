package com.tomkp.moxy;

import com.tomkp.moxy.annotations.Moxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;

public class MoxyData {

    private static final Logger LOG = LoggerFactory.getLogger(MoxyData.class);

    private static final int DEFAULT_STATUS = 200;
    private static final String DEFAULT_CONTENT_TYPE = "text/plain";
    private static final int DEFAULT_PORT = 9001;


    private List<Moxy> moxies;


    public MoxyData(List<Moxy> moxies) {
        this.moxies = moxies;
    }


    public boolean isEmpty() {
        return moxies.isEmpty();
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
        if (responses != null) {
            return responses.length;
        }
        return 0;
    }


    public String getResponse(int index) {
        String[] responses = getResponses();
        return responses[index];
    }


    public String getFilename(int index) {
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


    public int getFileCount() {
        String[] files = getFiles();
        if (files != null) {
            return files.length;
        }
        return 0;
    }


    public List<Cookie> getCookies(int index) {
        String[] cookies = getCookies();
        List<Cookie> httpCookies = new ArrayList<Cookie>();
        if (cookies != null && cookies.length > 1) {
            String cookie = cookies[index];
            LOG.info("cookie: '{}'", cookie);
            httpCookies = createCookies(cookie);
        } else if (cookies != null && cookies.length == 1) {
            String cookie = cookies[0];
            LOG.info("cookie: '{}'", cookie);
            httpCookies = createCookies(cookie);
        }
        return httpCookies;
    }


    public String getContentType(int index) {
        String[] contentTypes = getContentTypes();
        String contentType;
        if (contentTypes != null && contentTypes.length > 1) {
            contentType = contentTypes[index];
        } else if (contentTypes != null && contentTypes.length == 1) {
            contentType = contentTypes[0];
        } else {
            contentType = DEFAULT_CONTENT_TYPE;
        }
        LOG.info("contentType: '{}'", contentType);
        return contentType;
    }


    public int getStatusCode(int index) {
        int[] statusCodes = getStatusCodes();
        int statusCode = DEFAULT_STATUS;
        if (statusCodes != null && statusCodes.length > 1) {
            statusCode = statusCodes[index];
        } else if (statusCodes != null && statusCodes.length == 1) {
            statusCode = statusCodes[0];
        }
        return statusCode;
    }


    public String getProxy() {
        String proxy = null;
        for (Moxy moxy : moxies) {
            proxy = moxy.proxy();
            if (proxy != null) {
                break;
            }
        }
        return proxy;
    }


    public boolean getIndexed() {
        boolean indexed = false;
        for (Moxy moxy : moxies) {
            indexed = moxy.indexed();
            if (indexed) {
                break;
            }
        }
        return indexed;
    }


    //.....


    private int[] getStatusCodes() {
        int[] statusCodes = null;
        for (Moxy moxy : moxies) {
            statusCodes = moxy.statusCode();
            if (statusCodes != null && statusCodes.length > 0) {
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
        String[] contentTypes = null;
        for (Moxy moxy : moxies) {
            contentTypes = moxy.contentType();
            if (contentTypes != null && contentTypes.length > 0) {
                break;
            }
        }
        return contentTypes;
    }


    private String[] getCookies() {
        String[] cookies = null;
        for (Moxy moxy : moxies) {
            cookies = moxy.cookie();
            if (cookies != null && cookies.length > 0) {
                break;
            }
        }
        return cookies;
    }


    private String[] getFiles() {
        String[] files = null;
        for (Moxy moxy : moxies) {
            files = moxy.file();
            if (files != null && files.length > 0) {
                break;
            }
        }
        return files;
    }


    private String[] getResponses() {
        String[] responses = null;
        for (Moxy moxy : moxies) {
            responses = moxy.response();
            if (responses != null && responses.length > 0) {
                break;
            }
        }
        return responses;
    }


}
