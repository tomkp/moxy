package com.tomkp.moxy;

import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.junit.MoxyRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.URL;

@RunWith(MoxyRunner.class)
public class InvalidAnnotationTest {


    @Test(expected = IOException.class)
    @Moxy(response = "hello", file = "whatever")
    public void cannotConfigureBothFilesAndResponse() throws Exception {
        new URL("http://localhost:9001/").openStream();
    }


}
