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
public class TemplateTest {


    @Test
    @Moxy(response = {"hello world!"}, template = {"world", "ABC"})
    public void templates() throws Exception {
        assertEquals("hello ABC!", Resources.toString(new URL("http://localhost:9001"), Charset.forName("UTF-8")));
    }


}
