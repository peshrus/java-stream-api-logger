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
    final String className = "java.util.stream.StreamSupport";
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
    final CtClass ctClass = ClassPool.getDefault().get(className);
    final byte[] initial = ctClass.toBytecode();
    ctClass.defrost();

    // Act
    final byte[] actual = streamTransformer
        .transform(null, className.replaceAll("\\.", "/"), null, null, initial);

    // Assert
    assertFunction.accept(initial, actual);
  }
}
