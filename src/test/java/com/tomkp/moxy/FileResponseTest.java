package com.tomkp.moxy;

import com.google.common.io.Resources;
import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.junit.MoxyRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

@RunWith(MoxyRunner.class)
public class FileResponseTest {


    @Test
    @Moxy(file = {"hello.xml"})
    public void relativeFileResponse() throws Exception {
        assertEquals("<hello>Moxy</hello>", readUrl());
    }


    @Test
    @Moxy(file = {"hello.xml", "goodbye.xml"})
    public void relativeFileResponses() throws Exception {
        assertEquals("<hello>Moxy</hello>", readUrl());
        assertEquals("<goodbye>Moxy</goodbye>", readUrl());
    }


    @Test
    @Moxy(file = {"/scripts/absolute.xml"})
    public void absoluteFileResponse() throws Exception {
        assertEquals("<absolute>Moxy</absolute>", readUrl());
    }


    private String readUrl() throws IOException {
        return Resources.toString(new URL("http://localhost:9001"), Charset.forName("UTF-8"));
    }

}
