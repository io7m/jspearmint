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

package com.io7m.jspearmint.analysis;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Functions over strings.
 */

public final class SMStrings
{
  private SMStrings()
  {

  }

  /**
   * Consume a UTF-8 string.
   *
   * @param operands The list of operands
   * @param index    The index
   *
   * @return A string
   */

  public static SMString consumeUTF8String(
    final List<Long> operands,
    final int index)
  {
    Objects.requireNonNull(operands, "operands");

    final var buffer =
      ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
    final var words =
      operands.subList(index, operands.size());

    try (var outputStream = new ByteArrayOutputStream(words.size() * 4)) {
      int wordsUsed = 1;
      for (final var word : words) {
        buffer.putInt(0, word.intValue());
        final byte byte0 = buffer.get(0);
        if ((int) byte0 == 0) {
          break;
        }
        outputStream.write(byte0);
        final byte byte1 = buffer.get(1);
        if ((int) byte1 == 0) {
          break;
        }
        outputStream.write(byte1);
        final byte byte2 = buffer.get(2);
        if ((int) byte2 == 0) {
          break;
        }
        outputStream.write(byte2);
        final byte byte3 = buffer.get(3);
        if ((int) byte3 == 0) {
          break;
        }
        outputStream.write(byte3);
        ++wordsUsed;
      }
      return SMString.builder()
        .setUsedWords(wordsUsed)
        .setText(makeString(outputStream))
        .build();
    } catch (final IOException e) {
      throw new IllegalStateException(e);
    }
  }

  private static String makeString(
    final ByteArrayOutputStream outputStream)
  {
    // CHECKSTYLE:OFF
    return outputStream.toString(UTF_8).trim();
    // CHECKSTYLE:ON
  }
}
