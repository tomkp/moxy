package com.tomkp.moxy;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.junit.MoxyRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MoxyRunner.class)
public class HttpPostTest {


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


    @Test
    @Moxy(proxy = "http://api.flickr.com", file = "flickr_soap.txt")
    public void flickrPostSoap() throws Exception {
        URL resource = this.getClass().getResource(".");
        File file = new File(resource.getPath(), "flickr_soap.txt");
        file.delete();

        String response = post(new URL("http://localhost:9001/services/soap/"), "<s:Envelope\n" +
                "\txmlns:s=\"http://www.w3.org/2003/05/soap-envelope\"\n" +
                "\txmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\"\n" +
                "\txmlns:xsd=\"http://www.w3.org/1999/XMLSchema\"\n" +
                ">\n" +
                "\t<s:Body>\n" +
                "\t\t<x:FlickrRequest xmlns:x=\"urn:flickr\">\n" +
                "\t\t\t<method>flickr.test.echo</method>\n" +
                "\t\t\t<name>value</name>\n" +
                "\t\t</x:FlickrRequest>\n" +
                "\t</s:Body>\n" +
                "</s:Envelope>");

        assertTrue(file.exists());
        assertEquals(response, Files.toString(file, Charset.forName("UTF-8")));
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
