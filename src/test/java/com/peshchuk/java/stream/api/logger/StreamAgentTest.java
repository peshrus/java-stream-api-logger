package com.peshchuk.java.stream.api.logger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.lang.instrument.Instrumentation;
import org.junit.Test;

public class StreamAgentTest {

  @Test
  public void premain() {
    // Arrange
    final Instrumentation instrumentation = mock(Instrumentation.class);

    // Act
    StreamAgent.premain(null, instrumentation);

    // Assert
    verify(instrumentation).addTransformer(any(StreamTransformer.class));
  }
}
