package com.tomkp.moxy.readers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class AbsoluteFileReader {

    private static final Logger LOG = LoggerFactory.getLogger(AbsoluteFileReader.class);
    
    public InputStream readAbsoluteFile(String filename) {
        LOG.info("read '{}'", filename);
        return this.getClass().getResourceAsStream(filename);
    }
}