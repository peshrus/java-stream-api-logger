package com.peshchuk.java.stream.api.logger;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.slf4j.LoggerFactory.getLogger;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;

public class PeekConsumerTest {

  private static final String METHOD_NAME = "testMethod";

  private final PeekConsumer peekConsumer = new PeekConsumer(METHOD_NAME);

  @Test
  public void acceptObject() {
    fixture("value");
  }

  @Test
  public void acceptInt() {
    fixture(1);
  }

  @Test
  public void acceptLong() {
    fixture(2L);
  }

  @Test
  public void acceptDouble() {
    fixture(3.0);
  }

  private void fixture(Object value) {
    // Arrange
    final AtomicReference<Level> loggingLevel = new AtomicReference<>(null);
    final StringBuilder loggingMsg = new StringBuilder();
    final Appender<ILoggingEvent> appender = mockAppender();

    doAnswer(invocation -> {
      final ILoggingEvent event = invocation.getArgument(0);

      loggingLevel.set(event.getLevel());
      loggingMsg.append(event.getFormattedMessage());

      return null;
    }).when(appender).doAppend(any());

    // Act
    if (value instanceof Integer) {
      peekConsumer.accept((int) value);
    } else if (value instanceof Long) {
      peekConsumer.accept((long) value);
    } else if (value instanceof Double) {
      peekConsumer.accept((double) value);
    } else {
      peekConsumer.accept(value);
    }

    // Assert
    assertEquals(Level.DEBUG, loggingLevel.get());
    assertEquals(value + " (JUnitStarter.java:70)", loggingMsg.toString());
  }

  private Appender<ILoggingEvent> mockAppender() {
    final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    final StackTraceElement stEl = stackTrace[stackTrace.length - 1];
    final Logger log =
        (Logger) getLogger(stEl.getClassName() + "." + stEl.getMethodName() + "." + METHOD_NAME);
    @SuppressWarnings("unchecked") final Appender<ILoggingEvent> appender = mock(Appender.class);

    log.addAppender(appender);

    return appender;
  }
}
