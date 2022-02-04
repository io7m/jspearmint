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

package com.io7m.jspearmint.disassembly.vanilla.internal;

import com.io7m.jspearmint.analysis.SMIdentifiers;
import com.io7m.jspearmint.analysis.SMTypes;
import com.io7m.jspearmint.api.SMInstruction;
import com.io7m.jspearmint.api.SMInstructions;
import com.io7m.jspearmint.disassembly.api.SMDisassemblerConfiguration;
import com.io7m.jspearmint.disassembly.api.SMDisassemblerType;
import com.io7m.jspearmint.parser.api.SMParsedHeader;
import com.io7m.jspearmint.parser.api.SMParsedInstruction;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.io7m.jspearmint.api.SMOperandKind.SM_ID_RESULT;
import static com.io7m.jspearmint.api.SMOperandKind.SM_ID_RESULT_TYPE;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * A basic disassembler.
 */

public final class SMDisassembler implements SMDisassemblerType
{
  /**
   * A basic disassembler.
   */

  public SMDisassembler()
  {

  }

  private static String formatOffset(
    final SMParsedInstruction parsedInstruction)
  {
    return String.format(
      "0x%08x |",
      Long.valueOf(parsedInstruction.byteOffset()));
  }

  private static String formatRawOperand(final Long operand)
  {
    return String.format("$0x%08x", Long.valueOf(operand.longValue()));
  }

  private static void disassembleInstruction(
    final BufferedWriter writer,
    final SMOpDisassemblerContextType context,
    final SMOpDisassemblers disassemblers,
    final SMParsedInstruction parsedInstruction)
    throws IOException
  {
    final var instructionDefinitionOpt =
      SMInstructions.byOpCode(parsedInstruction.opCode());

    if (instructionDefinitionOpt.isEmpty()) {
      disassembleRaw(parsedInstruction, writer);
      return;
    }

    final var instructionDefinition = instructionDefinitionOpt.get();
    writer.append(formatOffset(parsedInstruction));
    writer.append(" ");
    writer.append(
      formatAssignment(instructionDefinitionOpt, parsedInstruction)
    );
    writer.append(" ");
    writer.append(instructionDefinition.spirName());

    final var operands =
      formatInstructionOperands(context, disassemblers, parsedInstruction);
    if (!operands.isEmpty()) {
      writer.append(" ");
      writer.append(operands);
    }

    writer.newLine();
    writer.flush();
  }

  private static String formatInstructionOperands(
    final SMOpDisassemblerContextType context,
    final SMOpDisassemblers disassemblers,
    final SMParsedInstruction parsedInstruction)
  {
    return String.join(
      " ", disassemblers.findDisassembler(parsedInstruction)
        .disassemble(context, parsedInstruction));
  }

  private static void disassembleRaw(
    final SMParsedInstruction parsedInstruction,
    final BufferedWriter writer)
    throws IOException
  {
    writer.append(formatOffset(parsedInstruction));
    writer.append(" ");
    writer.append(formatAssignment(Optional.empty(), parsedInstruction));
    writer.append(" ");
    writer.append("?");
    writer.append(" ");

    for (final var operand : parsedInstruction.operands()) {
      writer.append(formatRawOperand(operand));
      writer.append(" ");
    }

    writer.newLine();
    writer.flush();
  }

  private static String formatAssignment(
    final Optional<SMInstruction> instructionDefinitionOpt,
    final SMParsedInstruction parsedInstruction)
  {
    final List<Long> parsedOperands = parsedInstruction.operands();

    if (instructionDefinitionOpt.isPresent()) {
      final var instructionDefinition = instructionDefinitionOpt.get();
      final var definedOperands = instructionDefinition.operands();

      if (definedOperands.size() >= 2) {
        final var resultType = definedOperands.get(0);
        final var resultId = definedOperands.get(1);
        if (resultType.kind() == SM_ID_RESULT_TYPE && resultId.kind() == SM_ID_RESULT) {
          final var idValue = parsedOperands.get(1);
          return String.format("%12s", String.format("%%%d =", idValue));
        }
      }

      if (definedOperands.size() >= 1) {
        final var resultId = definedOperands.get(0);
        if (resultId.kind() == SM_ID_RESULT) {
          final var idValue = parsedOperands.get(0);
          return String.format("%12s", String.format("%%%d =", idValue));
        }
      }
    }

    return String.format("%-12s", "");
  }

  @Override
  public void close()
  {

  }

  @Override
  public void disassemble(
    final SMDisassemblerConfiguration configuration,
    final SMParsedHeader header,
    final List<SMParsedInstruction> instructions,
    final OutputStream out)
    throws IOException
  {
    Objects.requireNonNull(configuration, "configuration");
    Objects.requireNonNull(header, "header");
    Objects.requireNonNull(instructions, "instructions");
    Objects.requireNonNull(out, "out");

    final var identifiers = SMIdentifiers.of(header, instructions);
    final var types = SMTypes.of(header, instructions);
    final var disassemblers = SMOpDisassemblers.create();

    final SMOpDisassemblerContextType context =
      new SMOpDisassemblerContext(identifiers, types, configuration);

    try (var writer = new BufferedWriter(new OutputStreamWriter(out, UTF_8))) {
      writer.append("; SPIR-V");
      writer.newLine();

      writer.append(String.format(
        "; Version %s.%s",
        Long.valueOf(header.versionMajor()),
        Long.valueOf(header.versionMinor())
      ));
      writer.newLine();

      writer.append(String.format(
        "; Generator: 0x%s",
        Long.toUnsignedString(header.generatorMagicNumber(), 16)
      ));
      writer.newLine();

      writer.append(String.format(
        "; Bound: %s",
        Long.valueOf(header.idBound())
      ));
      writer.newLine();

      writer.append(String.format(
        "; Schema: %s",
        Long.valueOf(header.schema())
      ));
      writer.newLine();
      writer.newLine();

      for (final var parsedInstruction : instructions) {
        disassembleInstruction(
          writer,
          context,
          disassemblers,
          parsedInstruction
        );
      }
    }
  }
}
