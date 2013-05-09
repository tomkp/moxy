package com.tomkp.moxy.readers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

public class StringReader {

    public InputStream readString(String response) {
        return new ByteArrayInputStream(response.getBytes(Charset.forName("UTF-8")));
    }
}