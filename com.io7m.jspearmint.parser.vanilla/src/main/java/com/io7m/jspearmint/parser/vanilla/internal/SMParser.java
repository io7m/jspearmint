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

package com.io7m.jspearmint.parser.vanilla.internal;

import com.io7m.jbssio.api.BSSReaderSequentialType;
import com.io7m.jspearmint.parser.api.SMParseException;
import com.io7m.jspearmint.parser.api.SMParsedInstruction;
import com.io7m.jspearmint.parser.api.SMParserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class SMParser implements SMParserType
{
  private static final Logger LOGGER =
    LoggerFactory.getLogger(SMParser.class);

  private static final byte[] MAGIC_BIG = {
    (byte) 0x07,
    (byte) 0x23,
    (byte) 0x02,
    (byte) 0x03,
  };

  private static final byte[] MAGIC_LITTLE = {
    (byte) 0x03,
    (byte) 0x02,
    (byte) 0x23,
    (byte) 0x07,
  };

  private final BSSReaderSequentialType reader;
  private final boolean bigEndian;
  private final long version;
  private final long generatorMagic;
  private final long bound;
  private final long reserved;
  private final long versionMajor;
  private final long versionMinor;

  private SMParser(
    final BSSReaderSequentialType inReader,
    final boolean inBigEndian,
    final long inVersion,
    final long inGeneratorMagic,
    final long inBound,
    final long inReserved,
    final long inVersionMajor,
    final long inVersionMinor)
  {
    this.reader = Objects.requireNonNull(inReader, "reader");
    this.bigEndian = inBigEndian;
    this.version = inVersion;
    this.generatorMagic = inGeneratorMagic;
    this.bound = inBound;
    this.reserved = inReserved;
    this.versionMajor = inVersionMajor;
    this.versionMinor = inVersionMinor;
  }

  public static SMParserType create(
    final BSSReaderSequentialType reader)
    throws IOException, SMParseException
  {
    final boolean bigEndian =
      determineEndianness(reader);

    final var rawVersion =
      readWord(reader, "version", bigEndian);
    final var generatorMagic =
      readWord(reader, "generatorMagic", bigEndian);
    final var bound =
      readWord(reader, "bound", bigEndian);
    final var reserved =
      readWord(reader, "reserved", bigEndian);

    final long versionMajor =
      ((rawVersion & 0x00ff0000L) >> 16) & 0xffL;
    final long versionMinor =
      ((rawVersion & 0x0000ff00L) >> 8) & 0xffL;

    return new SMParser(
      reader,
      bigEndian,
      rawVersion,
      generatorMagic,
      bound,
      reserved,
      versionMajor,
      versionMinor
    );
  }

  private static boolean determineEndianness(
    final BSSReaderSequentialType reader)
    throws IOException, SMParseException
  {
    final var magic = new byte[4];
    reader.readBytes("magicNumber", magic);

    final boolean bigEndian;
    if (Arrays.equals(magic, MAGIC_BIG)) {
      bigEndian = true;
    } else if (Arrays.equals(magic, MAGIC_LITTLE)) {
      bigEndian = false;
    } else {
      throw new SMParseException(
        String.format(
          "Unrecognized magic number: 0x%02x%02x%02x%02x",
          Byte.valueOf(magic[0]),
          Byte.valueOf(magic[1]),
          Byte.valueOf(magic[2]),
          Byte.valueOf(magic[3])
        ),
        reader.uri(),
        BigInteger.valueOf(reader.offsetCurrentAbsolute())
      );
    }

    LOGGER.debug("file is big-endian: {}", Boolean.valueOf(bigEndian));
    return bigEndian;
  }

  private static long readWord(
    final BSSReaderSequentialType reader,
    final String name,
    final boolean bigEndian)
    throws IOException
  {
    if (bigEndian) {
      return reader.readU32BE(name);
    }
    return reader.readU32LE(name);
  }

  private long readWord(
    final String name)
    throws IOException
  {
    if (this.bigEndian) {
      return this.reader.readU32BE(name);
    }
    return this.reader.readU32LE(name);
  }

  @Override
  public long rawVersionNumber()
  {
    return this.version;
  }

  @Override
  public long versionMajor()
  {
    return this.versionMajor;
  }

  @Override
  public long versionMinor()
  {
    return this.versionMinor;
  }

  @Override
  public long generatorMagicNumber()
  {
    return this.generatorMagic;
  }

  @Override
  public long idBound()
  {
    return this.bound;
  }

  @Override
  public Optional<SMParsedInstruction> parseNextInstruction()
    throws SMParseException
  {
    try {
      final long instructionHeader;
      try {
        instructionHeader = this.readWord("instructionHeader");
      } catch (final EOFException e) {
        return Optional.empty();
      }

      final var wordCount =
        ((instructionHeader & 0xFFFF0000L) >> 16) & 0xFFFFL;
      final var opCode =
        (instructionHeader & 0x0000FFFFL);

      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(
          "instruction 0x{} ({} words)",
          Long.toUnsignedString(opCode, 16),
          Long.valueOf(wordCount)
        );
      }

      final var operands = new ArrayList<Long>((int) wordCount);
      for (var index = 1L; index < wordCount; ++index) {
        operands.add(Long.valueOf(this.readWord("instructionOperand")));
      }

      return Optional.of(
        SMParsedInstruction.builder()
          .setOpCode(opCode)
          .setWordCount(wordCount)
          .setOperands(operands)
          .build()
      );
    } catch (final IOException e) {
      throw new SMParseException(
        e.getMessage(),
        e,
        this.reader.uri(),
        BigInteger.valueOf(this.reader.offsetCurrentAbsolute())
      );
    }
  }

  @Override
  public List<SMParsedInstruction> parseAllInstructions()
    throws SMParseException
  {
    final var instructions = new ArrayList<SMParsedInstruction>();

    while (true) {
      final var result = this.parseNextInstruction();
      if (result.isPresent()) {
        instructions.add(result.get());
      } else {
        break;
      }
    }
    return List.copyOf(instructions);
  }

  @Override
  public void close()
    throws IOException
  {
    this.reader.close();
  }
}
