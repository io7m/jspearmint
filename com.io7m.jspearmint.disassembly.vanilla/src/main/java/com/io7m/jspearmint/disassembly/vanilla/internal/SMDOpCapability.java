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

import com.io7m.jspearmint.api.SMCapability;
import com.io7m.jspearmint.api.SMInstruction;
import com.io7m.jspearmint.parser.api.SMParsedInstruction;

import java.util.List;

/**
 * OpCapability.
 */

public final class SMDOpCapability
  extends SMDAbstractOpDisassembler
{
  /**
   * Construct an op.
   */

  public SMDOpCapability()
  {
    super(SMInstruction.SM_OP_CAPABILITY);
  }

  @Override
  protected List<String> disassembleActual(
    final SMOpDisassemblerContextType context,
    final SMParsedInstruction parsedInstruction)
  {
    final var capability =
      SMCapability.ofInteger(parsedInstruction.operands().get(0).intValue());

    return List.of(capability.spirName());
  }
}
