Benchmarks run on 21 Nov 2018 with 10bff8d
```
Benchmark                                                                                     Mode  Cnt         Score        Error  Units
Benchmarks.flatMapMinMapReduceJavaStreams                                                    thrpt   10      7127.461 ±     26.712  ops/s
Benchmarks.flatMapMinMapReduceKool                                                           thrpt   10     19399.926 ±    116.910  ops/s
Benchmarks.rangeOneTo100CountJava                                                            thrpt   10   1559537.970 ±   3991.167  ops/s
Benchmarks.rangeOneTo100CountKool                                                            thrpt   10   5726053.686 ±  23573.843  ops/s
Benchmarks.readFileJava                                                                      thrpt   10      9510.631 ±     94.952  ops/s
Benchmarks.readFileKool                                                                      thrpt   10      9384.719 ±     48.280  ops/s
Benchmarks.toListJava                                                                        thrpt   10  11772680.369 ±  69198.490  ops/s
Benchmarks.toListKool                                                                        thrpt   10  19486989.089 ± 140329.032  ops/s
ShakespearePlaysScrabbleWithKool.measureThroughput                                          sample   90       119.746 ±      1.741  ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.00                  sample            115.737               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.50                  sample            118.358               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.90                  sample            122.880               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.95                  sample            134.021               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.99                  sample            142.606               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.999                 sample            142.606               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.9999                sample            142.606               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p1.00                  sample            142.606               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput                            sample  255        40.360 ±      0.471  ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.00    sample             38.404               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.50    sample             39.846               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.90    sample             42.205               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.95    sample             43.713               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.99    sample             52.085               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.999   sample             56.885               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.9999  sample             56.885               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p1.00    sample             56.885               ms/op
```
