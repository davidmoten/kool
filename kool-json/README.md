# kool-json
JSON support for [kool](..).

Status: *in development*

## About
Project goals are these:

* present queries on streams of JSON text as a Kool stream (`Stream`, `Maybe`, `Single`)
* reduce memory, cpu and io use by only parsing JSON parts that are queried  
* make a certain class of JSON query efficient and easily expressable in Kool

## Examples

We'll use this JSON to show some extraction methods:

```json
{
  "books": [
    {
      "isbn": "9781593275846",
      "title": "Eloquent JavaScript, Second Edition",
      "subtitle": "A Modern Introduction to Programming",
      "author": "Marijn Haverbeke",
      "published": "2014-12-14T00:00:00.000Z",
      "publisher": "No Starch Press",
      "pages": 472,
      "description": "JavaScript lies at the heart of almost every modern web application, from social apps to the newest browser-based games. Though simple for beginners to pick up and play with, JavaScript is a flexible, complex language that you can use to build full-scale applications.",
      "website": "http://eloquentjavascript.net/"
    },
    ...
}
```

### Extract a repeated element as a Stream
Here we count the distinct authors from the input JSON:

```java
long count = 
  Json.stream(inputStream)
    .fieldArray("books") //
    .field("author") //
    .map(node -> node.asText()) //
    .distinct() //
    .count() //
    .blockingGet();      .
```

### Parse an array and map each item to an object
Given a streaming array of JSON like this:

```json
[{"name":"Civic","datetime":"2020-01-10T08:00:00.000","aqi_pm2_5":"36"}
,{"name":"Civic","datetime":"2020-01-10T07:00:00.000","aqi_pm2_5":"36"}
,{"name":"Civic","datetime":"2020-01-10T06:00:00.000","aqi_pm2_5":"39"}
,{"name":"Civic","datetime":"2020-01-10T05:00:00.000","aqi_pm2_5":"43"}
,{"name":"Civic","datetime":"2020-01-10T04:00:00.000","aqi_pm2_5":"50"}
,{"name":"Civic","datetime":"2020-01-10T03:00:00.000","aqi_pm2_5":"54"}
,{"name":"Civic","datetime":"2020-01-10T02:00:00.000","aqi_pm2_5":"61"}
,{"name":"Civic","datetime":"2020-01-10T01:00:00.000","aqi_pm2_5":"73"}
,{"name":"Civic","datetime":"2020-01-10T00:00:00.000","aqi_pm2_5":"85"}
...
```

Object:

```java
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Record {
   
    @JsonProperty("datetime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "ADST")
    public Date time;
    
    @JsonProperty("aqi_pm2_5")
    public Double value;

    @JsonProperty("name")
    public String name;
    
}
```

Process it like this to map each row to a Jackson annotated class:

```java
  Json 
    .stream(in)
    .arrayNode()
    .flatMap(node -> node.values(Record.class))
    // ignore some records
    .filter(rec -> rec.value != null && rec.value > 50)
    // print the values to stdout
    .doOnNext(rec -> System.out.println(rec.value))
    // start the stream
    .go();
;
```    


## Usage notes
You'll notice that there is no method that takes an InputStream/Reader factory and autocloses it (using the Kool.using method). This is because under the covers a single `JsonParser` element is emitted which is a stateful singleton and really just a pointer to the current position of the parser in the JSON input. For those methods that return `Stream<JsonParser` it is necessary to map `JsonParser` to your own data object immediately it appears in the stream. Some stream operators dispose the upstream before emitting the final value so a `using` operator is not appropriate on a library method that returns `Stream<JsonParser>` because the created InputStream/Reader may be closed before the `JsonParser` has finished reading. Note that you might not notice this effect because a JsonParser uses a BufferedInputStream and if your input is smaller than the buffer size, closing the InputStream/Reader early may not have an effect because the whole stream was read into the buffer already.


