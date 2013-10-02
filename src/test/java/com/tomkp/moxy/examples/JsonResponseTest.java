package com.tomkp.moxy.examples;

import com.google.common.io.Resources;
import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.junit.MoxyRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

@RunWith(MoxyRunner.class)
public class JsonResponseTest {


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

}
