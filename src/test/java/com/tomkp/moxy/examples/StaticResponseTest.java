package com.tomkp.moxy.examples;

import com.google.common.io.Resources;
import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.junit.JunitMoxyRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

@RunWith(JunitMoxyRunner.class)
public class StaticResponseTest {


    @Test
    @Moxy(response = {"hello"})
    public void staticResponse() throws Exception {
        assertEquals("hello", Resources.toString(new URL("http://localhost:9001"), Charset.forName("UTF-8")));

    }


    @Test
    @Moxy(response = {"hello", "goodbye"})
    public void multipleResponse() throws Exception {
        URL url = new URL("http://localhost:9001");
        assertEquals("hello", Resources.toString(url, Charset.forName("UTF-8")));
        assertEquals("goodbye", Resources.toString(url, Charset.forName("UTF-8")));
    }



}
