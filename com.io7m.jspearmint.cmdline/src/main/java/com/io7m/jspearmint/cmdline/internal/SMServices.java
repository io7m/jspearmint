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

package com.io7m.jspearmint.cmdline.internal;

import com.io7m.jspearmint.disassembly.api.SMDisassemblerProviderType;
import com.io7m.jspearmint.parser.api.SMParserProviderType;

import java.util.Iterator;
import java.util.ServiceLoader;

final class SMServices
{
  private SMServices()
  {

  }

  public static SMParserProviderType findParsers()
  {
    final ServiceLoader<SMParserProviderType> loader =
      ServiceLoader.load(SMParserProviderType.class);
    final Iterator<SMParserProviderType> serviceIter =
      loader.iterator();

    while (serviceIter.hasNext()) {
      return serviceIter.next();
    }

    throw new IllegalStateException(noServicesMessage());
  }

  public static SMDisassemblerProviderType findDisassemblers()
  {
    final ServiceLoader<SMDisassemblerProviderType> loader =
      ServiceLoader.load(SMDisassemblerProviderType.class);
    final Iterator<SMDisassemblerProviderType> serviceIter =
      loader.iterator();

    while (serviceIter.hasNext()) {
      return serviceIter.next();
    }

    throw new IllegalStateException(noServicesMessage());
  }

  private static String noServicesMessage()
  {
    return String.format(
      "No available services of type: %s",
      SMParserProviderType.class.getCanonicalName()
    );
  }
}
