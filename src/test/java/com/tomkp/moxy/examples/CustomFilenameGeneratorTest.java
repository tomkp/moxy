package com.tomkp.moxy.examples;

import com.tomkp.moxy.PathAndQueryStringFilename;
import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.junit.MoxyRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;


@RunWith(MoxyRunner.class)
public class CustomFilenameGeneratorTest {


    @Test
    @Moxy(filenameGenerator = PathAndQueryStringFilename.class)
    public void pathAndQueryStringGenerator() throws Exception {
        new URL("http://localhost:9001/aaa/bbb?x=1&y=2").openStream();
    }


}
