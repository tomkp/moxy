package com.tomkp.moxy;

import com.tomkp.moxy.annotations.Moxy;
import org.junit.Test;

import java.net.URL;


public class ExtendClassAnnotationTest extends AbstractClassAnnotationTest {


    @Test
    @Moxy
    public void portSpecifiedAtSuperClassLevel() throws Exception {
        new URL("http://localhost:9002").openStream();
    }

    @Test
    @Moxy(port = 9003)
    public void portOverriddenAtMethodLevel() throws Exception {
        new URL("http://localhost:9003").openStream();
    }

}
