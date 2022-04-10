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

import com.io7m.jspearmint.disassembly.api.SMDisassemblerConfiguration;
import com.io7m.jspearmint.disassembly.api.SMDisassemblerProviderType;
import com.io7m.jspearmint.parser.api.SMParserProviderType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static com.io7m.jspearmint.tests.SMJSONTestDirectories.createTempDirectory;
import static com.io7m.jspearmint.tests.SMJSONTestDirectories.resourceStreamOf;
import static java.nio.charset.StandardCharsets.*;

public abstract class SMDisassemblersContract
{
  protected abstract SMDisassemblerProviderType disassemblers();

  protected abstract SMParserProviderType parsers();

  private Path directory;

  @BeforeEach
  public final void setup()
    throws IOException
  {
    this.directory = createTempDirectory();
  }

  @Test
  public void testExample0()
    throws Exception
  {
    try (var stream = this.resource("clip_space_triangle.vert.spv")) {
      try (var parser = this.parsers()
        .create(URI.create("urn:unknown"), stream)) {
        final var header = parser.header();
        final var instructions = parser.parseAllInstructions();

        final var byteOutput = new ByteArrayOutputStream();
        try (var disassembler = this.disassemblers().create()) {
          final var configuration =
            SMDisassemblerConfiguration.builder()
              .build();

          disassembler.disassemble(
            configuration,
            header,
            instructions,
            byteOutput
          );
        }

        Assertions.assertEquals(
          new String(byteOutput.toByteArray(), UTF_8),
          new String(this.resource("clip_space_triangle.vert.txt").readAllBytes(), UTF_8)
        );
      }
    }
  }

  private InputStream resource(final String name)
    throws IOException
  {
    return resourceStreamOf(SMDisassemblersContract.class, this.directory, name);
  }
}
