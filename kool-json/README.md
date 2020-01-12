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

```
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

```
[{"name":"City","datetime":"2020-01-10T08:00:00.000","aqi":"36"}
,{"name":"City","datetime":"2020-01-10T07:00:00.000","aqi":"36"}
,{"name":"City","datetime":"2020-01-10T06:00:00.000","aqi":"39"}
,{"name":"City","datetime":"2020-01-10T05:00:00.000","aqi":"43"}
,{"name":"City","datetime":"2020-01-10T04:00:00.000","aqi":"50"}
,{"name":"City","datetime":"2020-01-10T03:00:00.000","aqi":"54"}
,{"name":"City","datetime":"2020-01-10T02:00:00.000","aqi":"61"}
,{"name":"City","datetime":"2020-01-10T01:00:00.000","aqi":"73"}
,{"name":"City","datetime":"2020-01-10T00:00:00.000","aqi":"85"}
...
```

Mapping class:

```java
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Record {
   
    @JsonProperty("datetime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "ADST")
    public Date time;
    
    @JsonProperty("aqi")
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
```

### Parse an array and do what you like with each element
Given a streaming array of JSON like this:

```
[{"name":"City","datetime":"2020-01-10T08:00:00.000","aqi":"36"}
,{"name":"City","datetime":"2020-01-10T07:00:00.000","aqi":"36"}
,{"name":"City","datetime":"2020-01-10T06:00:00.000","aqi":"39"}
,{"name":"City","datetime":"2020-01-10T05:00:00.000","aqi":"43"}
,{"name":"City","datetime":"2020-01-10T04:00:00.000","aqi":"50"}
,{"name":"City","datetime":"2020-01-10T03:00:00.000","aqi":"54"}
,{"name":"City","datetime":"2020-01-10T02:00:00.000","aqi":"61"}
,{"name":"City","datetime":"2020-01-10T01:00:00.000","aqi":"73"}
,{"name":"City","datetime":"2020-01-10T00:00:00.000","aqi":"85"}
...
```

Process it like this to extract what we like from each element using `JsonNode`:

```java
  Json 
    .stream(in)
    .arrayNode()
    .flatMap(node -> node.values())
    // we now have a stream of JsonNode
    .map(node -> node.get("aqi").asInt())
    // ignore some records
    .filter(x -> x != null && x > 50)
    // print the values to stdout
    .doOnNext(System.out::println)
    // start the stream
    .go();
```

