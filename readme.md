# Moxy

  Moxy is for those times when you are __absolutely convinced__ that you need to run integration tests against a server.


## Examples

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