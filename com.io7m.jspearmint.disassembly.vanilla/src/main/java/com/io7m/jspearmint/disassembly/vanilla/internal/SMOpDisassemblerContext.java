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
import com.io7m.jspearmint.analysis.SMString;
import com.io7m.jspearmint.analysis.SMTypes;
import com.io7m.jspearmint.disassembly.api.SMDisassemblerConfiguration;

import java.util.Objects;

public final class SMOpDisassemblerContext
  implements SMOpDisassemblerContextType
{
  private final SMIdentifiers identifiers;
  private final SMDisassemblerConfiguration configuration;
  private final SMTypes types;

  SMOpDisassemblerContext(
    final SMIdentifiers inIdentifiers,
    final SMTypes inTypes,
    final SMDisassemblerConfiguration inConfiguration)
  {
    this.identifiers =
      Objects.requireNonNull(inIdentifiers, "identifiers");
    this.types =
      Objects.requireNonNull(inTypes, "inTypes");
    this.configuration =
      Objects.requireNonNull(inConfiguration, "configuration");
  }

  @Override
  public String idString(final Long x)
  {
    switch (this.configuration.identifiers()) {
      case RAW_NUMERIC: {
        return SMFormatting.idString(x);
      }
      case NAMED: {
        final String name = this.identifiers.idToName().get(x);
        final String id = SMFormatting.idString(x);
        if (name == null) {
          return id;
        }
        return String.format("%%%s", name);
      }
    }
    throw new IllegalStateException("Unreachable code");
  }

  @Override
  public String literal(final Long value)
  {
    return SMFormatting.literal(value);
  }

  @Override
  public String literalTyped(
    final Long type,
    final Long value)
  {
    final var idToType = this.types.idToType();
    final var typeValue = idToType.get(type);
    if (typeValue == null) {
      return "$?" + value;
    }

    switch (typeValue) {
      case SM_OP_TYPE_FLOAT:
        return SMFormatting.literalFloat(value);
      case SM_OP_TYPE_INT:
        return SMFormatting.literal(value);
      default:
        return "$?" + value;
    }
  }

  @Override
  public String quoteString(final SMString name)
  {
    return SMFormatting.quoteString(name);
  }
}
