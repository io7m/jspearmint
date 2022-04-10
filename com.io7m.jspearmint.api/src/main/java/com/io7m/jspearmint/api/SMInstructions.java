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

package com.io7m.jspearmint.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A registry of instructions.
 */

public final class SMInstructions
{
  private static final Map<Long, SMInstruction> BY_OPCODE =
    makeByOpCode();

  private SMInstructions()
  {

  }

  private static Map<Long, SMInstruction> makeByOpCode()
  {
    final var values = SMInstruction.values();
    final var byOpCode = new HashMap<Long, SMInstruction>(values.length);
    for (final var value : values) {
      byOpCode.put(Long.valueOf(value.value()), value);
    }
    return Map.copyOf(byOpCode);
  }

  /**
   * @param opCode The opcode
   *
   * @return The instruction associated with the opcode
   */

  public static Optional<SMInstruction> byOpCode(
    final long opCode)
  {
    return Optional.ofNullable(BY_OPCODE.get(Long.valueOf(opCode)));
  }
}
