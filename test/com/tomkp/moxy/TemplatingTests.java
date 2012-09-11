package com.tomkp.moxy;

import com.google.common.io.Resources;
import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.junit.MoxyRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;


@RunWith(MoxyRunner.class)
public class TemplatingTests {



    @Test
    @Moxy(response = "hello Moxy world", template = {"Moxy", "XYZ"})
    public void substituteInline() throws Exception {
        URL url = new URL("http://localhost:9001");
        assertEquals("hello XYZ world", Resources.toString(url, Charset.forName("UTF-8")));
    }


    @Test
    @Moxy(file = {"hello.xml"}, template = {"Moxy", "XYZ"})
    public void fileResponse() throws Exception {
        URL url = new URL("http://localhost:9001");
        assertEquals("<hello>XYZ</hello>", Resources.toString(url, Charset.forName("UTF-8")));
    }


}
