# kool
<a href="https://travis-ci.org/davidmoten/kool"><img src="https://travis-ci.org/davidmoten/kool.svg"/></a><br/>
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.davidmoten/kool/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.github.davidmoten/kool)<br/>
[![codecov](https://codecov.io/gh/davidmoten/kool/branch/master/graph/badge.svg)](https://codecov.io/gh/davidmoten/kool)


Alternative to `java.util.stream.Stream`:

* is sometimes faster (3x) for synchonronous use
* is sometimes slower than `j.u.s.Stream` especially for its primitive specializations
* has many **more operators** and is generally less verbose
* streams are **reusable**
* disposes resources
* is designed for synchronous use only
* models 0..1 and 1 element streams explicitly with `Maybe` and `Single`.
* does not support streams of nulls (use `Optional` or `Maybe`)

Status: *pre-alpha* (in development)

Maven site reports are [here](https://davidmoten.github.io/kool) including [javadoc](https://davidmoten.github.io/kool/apidocs/index.html).

If you need non-blocking and/or asynchronous streaming use [RxJava](https://github.com/ReactiveX/RxJava).

Note also that [ixjava](https://github.com/akarnokd/ixjava) predates this library and is also a pull-based and iterator-based library for reusable streams but does not model `Maybe` and `Single`.

## Checklist for new operators
* wrap calls to `it.next()` with `Preconditions.checkNotNull`
* wrap calls to function parameters passed to operator with `Preconditions.checkNotNull`
* dispose upstream iterables as soon as no longer required
* set upstream iterable reference to null (to help gc) when no longer required

