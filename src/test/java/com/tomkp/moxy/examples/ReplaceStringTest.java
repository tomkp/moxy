package com.tomkp.moxy.examples;

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
public class ReplaceStringTest {


    @Test
    @Moxy(response = {"hello world!"}, replace = {"world", "ABC"})
    public void simpleStaticResponse() throws Exception {
        assertEquals("hello ABC!", Resources.toString(new URL("http://localhost:9001"), Charset.forName("UTF-8")));
    }


    @Test
    @Moxy(response = {"hello world!", "hello hello", "world world chicken"},
            replace = {
                    "world", "ABC",
                    "chicken", "DEF",
                    "hello", "XYZ"
            })
    public void multipleStaticResponses() throws Exception {
        assertEquals("XYZ ABC!", Resources.toString(new URL("http://localhost:9001"), Charset.forName("UTF-8")));
        assertEquals("XYZ XYZ", Resources.toString(new URL("http://localhost:9001"), Charset.forName("UTF-8")));
        assertEquals("ABC ABC DEF", Resources.toString(new URL("http://localhost:9001"), Charset.forName("UTF-8")));
    }


    @Test
    @Moxy(proxy = "http://www.google.com/", file = {"google_robots.txt", "google_humans.txt"},
            replace = {
                    "e", "XXX",
                    "oo", ""
            }
    )
    public void replaceWhileProxying() throws Exception {
        URL resource = this.getClass().getResource(".");
        File robotsFile = new File(resource.getPath(), "google_robots.txt");
        File humansFile = new File(resource.getPath(), "google_humans.txt");
        robotsFile.delete();
        humansFile.delete();

        String robotsResponse = Resources.toString(new URL("http://localhost:9001/robots.txt"), Charset.forName("UTF-8"));
        String humansResponse = Resources.toString(new URL("http://localhost:9001/humans.txt"), Charset.forName("UTF-8"));
        assertTrue(Files.readFirstLine(robotsFile, Charset.forName("UTF-8")).startsWith("UsXXXr-agXXXnt: *"));
        assertTrue(Files.readFirstLine(humansFile, Charset.forName("UTF-8")).startsWith("GglXXX is built"));
        assertEquals(robotsResponse, Files.toString(robotsFile, Charset.forName("UTF-8")));
        assertEquals(humansResponse, Files.toString(humansFile, Charset.forName("UTF-8")));
    }



//    @Test(expected = MoxyException.class)
//    @Moxy(replace = {"one", "two", "three"})
//    public void replacementsComeInPairs() throws Exception {
//
//    }
}
