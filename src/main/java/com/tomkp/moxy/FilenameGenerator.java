package com.tomkp.moxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilenameGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(FilenameGenerator.class);


    public String generateFilename(String[] files, boolean indexed, int i) {
        String filename;
        if (indexed) {
            filename = files[0];
            filename = filename.replaceAll("\\$", String.valueOf(i + 1));
        } else {
            filename = files[i];
        }
        LOG.info("filename: '{}'", filename);
        return filename;
    }
}