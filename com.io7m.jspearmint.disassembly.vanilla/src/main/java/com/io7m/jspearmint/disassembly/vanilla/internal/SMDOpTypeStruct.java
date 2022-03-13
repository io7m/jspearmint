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

/**
 * OpTypeStruct
 */

public final class SMDOpTypeStruct
  extends SMDAbstractOpDisassembler
{
  /**
   * Construct an op.
   */
  public SMDOpTypeStruct()
  {
    super(SMInstruction.SM_OP_TYPE_STRUCT);
  }

  @Override
  protected List<String> disassembleActual(
    final SMOpDisassemblerContextType context,
    final SMParsedInstruction parsedInstruction)
  {
    return List.of(
      context.idString(parsedInstruction.operands().get(1))
    );
  }
}
