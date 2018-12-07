Benchmarks run on 7 Dec 2018 with commit 40d2b0b
```
Benchmark                                                                                     Mode   Cnt        Score        Error  Units
Benchmarks.flatMapMinMapReduceJavaStreams                                                    thrpt    10     6485.959 ±     59.616  ops/s
Benchmarks.flatMapMinMapReduceKool                                                           thrpt    10    13592.975 ±    313.123  ops/s
Benchmarks.mapToListJava                                                                     thrpt    10  6708364.123 ± 320018.709  ops/s
Benchmarks.mapToListKool                                                                     thrpt    10  7925348.160 ± 174806.278  ops/s
Benchmarks.rangeOneTo100CountJava                                                            thrpt    10  2515535.728 ±  59782.960  ops/s
Benchmarks.rangeOneTo100CountKool                                                            thrpt    10  5666007.803 ±  27968.620  ops/s
Benchmarks.readFileJava                                                                      thrpt    10     9331.666 ±    145.206  ops/s
Benchmarks.readFileKool                                                                      thrpt    10     8672.499 ±    196.034  ops/s
ShakespearePlaysScrabbleWithKool.measureThroughput                                          sample  1797       27.872 ±      0.123  ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.00                  sample             26.411               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.50                  sample             27.492               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.90                  sample             28.967               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.95                  sample             29.888               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.99                  sample             35.730               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.999                 sample             43.223               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.9999                sample             46.727               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p1.00                  sample             46.727               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput                            sample  1165       43.051 ±      0.204  ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.00    sample             40.632               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.50    sample             42.533               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.90    sample             44.958               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.95    sample             46.399               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.99    sample             52.867               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.999   sample             65.161               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.9999  sample             66.912               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p1.00    sample             66.912               ms/op
```
