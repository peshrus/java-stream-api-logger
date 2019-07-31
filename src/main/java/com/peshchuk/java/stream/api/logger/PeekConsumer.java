package com.peshchuk.java.stream.api.logger;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import org.slf4j.Logger;

@SuppressWarnings("unused")
public class PeekConsumer implements Consumer, IntConsumer, LongConsumer, DoubleConsumer {

  private final String methodName;

  public PeekConsumer(String methodName) {
    this.methodName = methodName;
  }

  @Override
  public void accept(final Object value) {
    final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    final StackTraceElement stEl = stackTrace[stackTrace.length - 1];
    final Logger log =
        getLogger(stEl.getClassName() + "." + stEl.getMethodName() + "." + methodName);

    log.debug("{} ({}:{})",
        value,
        stEl.getFileName(),
        stEl.getLineNumber()
    );
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
