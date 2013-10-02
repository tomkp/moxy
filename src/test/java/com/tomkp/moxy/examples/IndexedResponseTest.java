package com.tomkp.moxy.examples;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.junit.MoxyRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MoxyRunner.class)
public class IndexedResponseTest {



    @Test
    @Moxy(file = "/scripts/absolute.$.xml", indexed = true)
    public void absoluteIndexedResponses() throws Exception {
        assertEquals("<p>absolute indexed 1</p>", readUrl());
        assertEquals("<p>absolute indexed 2</p>", readUrl());
        assertEquals("<p>absolute indexed 3</p>", readUrl());
    }



    @Test
    @Moxy(file = "relative.$.xml", indexed = true)
    public void relativeIndexedResponses() throws Exception {
        assertEquals("<p>relative indexed 1</p>", readUrl());
        assertEquals("<p>relative indexed 2</p>", readUrl());
        assertEquals("<p>relative indexed 3</p>", readUrl());
    }


    @Test
    @Moxy(proxy = "http://www.google.com/", file = "google.$.txt", indexed = true)
    public void captureResponsesFromGoogle() throws Exception {
        URL resource = this.getClass().getResource(".");
        File robotsFile = new File(resource.getPath(), "google.1.txt");
        File humansFile = new File(resource.getPath(), "google.2.txt");
        robotsFile.delete();
        humansFile.delete();

        String robotsResponse = Resources.toString(new URL("http://localhost:9001/robots.txt"), Charset.forName("UTF-8"));
        String humansResponse = Resources.toString(new URL("http://localhost:9001/humans.txt"), Charset.forName("UTF-8"));
        assertTrue(Files.readFirstLine(robotsFile, Charset.forName("UTF-8")).startsWith("User-agent: *"));
        assertTrue(Files.readFirstLine(humansFile, Charset.forName("UTF-8")).startsWith("Google is built"));
        assertEquals(robotsResponse, Files.toString(robotsFile, Charset.forName("UTF-8")));
        assertEquals(humansResponse, Files.toString(humansFile, Charset.forName("UTF-8")));
    }


    private String readUrl() throws IOException {
        return Resources.toString(new URL("http://localhost:9001"), Charset.forName("UTF-8"));
    }



}
