# Moxy

  Moxy is for those times when you are __absolutely convinced__ that you need to run integration tests against a server.

## Usage

  Annotate your test classes with ``` @RunWith(MoxyRunner.class) ```

  Annotate your tests with ``` @Moxy ```


## Examples

  Mock server running on port 9001 returns ``` hello world ``` for all requests


```java

@RunWith(MoxyRunner.class)
public class Examples {

    @Test
    @Moxy(response = "hello world")
    public void singleResponse() throws Exception {
        assertEquals("hello world", Resources.toString(new URL("http://localhost:9001"), Charset.forName("UTF-8")));
    }

}

```

Multiple responses


```java

    @Test
    @Moxy(response = {"hello", "goodbye"})
    public void multipleResponses() throws Exception {
        URL url = new URL("http://localhost:9001");
        assertEquals("hello", Resources.toString(url, Charset.forName("UTF-8")));
        assertEquals("goodbye", Resources.toString(url, Charset.forName("UTF-8")));
    }

 ```


JSON response


```java

    @Test
    @Moxy(response = "{\"id\": 1, \"mood\": \"Adventurous\"}", contentType = "application/json")
    public void staticResponse() throws Exception {
        assertEquals("{\"id\": 1, \"mood\": \"Adventurous\"}", Resources.toString(new URL("http://localhost:9001"), Charset.forName("UTF-8")));
    }

```

JSON from a relative file


```java

    @Test
    @Moxy(file = "example.json", contentType = "application/json")
    public void fileResponse() throws Exception {
        assertEquals("{\"id\": 1, \"mood\": \"Adventurous\"}", Resources.toString(new URL("http://localhost:9001"), Charset.forName("UTF-8")));
    }

```


XML from absolute files


```java

    @Test
    @Moxy(file = {"/scripts/example1.xml", "/scripts/example2.xml"})
    public void fileResponses() throws Exception {
        URL url = new URL("http://localhost:9001");
        assertEquals("<example>ONE</example>", Resources.toString(url, Charset.forName("UTF-8")));
        assertEquals("<example>TWO</example>", Resources.toString(url, Charset.forName("UTF-8")));
    }

```


Use Moxy as a proxy


```java

    @Test
    @Moxy(proxy = "http://www.google.com/robots.txt")
    public void proxyToGoogle() throws Exception {
        String response = Resources.toString(new URL("http://localhost:9001"), Charset.forName("UTF-8"));
        assertTrue(response.startsWith("User-agent: *"));
    }

```
