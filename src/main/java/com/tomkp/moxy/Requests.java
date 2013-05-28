package com.tomkp.moxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public final class Requests {

    private static final Logger LOG = LoggerFactory.getLogger(Requests.class);


    private static List<Map<String, String[]>> parameters;
    private static List<Map<String, String>> headers;
    private static List<String> queryStrings;
    private static List<String> requestUris;
    private static List<String> pathInfos;


    private Requests() {}


    public static void reset() {
        parameters = new ArrayList<Map<String, String[]>>();
        headers = new ArrayList<Map<String, String>>();
        queryStrings = new ArrayList<String>();
        requestUris = new ArrayList<String>();
        pathInfos = new ArrayList<String>();
    }


    public static List<Map<String, String[]>> getParameters() {
        return parameters;
    }


    public static List<Map<String, String>> getHeaders() {
        return headers;
    }


    public static List<String> getQueryStrings() {
        return queryStrings;
    }


    public static List<String> getPathInfos() {
        return pathInfos;
    }


    public static List<String> getRequestUris() {
        return requestUris;
    }


    public static void capture(HttpServletRequest httpServletRequest) {
        recordParameters(httpServletRequest.getParameterMap());
        recordHeaders(httpServletRequest);
        recordQueryString(httpServletRequest.getQueryString());
        recordPathInfo(httpServletRequest.getPathInfo());
        recordRequestUri(httpServletRequest.getRequestURI());
    }


    //............



    private static void recordQueryString(String queryString) {
        queryStrings.add(queryString);
    }

    private static void recordRequestUri(String requestUri) {
        requestUris.add(requestUri);
    }

    private static void recordPathInfo(String pathInfo) {
        pathInfos.add(pathInfo);
    }

    private static void recordParameters(Map<String, String[]> params) {
        parameters.add(params);
    }

    private static void recordHeaders(HttpServletRequest httpServletRequest) {
        headers.add(extractHeaders(httpServletRequest));
    }


    private static Map<String, String> extractHeaders(HttpServletRequest httpServletRequest) {
        Map<String, String> headerMap = new LinkedHashMap<String, String>();
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerKey = headerNames.nextElement();
            String headerValue = httpServletRequest.getHeader(headerKey);
            LOG.info("header '{}:{}'", headerKey, headerValue);
            headerMap.put(headerKey, headerValue);
        }
        return headerMap;
    }

}
