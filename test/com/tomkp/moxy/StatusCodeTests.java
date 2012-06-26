package com.tomkp.moxy;

import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.junit.MoxyRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.assertEquals;

@RunWith(MoxyRunner.class)
public class StatusCodeTests {


    @Test
    @Moxy
    public void statusCodeDefaultIs200() throws Exception {
        HttpURLConnection connection = (HttpURLConnection)(new URL("http://localhost:9001").openConnection());
        assertEquals(200, connection.getResponseCode());
    }


    @Test
    @Moxy(statusCode = 201)
    public void statusCodeIsConfigurable() throws Exception {
        HttpURLConnection connection = (HttpURLConnection)(new URL("http://localhost:9001").openConnection());
        assertEquals(201, connection.getResponseCode());
    }


    @Test
    @Moxy(statusCode = {500, 500, 404, 200})
    public void statusCodes() throws Exception {
        URL url = new URL("http://localhost:9001");
        assertEquals(500, ((HttpURLConnection)(url.openConnection())).getResponseCode());
        assertEquals(500, ((HttpURLConnection)(url.openConnection())).getResponseCode());
        assertEquals(404, ((HttpURLConnection)(url.openConnection())).getResponseCode());
        assertEquals(200, ((HttpURLConnection)(url.openConnection())).getResponseCode());
    }
}
