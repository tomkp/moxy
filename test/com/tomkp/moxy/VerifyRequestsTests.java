package com.tomkp.moxy;

import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;
import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.junit.MoxyRunner;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.MultiMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(MoxyRunner.class)
public class VerifyRequestsTests {



    @Test
    @Moxy
    public void verifyRequestParameters() throws Exception {
        new URL("http://localhost:9001/?a=b&a=c").openStream();
        assertArrayEquals(new String[] {"b", "c"}, Requests.getParameters().get(0).get("a"));
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
