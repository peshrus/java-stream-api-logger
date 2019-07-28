package com.peshchuk.java.stream.api.logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StreamTransformer implements ClassFileTransformer {

  private static final KeySetView<Object, Boolean> INSTRUMENTED = ConcurrentHashMap.newKeySet();
  private static final String PEEK = "peek";

  public byte[] transform(final ClassLoader loader, final String className,
      final Class classBeingRedefined, final ProtectionDomain protectionDomain,
      final byte[] classfileBuffer) {
    if (!className.startsWith("java/util/stream")) {
      return classfileBuffer;
    }

    if (!INSTRUMENTED.add(className)) {
      return classfileBuffer;
    }

    log.debug("Instrumentation start: {}", className);

    try {
      return doInstrumentation(classfileBuffer);
    } catch (final Exception ex) {
      log.error("Instrumentation error: {}", className, ex);
      return classfileBuffer;
    } finally {
      log.debug("Instrumentation end: {}", className);
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
          log.debug("Instrument method: {}", methodName);
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

    return !PEEK.equals(methodName)
        && !ctMethod.isEmpty()
        && !returnType.equals(baseStreamClass)
        && returnType.subtypeOf(baseStreamClass);
  }
}
