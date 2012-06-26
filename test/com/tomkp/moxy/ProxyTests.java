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
    @Moxy(proxy = "http://www.google.com/robots.txt")
    public void proxyToGoogle() throws Exception {
        String response = Resources.toString(new URL("http://localhost:9001"), Charset.forName("UTF-8"));
        assertTrue(response.startsWith("User-agent: *"));
    }


    @Test
    @Moxy(proxy = "http://www.google.com/robots.txt", file = "google_robots.txt")
    public void captureResponseFromGoogle() throws Exception {
        URL resource = this.getClass().getResource(".");
        File file = new File(resource.getPath(), "google_robots.txt");
        file.delete();

        Resources.toString(new URL("http://localhost:9001"), Charset.forName("UTF-8"));
        assertTrue(file.exists());
        assertTrue(Files.readFirstLine(file, Charset.forName("UTF-8")).startsWith("User-agent: *"));

    }

}
