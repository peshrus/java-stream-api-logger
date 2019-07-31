package com.peshchuk.java.stream.api.logger;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.function.BiConsumer;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.junit.Assert;
import org.junit.Test;

public class StreamTransformerTest {

  private final StreamTransformer streamTransformer = new StreamTransformer();

  @Test
  public void transform_WrongPackage() {
    // Arrange
    final byte[] expected = new byte[]{1, 2, 3};

    // Act
    final byte[] actual = streamTransformer.transform(null, "fake", null, null, expected);

    // Assert
    assertEquals(expected, actual);
  }

  @Test
  public void transform_InstrumentationError() throws Exception {
    // Arrange
    final String className = "java.util.stream.IntPipeline";
    final byte[] expected = ClassPool.getDefault().get(className).toBytecode();

    // Act
    final byte[] actual = streamTransformer
        .transform(null, className.replaceAll("\\.", "/"), null, null, expected);

    // Assert
    assertEquals(expected, actual);
  }

  @Test
  public void transform_StreamSupport() throws Exception {
    fixture(Assert::assertEquals, "java.util.stream.StreamSupport");
  }

  @Test
  public void transform_BaseStream() throws Exception {
    fixture(Assert::assertNotEquals, "java.util.stream.BaseStream");
  }

  private void fixture(final BiConsumer<byte[], byte[]> assertFunction, final String className)
      throws NotFoundException, IOException, CannotCompileException {
    // Arrange
    final String transformClassName = className.replaceAll("\\.", "/");
    final CtClass ctClass = ClassPool.getDefault().get(className);
    final byte[] initial = ctClass.toBytecode();
    ctClass.defrost();

    // Act 1
    final byte[] actual1 = streamTransformer
        .transform(null, transformClassName, null, null, initial);

    // Assert 1
    assertFunction.accept(initial, actual1);

    // Act 2
    final byte[] actual2 = streamTransformer
        .transform(null, transformClassName, null, null, initial);

    // Assert 1
    assertEquals(initial, actual2);
  }
}
