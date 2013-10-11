package com.tomkp.moxy.filenames;

import com.tomkp.moxy.profile.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class PathAndQueryStringFilename implements FilenameGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(PathAndQueryStringFilename.class);

    private Map<String, String> suffixes = new HashMap<>();

    public PathAndQueryStringFilename() {
        suffixes.put("application/json", "json");
        suffixes.put("application/xml", "xml");
        suffixes.put("application/javascript", "json");
        suffixes.put("application/atom+xml", "xml");
        suffixes.put("application/pdf", "pdf");
        suffixes.put("application/rdf+xml:", "xml");
        suffixes.put("application/rss+xml", "xml");
        suffixes.put("application/soap+xml", "xml");
        suffixes.put("application/xhtml+xml", "xml");
        suffixes.put("application/zip", "zip");
        suffixes.put("application/gzip", "zip");

        suffixes.put("text/css", "css");
        suffixes.put("text/csv", "csv");
        suffixes.put("text/html", "html");
        suffixes.put("text/plain", "txt");
        suffixes.put("text/xml", "xml");
    }


    @Override
    public String generate(HttpServletRequest request, Profile profile) {
        String pathInfo = request.getPathInfo();
        String queryString = request.getQueryString();
        String filename = "";
        if (pathInfo != null) {
            filename += pathInfo.substring(1, pathInfo.length());
        }

        if (filename.isEmpty()) {
            filename = "index";
        }

        if (queryString != null) {
            filename += "?" + queryString;
        }

        String contentType = profile.getContentType();
        if (contentType != null) {
            String suffix = suffixes.get(contentType);
            if (suffix != null) {
                filename += "." + suffix;
            }
        }

        LOG.info("filename: '{}'", filename);
        return filename;
    }



}