package com.tomkp.moxy.examples;

import com.google.common.io.Resources;
import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.junit.MoxyRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;


@RunWith(MoxyRunner.class)
@Moxy(
        port = 9002,
        response = "class",
        contentType = "class",
        statusCode = 201
)
public class ClassAnnotationTest {


    @Test
    @Moxy
    public void portSpecifiedAtClassLevel() throws Exception {
        new URL("http://localhost:9002").openStream();
    }

    @Test
    @Moxy(port = 9003)
    public void portOverriddenAtMethodLevel() throws Exception {
        new URL("http://localhost:9003").openStream();
    }


    @Test
    @Moxy
    public void responseSpecifiedAtClassLevel() throws Exception {
        URL url = new URL("http://localhost:9002");
        assertEquals("class", Resources.toString(url, Charset.forName("UTF-8")));

    }

    @Test
    @Moxy(response = "method")
    public void responseOverriddenAtMethodLevel() throws Exception {
        URL url = new URL("http://localhost:9002");
        assertEquals("method", Resources.toString(url, Charset.forName("UTF-8")));
    }



    @Test
    @Moxy
    public void contentTypeSpecifiedAtClassLevel() throws Exception {
        HttpURLConnection connection = (HttpURLConnection) (new URL("http://localhost:9002").openConnection());
        assertEquals("class", connection.getContentType());
    }


    @Test
    @Moxy(contentType = "method")
    public void contentTypeOverriddenAtMethodLevel() throws Exception {
        HttpURLConnection connection = (HttpURLConnection) (new URL("http://localhost:9002").openConnection());
        assertEquals("method", connection.getContentType());
    }


    @Test
    @Moxy
    public void statusCodesSpecifiedAtClassLevel() throws Exception {
        HttpURLConnection connection = (HttpURLConnection) (new URL("http://localhost:9002").openConnection());
        assertEquals(201, connection.getResponseCode());
    }


    @Test
    @Moxy(statusCode = {403, 404})
    public void statusCodesOverriddenAtMethodLevel() throws Exception {
        URL url = new URL("http://localhost:9002");
        assertEquals(403,  ((HttpURLConnection) (url.openConnection())).getResponseCode());
        assertEquals(404,  ((HttpURLConnection) (url.openConnection())).getResponseCode());
    }

}
