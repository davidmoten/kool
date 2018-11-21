# kool
<a href="https://travis-ci.org/davidmoten/kool"><img src="https://travis-ci.org/davidmoten/kool.svg"/></a><br/>
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.davidmoten/kool/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.github.davidmoten/kool)<br/>
[![codecov](https://codecov.io/gh/davidmoten/kool/branch/master/graph/badge.svg)](https://codecov.io/gh/davidmoten/kool)


Alternative to `java.util.stream.Stream`:

* is sometimes much faster for synchonronous use ([benchmarks](benchmarks.md))
* has many **more operators** and is generally less verbose
* streams are **reusable**
* **disposes** resources
* is designed for synchronous use only
* models 0..1 and 1 element streams explicitly with **`Maybe`** and **`Single`**.
* does not support streams of nulls (use `Optional` or `Maybe`)
* ~10% faster on *Shakespeare Plays Scrabble* benchmark

Status: *pre-alpha* (in development)

Maven site reports are [here](https://davidmoten.github.io/kool) including [javadoc](https://davidmoten.github.io/kool/apidocs/index.html).

If you need non-blocking and/or asynchronous streaming use [RxJava](https://github.com/ReactiveX/RxJava).

Note also that [IxJava](https://github.com/akarnokd/ixjava) predates this library and is also a pull-based and iterator-based library for reusable streams but does not model `Maybe` and `Single`.

## How to build
```bash
mvn clean install
```

## Getting started
Add this dependency to your pom.xml:

```xml
<dependency>
    <groupId>com.github.davidmoten</groupId>
    <artifactId>kool</artifactId>
    <version>VERSION_HERE</version>
</dependency>
```
## Example
```java
import org.davidmoten.kool.Stream;

Stream //
  .range(1, 10)
  .flatMap(n -> Stream
      .range(1, n)
      .reduceWithInitialValue(0, (a, b) -> a + b))
  .mapWithIndex(1)
  .println()
  .forEach();
```

output:
```
Indexed[index=1, value=1]
Indexed[index=2, value=3]
Indexed[index=3, value=6]
Indexed[index=4, value=10]
Indexed[index=5, value=15]
Indexed[index=6, value=21]
Indexed[index=7, value=28]
Indexed[index=8, value=36]
Indexed[index=9, value=45]
Indexed[index=10, value=55]
```

## Checklist for new operators
* wrap calls to `it.next()` with `Preconditions.checkNotNull`
* wrap calls to function parameters passed to operator with `Preconditions.checkNotNull`
* dispose upstream iterables as soon as no longer required
* don't dispose upstream iterable as soon as hasNext returns true
* set upstream iterable reference to null (to help gc) when no longer required

