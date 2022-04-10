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

package com.io7m.jspearmint.disassembly.vanilla.internal;

import com.io7m.jspearmint.parser.api.SMParsedInstruction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A disassembler that only prints numbers.
 */

public final class SMRawDisassembler implements SMOpDisassemblerType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(SMRawDisassembler.class);

  /**
   * A disassembler that only prints numbers.
   */

  public SMRawDisassembler()
  {

  }

  @Override
  public List<String> disassemble(
    final SMOpDisassemblerContextType context,
    final SMParsedInstruction instruction)
  {
    LOG.warn(
      "0x{}: unrecognized instruction: opcode {}",
      Long.toUnsignedString(instruction.byteOffset(), 16),
      Long.valueOf(instruction.opCode())
    );

    return instruction.operands()
      .stream()
      .map(id -> SMFormatting.literal(id.longValue()))
      .collect(Collectors.toList());
  }
}
