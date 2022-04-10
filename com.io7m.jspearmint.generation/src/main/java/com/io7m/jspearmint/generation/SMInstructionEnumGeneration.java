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

package com.io7m.jspearmint.generation;

import com.io7m.jspearmint.json_registry.SMJSONInstruction;
import com.io7m.jspearmint.json_registry.SMJSONInstructionOperand;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.squareup.javapoet.TypeName.INT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * Functions to generate instruction enums.
 */

public final class SMInstructionEnumGeneration
{
  private SMInstructionEnumGeneration()
  {

  }

  /**
   * Generate instruction enums.
   *
   * @param packageName  The package name
   * @param instructions The instructions
   *
   * @return An instruction enum
   */

  public static TypeSpec generate(
    final String packageName,
    final List<SMJSONInstruction> instructions)
  {
    Objects.requireNonNull(instructions, "instructions");

    final var className =
      ClassName.get(packageName, "SMInstruction");

    final TypeSpec.Builder typeBuilder =
      TypeSpec.enumBuilder(className)
        .addJavadoc("The kind of SPIR-V instructions.")
        .addSuperinterface(ClassName.get(packageName, "SMEnumType"))
        .addModifiers(PUBLIC);

    for (final var instruction : instructions) {
      typeBuilder.addEnumConstant(
        transformEnumConstantName(instruction.name),
        TypeSpec.anonymousClassBuilder(
            "$L,$S,$L,$L",
            instruction.opcode,
            instruction.name,
            instructionOperandList(packageName, instruction.operands),
            Integer.valueOf(instructionMinimumOperands(instruction.operands))
          ).addJavadoc(instruction.name)
          .build()
      );
    }

    typeBuilder.addFields(generateFields(packageName));
    typeBuilder.addMethods(generateMethods(packageName));
    typeBuilder.addMethod(generateEnumConstructor(packageName));
    return typeBuilder.build();
  }

  private static int instructionMinimumOperands(
    final List<SMJSONInstructionOperand> operands)
  {
    int required = 0;
    for (int index = 0; index < operands.size(); ++index) {
      final var operand = operands.get(index);
      if (!operand.quantifier.trim().isEmpty()) {
        break;
      }
      ++required;
    }
    return required;
  }

  private static Iterable<MethodSpec> generateMethods(
    final String packageName)
  {
    return List.of(
      MethodSpec.methodBuilder("operands")
        .addModifiers(PUBLIC)
        .returns(listOfOperands(packageName))
        .addCode("return this.operands;")
        .build(),
      MethodSpec.methodBuilder("spirName")
        .addModifiers(PUBLIC)
        .returns(String.class)
        .addCode("return this.spirName;")
        .build(),
      MethodSpec.methodBuilder("value")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .returns(INT)
        .addCode("return this.value;")
        .build(),
      MethodSpec.methodBuilder("minimumOperandCount")
        .addModifiers(PUBLIC)
        .returns(INT)
        .addCode("return this.minimumOperandCount;")
        .build()
    );
  }

  private static CodeBlock instructionOperandList(
    final String packageName,
    final List<SMJSONInstructionOperand> operands)
  {
    final CodeBlock.Builder codeBuilder =
      CodeBlock.builder()
        .add("java.util.List.of(");

    final int count = operands.size();
    for (int index = 0; index < count; ++index) {
      final var operand = operands.get(index);
      codeBuilder.add(
        "SMOperand.of($T.$L,$T.$L,$S)",
        ClassName.get(packageName, "SMOperandKind"),
        transformEnumConstantName(operand.kind),
        ClassName.get(packageName, "SMOperandQuantifier"),
        transformQuantifier(operand.quantifier),
        Optional.ofNullable(operand.name).orElse("").replace("'", "")
      );

      if (index + 1 < count) {
        codeBuilder.add(",");
      }
    }

    return codeBuilder.add(")")
      .build();
  }

  private static String transformQuantifier(
    final String quantifier)
  {
    switch (quantifier) {
      case "":
        return "SM_ONE";
      case "?":
        return "SM_OPTIONAL";
      case "*":
        return "SM_REPEATED";
      default:
        throw new IllegalStateException("Unrecognized qualifier");
    }
  }

  /**
   * Transform an enum constant name.
   *
   * @param constantName The raw name
   *
   * @return A transformed name
   */

  public static String transformEnumConstantName(
    final String constantName)
  {
    Objects.requireNonNull(constantName, "constantName");

    final var upperCase = new StringBuilder(128);
    upperCase.append("SM_");

    var codepointNow = 0;
    var codepointThen = 0;
    for (int index = 0; index < constantName.length(); ++index) {
      codepointNow = constantName.codePointAt(index);
      if (Character.isUpperCase(codepointNow)) {
        if (Character.isAlphabetic(codepointThen) && Character.isLowerCase(
          codepointThen)) {
          upperCase.append('_');
        }
      }

      upperCase.appendCodePoint(Character.toUpperCase(codepointNow));
      codepointThen = codepointNow;
    }

    return upperCase.toString();
  }

  private static Iterable<FieldSpec> generateFields(
    final String packageName)
  {
    final ParameterizedTypeName listParameterized =
      listOfOperands(packageName);

    return List.of(
      FieldSpec.builder(INT, "value", FINAL, PRIVATE)
        .build(),
      FieldSpec.builder(String.class, "spirName", FINAL, PRIVATE)
        .build(),
      FieldSpec.builder(listParameterized, "operands", FINAL, PRIVATE)
        .build(),
      FieldSpec.builder(INT, "minimumOperandCount", FINAL, PRIVATE)
        .build()
    );
  }

  private static MethodSpec generateEnumConstructor(
    final String packageName)
  {
    final ParameterizedTypeName listParameterized =
      listOfOperands(packageName);

    return MethodSpec.constructorBuilder()
      .addParameter(INT, "inValue", FINAL)
      .addParameter(String.class, "inSpirName", FINAL)
      .addParameter(listParameterized, "inOperands", FINAL)
      .addParameter(INT, "inMinimumOperandCount", FINAL)
      .addCode(
        CodeBlock.builder()
          .addStatement("this.value = inValue")
          .addStatement("this.spirName = inSpirName")
          .addStatement("this.operands = inOperands")
          .addStatement("this.minimumOperandCount = inMinimumOperandCount")
          .build()
      ).build();
  }

  private static ParameterizedTypeName listOfOperands(
    final String packageName)
  {
    final var listName =
      ClassName.get(List.class);
    final var operandType =
      ClassName.get(packageName, "SMOperand");
    return ParameterizedTypeName.get(listName, operandType);
  }
}
