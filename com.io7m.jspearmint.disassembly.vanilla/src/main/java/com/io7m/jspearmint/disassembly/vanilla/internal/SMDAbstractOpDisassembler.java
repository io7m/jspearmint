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

package com.io7m.jspearmint.disassembly.vanilla.internal;

import com.io7m.jspearmint.api.SMInstruction;
import com.io7m.jspearmint.parser.api.SMParsedInstruction;

import java.util.List;
import java.util.Objects;

abstract class SMDAbstractOpDisassembler implements SMOpDisassemblerType
{
  private final SMInstruction instruction;

  SMDAbstractOpDisassembler(
    final SMInstruction inInstructionDefinition)
  {
    this.instruction =
      Objects.requireNonNull(inInstructionDefinition, "instructionDefinition");
  }

  public final SMInstruction instruction()
  {
    return this.instruction;
  }

  @Override
  public final List<String> disassemble(
    final SMOpDisassemblerContextType context,
    final SMParsedInstruction parsedInstruction)
  {
    Objects.requireNonNull(context, "context");
    Objects.requireNonNull(parsedInstruction, "instruction");

    final int definedOpcode = this.instruction.value();
    final long parsedOpcode = parsedInstruction.opCode();
    if (parsedOpcode != definedOpcode) {
      throw new IllegalArgumentException(
        String.format(
          "Expected opcode %s but received %s",
          Long.toUnsignedString(parsedOpcode),
          Long.toUnsignedString(definedOpcode)
        )
      );
    }

    final int parsedCount = parsedInstruction.operands().size();
    final int minimumCount = this.instruction.minimumOperandCount();
    if (parsedCount < minimumCount) {
      throw new IllegalArgumentException(
        String.format(
          "Expected at least %s operands, but received %s",
          Integer.toUnsignedString(minimumCount),
          Integer.toUnsignedString(parsedCount)
        )
      );
    }

    return this.disassembleActual(context, parsedInstruction);
  }

  protected abstract List<String> disassembleActual(
    SMOpDisassemblerContextType context,
    SMParsedInstruction parsedInstruction
  );
}
