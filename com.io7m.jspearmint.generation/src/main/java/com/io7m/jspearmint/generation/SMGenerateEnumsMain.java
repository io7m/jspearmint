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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.io7m.jspearmint.json_registry.SMJSONOperandKind;
import com.squareup.javapoet.JavaFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * The main program that generates enums.
 */

public final class SMGenerateEnumsMain
{
  private static final Logger LOG =
    LoggerFactory.getLogger(SMGenerateEnumsMain.class);

  private SMGenerateEnumsMain()
  {

  }

  /**
   * Command-line entry point.
   *
   * @param args Command-line arguments
   *
   * @throws Exception On errors
   */

  public static void main(
    final String[] args)
    throws Exception
  {
    final var arguments = new Arguments();
    final var commander =
      JCommander.newBuilder()
        .programName("GenerateEnums")
        .addObject(arguments)
        .build();

    commander.parse(args);

    final var registry = SMSources.registry();
    final var packageName = "com.io7m.jspearmint.api";
    for (final SMJSONOperandKind kind : registry.operandKinds) {
      LOG.debug("kind: {}", kind.kind);

      if (kind.enumerants.isEmpty()) {
        continue;
      }

      final var typeSpec =
        SMEnumGeneration.generate(packageName, kind);
      final var fileBuilder =
        JavaFile.builder(packageName, typeSpec);

      final var javaFile = fileBuilder.build();
      Files.createDirectories(arguments.outputDirectory);
      final var classFile = String.format("%s.java", typeSpec.name);
      final var outputFile = arguments.outputDirectory.resolve(classFile);
      try (var output = Files.newBufferedWriter(outputFile, UTF_8)) {
        output.append(javaFile.toString());
        output.append("\n");
      }
    }
  }

  private static final class Arguments
  {
    @Parameter(
      names = "--outputDirectory",
      required = true
    )
    private Path outputDirectory;

    Arguments()
    {

    }
  }
}
