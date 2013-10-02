package com.tomkp.moxy.examples;

import com.tomkp.moxy.annotations.Moxy;
import com.tomkp.moxy.junit.MoxyRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.assertEquals;

@RunWith(MoxyRunner.class)
public class CookieTest {


    @Test
    @Moxy(cookie = "Set-Cookie: PubAuth1=134240759%2C134240757%2C134240754%2C%2B255084548049850%2C%2B114347059694522%2C%2B0%2C3472674174%2C1060798794%2CtEe9tPJ9pawRIWDHIn47sg; path=/; secure")
    public void setsCookie() throws Exception {
        HttpURLConnection connection = (HttpURLConnection) (new URL("http://localhost:9001").openConnection());
        String cookie = connection.getHeaderField("Set-Cookie");
        assertEquals("PubAuth1=\"134240759%2C134240757%2C134240754%2C%2B255084548049850%2C%2B114347059694522%2C%2B0%2C3472674174%2C1060798794%2CtEe9tPJ9pawRIWDHIn47sg\";Version=1;Path=/;Discard;Secure", cookie);
    }



    @Test
    @Moxy(cookie = {"Set-Cookie: PubAuth1=ABC; path=/; secure", "Set-Cookie: PubAuth1=DEF; path=/; secure"})
    public void setsCookies() throws Exception {
        URL url = new URL("http://localhost:9001");
        HttpURLConnection connection;
        String cookie;

        connection = (HttpURLConnection) (url.openConnection());
        cookie = connection.getHeaderField("Set-Cookie");
        assertEquals("PubAuth1=ABC;Path=/;Secure", cookie);

        connection = (HttpURLConnection) (url.openConnection());
        cookie = connection.getHeaderField("Set-Cookie");
        assertEquals("PubAuth1=DEF;Path=/;Secure", cookie);
    }


}
