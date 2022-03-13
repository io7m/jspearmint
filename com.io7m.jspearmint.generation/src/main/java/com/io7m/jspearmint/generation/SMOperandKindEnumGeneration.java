/*
 * Copyright © 2020 Mark Raynsford <code@io7m.com> https://io7m.com
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

import com.io7m.jspearmint.json_registry.SMJSONOperandKind;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.List;
import java.util.Objects;

import static com.io7m.jspearmint.generation.SMInstructionEnumGeneration.transformEnumConstantName;
import static com.squareup.javapoet.TypeName.INT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * Functions to generate operand kind enums.
 */

public final class SMOperandKindEnumGeneration
{
  private SMOperandKindEnumGeneration()
  {

  }

  /**
   * Generate operand kind enums.
   *
   * @param packageName  The package name
   * @param operandKinds The operand kind
   *
   * @return An instruction enum
   */

  public static TypeSpec generate(
    final String packageName,
    final List<SMJSONOperandKind> operandKinds)
  {
    Objects.requireNonNull(operandKinds, "operandKinds");

    final var className =
      ClassName.get(packageName, "SMOperandKind");

    final TypeSpec.Builder typeBuilder =
      TypeSpec.enumBuilder(className)
        .addJavadoc("The kind of SPIR-V operands.")
        .addSuperinterface(ClassName.get(packageName, "SMEnumType"))
        .addModifiers(PUBLIC);

    for (int index = 0; index < operandKinds.size(); ++index) {
      final var operandKind = operandKinds.get(index);
      typeBuilder.addEnumConstant(
        transformEnumConstantName(operandKind.kind),
        TypeSpec.anonymousClassBuilder(
            "$L,$S",
            Integer.valueOf(index),
            operandKind.kind
          ).addJavadoc(operandJavaDoc(operandKind))
          .build()
      );
    }

    typeBuilder.addFields(generateValueFields());
    typeBuilder.addMethod(generateEnumConstructor());
    typeBuilder.addMethods(generateValueMethods());
    return typeBuilder.build();
  }

  private static String operandJavaDoc(
    final SMJSONOperandKind operandKind)
  {
    return String.format("%s: %s", operandKind.kind, docOrNothing(operandKind));
  }

  private static String docOrNothing(
    final SMJSONOperandKind operandKind)
  {
    if (operandKind.doc == null) {
      return "No documentation";
    }
    return operandKind.doc;
  }

  private static List<FieldSpec> generateValueFields()
  {
    return List.of(
      FieldSpec.builder(INT, "value", FINAL, PRIVATE)
        .build(),
      FieldSpec.builder(String.class, "spirName", FINAL, PRIVATE)
        .build()
    );
  }

  private static List<MethodSpec> generateValueMethods()
  {
    return List.of(
      MethodSpec.methodBuilder("value")
        .addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .returns(INT)
        .addCode("return this.value;")
        .build(),

      MethodSpec.methodBuilder("spirName")
        .addModifiers(PUBLIC)
        .returns(String.class)
        .addCode("return this.spirName;")
        .build()
    );
  }

  private static MethodSpec generateEnumConstructor()
  {
    return MethodSpec.constructorBuilder()
      .addParameter(INT, "inValue", FINAL)
      .addParameter(String.class, "inSpirName", FINAL)
      .addCode(
        CodeBlock.builder()
          .add("this.value = inValue;")
          .add("this.spirName = inSpirName;")
          .build()
      ).build();
  }
}
