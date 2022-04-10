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

import com.io7m.jspearmint.analysis.SMString;
import com.io7m.jspearmint.api.SMAddressingModel;
import com.io7m.jspearmint.api.SMCapability;
import com.io7m.jspearmint.api.SMFunctionControl;
import com.io7m.jspearmint.api.SMMemoryAccess;
import com.io7m.jspearmint.api.SMMemoryModel;
import com.io7m.jspearmint.api.SMSourceLanguage;
import com.io7m.jspearmint.api.SMStorageClass;
import com.io7m.jspearmint.disassembly.vanilla.internal.SMDOpAccessChain;
import com.io7m.jspearmint.disassembly.vanilla.internal.SMDOpCapability;
import com.io7m.jspearmint.disassembly.vanilla.internal.SMDOpCompositeConstruct;
import com.io7m.jspearmint.disassembly.vanilla.internal.SMDOpCompositeExtract;
import com.io7m.jspearmint.disassembly.vanilla.internal.SMDOpConstant;
import com.io7m.jspearmint.disassembly.vanilla.internal.SMDOpExtInstImport;
import com.io7m.jspearmint.disassembly.vanilla.internal.SMDOpFunction;
import com.io7m.jspearmint.disassembly.vanilla.internal.SMDOpFunctionEnd;
import com.io7m.jspearmint.disassembly.vanilla.internal.SMDOpLabel;
import com.io7m.jspearmint.disassembly.vanilla.internal.SMDOpLoad;
import com.io7m.jspearmint.disassembly.vanilla.internal.SMDOpMemberName;
import com.io7m.jspearmint.disassembly.vanilla.internal.SMDOpMemoryModel;
import com.io7m.jspearmint.disassembly.vanilla.internal.SMDOpName;
import com.io7m.jspearmint.disassembly.vanilla.internal.SMDOpReturn;
import com.io7m.jspearmint.disassembly.vanilla.internal.SMDOpSource;
import com.io7m.jspearmint.disassembly.vanilla.internal.SMDOpStore;
import com.io7m.jspearmint.disassembly.vanilla.internal.SMDOpTypeFloat;
import com.io7m.jspearmint.disassembly.vanilla.internal.SMDOpTypeInt;
import com.io7m.jspearmint.disassembly.vanilla.internal.SMDOpTypePointer;
import com.io7m.jspearmint.disassembly.vanilla.internal.SMDOpTypeStruct;
import com.io7m.jspearmint.disassembly.vanilla.internal.SMDOpTypeVector;
import com.io7m.jspearmint.disassembly.vanilla.internal.SMDOpTypeVoid;
import com.io7m.jspearmint.disassembly.vanilla.internal.SMDOpVariable;
import com.io7m.jspearmint.disassembly.vanilla.internal.SMFormatting;
import com.io7m.jspearmint.disassembly.vanilla.internal.SMOpDisassemblerContextType;
import com.io7m.jspearmint.parser.api.SMParsedInstruction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static com.io7m.jspearmint.api.SMInstruction.*;

public final class SMOpDisassemblersTest
{
  private SMOpDisassemblerContextType context;

  @BeforeEach
  public void setup()
  {
    this.context = Mockito.mock(SMOpDisassemblerContextType.class);

    Mockito.when(
      this.context.idString(Mockito.any()))
      .thenAnswer(invocationOnMock -> {
        return "%" + invocationOnMock.getArgument(0);
      });

    Mockito.when(
      this.context.literal(Mockito.any()))
      .thenAnswer(invocationOnMock -> {
        return "$" + invocationOnMock.getArgument(0);
      });

    Mockito.when(
      this.context.quoteString(Mockito.any()))
      .thenAnswer(invocationOnMock -> {
        return SMFormatting.quoteString((SMString) invocationOnMock.getArgument(0));
      });
  }

  @Test
  public void opName()
  {
    final var instruction =
      createInstruction(SM_OP_NAME.value(), 0L, 0x434241L);
    final var tokens =
      new SMDOpName().disassemble(this.context, instruction);
    Assertions.assertEquals( "%0", tokens.get(0));
    Assertions.assertEquals( "\"ABC\"", tokens.get(1));
  }

  @Test
  public void opReturn()
  {
    final var instruction =
      createInstruction(SM_OP_RETURN.value());
    final var tokens =
      new SMDOpReturn().disassemble(this.context, instruction);
    Assertions.assertEquals( 0, tokens.size());
  }

  @Test
  public void opSource()
  {
    final var instruction =
      createInstruction(SM_OP_SOURCE.value(), SMSourceLanguage.SM_GLSL.value(), 450L);
    final var tokens =
      new SMDOpSource().disassemble(this.context, instruction);
    Assertions.assertEquals( "GLSL", tokens.get(0));
    Assertions.assertEquals( "$450", tokens.get(1));
    Assertions.assertEquals( 2, tokens.size());
  }

  @Test
  public void opLabel()
  {
    final var instruction =
      createInstruction(SM_OP_LABEL.value(), 0x5L);
    final var tokens =
      new SMDOpLabel().disassemble(this.context, instruction);
    Assertions.assertEquals( 0, tokens.size());
  }

  @Test
  public void opLoad()
  {
    final var instruction =
      createInstruction(SM_OP_LOAD.value(), 3L, 5L, 7L);
    final var tokens =
      new SMDOpLoad().disassemble(this.context, instruction);
    Assertions.assertEquals( "%3", tokens.get(0));
    Assertions.assertEquals( "%7", tokens.get(1));
    Assertions.assertEquals( 2, tokens.size());
  }

  @Test
  public void opStore()
  {
    final var instruction =
      createInstruction(SM_OP_STORE.value(), 3L, 5L, SMMemoryAccess.SM_ALIGNED.value());
    final var tokens =
      new SMDOpStore().disassemble(this.context, instruction);
    Assertions.assertEquals( "%3", tokens.get(0));
    Assertions.assertEquals( "%5", tokens.get(1));
    Assertions.assertEquals( "Aligned", tokens.get(2));
    Assertions.assertEquals( 3, tokens.size());
  }

  @Test
  public void opStoreNone()
  {
    final var instruction =
      createInstruction(SM_OP_STORE.value(), 3L, 5L);
    final var tokens =
      new SMDOpStore().disassemble(this.context, instruction);
    Assertions.assertEquals( "%3", tokens.get(0));
    Assertions.assertEquals( "%5", tokens.get(1));
    Assertions.assertEquals( "None", tokens.get(2));
    Assertions.assertEquals( 3, tokens.size());
  }

  @Test
  public void opTypeFloat()
  {
    final var instruction =
      createInstruction(SM_OP_TYPE_FLOAT.value(), 0L, 32L);
    final var tokens =
      new SMDOpTypeFloat().disassemble(this.context, instruction);
    Assertions.assertEquals( "$32", tokens.get(0));
    Assertions.assertEquals( 1, tokens.size());
  }

  @Test
  public void opTypeInt()
  {
    final var instruction =
      createInstruction(SM_OP_TYPE_INT.value(), 0L, 32L, 1L);
    final var tokens =
      new SMDOpTypeInt().disassemble(this.context, instruction);
    Assertions.assertEquals( "$32", tokens.get(0));
    Assertions.assertEquals( "$1", tokens.get(1));
    Assertions.assertEquals( 2, tokens.size());
  }

  @Test
  public void opTypeVoid()
  {
    final var instruction =
      createInstruction(SM_OP_TYPE_VOID.value(), 0L);
    final var tokens =
      new SMDOpTypeVoid().disassemble(this.context, instruction);
    Assertions.assertEquals( 0, tokens.size());
  }

  @Test
  public void opTypePointer()
  {
    final var instruction =
      createInstruction(SM_OP_TYPE_POINTER.value(), 0L, SMStorageClass.SM_INPUT.value(), 7L);
    final var tokens =
      new SMDOpTypePointer().disassemble(this.context, instruction);
    Assertions.assertEquals( "Input", tokens.get(0));
    Assertions.assertEquals( "%7", tokens.get(1));
    Assertions.assertEquals( 2, tokens.size());
  }

  @Test
  public void opTypeStruct()
  {
    final var instruction =
      createInstruction(SM_OP_TYPE_STRUCT.value(), 0L, 7L);
    final var tokens =
      new SMDOpTypeStruct().disassemble(this.context, instruction);
    Assertions.assertEquals( "%7", tokens.get(0));
    Assertions.assertEquals( 1, tokens.size());
  }

  @Test
  public void opTypeVector()
  {
    final var instruction =
      createInstruction(SM_OP_TYPE_VECTOR.value(), 0L, 7L, 3L);
    final var tokens =
      new SMDOpTypeVector().disassemble(this.context, instruction);
    Assertions.assertEquals( "%7", tokens.get(0));
    Assertions.assertEquals( "$3", tokens.get(1));
    Assertions.assertEquals( 2, tokens.size());
  }

  @Test
  public void opCapability()
  {
    final var instruction =
      createInstruction(SM_OP_CAPABILITY.value(), SMCapability.SM_SHADER.value());
    final var tokens =
      new SMDOpCapability().disassemble(this.context, instruction);
    Assertions.assertEquals( "Shader", tokens.get(0));
    Assertions.assertEquals( 1, tokens.size());
  }

  @Test
  public void opExtInstImport()
  {
    final var instruction =
      createInstruction(SM_OP_EXT_INST_IMPORT.value(), 0L, 0x434241L);
    final var tokens =
      new SMDOpExtInstImport().disassemble(this.context, instruction);
    Assertions.assertEquals( "\"ABC\"", tokens.get(0));
    Assertions.assertEquals( 1, tokens.size());
  }

  @Test
  public void opMemoryModel()
  {
    final var instruction =
      createInstruction(SM_OP_MEMORY_MODEL.value(),
                        SMAddressingModel.SM_LOGICAL.value(),
                        SMMemoryModel.SM_GLSL450.value());
    final var tokens =
      new SMDOpMemoryModel().disassemble(this.context, instruction);
    Assertions.assertEquals( "Logical", tokens.get(0));
    Assertions.assertEquals( "GLSL450", tokens.get(1));
    Assertions.assertEquals( 2, tokens.size());
  }

  @Test
  public void opMemberName()
  {
    final var instruction =
      createInstruction(SM_OP_MEMBER_NAME.value(),
                        8L,
                        0L,
                        0x434241L);
    final var tokens =
      new SMDOpMemberName().disassemble(this.context, instruction);
    Assertions.assertEquals( "%8", tokens.get(0));
    Assertions.assertEquals( "$0", tokens.get(1));
    Assertions.assertEquals( "\"ABC\"", tokens.get(2));
    Assertions.assertEquals( 3, tokens.size());
  }

  @Test
  public void opVariable()
  {
    final var instruction =
      createInstruction(SM_OP_VARIABLE.value(),
                        14L,
                        0L,
                        SMStorageClass.SM_OUTPUT.value());
    final var tokens =
      new SMDOpVariable().disassemble(this.context, instruction);
    Assertions.assertEquals( "%14", tokens.get(0));
    Assertions.assertEquals( "Output", tokens.get(1));
    Assertions.assertEquals( 2, tokens.size());
  }

  @Test
  public void opVariableExtras()
  {
    final var instruction =
      createInstruction(SM_OP_VARIABLE.value(),
                        14L,
                        0L,
                        SMStorageClass.SM_OUTPUT.value(),
                        33L);
    final var tokens =
      new SMDOpVariable().disassemble(this.context, instruction);
    Assertions.assertEquals( "%14", tokens.get(0));
    Assertions.assertEquals( "Output", tokens.get(1));
    Assertions.assertEquals( "%33", tokens.get(2));
    Assertions.assertEquals( 3, tokens.size());
  }

  @Test
  public void opFunctionEnd()
  {
    final var instruction =
      createInstruction(SM_OP_FUNCTION_END.value());
    final var tokens =
      new SMDOpFunctionEnd().disassemble(this.context, instruction);
    Assertions.assertEquals( 0, tokens.size());
  }

  @Test
  public void opCompositeExtract()
  {
    final var instruction =
      createInstruction(SM_OP_COMPOSITE_EXTRACT.value(),
                        13L,
                        0L,
                        17L,
                        23L
      );
    final var tokens =
      new SMDOpCompositeExtract().disassemble(this.context, instruction);
    Assertions.assertEquals( "%13", tokens.get(0));
    Assertions.assertEquals( "%17", tokens.get(1));
    Assertions.assertEquals( "$23", tokens.get(2));
    Assertions.assertEquals( 3, tokens.size());
  }

  @Test
  public void opCompositeConstruct()
  {
    final var instruction =
      createInstruction(SM_OP_COMPOSITE_CONSTRUCT.value(),
                        13L,
                        0L,
                        17L,
                        23L
      );
    final var tokens =
      new SMDOpCompositeConstruct().disassemble(this.context, instruction);
    Assertions.assertEquals( "%13", tokens.get(0));
    Assertions.assertEquals( "%17", tokens.get(1));
    Assertions.assertEquals( "%23", tokens.get(2));
    Assertions.assertEquals( 3, tokens.size());
  }

  @Test
  public void opAccessChain()
  {
    final var instruction =
      createInstruction(SM_OP_ACCESS_CHAIN.value(),
                        13L,
                        0L,
                        17L,
                        23L
      );
    final var tokens =
      new SMDOpAccessChain().disassemble(this.context, instruction);
    Assertions.assertEquals( "%13", tokens.get(0));
    Assertions.assertEquals( "%17", tokens.get(1));
    Assertions.assertEquals( "%23", tokens.get(2));
    Assertions.assertEquals( 3, tokens.size());
  }

  @Test
  public void opConstantIntegral()
  {
    Mockito.when(
      this.context.literalTyped(Mockito.any(), Mockito.any()))
      .thenAnswer(invocationOnMock -> {
        return "$" + invocationOnMock.getArgument(1);
      });

    final var instruction =
      createInstruction(SM_OP_CONSTANT.value(),
                        13L,
                        0L,
                        1L
      );
    final var tokens =
      new SMDOpConstant().disassemble(this.context, instruction);
    Assertions.assertEquals( "%13", tokens.get(0));
    Assertions.assertEquals( "$1", tokens.get(1));
    Assertions.assertEquals( 2, tokens.size());
  }

  @Test
  public void opConstantFloat()
  {
    Mockito.when(
      this.context.literalTyped(Mockito.any(), Mockito.any()))
      .thenAnswer(invocationOnMock -> {
        return "$" + ((Long) invocationOnMock.getArgument(1)).floatValue();
      });

    final var instruction =
      createInstruction(SM_OP_CONSTANT.value(),
                        13L,
                        0L,
                        1L
      );
    final var tokens =
      new SMDOpConstant().disassemble(this.context, instruction);
    Assertions.assertEquals( "%13", tokens.get(0));
    Assertions.assertEquals( "$1.0", tokens.get(1));
    Assertions.assertEquals( 2, tokens.size());
  }

  @Test
  public void opFunction()
  {
    final var instruction =
      createInstruction(SM_OP_FUNCTION.value(),
                        13L,
                        0L,
                        SMFunctionControl.SM_CONST.value(),
                        17L
      );
    final var tokens =
      new SMDOpFunction().disassemble(this.context, instruction);
    Assertions.assertEquals( "%13", tokens.get(0));
    Assertions.assertEquals( "Const", tokens.get(1));
    Assertions.assertEquals( "%17", tokens.get(2));
    Assertions.assertEquals( 3, tokens.size());
  }

  private static SMParsedInstruction createInstruction(
    final int opcode,
    final long... operands)
  {
    return SMParsedInstruction.builder()
      .setOpCode(opcode)
      .setWordCount(operands.length)
      .setByteOffset(0L)
      .addOperands(operands)
      .build();
  }
}
