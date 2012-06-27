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

import static org.junit.Assert.*;

@RunWith(MoxyRunner.class)
public class ProxyTests {



    @Test
    @Moxy(proxy = "http://www.google.com")
    public void proxyPathToGoogle() throws Exception {
        String response = Resources.toString(new URL("http://localhost:9001/robots.txt"), Charset.forName("UTF-8"));
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
    @Moxy(proxy = "http://www.google.com/", file = {"google_robots.txt", "google_humans.txt"})
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


    @Test
    @Moxy(proxy = "http://www.apple.com")
    public void proxyWithQueryString() throws Exception {
        URL url = new URL("http://localhost:9001/search/?q=ipod");
        String response = Resources.toString(url, Charset.forName("UTF-8"));
        assertTrue(response.contains("Search Results"));
    }


}
