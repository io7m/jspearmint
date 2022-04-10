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

package com.io7m.jspearmint.parser.vanilla.internal;

import com.io7m.jbssio.api.BSSReaderSequentialType;
import com.io7m.jspearmint.parser.api.SMParseException;
import com.io7m.jspearmint.parser.api.SMParsedHeader;
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

/**
 * The default parser implementation.
 */

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
  private final SMParsedHeader header;

  private SMParser(
    final BSSReaderSequentialType inReader,
    final boolean inBigEndian,
    final SMParsedHeader inHeader)
  {
    this.reader = Objects.requireNonNull(inReader, "reader");
    this.bigEndian = inBigEndian;
    this.header = Objects.requireNonNull(inHeader, "inHeader");
  }

  /**
   * Create a parser.
   *
   * @param reader The input reader
   *
   * @return A new parser
   *
   * @throws IOException      On errors
   * @throws SMParseException On errors
   */

  public static SMParser create(
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

    final var header =
      SMParsedHeader.builder()
        .setGeneratorMagicNumber(generatorMagic)
        .setIdBound(bound)
        .setRawVersionNumber(rawVersion)
        .setSchema(reserved)
        .build();

    return new SMParser(
      reader,
      bigEndian,
      header
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
  public SMParsedHeader header()
  {
    return this.header;
  }

  @Override
  public Optional<SMParsedInstruction> parseNextInstruction()
    throws SMParseException
  {
    try {
      final long offset = this.reader.offsetCurrentAbsolute();

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
          .setByteOffset(offset)
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
