package com.tomkp.moxy;

import com.google.common.io.Resources;
import com.sun.org.apache.regexp.internal.RE;
import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.junit.MoxyRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

@RunWith(MoxyRunner.class)
public class InvalidAnnotationTests {


    @Test(expected = IOException.class)
    @Moxy(response = "hello", file = "whatever")
    public void cannotConfigureBothFilesAndResponse() throws Exception {
        new URL("http://localhost:9001/").openStream();
    }


}
