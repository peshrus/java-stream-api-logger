package com.peshchuk.java.stream.api.logger;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unused")
@Slf4j
public class StreamTransformer implements ClassFileTransformer {

  private static final String PACKAGE_TO_INSTRUMENT = "java/util/stream";
  private static final List<String> NOT_TO_INSTRUMENT =
      unmodifiableList(asList("peek", "makeRef"));

  private static final Set<String> instrumented = ConcurrentHashMap.newKeySet();

  public byte[] transform(final ClassLoader loader, final String className,
      final Class classBeingRedefined, final ProtectionDomain protectionDomain,
      final byte[] classfileBuffer) {
    if (!className.startsWith(PACKAGE_TO_INSTRUMENT)) {
      return classfileBuffer;
    }

    if (!instrumented.add(className)) {
      return classfileBuffer;
    }

    log.trace("Instrumentation start: {}", className);

    try {
      return doInstrumentation(classfileBuffer);
    } catch (final Exception ex) {
      log.error("Instrumentation error: {}", className, ex);
      return classfileBuffer;
    } finally {
      log.trace("Instrumentation end: {}", className);
    }
  }

  private byte[] doInstrumentation(final byte[] classfileBuffer)
      throws IOException, CannotCompileException, NotFoundException {
    final ClassPool classPool = ClassPool.getDefault();
    final CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
    final CtClass streamSupportClass = classPool.getCtClass("java.util.stream.StreamSupport");

    if (ctClass.equals(streamSupportClass)) {
      return classfileBuffer;
    }

    final CtClass baseStreamClass = classPool.getCtClass("java.util.stream.BaseStream");
    final CtMethod[] ctMethods = ctClass.getMethods();

    for (final CtMethod ctMethod : ctMethods) {
      final String methodName = ctMethod.getName();

      try {
        if (isToInstrument(ctMethod, methodName, baseStreamClass)) {
          log.trace("Instrument method: {}", methodName);
          ctMethod
              .insertAfter(
                  "$_ = $_.peek(new com.peshchuk.java.stream.api.logger.PeekConsumer(\"" + ctMethod
                      .getName() + "\"));");
        }
      } catch (final NotFoundException ex) {
        log.error("Instrumentation error: {}#{}", ctClass.getName(), methodName, ex);
        return classfileBuffer;
      }
    }

    byte[] byteCode = ctClass.toBytecode();
    ctClass.detach();

    return byteCode;
  }

  private boolean isToInstrument(final CtMethod ctMethod, final String methodName,
      final CtClass baseStreamClass) throws NotFoundException {
    final CtClass returnType = ctMethod.getReturnType();

    return !NOT_TO_INSTRUMENT.contains(methodName)
        && !ctMethod.isEmpty()
        && !returnType.equals(baseStreamClass)
        && returnType.subtypeOf(baseStreamClass);
  }
}
