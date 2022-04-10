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

import com.io7m.jspearmint.api.SMInstructions;
import com.io7m.jspearmint.parser.api.SMParsedHeader;
import com.io7m.jspearmint.parser.api.SMParsedInstruction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A set of identifiers.
 */

public final class SMIdentifiers
{
  private static final Logger LOG =
    LoggerFactory.getLogger(SMIdentifiers.class);

  private final Map<String, Long> nameToId;
  private final Map<Long, String> idToName;

  private SMIdentifiers(
    final Map<String, Long> inNameToId,
    final Map<Long, String> inIdToName)
  {
    this.nameToId = Objects.requireNonNull(inNameToId, "inNameToId");
    this.idToName = Objects.requireNonNull(inIdToName, "inIdToName");
  }

  /**
   * Collect identifiers.
   *
   * @param header       The parsed header
   * @param instructions The instructions
   *
   * @return A set of identifiers
   */

  public static SMIdentifiers of(
    final SMParsedHeader header,
    final List<SMParsedInstruction> instructions)
  {
    Objects.requireNonNull(header, "header");
    Objects.requireNonNull(instructions, "instructions");

    final var nameToId =
      new HashMap<String, Long>(instructions.size());
    final var idToName =
      new HashMap<Long, String>(instructions.size());

    for (final var instruction : instructions) {
      final var definitionOpt = SMInstructions.byOpCode(instruction.opCode());
      if (definitionOpt.isEmpty()) {
        continue;
      }
      final var definition = definitionOpt.get();
      switch (definition) {
        case SM_OP_NAME: {
          final var id = instruction.operands().get(0);
          var name = SMStrings.consumeUTF8String(instruction.operands(), 1);
          if (name.text().isEmpty()) {
            name = name.withText("_");
          }

          if (nameToId.containsKey(name.text())) {
            throw new IllegalArgumentException(
              String.format("Name redefinition: %s", name.text())
            );
          }

          nameToId.put(name.text(), id);
          idToName.put(id, name.text());
          break;
        }
        default: {

        }
      }
    }

    return new SMIdentifiers(
      Map.copyOf(nameToId),
      Map.copyOf(idToName)
    );
  }

  /**
   * @return A mapping of names to ids
   */

  public Map<String, Long> nameToId()
  {
    return this.nameToId;
  }

  /**
   * @return A mapping of ids to names
   */

  public Map<Long, String> idToName()
  {
    return this.idToName;
  }

}
