package com.tomkp.moxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

public class PathAndQueryStringFilename implements FilenameGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(PathAndQueryStringFilename.class);

    @Override
    public String generate(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        String queryString = request.getQueryString();
        String filename = "";
        if (pathInfo != null) {
            filename += pathInfo;
        }
        if (queryString != null) {
            filename += "?" + queryString;
        }
        LOG.info("filename: '{}'", filename);
        return filename;
    }
}