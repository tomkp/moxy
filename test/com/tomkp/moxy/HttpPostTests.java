package com.tomkp.moxy;

import com.google.common.io.ByteStreams;
import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.junit.MoxyRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

@RunWith(MoxyRunner.class)
public class HttpPostTests {


    @Test
    @Moxy(response = "hello")
    public void postData() throws Exception {
        URL url = new URL("http://localhost:9001");
        post(url, "hello");
        put(url, "hello");
        delete(url);
    }



    private String post(URL url, String body) throws Exception {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        OutputStreamWriter out = new OutputStreamWriter(httpURLConnection.getOutputStream());
        out.write(body);
        out.close();
        return new String(ByteStreams.toByteArray(httpURLConnection.getInputStream()));
    }


    private String put(URL url, String body) throws Exception {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("PUT");
        OutputStreamWriter out = new OutputStreamWriter(httpURLConnection.getOutputStream());
        out.write(body);
        out.close();
        return new String(ByteStreams.toByteArray(httpURLConnection.getInputStream()));
    }


    private String delete(URL url) throws Exception {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpURLConnection.setRequestMethod("DELETE");
        httpURLConnection.connect();
        return new String(ByteStreams.toByteArray(httpURLConnection.getInputStream()));
    }



}
