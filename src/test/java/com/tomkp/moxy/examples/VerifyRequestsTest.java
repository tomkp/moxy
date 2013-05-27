package com.tomkp.moxy.examples;

import com.google.common.io.ByteStreams;
import com.tomkp.moxy.Requests;
import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.junit.MoxyRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.assertEquals;

@RunWith(MoxyRunner.class)
public class VerifyRequestsTest {



    @Test
    @Moxy
    public void verifyRequestParameters() throws Exception {
        new URL("http://localhost:9001/?a=b&a=c").openStream();
        Assert.assertArrayEquals(new String[]{"b", "c"}, Requests.getParameters().get(0).get("a"));
    }


    @Test
    @Moxy
    public void verifyRequestHeaders() throws Exception {
        URL url = new URL("http://localhost:9001");
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.connect();
        ByteStreams.toByteArray(httpURLConnection.getInputStream());
        assertEquals("application/x-www-form-urlencoded", Requests.getHeaders().get(0).get("Content-Type"));
    }


    @Test
    @Moxy
    public void verifyQueryStings() throws Exception {
        new URL("http://localhost:9001/?a=b&a=c").openStream();
        assertEquals("a=b&a=c", Requests.getQueryStrings().get(0));
    }


    @Test
    @Moxy
    public void verifyRequestUri() throws Exception {
        new URL("http://localhost:9001/context/path.html").openStream();
        assertEquals("/context/path.html", Requests.getRequestUris().get(0));
    }


    @Test
    @Moxy
    public void verifyPathInfo() throws Exception {
        new URL("http://localhost:9001/context/path.html").openStream();
        assertEquals("/context/path.html", Requests.getPathInfos().get(0));
    }
}
