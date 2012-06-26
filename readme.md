# Moxy

  Moxy is for those times when you are __absolutely convinced__ that you need to run integration tests against a server.


## Examples


Returning strings


```java

@RunWith(MoxyRunner.class)
public class Examples {


    @Test
    @Moxy(response = "hello")
    public void singleResponse() throws Exception {
        assertEquals("hello", Resources.toString(new URL("http://localhost:9001"), Charset.forName("UTF-8")));
    }


    @Test
    @Moxy(response = {"hello", "goodbye"})
    public void multipleResponses() throws Exception {
        URL url = new URL("http://localhost:9001");
        assertEquals("hello", Resources.toString(url, Charset.forName("UTF-8")));
        assertEquals("goodbye", Resources.toString(url, Charset.forName("UTF-8")));
    }

}

```




Returning json


```java


@RunWith(MoxyRunner.class)
public class JsonResponseTests {


    @Test
    @Moxy(response = "{\"id\": 1, \"created\": 1339714800000, \"mood\": \"Adventurous\"}", contentType = "application/json")
    public void staticResponse() throws Exception {
        assertEquals("{\"id\": 1, \"created\": 1339714800000, \"mood\": \"Adventurous\"}", Resources.toString(new URL("http://localhost:9001"), Charset.forName("UTF-8")));
    }


    @Test
    @Moxy(file = "example.json", contentType = "application/json")
    public void fileResponse() throws Exception {
        assertEquals("{\"id\": 1, \"created\": 1339714800000, \"mood\": \"Adventurous\"}", Resources.toString(new URL("http://localhost:9001"), Charset.forName("UTF-8")));
    }

}


```