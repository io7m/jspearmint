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
import static javax.lang.model.element.Modifier.STATIC;

/**
 * Functions to generate enum declarations.
 */

public final class SMEnumGeneration
{
  private SMEnumGeneration()
  {

  }

  /**
   * Generate enum types.
   *
   * @param packageName The package name
   * @param kind        The operand kind
   *
   * @return An enum type
   */

  public static TypeSpec generate(
    final String packageName,
    final SMJSONOperandKind kind)
  {
    Objects.requireNonNull(kind, "operandKind");

    final var className =
      String.format("SM%s", kind.kind);
    final var qualifiedName =
      ClassName.get(packageName, className);

    final TypeSpec.Builder typeBuilder =
      TypeSpec.enumBuilder(className)
        .addModifiers(PUBLIC);

    switch (kind.category) {
      case "ValueEnum": {
        typeBuilder.addSuperinterface(
          ClassName.get(packageName, "SMValueEnumType")
        );
        break;
      }
      case "BitEnum": {
        typeBuilder.addSuperinterface(
          ClassName.get(packageName, "SMBitEnumType")
        );
        break;
      }
      default: {
        typeBuilder.addSuperinterface(
          ClassName.get(packageName, "SMEnumType")
        );
      }
    }

    typeBuilder.addJavadoc(generateClassJavadoc(kind));
    for (final var enumConstant : kind.enumerants) {
      typeBuilder.addEnumConstant(
        transformEnumConstantName(enumConstant.enumerant),
        TypeSpec.anonymousClassBuilder(
            "$L,$S",
            enumConstant.value,
            enumConstant.enumerant
          ).addJavadoc(enumConstant.enumerant)
          .build()
      );
    }

    typeBuilder.addFields(generateValueFields());
    typeBuilder.addMethod(generateEnumConstructor());
    typeBuilder.addMethods(generateValueMethods(qualifiedName));
    return typeBuilder.build();
  }

  private static List<FieldSpec> generateValueFields()
  {
    return List.of(
      FieldSpec.builder(INT, "value", FINAL, PRIVATE).build(),
      FieldSpec.builder(String.class, "spirName", FINAL, PRIVATE).build()
    );
  }

  private static List<MethodSpec> generateValueMethods(
    final ClassName thisType)
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
        .build(),

      MethodSpec.methodBuilder("ofInteger")
        .addModifiers(PUBLIC, STATIC)
        .addParameter(INT, "x", FINAL)
        .returns(thisType)
        .beginControlFlow("for (var v : $L.values())\n", thisType)
        .beginControlFlow("if (v.value() == x)")
        .addCode("return v;")
        .endControlFlow()
        .endControlFlow()
        .addCode(
          "throw new IllegalArgumentException($S + x);\n",
          String.format("Unrecognized %s value: ", thisType.simpleName()))
        .build()
    );
  }

  private static String generateClassJavadoc(
    final SMJSONOperandKind kind)
  {
    final var javadocBuilder = new StringBuilder(128);
    javadocBuilder.append("Kind: ");
    javadocBuilder.append(kind.kind);
    javadocBuilder.append("\n");
    javadocBuilder.append("Category: ");
    javadocBuilder.append(kind.category);
    javadocBuilder.append("\n");

    if (kind.doc != null) {
      javadocBuilder.append(kind.doc);
    }
    return javadocBuilder.toString();
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
