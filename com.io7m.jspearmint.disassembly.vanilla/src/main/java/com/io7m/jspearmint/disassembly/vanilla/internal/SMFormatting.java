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

import com.io7m.jspearmint.analysis.SMString;
import org.apache.commons.text.StringEscapeUtils;

/**
 * Functions to perform formatting.
 */

public final class SMFormatting
{
  private SMFormatting()
  {

  }

  /**
   * @param id The ID
   *
   * @return The string of the given ID
   */

  public static String idString(
    final Integer id)
  {
    return idString(id.intValue());
  }

  /**
   * @param id The ID
   *
   * @return The string of the given ID
   */

  public static String idString(
    final Long id)
  {
    return idString(id.longValue());
  }

  /**
   * @param id The ID
   *
   * @return The string of the given ID
   */

  public static String idString(
    final int id)
  {
    return "%" + id;
  }

  /**
   * @param id The ID
   *
   * @return The string of the given ID
   */

  public static String idString(
    final long id)
  {
    return "%" + id;
  }

  /**
   * @param text The text
   *
   * @return The quoted string of the given value
   */

  public static String quoteString(
    final String text)
  {
    return '"' + StringEscapeUtils.escapeJava(text) + '"';
  }

  /**
   * @param text The text
   *
   * @return The quoted string of the given value
   */

  public static String quoteString(
    final SMString text)
  {
    return quoteString(text.text());
  }

  /**
   * @param value The value
   *
   * @return The literal of the given value
   */

  public static String literal(final long value)
  {
    return "$" + Long.toUnsignedString(value);
  }

  /**
   * @param value The value
   *
   * @return The literal of the given value
   */

  public static String literal(final Long value)
  {
    return literal(value.longValue());
  }

  /**
   * @param value The value
   *
   * @return The literal of the given value
   */

  public static String literalFloat(final long value)
  {
    return "$" + Float.intBitsToFloat((int) value);
  }

  /**
   * @param value The value
   *
   * @return The literal of the given value
   */

  public static String literalFloat(final Long value)
  {
    return literalFloat(value.longValue());
  }
}
