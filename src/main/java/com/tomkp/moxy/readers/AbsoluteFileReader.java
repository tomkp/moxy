package com.tomkp.moxy.readers;

import java.io.InputStream;

public class AbsoluteFileReader {

    public InputStream readAbsoluteFile(Class<?> testClass, String filename) {
        return testClass.getClass().getResourceAsStream(filename);
    }
}