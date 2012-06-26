package com.tomkp.moxy;

import com.google.common.io.Resources;
import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.junit.MoxyRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

@RunWith(MoxyRunner.class)
public class CompoundAttributesTests {


    @Test
    @Moxy(port = 9002, response = {"hello", "goodbye"})
    public void specifyPortAndResponses() throws Exception {
        URL url = new URL("http://localhost:9002");
        assertEquals("hello", Resources.toString(url, Charset.forName("UTF-8")));
        assertEquals("goodbye", Resources.toString(url, Charset.forName("UTF-8")));
    }


    @Test
    @Moxy(port = 9002, statusCode = 201)
    public void specifyPortAndStatusCodes() throws Exception {
        URL url = new URL("http://localhost:9002");
        assertEquals(201, ((HttpURLConnection)(url.openConnection())).getResponseCode());
        assertEquals(201, ((HttpURLConnection)(url.openConnection())).getResponseCode());
    }

}
