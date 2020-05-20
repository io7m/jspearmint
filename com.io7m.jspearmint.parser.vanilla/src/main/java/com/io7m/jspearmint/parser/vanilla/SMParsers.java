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

package com.io7m.jspearmint.parser.vanilla;

import com.io7m.jbssio.api.BSSReaderProviderType;
import com.io7m.jspearmint.parser.api.SMParseException;
import com.io7m.jspearmint.parser.api.SMParserProviderType;
import com.io7m.jspearmint.parser.api.SMParserType;
import com.io7m.jspearmint.parser.vanilla.internal.SMParser;
import org.osgi.service.component.annotations.Component;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.util.Objects;
import java.util.ServiceLoader;

/**
 * The default {@link SMParserProviderType} implementation.
 */

@Component(service = SMParserProviderType.class)
public final class SMParsers implements SMParserProviderType
{
  private final BSSReaderProviderType readers;

  /**
   * Construct a parser provider using the given BSSIO readers.
   *
   * @param inReaders The reader provider
   */

  public SMParsers(
    final BSSReaderProviderType inReaders)
  {
    this.readers = Objects.requireNonNull(inReaders, "readers");
  }

  /**
   * Construct a parser provider.
   */

  public SMParsers()
  {
    this(
      ServiceLoader.load(BSSReaderProviderType.class)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException(
          String.format(
            "No available implementations of type %s",
            BSSReaderProviderType.class.getCanonicalName())))
    );
  }

  @Override
  public SMParserType create(
    final URI uri,
    final InputStream stream)
    throws SMParseException
  {
    Objects.requireNonNull(uri, "uri");
    Objects.requireNonNull(stream, "stream");

    try {
      return SMParser.create(
        this.readers.createReaderFromStream(
          uri,
          stream,
          "root")
      );
    } catch (final IOException e) {
      throw new SMParseException(e, uri, BigInteger.ZERO);
    }
  }
}
