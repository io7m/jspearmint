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

import com.io7m.jspearmint.parser.api.SMParseException;
import com.io7m.jspearmint.parser.api.SMParserProviderType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;

import static com.io7m.jspearmint.tests.SMJSONTestDirectories.createTempDirectory;
import static com.io7m.jspearmint.tests.SMJSONTestDirectories.resourceStreamOf;

public abstract class SMParserContract
{
  protected abstract SMParserProviderType parsers();

  private Path directory;

  @BeforeEach
  public final void setup()
    throws IOException
  {
    this.directory = createTempDirectory();
  }

  /**
   * An empty file immediately results in an exception.
   *
   * @throws Exception On errors
   */

  @Test
  public final void emptyFileCannotBeParsed()
    throws Exception
  {
    Assertions.assertThrows(SMParseException.class, () -> {
      try (var stream = this.resource("empty.sv")) {
        try (var parser = this.parsers()
          .create(URI.create("urn:unknown"), stream)) {

        }
      }
    });
  }

  /**
   * The smallest possible big-endian file that can be parsed.
   *
   * @throws Exception On errors
   */

  @Test
  public final void bigEndianTrivialFileIsParsed()
    throws Exception
  {
    try (var stream = this.resource("trivialBig.sv")) {
      try (var parser = this.parsers()
        .create(URI.create("urn:unknown"), stream)) {
        final var header = parser.header();
        Assertions.assertEquals(0L, header.generatorMagicNumber());
        Assertions.assertEquals(0L, header.idBound());
        Assertions.assertEquals(1L, header.versionMajor());
        Assertions.assertEquals(3L, header.versionMinor());
        Assertions.assertNotEquals(0L, header.rawVersionNumber());
      }
    }
  }

  /**
   * The smallest possible little-endian file that can be parsed.
   *
   * @throws Exception On errors
   */

  @Test
  public final void littleEndianTrivialFileIsParsed()
    throws Exception
  {
    try (var stream = this.resource("trivialLittle.sv")) {
      try (var parser = this.parsers()
        .create(URI.create("urn:unknown"), stream)) {
        final var header = parser.header();
        Assertions.assertEquals(0L, header.generatorMagicNumber());
        Assertions.assertEquals(0L, header.idBound());
        Assertions.assertEquals(1L, header.versionMajor());
        Assertions.assertEquals(3L, header.versionMinor());
        Assertions.assertNotEquals(0L, header.rawVersionNumber());
      }
    }
  }

  /**
   * A bad magic number can't be parsed.
   *
   * @throws Exception On errors
   */

  @Test
  public final void badMagicNumberFails()
    throws Exception
  {
    Assertions.assertThrows(SMParseException.class, () -> {
      try (var stream = this.resource("badMagicNumber.sv")) {
        try (var parser = this.parsers()
          .create(URI.create("urn:unknown"), stream)) {
        }
      }
    });
  }

  /**
   * The clip-space triangle shader is parsed correctly.
   *
   * @throws Exception On errors
   */

  @Test
  public final void clipSpaceTriangleShaderFileIsParsed()
    throws Exception
  {
    try (var stream = this.resource("clip_space_triangle.vert.spv")) {
      try (var parser = this.parsers()
        .create(URI.create("urn:unknown"), stream)) {
        final var header = parser.header();
        Assertions.assertEquals(0x80007L, header.generatorMagicNumber());
        Assertions.assertEquals(33L, header.idBound());
        Assertions.assertEquals(1L, header.versionMajor());
        Assertions.assertEquals(0L, header.versionMinor());
        Assertions.assertNotEquals(0L, header.rawVersionNumber());

        final var instructions = parser.parseAllInstructions();
        Assertions.assertEquals(57, instructions.size());
        Assertions.assertEquals(0x11L, instructions.get(0).opCode());
        Assertions.assertEquals(0x38L, instructions.get(instructions.size() - 1).opCode());
      }
    }
  }

  /**
   * The clip-space triangle (big endian) shader is parsed correctly.
   *
   * @throws Exception On errors
   */

  @Test
  public final void clipSpaceTriangleBigEndianShaderFileIsParsed()
    throws Exception
  {
    try (var stream = this.resource("clip_space_triangle.vert_big.spv")) {
      try (var parser = this.parsers()
        .create(URI.create("urn:unknown"), stream)) {
        final var header = parser.header();
        Assertions.assertEquals(0x80007L, header.generatorMagicNumber());
        Assertions.assertEquals(33L, header.idBound());
        Assertions.assertEquals(1L, header.versionMajor());
        Assertions.assertEquals(0L, header.versionMinor());
        Assertions.assertNotEquals(0L, header.rawVersionNumber());

        final var instructions = parser.parseAllInstructions();
        Assertions.assertEquals(57, instructions.size());
        Assertions.assertEquals(0x11L, instructions.get(0).opCode());
        Assertions.assertEquals(0x38L, instructions.get(instructions.size() - 1).opCode());
      }
    }
  }

  /**
   * A truncated file can't be parsed.
   *
   * @throws Exception On errors
   */

  @Test
  public final void clipSpaceTriangleShaderFileIsTruncated()
    throws Exception
  {
    Assertions.assertThrows(SMParseException.class, () -> {
      try (var stream = this.resource("clip_space_triangle_truncated.spv")) {
        try (var parser = this.parsers()
          .create(URI.create("urn:unknown"), stream)) {
          parser.parseAllInstructions();
        }
      }
    });
  }

  private InputStream resource(final String name)
    throws IOException
  {
    return resourceStreamOf(SMParserContract.class, this.directory, name);
  }
}
