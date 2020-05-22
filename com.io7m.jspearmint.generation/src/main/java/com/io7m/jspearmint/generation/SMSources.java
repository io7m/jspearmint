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

package com.io7m.jspearmint.generation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.io7m.jspearmint.json_registry.SMJSONRegistry;

import java.io.IOException;
import java.io.InputStream;

public final class SMSources
{
  private SMSources()
  {

  }

  public static InputStream sources()
  {
    return SMGenerateInstructionEnumMain.class.getResourceAsStream(
      "/com/io7m/jspearmint/generation/spirv_headers/include/spirv/unified1/spirv.core.grammar.json"
    );
  }

  public static SMJSONRegistry registry()
    throws IOException
  {
    try (var stream = sources()) {
      final var mapper = new ObjectMapper();
      return mapper.readValue(stream, SMJSONRegistry.class);
    }
  }
}
