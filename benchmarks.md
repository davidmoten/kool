Benchmarks run on 29 Nov 2018 with master 
```
Benchmarks.flatMapMinMapReduceJavaStreams                                                    thrpt    10     6924.045 ±    198.943  ops/s
Benchmarks.flatMapMinMapReduceKool                                                           thrpt    10    17408.157 ±   1133.835  ops/s
Benchmarks.mapToListJava                                                                     thrpt    10  6533316.797 ± 518485.870  ops/s
Benchmarks.mapToListKool                                                                     thrpt    10  8072965.053 ±  28400.164  ops/s
Benchmarks.rangeOneTo100CountJava                                                            thrpt    10  2612210.100 ±  49420.806  ops/s
Benchmarks.rangeOneTo100CountKool                                                            thrpt    10  5597065.708 ±  41683.973  ops/s
Benchmarks.readFileJava                                                                      thrpt    10     9303.867 ±    155.266  ops/s
Benchmarks.readFileKool                                                                      thrpt    10     8605.161 ±     60.339  ops/s
ShakespearePlaysScrabbleWithKool.measureThroughput                                          sample  1524       32.904 ±      0.246  ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.00                  sample             30.736               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.50                  sample             32.080               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.90                  sample             34.865               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.95                  sample             36.766               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.99                  sample             47.841               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.999                 sample             61.394               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p0.9999                sample             63.046               ms/op
ShakespearePlaysScrabbleWithKool.measureThroughput:measureThroughput·p1.00                  sample             63.046               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput                            sample  1240       40.486 ±      0.409  ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.00    sample             37.421               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.50    sample             39.125               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.90    sample             44.165               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.95    sample             49.342               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.99    sample             61.287               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.999   sample             77.568               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p0.9999  sample             77.726               ms/op
ShakespearePlaysScrabbleWithNonParallelStreams.measureThroughput:measureThroughput·p1.00    sample             77.726               ms/op
```
