# Moxy

  Moxy is for those times when you are __absolutely convinced__ that you need to run integration tests against an http server.

[![Build Status](https://travis-ci.org/tomkp/moxy.png)](https://travis-ci.org/tomkp/moxy)




```java

@RunWith(MoxyRunner.class)
public class MyTest {

    @Test
    @Moxy(response = "hello world")
    public void example() throws Exception {

        // opening http://locahost:9001 will return 'hello world'
    }

}

```


  [Check out some more examples](https://github.com/tomkp/moxy#examples)

## Usage

  Annotate your test classes with ```@RunWith(MoxyRunner.class)```

  Annotate your tests with ```@Moxy```

## Configuration

  You can configure the response body and headers.

### response body

  There are two ways of responding. You can either load the response from a file, or you can inline the response n the annotation.

  - ```response``` this allows you to inline your response
  - ```file``` the response body is loaded from a file (using either a relative or absolute path)

### response headers

  - ```contentType``` the content type, the default is```text/plain```
  - ```statusCode``` the HTTP status code, the default is ```200```

  Wherever multiple values can be given the following logic will be used:

  - 0: use default value
  - 1: always return this value
  - x: a value for each request

  eg:

  ```@Moxy(statusCode = {500, 500, 200})``` - the first 2 requests get status code ```500```, the third request gets a ```200```

  ```@Moxy(statusCode = 404)``` - all requests get a ```404```

  ```@Moxy``` - all requests get the default status code ```200```



## Why you shouldn't use Moxy

  Before using Moxy you should ask yourself if it is truly necessary.

  Can you refactor the code and mock out the dependency instead?

  It might look like a unit test but it is not. Although the overhead of starting a server for *every* test is fairly low it's still going to run a lot slower than an actual unit test.

  If you insist on using Moxy - then keep the tests to a minimum.


## Examples

  Mock server running on port 9001 returns ```hello world``` for all requests


```java

@RunWith(MoxyRunner.class)
public class Examples {

    @Test
    @Moxy(response = "hello world")
    public void singleResponse() throws Exception {
        URL url = new URL("http://localhost:9001");
        assertEquals("hello world", Resources.toString(url, Charset.forName("UTF-8")));
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
        URL url = new URL("http://localhost:9001");
        assertEquals("{\"id\": 1, \"mood\": \"Adventurous\"}", Resources.toString(url, Charset.forName("UTF-8")));
    }

```


JSON from a relative file


```java


    @Test
    @Moxy(file = "example.json", contentType = "application/json")
    public void fileResponse() throws Exception {
        URL url = new URL("http://localhost:9001");
        assertEquals("{\"id\": 1, \"mood\": \"Adventurous\"}", Resources.toString(url, Charset.forName("UTF-8")));
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
    @Moxy(proxy = "http://www.google.com")
    public void proxyToGoogle() throws Exception {
        URL url = new URL("http://localhost:9001/robots.txt");
        String response = Resources.toString(url, Charset.forName("UTF-8"));
        assertTrue(response.startsWith("User-agent: *"));
    }

```


Capture response when proxying


```java

    @Test
    @Moxy(proxy = "http://www.google.com", file = "google_robots.txt")
    public void captureResponseFromGoogle() throws Exception {
        URL resource = this.getClass().getResource(".");
        File file = new File(resource.getPath(), "google_robots.txt");

        Resources.toString(new URL("http://localhost:9001/robots.txt"), Charset.forName("UTF-8"));
        assertTrue(Files.readFirstLine(file, Charset.forName("UTF-8")).startsWith("User-agent: *"));
    }

```


Capture responses when proxying


```java

    @Test
    @Moxy(proxy = "http://www.google.com", file = {"google_robots.txt", "google_humans.txt"})
    public void captureResponsesFromGoogle() throws Exception {
        URL resource = this.getClass().getResource(".");
        File robotsFile = new File(resource.getPath(), "google_robots.txt");
        File siteMapFile = new File(resource.getPath(), "google_humans.txt");

        Resources.toString(new URL("http://localhost:9001/robots.txt"), Charset.forName("UTF-8"));
        Resources.toString(new URL("http://localhost:9001/humans.txt"), Charset.forName("UTF-8"));
        assertTrue(Files.readFirstLine(robotsFile, Charset.forName("UTF-8")).startsWith("User-agent: *"));
        assertTrue(Files.readFirstLine(siteMapFile, Charset.forName("UTF-8")).startsWith("Google is built"));
    }

```


Multiple content types;


```java

    @Test
    @Moxy(contentType = {"application/json", "text/xml"})
    public void multipleContentTypes() throws Exception {
        HttpURLConnection connection;
        URL url = new URL("http://localhost:9001");

        connection = (HttpURLConnection) (url.openConnection());
        assertEquals("application/json", connection.getContentType());

        connection = (HttpURLConnection) (url.openConnection());
        assertEquals("text/xml", connection.getContentType());
    }

```
