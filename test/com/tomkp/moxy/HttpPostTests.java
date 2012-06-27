package com.tomkp.moxy;

import com.google.common.io.ByteStreams;
import com.google.common.io.InputSupplier;
import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.junit.MoxyRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.assertEquals;

@RunWith(MoxyRunner.class)
public class HttpPostTests {


    @Test
    @Moxy(response = "hello post")
    public void httpPost() throws Exception {
        URL url = new URL("http://localhost:9001");
        assertEquals("hello post", post(url, "hello"));
    }


    @Test
    @Moxy(response = "hello put")
    public void httpPut() throws Exception {
        URL url = new URL("http://localhost:9001");
        assertEquals("hello put", put(url, "hello"));
    }


    @Test
    @Moxy(response = "hello delete")
    public void httpDelete() throws Exception {
        URL url = new URL("http://localhost:9001");
        assertEquals("hello delete", delete(url));
    }


    private String post(URL url, String body) throws Exception {
        return write(url, body, "POST");
    }


    private String put(URL url, String body) throws Exception {
        return write(url, body, "PUT");
    }


    private String delete(URL url) throws Exception {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpURLConnection.setRequestMethod("DELETE");
        httpURLConnection.connect();
        return new String(ByteStreams.toByteArray(httpURLConnection.getInputStream()));
    }


    private String write(URL url, String body, String method) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod(method);
        InputSupplier<ByteArrayInputStream> inputSupplier = ByteStreams.newInputStreamSupplier(body.getBytes());
        ByteStreams.copy(inputSupplier, httpURLConnection.getOutputStream());
        return new String(ByteStreams.toByteArray(httpURLConnection.getInputStream()));
    }



}
