package com.tomkp.moxy;

import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.junit.MoxyRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.assertEquals;

@RunWith(MoxyRunner.class)
public class ContentTypeTest {


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


    @Test
    @Moxy(contentType = {"application/json", "text/xml"})
    public void multipleContentTypes() throws Exception {
        HttpURLConnection connection;
        URL url = new URL("http://localhost:9001");

        connection = (HttpURLConnection) (url.openConnection());
        assertEquals("application/json", connection.getContentType());

        connection = (HttpURLConnection) (url.openConnection());
        assertEquals("text/xml", connection.getContentType());
    }

}
