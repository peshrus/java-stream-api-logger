[![Travis CI](https://travis-ci.org/peshrus/java-stream-api-logger.svg?branch=master)](https://travis-ci.org/peshrus/java-stream-api-logger)
# Java Stream API Logger
Java Stream API Logger is an [instrumentation](https://docs.oracle.com/javase/8/docs/technotes/guides/instrumentation/index.html) library for logging of API methods returning [Stream](https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html).
It is needed when you want to understand what happens in the Streams created inside of your code.

## Command Line to Use the Logger
`java -Xbootclasspath/a:javassist-3.25.0-GA.jar;slf4j-api-2.0.0-alpha0.jar;java-stream-api-logger-1.0.jar -javaagent:java-stream-api-logger-1.0.jar <YourClass>`

[`-Xbootclasspath/a`](https://docs.oracle.com/javase/8/docs/technotes/tools/findingclasses.html#bootclass) is needed because `java-stream-api-logger-1.0.jar` contains a class injected into instrumented classes.
`javassist-3.25.0-GA.jar;slf4j-api-2.0.0-alpha0.jar` are needed because `java-stream-api-logger-1.0.jar` uses them.

## Dependencies
Your project needs to have a runtime dependency to any [SLF4J](https://www.slf4j.org/) implementation, e.g. `runtime 'ch.qos.logback:logback-classic:1.3.0-alpha4'`, to see log messages.  

## Example
### Code
```java
package com.peshchuk.java.stream.api.logger.demo;

import java.util.stream.Stream;

public class Test {

  public static void main(String[] args) {
    final Integer min = Stream.iterate(1, i -> i + 1)
        .sequential()
        .map(i -> i + 1)
        .limit(5)
        .filter(i -> i % 2 == 0)
        .skip(1)
        .distinct()
        .sorted()
        .peek(i -> System.out.printf("Peek: %d\n", i))
        .min(Integer::compareTo)
        .orElse(-1);
    System.out.printf("Min: %d\n\n", min);
  }
}
```

### Output
```
22:29:35.337 [main] DEBUG com.peshchuk.java.stream.api.logger.demo.Test.main.iterate - 1 (Test.java:17)
22:29:35.339 [main] DEBUG com.peshchuk.java.stream.api.logger.demo.Test.main.map - 2 (Test.java:17)
22:29:35.340 [main] DEBUG com.peshchuk.java.stream.api.logger.demo.Test.main.limit - 2 (Test.java:17)
22:29:35.340 [main] DEBUG com.peshchuk.java.stream.api.logger.demo.Test.main.filter - 2 (Test.java:17)
22:29:35.340 [main] DEBUG com.peshchuk.java.stream.api.logger.demo.Test.main.iterate - 2 (Test.java:17)
22:29:35.340 [main] DEBUG com.peshchuk.java.stream.api.logger.demo.Test.main.map - 3 (Test.java:17)
22:29:35.340 [main] DEBUG com.peshchuk.java.stream.api.logger.demo.Test.main.limit - 3 (Test.java:17)
22:29:35.340 [main] DEBUG com.peshchuk.java.stream.api.logger.demo.Test.main.iterate - 3 (Test.java:17)
22:29:35.340 [main] DEBUG com.peshchuk.java.stream.api.logger.demo.Test.main.map - 4 (Test.java:17)
22:29:35.340 [main] DEBUG com.peshchuk.java.stream.api.logger.demo.Test.main.limit - 4 (Test.java:17)
22:29:35.340 [main] DEBUG com.peshchuk.java.stream.api.logger.demo.Test.main.filter - 4 (Test.java:17)
22:29:35.340 [main] DEBUG com.peshchuk.java.stream.api.logger.demo.Test.main.skip - 4 (Test.java:17)
22:29:35.340 [main] DEBUG com.peshchuk.java.stream.api.logger.demo.Test.main.distinct - 4 (Test.java:17)
22:29:35.340 [main] DEBUG com.peshchuk.java.stream.api.logger.demo.Test.main.iterate - 4 (Test.java:17)
22:29:35.340 [main] DEBUG com.peshchuk.java.stream.api.logger.demo.Test.main.map - 5 (Test.java:17)
22:29:35.341 [main] DEBUG com.peshchuk.java.stream.api.logger.demo.Test.main.limit - 5 (Test.java:17)
22:29:35.341 [main] DEBUG com.peshchuk.java.stream.api.logger.demo.Test.main.iterate - 5 (Test.java:17)
22:29:35.341 [main] DEBUG com.peshchuk.java.stream.api.logger.demo.Test.main.map - 6 (Test.java:17)
22:29:35.341 [main] DEBUG com.peshchuk.java.stream.api.logger.demo.Test.main.limit - 6 (Test.java:17)
22:29:35.341 [main] DEBUG com.peshchuk.java.stream.api.logger.demo.Test.main.filter - 6 (Test.java:17)
22:29:35.341 [main] DEBUG com.peshchuk.java.stream.api.logger.demo.Test.main.skip - 6 (Test.java:17)
22:29:35.341 [main] DEBUG com.peshchuk.java.stream.api.logger.demo.Test.main.distinct - 6 (Test.java:17)
22:29:35.342 [main] DEBUG com.peshchuk.java.stream.api.logger.demo.Test.main.sorted - 4 (Test.java:17)
Peek: 4
22:29:35.345 [main] DEBUG com.peshchuk.java.stream.api.logger.demo.Test.main.sorted - 6 (Test.java:17)
Peek: 6
Min: 4


```
