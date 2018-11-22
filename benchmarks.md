Benchmarks run on 22 Nov 2018 with d6815b5 
```
Benchmark                                                                                     Mode   Cnt        Score        Error  Units
Benchmarks.flatMapMinMapReduceJavaStreams                                                    thrpt    10     6301.503 ±     58.598  ops/s
Benchmarks.flatMapMinMapReduceKool                                                           thrpt    10    18660.321 ±    249.243  ops/s
Benchmarks.mapToListJava                                                                     thrpt    10  6741600.490 ±  76856.319  ops/s
Benchmarks.mapToListKool                                                                     thrpt    10  7904785.590 ± 101339.394  ops/s
Benchmarks.rangeOneTo100CountJava                                                            thrpt    10  2555426.908 ±  27075.046  ops/s
Benchmarks.rangeOneTo100CountKool                                                            thrpt    10  5607917.720 ±  37237.498  ops/s
Benchmarks.readFileJava                                                                      thrpt    10     9198.531 ±    144.433  ops/s
Benchmarks.readFileKool                                                                      thrpt    10    10901.400 ±    154.180  ops/s
ShakespearePlaysScrabbleWithKool.measureThroughput                                          sample  1154       43.418 ±      1.539  ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.00                  sample             39.649               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.50                  sample             41.353               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.90                  sample             45.777               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.95                  sample             52.265               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.99                  sample             67.535               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.999                 sample            469.476               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.9999                sample            513.278               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p1.00                  sample            513.278               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput                            sample  1069       46.899 ±      0.489  ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.00    sample             43.385               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.50    sample             45.416               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.90    sample             50.135               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.95    sample             56.721               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.99    sample             71.159               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.999   sample             83.176               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.9999  sample             83.231               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p1.00    sample             83.231               ms/op
```
