package com.peshchuk.java.stream.api.logger;

import java.lang.instrument.Instrumentation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StreamAgent {

  public static void premain(final String agentArgs, final Instrumentation instrumentation) {
    log.debug("Add StreamTransformer");
    instrumentation.addTransformer(new StreamTransformer());
  }
}
