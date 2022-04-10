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

package com.io7m.jspearmint.analysis;

import com.io7m.jspearmint.api.SMInstruction;
import com.io7m.jspearmint.api.SMInstructions;
import com.io7m.jspearmint.parser.api.SMParsedHeader;
import com.io7m.jspearmint.parser.api.SMParsedInstruction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Functions to consume types from streams.
 */

public final class SMTypes
{
  private final Map<Long, SMInstruction> idToType;

  private SMTypes(
    final Map<Long, SMInstruction> inIdToType)
  {
    this.idToType = Objects.requireNonNull(inIdToType, "inIdToType");
  }

  /**
   * Consume types from a stream.
   *
   * @param header       The parsed header
   * @param instructions The instruction stream
   *
   * @return A set of types
   */

  public static SMTypes of(
    final SMParsedHeader header,
    final List<SMParsedInstruction> instructions)
  {
    Objects.requireNonNull(header, "header");
    Objects.requireNonNull(instructions, "instructions");

    final var idToType =
      new HashMap<Long, SMInstruction>(instructions.size());

    for (final var instruction : instructions) {
      final var definitionOpt = SMInstructions.byOpCode(instruction.opCode());
      if (definitionOpt.isEmpty()) {
        continue;
      }
      final var definition = definitionOpt.get();
      switch (definition) {
        case SM_OP_TYPE_INT:
        case SM_OP_TYPE_FUNCTION:
        case SM_OP_TYPE_POINTER:
        case SM_OP_TYPE_STRUCT:
        case SM_OP_TYPE_VECTOR:
        case SM_OP_TYPE_VOID:
        case SM_OP_TYPE_ARRAY:
        case SM_OP_TYPE_BOOL:
        case SM_OP_TYPE_FLOAT: {
          final var id = instruction.operands().get(0);
          idToType.put(id, definition);
          break;
        }
        default: {

        }
      }
    }

    return new SMTypes(
      Map.copyOf(idToType)
    );
  }

  /**
   * @return The type for the given ID
   */

  public Map<Long, SMInstruction> idToType()
  {
    return this.idToType;
  }
}
