package com.tomkp.moxy;

import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.google.common.io.Resources;
import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.junit.MoxyRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

@RunWith(MoxyRunner.class)
public class StaticResponseTests {


    @Test
    @Moxy(responses = {"hello"})
    public void staticResponse() throws Exception {
        assertEquals("hello", Resources.toString(new URL("http://localhost:9001"), Charset.forName("UTF-8")));

    }


    @Test
    @Moxy(responses = {"hello", "goodbye"})
    public void multipleResponse() throws Exception {
        URL url = new URL("http://localhost:9001");
        assertEquals("hello", Resources.toString(url, Charset.forName("UTF-8")));
        assertEquals("goodbye", Resources.toString(url, Charset.forName("UTF-8")));
    }



}
