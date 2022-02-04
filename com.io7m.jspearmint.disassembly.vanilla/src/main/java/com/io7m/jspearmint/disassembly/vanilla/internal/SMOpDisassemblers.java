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

import com.io7m.jspearmint.api.SMInstruction;
import com.io7m.jspearmint.api.SMInstructions;
import com.io7m.jspearmint.parser.api.SMParsedInstruction;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

final class SMOpDisassemblers
{
  private static final List<SMDAbstractOpDisassembler> OP_DISASSEMBLERS =
    List.of(
      new SMDOpAccessChain(),
      new SMDOpCapability(),
      new SMDOpCompositeConstruct(),
      new SMDOpCompositeExtract(),
      new SMDOpConstant(),
      new SMDOpDecorate(),
      new SMDOpEntryPoint(),
      new SMDOpExtInstImport(),
      new SMDOpFunction(),
      new SMDOpFunctionEnd(),
      new SMDOpLabel(),
      new SMDOpLoad(),
      new SMDOpMemberDecorate(),
      new SMDOpMemberName(),
      new SMDOpMemoryModel(),
      new SMDOpName(),
      new SMDOpReturn(),
      new SMDOpSource(),
      new SMDOpStore(),
      new SMDOpTypeFloat(),
      new SMDOpTypeFunction(),
      new SMDOpTypeInt(),
      new SMDOpTypePointer(),
      new SMDOpTypeStruct(),
      new SMDOpTypeVector(),
      new SMDOpTypeVoid(),
      new SMDOpVariable()
    );

  private final Map<SMInstruction, SMDAbstractOpDisassembler> disassemblers;
  private final SMRawDisassembler rawDisassembler;

  private SMOpDisassemblers(
    final Map<SMInstruction, SMDAbstractOpDisassembler> inDisassemblers)
  {
    this.disassemblers =
      Objects.requireNonNull(inDisassemblers, "disassemblers");
    this.rawDisassembler =
      new SMRawDisassembler();
  }

  public static SMOpDisassemblers create()
  {
    final Map<SMInstruction, SMDAbstractOpDisassembler> disassemblers =
      OP_DISASSEMBLERS.stream().collect(collectToMap());

    return new SMOpDisassemblers(disassemblers);
  }

  private static Collector<SMDAbstractOpDisassembler, ?, Map<SMInstruction, SMDAbstractOpDisassembler>> collectToMap()
  {
    return Collectors.toMap(
      SMDAbstractOpDisassembler::instruction,
      o -> o
    );
  }

  public SMOpDisassemblerType findDisassembler(
    final SMParsedInstruction parsedInstruction)
  {
    return SMInstructions.byOpCode(parsedInstruction.opCode())
      .flatMap(i -> Optional.ofNullable(this.disassemblers.get(i)))
      .map(o -> (SMOpDisassemblerType) o)
      .orElse(this.rawDisassembler);
  }
}
