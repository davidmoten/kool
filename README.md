# kool
<a href="https://travis-ci.org/davidmoten/kool"><img src="https://travis-ci.org/davidmoten/kool.svg"/></a><br/>
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.davidmoten/kool/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.github.davidmoten/kool)<br/>
[![codecov](https://codecov.io/gh/davidmoten/kool/branch/master/graph/badge.svg)](https://codecov.io/gh/davidmoten/kool)


Alternative to `java.uti.stream.Stream` that:

* is sometimes faster (about 2x) for synchonronous use
* has many more operators and is less verbose
* streams are reusable
* disposes resources
* is designed for synchronous use only
* models 0..1 and 1 element streams explicitly with `Maybe` and `Single`.

Status: *pre-alpha* (in development)

If you need non-blocking and/or asynchronous streaming use [RxJava](https://github.com/ReactiveX/RxJava).

Note also that [ixjava](https://github.com/akarnokd/ixjava) predates this library and is also a pull-based and iterator-based library for reusable streams but does not model `Maybe` and `Single`.



