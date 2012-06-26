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
public class FileResponseTests {


    @Test
    @Moxy(files = {"hello.xml"})
    public void fileResponse() throws Exception {
        assertEquals("<hello>Moxy</hello>", Resources.toString(new URL("http://localhost:9001"), Charset.forName("UTF-8")));
    }


    @Test
    @Moxy(files = {"hello.xml", "goodbye.xml"})
    public void fileResponses() throws Exception {
        assertEquals("<hello>Moxy</hello>", Resources.toString(new URL("http://localhost:9001"), Charset.forName("UTF-8")));
        assertEquals("<goodbye>Moxy</goodbye>", Resources.toString(new URL("http://localhost:9001"), Charset.forName("UTF-8")));
    }


}
