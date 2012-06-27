package com.tomkp.moxy;

import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.junit.MoxyRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.assertEquals;

@RunWith(MoxyRunner.class)
public class ContentTypeTests {


    @Test
    @Moxy
    public void contentTypeDefaultIsTextPlain() throws Exception {
        HttpURLConnection connection = (HttpURLConnection) (new URL("http://localhost:9001").openConnection());
        assertEquals("text/plain", connection.getContentType());
    }


    @Test
    @Moxy(contentType = "application/json")
    public void contentTypeIsConfigurable() throws Exception {
        HttpURLConnection connection = (HttpURLConnection) (new URL("http://localhost:9001").openConnection());
        assertEquals("application/json", connection.getContentType());
    }

}
