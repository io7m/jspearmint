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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.io7m.jspearmint.json_registry.SMJSONRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;

import static com.io7m.jspearmint.tests.SMJSONTestDirectories.createTempDirectory;
import static com.io7m.jspearmint.tests.SMJSONTestDirectories.resourceStreamOf;

public final class SMJSONRegistryTest
{
  private Path directory;

  @BeforeEach
  public void setup()
    throws IOException
  {
    this.directory = createTempDirectory();
  }

  /**
   * The registry, when parsed, contains the expected data.
   *
   * @throws IOException On errors
   */

  @Test
  public void registryContainsExpectedData()
    throws IOException
  {
    try (var stream = resourceStreamOf(
      SMJSONRegistryTest.class,
      this.directory,
      "spirv_headers/include/spirv/unified1/spirv.core.grammar.json")) {

      final var mapper = new ObjectMapper();
      final var registry = mapper.readValue(stream, SMJSONRegistry.class);

      Assertions.assertEquals("0x07230203", registry.magicNumber);
      Assertions.assertEquals(BigInteger.ONE, registry.majorVersion);
      Assertions.assertEquals(BigInteger.valueOf(6L), registry.minorVersion);
      Assertions.assertEquals(667, registry.instructions.size());
      Assertions.assertEquals(26, registry.instructionClasses.size());
      Assertions.assertEquals(53, registry.operandKinds.size());
    }
  }
}
