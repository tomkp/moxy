package com.tomkp.moxy;

import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.junit.MoxyRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;

import static org.junit.Assert.*;


@RunWith(MoxyRunner.class)
public class PortTests {


    @Test
    @Moxy
    public void serverIsStartedOnDefaultPort() throws Exception {
        new URL("http://localhost:9001").openStream();
    }

    @Test
    @Moxy(port = 9002)
    public void serverIsStartedOnSpecifiedPort() throws Exception {
        new URL("http://localhost:9002").openStream();
    }
}
