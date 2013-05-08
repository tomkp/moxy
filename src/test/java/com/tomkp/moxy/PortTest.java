package com.tomkp.moxy;

import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.junit.MoxyRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;


@RunWith(MoxyRunner.class)
public class PortTest {


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
