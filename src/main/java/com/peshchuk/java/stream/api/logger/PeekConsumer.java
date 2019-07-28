package com.peshchuk.java.stream.api.logger;

import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unused")
@Slf4j
public class PeekConsumer implements Consumer, IntConsumer, LongConsumer, DoubleConsumer {

  private final String methodName;

  public PeekConsumer(String methodName) {
    this.methodName = methodName;
  }

  @Override
  public void accept(final Object value) {
    final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    final StackTraceElement stEl = stackTrace[stackTrace.length - 1];

    log.info("{}.{}({}:{}) -> {}: {}",
        stEl.getClassName(),
        stEl.getMethodName(),
        stEl.getFileName(),
        stEl.getLineNumber(),
        methodName,
        value);
  }

  @Override
  public void accept(final int value) {
    this.accept((Object) value);
  }

  @Override
  public void accept(final long value) {
    this.accept((Object) value);
  }

  @Override
  public void accept(final double value) {
    this.accept((Object) value);
  }
}
