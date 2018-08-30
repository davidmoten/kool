# kool
Alternative to `java.uti.stream.Stream` that:

* is sometimes faster (about 2x) for single threaded use
* has many more operators and is less verbose
* streams are reusable
* disposes resources

Status: *pre-alpha* (in development)

If you need non-blocking and/or asynchronous streaming use [RxJava](https://github.com/ReactiveX/RxJava).

Note also that [ixjava](https://github.com/akarnokd/ixjava) predates this library and is also a pull-based and iterator-based library for reusable streams.



