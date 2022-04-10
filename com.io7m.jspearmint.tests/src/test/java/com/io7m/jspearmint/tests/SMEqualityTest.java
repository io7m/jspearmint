/*
 * Copyright © 2020 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jspearmint.tests;

import com.io7m.jspearmint.analysis.SMString;
import com.io7m.jspearmint.api.SMAccessQualifier;
import com.io7m.jspearmint.api.SMAddressingModel;
import com.io7m.jspearmint.api.SMBuiltIn;
import com.io7m.jspearmint.api.SMCapability;
import com.io7m.jspearmint.api.SMDecoration;
import com.io7m.jspearmint.api.SMDim;
import com.io7m.jspearmint.api.SMExecutionMode;
import com.io7m.jspearmint.api.SMExecutionModel;
import com.io7m.jspearmint.api.SMFPFastMathMode;
import com.io7m.jspearmint.api.SMFPRoundingMode;
import com.io7m.jspearmint.api.SMFunctionControl;
import com.io7m.jspearmint.api.SMFunctionParameterAttribute;
import com.io7m.jspearmint.api.SMGroupOperation;
import com.io7m.jspearmint.api.SMImageChannelDataType;
import com.io7m.jspearmint.api.SMImageChannelOrder;
import com.io7m.jspearmint.api.SMImageFormat;
import com.io7m.jspearmint.api.SMImageOperands;
import com.io7m.jspearmint.api.SMInstruction;
import com.io7m.jspearmint.api.SMKernelEnqueueFlags;
import com.io7m.jspearmint.api.SMKernelProfilingInfo;
import com.io7m.jspearmint.api.SMLinkageType;
import com.io7m.jspearmint.api.SMLoopControl;
import com.io7m.jspearmint.api.SMMemoryAccess;
import com.io7m.jspearmint.api.SMMemoryModel;
import com.io7m.jspearmint.api.SMMemorySemantics;
import com.io7m.jspearmint.api.SMOperandKind;
import com.io7m.jspearmint.api.SMRayFlags;
import com.io7m.jspearmint.api.SMRayQueryCandidateIntersectionType;
import com.io7m.jspearmint.api.SMRayQueryCommittedIntersectionType;
import com.io7m.jspearmint.api.SMRayQueryIntersection;
import com.io7m.jspearmint.api.SMSamplerAddressingMode;
import com.io7m.jspearmint.api.SMSamplerFilterMode;
import com.io7m.jspearmint.api.SMScope;
import com.io7m.jspearmint.api.SMSelectionControl;
import com.io7m.jspearmint.api.SMSourceLanguage;
import com.io7m.jspearmint.api.SMStorageClass;
import com.io7m.jspearmint.parser.api.SMParsedInstruction;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Stream;

public final class SMEqualityTest
{
  private static final List<Class<?>> ENUM_CLASSES =
    List.of(
      SMAccessQualifier.class,
      SMAddressingModel.class,
      SMBuiltIn.class,
      SMCapability.class,
      SMDecoration.class,
      SMDim.class,
      SMExecutionMode.class,
      SMExecutionModel.class,
      SMFPFastMathMode.class,
      SMFPRoundingMode.class,
      SMFunctionControl.class,
      SMFunctionParameterAttribute.class,
      SMGroupOperation.class,
      SMImageChannelDataType.class,
      SMImageChannelOrder.class,
      SMImageFormat.class,
      SMImageOperands.class,
      SMInstruction.class,
      SMKernelEnqueueFlags.class,
      SMKernelProfilingInfo.class,
      SMLinkageType.class,
      SMLoopControl.class,
      SMMemoryAccess.class,
      SMMemoryModel.class,
      SMMemorySemantics.class,
      SMOperandKind.class,
      SMRayFlags.class,
      SMRayQueryCandidateIntersectionType.class,
      SMRayQueryCommittedIntersectionType.class,
      SMRayQueryIntersection.class,
      SMSamplerAddressingMode.class,
      SMSamplerFilterMode.class,
      SMScope.class,
      SMSelectionControl.class,
      SMSourceLanguage.class,
      SMStorageClass.class
    );

  private static DynamicTest testEnumMake(
    final Class<?> clazz)
  {
    return DynamicTest.dynamicTest(
      String.format("testEnum%s", clazz.getSimpleName()),
      () -> checkEnumEquality(clazz));
  }

  private static DynamicTest testEnumValueMake(
    final Class<?> clazz)
  {
    return DynamicTest.dynamicTest(
      String.format("testEnumValue%s", clazz.getSimpleName()),
      () -> checkEnumValueEquality(clazz));
  }

  private static DynamicTest testEnumOfIntegerMake(
    final Class<?> clazz)
  {
    return DynamicTest.dynamicTest(
      String.format("testEnumOfInteger%s", clazz.getSimpleName()),
      () -> checkEnumOfInteger(clazz));
  }

  private static void checkEnumEquality(
    final Class<?> clazz)
  {
    EqualsVerifier.forClass(clazz)
      .verify();
  }

  private static void checkEnumValueEquality(
    final Class<?> clazz)
  {
    try {
      final var valuesMethod =
        clazz.getMethod("values");
      final var valueMethod =
        clazz.getMethod("value");
      final var spirNameMethod =
        clazz.getMethod("spirName");
      final Object[] values =
        (Object[]) valuesMethod.invoke(clazz);

      for (final var value : values) {
        for (final var other : values) {
          if (Objects.equals(value, other)) {
            continue;
          }
          final Object valueThis = valueMethod.invoke(value);
          final Object valueOther = valueMethod.invoke(other);
          if (Objects.equals(valueThis, valueOther)) {
            if (isExceptionPair(value.toString(), other.toString())) {
              continue;
            }
          }

          Assertions.assertNotEquals(
            valueThis,
            valueOther,
            String.format(
              "%s (%s) != %s (%s)",
              value,
              valueThis,
              other,
              valueOther
            )
          );

          final Object nameThis = spirNameMethod.invoke(value);
          final Object nameOther = spirNameMethod.invoke(other);

          Assertions.assertNotEquals(
            nameThis,
            nameOther,
            String.format(
              "%s (%s) != %s (%s)",
              value,
              nameThis,
              other,
              nameOther
            )
          );
        }
      }
    } catch (final NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      throw new IllegalStateException(e);
    }
  }

  private static void checkEnumOfInteger(
    final Class<?> clazz)
  {
    try {
      final var valuesMethod =
        clazz.getMethod("values");
      final var valueMethod =
        clazz.getMethod("value");
      final var ofIntegerMethod =
        clazz.getMethod("ofInteger", int.class);
      final Object[] values =
        (Object[]) valuesMethod.invoke(clazz);

      for (final var value : values) {
        final var i = (Integer) valueMethod.invoke(value);
        final var other = ofIntegerMethod.invoke(clazz, i);

        if (!Objects.equals(value, other)) {
          if (isExceptionPair(value.toString(), other.toString())) {
            continue;
          }
        }
        Assertions.assertEquals(value, other);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
          try {
            ofIntegerMethod.invoke(clazz, Integer.valueOf(Integer.MAX_VALUE));
          } catch (final InvocationTargetException e) {
            throw e.getCause();
          }
        });
      }
    } catch (final NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      throw new IllegalStateException(e);
    }
  }

  private static final Map<String, String> EQUAL_EXCEPTIONS =
    makeEqualExceptions();

  private static Map<String, String> makeEqualExceptions()
  {
    try {
      final Properties properties = new Properties();
      try (var stream = SMEqualityTest.class.getResourceAsStream(
        "equalEnums.properties")) {
        properties.load(stream);
      }
      final var names = new HashMap<String, String>();
      for (final var name : properties.stringPropertyNames()) {
        names.put(name, properties.getProperty(name));
      }
      return Map.copyOf(names);
    } catch (final IOException e) {
      throw new IllegalStateException(e);
    }
  }

  private static boolean isExceptionPair(
    final String valueA,
    final String valueB)
  {
    return Objects.equals(EQUAL_EXCEPTIONS.get(valueA), valueB)
      || Objects.equals(EQUAL_EXCEPTIONS.get(valueB), valueA);
  }

  private static DynamicTest testInstructionMake(
    final SMInstruction instruction)
  {
    return DynamicTest.dynamicTest(
      String.format("testInstruction%s", instruction.spirName()),
      () -> checkInstructionEquality(instruction));
  }

  private static void checkInstructionEquality(
    final SMInstruction instruction)
  {
    final var instructions = Set.of(SMInstruction.values());
    final var without = new HashSet<>(instructions);
    without.remove(instruction);
    for (final var other : without) {
      Assertions.assertNotEquals(instruction, other);
    }
    Assertions.assertEquals(instruction, instruction);
    Assertions.assertEquals(instruction.operands(), instruction.operands());
  }

  @Test
  public void testSMParsedInstruction()
  {
    EqualsVerifier.forClass(SMParsedInstruction.class)
      .withNonnullFields("operands")
      .verify();
  }

  @Test
  public void testSMString()
  {
    EqualsVerifier.forClass(SMString.class)
      .withNonnullFields("text")
      .verify();
  }

  @TestFactory
  public Stream<DynamicTest> testEnums()
  {
    return ENUM_CLASSES.stream().map(SMEqualityTest::testEnumMake);
  }

  @TestFactory
  public Stream<DynamicTest> testEnumsValue()
  {
    return ENUM_CLASSES.stream().map(SMEqualityTest::testEnumValueMake);
  }

  @TestFactory
  public Stream<DynamicTest> testEnumsOfInteger()
  {
    return ENUM_CLASSES.stream()
      .filter(c -> !"SMInstruction".equals(c.getSimpleName()))
      .filter(c -> !"SMOperandKind".equals(c.getSimpleName()))
      .map(SMEqualityTest::testEnumOfIntegerMake);
  }

  @TestFactory
  public Stream<DynamicTest> testInstructions()
  {
    return Stream.of(SMInstruction.values())
      .map(SMEqualityTest::testInstructionMake);
  }
}
