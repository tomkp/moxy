package com.tomkp.moxy;

import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.junit.MoxyRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;


@RunWith(MoxyRunner.class)
@Moxy(port = 9002)
public class ClassAnnotationTests {


    @Test
    @Moxy
    public void portSpecifiedAtClassLevel() throws Exception {
        new URL("http://localhost:9002").openStream();
    }

    @Test
    @Moxy(port = 9003)
    public void portOverriddenAtMethodLevel() throws Exception {
        new URL("http://localhost:9003").openStream();
    }

}
