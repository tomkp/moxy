package com.tomkp.moxy;

import com.google.common.base.Charsets;
import com.google.common.io.BaseEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

public class Base64EncodedPathAndQueryStringFilename implements FilenameGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(Base64EncodedPathAndQueryStringFilename.class);

    @Override
    public String generate(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        String queryString = request.getQueryString();
        LOG.info("pathInfo = " + pathInfo);
        LOG.info("queryString = " + queryString);
        String filename = "";
        if (pathInfo != null) {
            filename += pathInfo;
        }
        if (queryString != null) {
            filename += "?" + queryString;
        }
        String encode = BaseEncoding.base64Url().encode(filename.getBytes(Charsets.UTF_8));
        LOG.info("encode '" + filename + "' as '" + encode + "'");
        return encode;
    }
}