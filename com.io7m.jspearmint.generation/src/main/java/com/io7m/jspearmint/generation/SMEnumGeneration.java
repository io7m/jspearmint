/*
 * Copyright © 2020 Mark Raynsford <code@io7m.com> http://io7m.com
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

import java.util.Objects;

import static com.io7m.jspearmint.generation.SMInstructionEnumGeneration.transformEnumConstantName;
import static com.squareup.javapoet.TypeName.INT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

public final class SMEnumGeneration
{
  private SMEnumGeneration()
  {

  }

  public static TypeSpec generate(
    final String packageName,
    final SMJSONOperandKind kind)
  {
    Objects.requireNonNull(kind, "operandKind");

    final var className =
      String.format("SM%s", kind.kind);

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
          "$L",
          enumConstant.value
        ).addJavadoc(enumConstant.enumerant)
          .build()
      );
    }
    typeBuilder.addField(generateValueField());
    typeBuilder.addMethod(generateEnumConstructor());
    typeBuilder.addMethod(generateValueMethod());
    return typeBuilder.build();
  }

  private static FieldSpec generateValueField()
  {
    return FieldSpec.builder(INT, "value", FINAL, PRIVATE)
      .build();
  }

  private static MethodSpec generateValueMethod()
  {
    return MethodSpec.methodBuilder("value")
      .addAnnotation(Override.class)
      .addModifiers(PUBLIC)
      .returns(INT)
      .addCode("return this.value;")
      .build();
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
      .addParameter(INT, "value", FINAL)
      .addCode(
        CodeBlock.builder()
          .add("this.value = value;")
          .build()
      ).build();
  }
}
