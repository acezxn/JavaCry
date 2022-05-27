package org.apache.commons.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public interface CommandLineParser {

   CommandLine parse(Options var1, String[] var2) throws ParseException;

   CommandLine parse(Options var1, String[] var2, boolean var3) throws ParseException;
}
