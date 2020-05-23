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

package com.io7m.jspearmint.cmdline;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.internal.Console;
import com.io7m.jspearmint.cmdline.internal.SMCommandDisassemble;
import com.io7m.jspearmint.cmdline.internal.SMCommandRoot;
import com.io7m.jspearmint.cmdline.internal.SMCommandType;
import com.io7m.jspearmint.cmdline.internal.SMCommandVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Main command line entry point.
 */

public final class Main implements Runnable
{
  private static final Logger LOG = LoggerFactory.getLogger(Main.class);

  private final Map<String, SMCommandType> commands;
  private final JCommander commander;
  private final String[] args;
  private int exitCode;

  public Main(
    final String[] inArgs)
  {
    this.args =
      Objects.requireNonNull(inArgs, "Command line arguments");

    final SMCommandRoot r = new SMCommandRoot();
    final SMCommandVersion version =
      new SMCommandVersion();
    final SMCommandDisassemble disassemble =
      new SMCommandDisassemble();

    this.commands = new HashMap<>(8);
    this.commands.put("version", version);
    this.commands.put("disassemble", disassemble);

    this.commander = new JCommander(r);
    this.commander.setProgramName("brooklime");
    this.commander.addCommand("version", version);
    this.commander.addCommand("disassemble", disassemble);
  }

  /**
   * The main entry point.
   *
   * @param args Command line arguments
   */

  public static void main(final String[] args)
  {
    final Main cm = new Main(args);
    cm.run();
    System.exit(cm.exitCode());
  }

  /**
   * @return The program exit code
   */

  public int exitCode()
  {
    return this.exitCode;
  }

  @Override
  public void run()
  {
    try {
      this.commander.parse(this.args);

      final String cmd = this.commander.getParsedCommand();
      if (cmd == null) {
        final StringBuilderConsole console = new StringBuilderConsole();
        this.commander.setConsole(console);
        this.commander.usage();
        LOG.info("Arguments required.\n{}", console.builder.toString());
        this.exitCode = 1;
        return;
      }

      final SMCommandType command = this.commands.get(cmd);
      final SMCommandType.Status status = command.execute();
      this.exitCode = status.exitCode();
    } catch (final ParameterException e) {
      LOG.error("{}", e.getMessage());
      this.exitCode = 1;
    } catch (final Exception e) {
      LOG.error("{}", e.getMessage(), e);
      this.exitCode = 1;
    }
  }

  private static final class StringBuilderConsole implements Console
  {
    private final StringBuilder builder;

    StringBuilderConsole()
    {
      this.builder = new StringBuilder(128);
    }

    @Override
    public void print(final String s)
    {
      this.builder.append(s);
    }

    @Override
    public void println(final String s)
    {
      this.builder.append(s);
      this.builder.append('\n');
    }

    @Override
    public char[] readPassword(final boolean b)
    {
      return new char[0];
    }
  }
}
