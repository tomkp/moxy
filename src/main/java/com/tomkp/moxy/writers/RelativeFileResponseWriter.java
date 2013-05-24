package com.tomkp.moxy.writers;

import com.tomkp.moxy.HttpResponseWriter;

public class RelativeFileResponseWriter {

    private final HttpResponseWriter httpResponseWriter;


    public RelativeFileResponseWriter(HttpResponseWriter httpResponseWriter) {
        this.httpResponseWriter = httpResponseWriter;
    }


}
