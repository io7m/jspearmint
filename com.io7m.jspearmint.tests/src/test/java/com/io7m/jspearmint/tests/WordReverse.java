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

package com.io7m.jspearmint.tests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WordReverse
{
  private WordReverse()
  {

  }

  public static void main(final String[] args)
    throws IOException
  {
    try (var fileIn = Files.newInputStream(Paths.get(args[0]))) {
      try (var fileOut = Files.newOutputStream(Paths.get(args[1]))) {
        final var buffer = new byte[4];
        final var swap = new byte[4];
        while (true) {
          final var r = fileIn.read(buffer);
          if (r <= 0) {
            break;
          }

          swap[0] = buffer[3];
          swap[1] = buffer[2];
          swap[2] = buffer[1];
          swap[3] = buffer[0];
          fileOut.write(swap);
        }
      }
    }
  }
}
