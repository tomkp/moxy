package com.tomkp.moxy;

import com.google.common.io.Resources;
import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.junit.MoxyRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MoxyRunner.class)
public class Examples {

    @Test
    @Moxy(response = "hello world")
    public void singleResponse() throws Exception {
        assertEquals("hello world", Resources.toString(new URL("http://localhost:9001"), Charset.forName("UTF-8")));
    }


    @Test
    @Moxy(response = {"hello", "goodbye"})
    public void multipleResponses() throws Exception {
        URL url = new URL("http://localhost:9001");
        assertEquals("hello", Resources.toString(url, Charset.forName("UTF-8")));
        assertEquals("goodbye", Resources.toString(url, Charset.forName("UTF-8")));
    }


    @Test
    @Moxy(response = "{\"id\": 1, \"mood\": \"Adventurous\"}", contentType = "application/json")
    public void staticResponse() throws Exception {
        assertEquals("{\"id\": 1, \"mood\": \"Adventurous\"}", Resources.toString(new URL("http://localhost:9001"), Charset.forName("UTF-8")));
    }


    @Test
    @Moxy(file = "example.json", contentType = "application/json")
    public void fileResponse() throws Exception {
        assertEquals("{\"id\": 1, \"mood\": \"Adventurous\"}", Resources.toString(new URL("http://localhost:9001"), Charset.forName("UTF-8")));
    }


    @Test
    @Moxy(file = {"/scripts/example1.xml", "/scripts/example2.xml"})
    public void fileResponses() throws Exception {
        URL url = new URL("http://localhost:9001");
        assertEquals("<example>ONE</example>", Resources.toString(url, Charset.forName("UTF-8")));
        assertEquals("<example>TWO</example>", Resources.toString(url, Charset.forName("UTF-8")));
    }


    @Test
    @Moxy(proxy = "http://www.google.com/robots.txt")
    public void proxyToGoogle() throws Exception {
        String response = Resources.toString(new URL("http://localhost:9001"), Charset.forName("UTF-8"));
        assertTrue(response.startsWith("User-agent: *"));
    }

}
