/*
 * Copyright © 2020 Mark Raynsford <code@io7m.com> https://io7m.com
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

package com.io7m.jspearmint.json_registry;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigInteger;
import java.util.List;

/**
 * An instruction definition.
 */

// CHECKSTYLE:OFF

@JsonDeserialize
public final class SMJSONInstruction
{
  @JsonProperty(
    required = true,
    value = "opname"
  )
  public String name;

  @JsonProperty(
    required = true,
    value = "class"
  )
  public String className;

  @JsonProperty(
    required = true,
    value = "opcode"
  )
  public BigInteger opcode = BigInteger.ZERO;

  @JsonProperty(
    required = true,
    value = "operands"
  )
  public List<SMJSONInstructionOperand> operands = List.of();

  @JsonProperty(
    required = false,
    value = "capabilities"
  )
  public List<String> capabilities = List.of();

  @JsonProperty(
    required = false,
    value = "version"
  )
  public String version = "";

  @JsonProperty(
    required = false,
    value = "lastVersion"
  )
  public String lastVersion = "";

  @JsonProperty(
    required = false,
    value = "extensions"
  )
  public List<String> extensions = List.of();

  public SMJSONInstruction()
  {

  }
}
