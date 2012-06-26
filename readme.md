# Moxy

  Moxy is for those times when you are __absolutely convinced__ that you need to run integration tests against a server.

## Usage

  Annotate your test classes with ```java @RunWith(MoxyRunner.class) ```

  Annotate your tests with ```java @Moxy ```


## Examples


Returns _hello world_


```java

@RunWith(MoxyRunner.class)
public class Example {

    @Test
    @Moxy(response = "hello world")
    public void singleResponse() throws Exception {
        assertEquals("hello", Resources.toString(new URL("http://localhost:9001"), Charset.forName("UTF-8")));
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

JSON from a file


```java

    @Test
    @Moxy(file = "example.json", contentType = "application/json")
    public void fileResponse() throws Exception {
        assertEquals("{\"id\": 1, \"mood\": \"Adventurous\"}", Resources.toString(new URL("http://localhost:9001"), Charset.forName("UTF-8")));
    }

```