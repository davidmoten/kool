Benchmarks run on 22 Nov 2018 with d6815b5 
```
Benchmark                                                                                     Mode   Cnt        Score        Error  Units
Benchmarks.flatMapMinMapReduceJavaStreams                                                    thrpt    10     6098.031 ±     63.536  ops/s
Benchmarks.flatMapMinMapReduceKool                                                           thrpt    10    17190.846 ±    570.580  ops/s
Benchmarks.mapToListJava                                                                     thrpt    10  6497506.548 ± 406556.461  ops/s
Benchmarks.mapToListKool                                                                     thrpt    10  8765503.283 ± 141985.176  ops/s
Benchmarks.rangeOneTo100CountJava                                                            thrpt    10  2483297.028 ±  20472.059  ops/s
Benchmarks.rangeOneTo100CountKool                                                            thrpt    10  5537852.725 ±  50081.902  ops/s
Benchmarks.readFileJava                                                                      thrpt    10     9129.486 ±     86.685  ops/s
Benchmarks.readFileKool                                                                      thrpt    10     8475.793 ±    117.760  ops/s
ShakespearePlaysScrabbleWithKool.measureThroughput                                          sample  1110       45.241 ±      0.453  ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.00                  sample             41.091               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.50                  sample             43.844               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.90                  sample             49.742               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.95                  sample             55.378               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.99                  sample             65.529               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.999                 sample             77.202               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.9999                sample             77.332               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p1.00                  sample             77.332               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput                            sample  1112       45.199 ±      0.444  ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.00    sample             41.288               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.50    sample             43.581               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.90    sample             50.620               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.95    sample             55.253               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.99    sample             65.035               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.999   sample             71.519               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.9999  sample             71.696               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p1.00    sample             71.696               ms/op
```
