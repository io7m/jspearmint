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

package com.io7m.jspearmint.parser.api;

import com.io7m.immutables.styles.ImmutablesStyleType;
import org.immutables.value.Value;

/**
 * A parsed instruction.
 */

@ImmutablesStyleType
@Value.Immutable
public interface SMParsedHeaderType
{
  /**
   * @return The raw version number as it appeared in the header
   */

  long rawVersionNumber();

  /**
   * @return The generator magic number as it appeared in the header
   */

  long generatorMagicNumber();

  /**
   * @return The ID bound as it appeared in the header
   */

  long idBound();

  /**
   * @return The schema magic number as it appeared in the header
   */

  long schema();

  /**
   * @return The SPIR-V major version
   */

  default long versionMajor()
  {
    return ((this.rawVersionNumber() & 0x00ff0000L) >> 16) & 0xffL;
  }

  /**
   * @return The SPIR-V minor version
   */

  default long versionMinor()
  {
    return ((this.rawVersionNumber() & 0x0000ff00L) >> 8) & 0xffL;
  }
}
