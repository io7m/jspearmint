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

package com.io7m.jspearmint.cmdline.internal;

import com.beust.jcommander.Parameters;
import com.io7m.claypot.core.CLPAbstractCommand;
import com.io7m.claypot.core.CLPCommandContextType;

import java.io.InputStream;
import java.util.Properties;

/**
 * Show the application version.
 */

@Parameters(commandDescription = "Show the application version")
public final class SMCommandVersion extends CLPAbstractCommand
{
  /**
   * Construct a command.
   *
   * @param context The context
   */

  public SMCommandVersion(
    final CLPCommandContextType context)
  {
    super(context);
  }

  @Override
  public Status executeActual()
    throws Exception
  {
    try (var stream = versionStream()) {
      final var properties = new Properties();
      properties.load(stream);
      System.out.println(properties.getProperty("version"));
    }
    return Status.SUCCESS;
  }

  private static InputStream versionStream()
  {
    return SMCommandVersion.class.getResourceAsStream(
      "version.properties"
    );
  }

  @Override
  public String name()
  {
    return "version";
  }
}
