package com.tomkp.moxy.examples;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.filenames.PathAndQueryStringFilename;
import com.tomkp.moxy.junit.MoxyRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(MoxyRunner.class)
public class PathAndQueryStringFilenameGeneratorTest {


    @Test
    @Moxy(filenameGenerator = PathAndQueryStringFilename.class)
    public void replayQuerys() throws Exception {
        assertEquals("pathAndQuery, hello?x=1&y=2", Resources.toString(new URL("http://localhost:9001/hello?x=1&y=2"), Charset.forName("UTF-8")));
    }


    @Test
    @Moxy(filenameGenerator = PathAndQueryStringFilename.class, file = "hello?x=1&y=2")
    public void replayQuery() throws Exception {
        assertEquals("pathAndQuery, hello?x=1&y=2", Resources.toString(new URL("http://localhost:9001/hello?x=1&y=2"), Charset.forName("UTF-8")));
    }


    @Test
    @Moxy(filenameGenerator = PathAndQueryStringFilename.class, file = "aaa/bbb?x=1&y=2")
    public void replayQueryWithNestedPath() throws Exception {
        assertEquals("pathAndQueryWithPath, aaa/bbb?x=1&y=2", Resources.toString(new URL("http://localhost:9001/aaa/bbb?x=1&y=2"), Charset.forName("UTF-8")));
    }


    @Test
    @Moxy(filenameGenerator = PathAndQueryStringFilename.class, file = "aaa/bbb?x=1&y=2", contentType = "application/json")
    public void replayQueryWithNestedPathAndContentType() throws Exception {
        assertEquals("pathAndQueryWithContentType, aaa/bbb?x=1&y=2.json", Resources.toString(new URL("http://localhost:9001/aaa/bbb?x=1&y=2"), Charset.forName("UTF-8")));
    }


    @Test
    @Moxy(proxy = "https://status.github.com", filenameGenerator = PathAndQueryStringFilename.class, contentType = "application/json")
    public void recordProxiedResponse() throws Exception {
        URL resource = this.getClass().getResource(".");
        File file = new File(resource.getPath(), "api.json?x=1.json");
        file.delete();

        String response = Resources.toString(new URL("http://localhost:9001/api.json?x=1"), Charset.forName("UTF-8"));
        assertTrue(file.exists());
        assertEquals(response, Files.toString(file, Charset.forName("UTF-8")));
    }


    @Test
    @Moxy(proxy = "https://status.github.com", filenameGenerator = PathAndQueryStringFilename.class, contentType = "application/json")
    public void recordProxiedResponseNested() throws Exception {
        URL resource = this.getClass().getResource(".");
        File file = new File(resource.getPath() + "api/", "status.json?x=1.json");
        file.delete();

        String response = Resources.toString(new URL("http://localhost:9001/api/status.json?x=1"), Charset.forName("UTF-8"));
        assertTrue(file.exists());
        assertEquals(response, Files.toString(file, Charset.forName("UTF-8")));
    }

}
