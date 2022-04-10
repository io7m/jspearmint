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

import com.io7m.jspearmint.cmdline.Main;
import com.io7m.jspearmint.cmdline.MainExitless;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.util.stream.Stream;

public final class SMCommandHelpTest
{
  @Test
  public void helpOK()
    throws IOException
  {
    MainExitless.main(
      new String[]{
        "help"
      }
    );
  }

  @Test
  public void helpHelpOK()
    throws IOException
  {
    MainExitless.main(
      new String[]{
        "help",
        "help"
      }
    );
  }

  @TestFactory
  public Stream<DynamicTest> helpVMHelpOK()
  {
    return new Main(new String[]{})
      .commandNames()
      .map(name -> DynamicTest.dynamicTest(
        String.format("help %s", name),
        () -> {
          MainExitless.main(
            new String[]{
              "help",
              name
            }
          );
        }
      ));
  }
}
