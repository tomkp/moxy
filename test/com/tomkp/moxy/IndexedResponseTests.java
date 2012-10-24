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
public class IndexedResponseTests {



    @Test
    @Moxy(file = {"/scripts/indexed$.txt"}, indexed = true)
    public void absoluteIndexedResponses() throws Exception {
        assertEquals("absolute indexed 1", readUrl());
        assertEquals("absolute indexed 2", readUrl());
        assertEquals("absolute indexed 3", readUrl());
    }



    @Test
    @Moxy(file = {"indexed$.txt"}, indexed = true)
    public void relativeIndexedResponses() throws Exception {
        assertEquals("relative indexed 1", readUrl());
        assertEquals("relative indexed 2", readUrl());
        assertEquals("relative indexed 3", readUrl());
    }


    private String readUrl() throws IOException {
        return Resources.toString(new URL("http://localhost:9001"), Charset.forName("UTF-8"));
    }



}
