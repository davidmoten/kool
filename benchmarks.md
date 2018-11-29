Benchmarks run on 29 Nov 2018 with commit a05a2f2 
```
Benchmark                                                                                     Mode   Cnt        Score        Error  Units
Benchmarks.flatMapMinMapReduceJavaStreams                                                    thrpt    10     6183.416 ±    139.495  ops/s
Benchmarks.flatMapMinMapReduceKool                                                           thrpt    10    20576.853 ±    530.743  ops/s
Benchmarks.mapToListJava                                                                     thrpt    10  6725291.729 ± 225749.294  ops/s
Benchmarks.mapToListKool                                                                     thrpt    10  7959925.128 ± 155955.805  ops/s
Benchmarks.rangeOneTo100CountJava                                                            thrpt    10  2599460.010 ±  37246.335  ops/s
Benchmarks.rangeOneTo100CountKool                                                            thrpt    10  5711771.754 ±  35779.804  ops/s
Benchmarks.readFileJava                                                                      thrpt    10     9552.657 ±     95.257  ops/s
Benchmarks.readFileKool                                                                      thrpt    10     8740.712 ±    162.297  ops/s
ShakespearePlaysScrabbleWithKool.measureThroughput                                          sample  1869       26.816 ±      0.119  ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.00                  sample             25.526               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.50                  sample             26.411               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.90                  sample             27.886               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.95                  sample             28.574               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.99                  sample             33.364               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.999                 sample             45.286               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.9999                sample             61.080               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p1.00                  sample             61.080               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput                            sample  1267       39.563 ±      0.176  ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.00    sample             37.945               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.50    sample             39.191               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.90    sample             40.567               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.95    sample             41.550               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.99    sample             49.760               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.999   sample             61.561               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.9999  sample             62.194               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p1.00    sample             62.194               ms/op
```
