package com.tomkp.moxy;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.junit.MoxyRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MoxyRunner.class)
public class Examples {

    @Test
    @Moxy(response = "hello world")
    public void singleResponse() throws Exception {
        URL url = new URL("http://localhost:9001");
        assertEquals("hello world", Resources.toString(url, Charset.forName("UTF-8")));
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
        URL url = new URL("http://localhost:9001");
        assertEquals("{\"id\": 1, \"mood\": \"Adventurous\"}", Resources.toString(url, Charset.forName("UTF-8")));
    }


    @Test
    @Moxy(file = "example.json", contentType = "application/json")
    public void fileResponse() throws Exception {
        URL url = new URL("http://localhost:9001");
        assertEquals("{\"id\": 1, \"mood\": \"Adventurous\"}", Resources.toString(url, Charset.forName("UTF-8")));
    }


    @Test
    @Moxy(file = {"/scripts/example1.xml", "/scripts/example2.xml"})
    public void fileResponses() throws Exception {
        URL url = new URL("http://localhost:9001");
        assertEquals("<example>ONE</example>", Resources.toString(url, Charset.forName("UTF-8")));
        assertEquals("<example>TWO</example>", Resources.toString(url, Charset.forName("UTF-8")));
    }


    @Test
    @Moxy(proxy = "http://www.google.com")
    public void proxyToGoogle() throws Exception {
        URL url = new URL("http://localhost:9001/robots.txt");
        String response = Resources.toString(url, Charset.forName("UTF-8"));
        assertTrue(response.startsWith("User-agent: *"));
    }



    @Test
    @Moxy(proxy = "http://www.google.com", file = "google_robots.txt")
    public void captureResponseFromGoogle() throws Exception {
        URL resource = this.getClass().getResource(".");
        File file = new File(resource.getPath(), "google_robots.txt");
        file.delete();

        Resources.toString(new URL("http://localhost:9001/robots.txt"), Charset.forName("UTF-8"));
        assertTrue(file.exists());
        assertTrue(Files.readFirstLine(file, Charset.forName("UTF-8")).startsWith("User-agent: *"));

    }


    @Test
    @Moxy(proxy = "http://www.google.com", file = {"google_robots.txt", "google_humans.txt"})
    public void captureResponsesFromGoogle() throws Exception {
        URL resource = this.getClass().getResource(".");
        File robotsFile = new File(resource.getPath(), "google_robots.txt");
        File siteMapFile = new File(resource.getPath(), "google_humans.txt");
        robotsFile.delete();
        siteMapFile.delete();

        Resources.toString(new URL("http://localhost:9001/robots.txt"), Charset.forName("UTF-8"));
        Resources.toString(new URL("http://localhost:9001/humans.txt"), Charset.forName("UTF-8"));
        assertTrue(Files.readFirstLine(robotsFile, Charset.forName("UTF-8")).startsWith("User-agent: *"));
        assertTrue(Files.readFirstLine(siteMapFile, Charset.forName("UTF-8")).startsWith("Google is built"));
    }


}
