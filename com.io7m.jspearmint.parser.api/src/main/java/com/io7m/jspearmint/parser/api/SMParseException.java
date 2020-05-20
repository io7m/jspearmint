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

import java.math.BigInteger;
import java.net.URI;
import java.util.Objects;

/**
 * An exception encountered during parsing.
 */

public final class SMParseException extends Exception
{
  private final URI source;
  private final BigInteger offset;

  /**
   * Construct an exception.
   *
   * @param message  The error message
   * @param inSource The source URI
   * @param inOffset The byte offset of the error
   */

  public SMParseException(
    final String message,
    final URI inSource,
    final BigInteger inOffset)
  {
    super(message);
    this.source = Objects.requireNonNull(inSource, "source");
    this.offset = Objects.requireNonNull(inOffset, "offset");
  }

  /**
   * Construct an exception.
   *
   * @param message  The error message
   * @param inSource The source URI
   * @param cause    The cause
   * @param inOffset The byte offset of the error
   */

  public SMParseException(
    final String message,
    final Throwable cause,
    final URI inSource,
    final BigInteger inOffset)
  {
    super(message, cause);
    this.source = Objects.requireNonNull(inSource, "source");
    this.offset = Objects.requireNonNull(inOffset, "offset");
  }

  /**
   * Construct an exception.
   *
   * @param inSource The source URI
   * @param cause    The cause
   * @param inOffset The byte offset of the error
   */

  public SMParseException(
    final Throwable cause,
    final URI inSource,
    final BigInteger inOffset)
  {
    super(cause);
    this.source = Objects.requireNonNull(inSource, "source");
    this.offset = Objects.requireNonNull(inOffset, "offset");
  }

  /**
   * @return The source URI
   */

  public URI source()
  {
    return this.source;
  }

  /**
   * @return The byte offset of the error
   */

  public BigInteger offset()
  {
    return this.offset;
  }
}
