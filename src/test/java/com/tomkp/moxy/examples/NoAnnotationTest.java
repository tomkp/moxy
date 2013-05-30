package com.tomkp.moxy.examples;

import com.tomkp.moxy.junit.JunitMoxyRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

@RunWith(JunitMoxyRunner.class)
public class NoAnnotationTest {


    @Test
    public void noMoxyTestMethodAnnotations() throws Exception {
        assertTrue(true);
    }

}
