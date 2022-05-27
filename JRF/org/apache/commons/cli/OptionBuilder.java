package org.apache.commons.cli;

import org.apache.commons.cli.Option;

@Deprecated
public final class OptionBuilder {

   private static String longOption;
   private static String description;
   private static String argName;
   private static boolean required;
   private static int argCount = -1;
   private static Class<?> type;
   private static boolean optionalArg;
   private static char valueSeparator;
   private static final OptionBuilder INSTANCE = new OptionBuilder();


   public static Option create() throws IllegalArgumentException {
      if(longOption == null) {
         reset();
         throw new IllegalArgumentException("must specify longopt");
      } else {
         return create((String)null);
      }
   }

   public static Option create(char opt) throws IllegalArgumentException {
      return create(String.valueOf(opt));
   }

   public static Option create(String opt) throws IllegalArgumentException {
      Option option = null;

      try {
         option = new Option(opt, description);
         option.setLongOpt(longOption);
         option.setRequired(required);
         option.setOptionalArg(optionalArg);
         option.setArgs(argCount);
         option.setType(type);
         option.setValueSeparator(valueSeparator);
         option.setArgName(argName);
      } finally {
         reset();
      }

      return option;
   }

   public static OptionBuilder hasArg() {
      argCount = 1;
      return INSTANCE;
   }

   public static OptionBuilder hasArg(boolean hasArg) {
      argCount = hasArg?1:-1;
      return INSTANCE;
   }

   public static OptionBuilder hasArgs() {
      argCount = -2;
      return INSTANCE;
   }

   public static OptionBuilder hasArgs(int num) {
      argCount = num;
      return INSTANCE;
   }

   public static OptionBuilder hasOptionalArg() {
      argCount = 1;
      optionalArg = true;
      return INSTANCE;
   }

   public static OptionBuilder hasOptionalArgs() {
      argCount = -2;
      optionalArg = true;
      return INSTANCE;
   }

   public static OptionBuilder hasOptionalArgs(int numArgs) {
      argCount = numArgs;
      optionalArg = true;
      return INSTANCE;
   }

   public static OptionBuilder isRequired() {
      required = true;
      return INSTANCE;
   }

   public static OptionBuilder isRequired(boolean newRequired) {
      required = newRequired;
      return INSTANCE;
   }

   private static void reset() {
      description = null;
      argName = null;
      longOption = null;
      type = String.class;
      required = false;
      argCount = -1;
      optionalArg = false;
      valueSeparator = 0;
   }

   public static OptionBuilder withArgName(String name) {
      argName = name;
      return INSTANCE;
   }

   public static OptionBuilder withDescription(String newDescription) {
      description = newDescription;
      return INSTANCE;
   }

   public static OptionBuilder withLongOpt(String newLongopt) {
      longOption = newLongopt;
      return INSTANCE;
   }

   public static OptionBuilder withType(Class<?> newType) {
      type = newType;
      return INSTANCE;
   }

   @Deprecated
   public static OptionBuilder withType(Object newType) {
      return withType((Class)newType);
   }

   public static OptionBuilder withValueSeparator() {
      valueSeparator = 61;
      return INSTANCE;
   }

   public static OptionBuilder withValueSeparator(char sep) {
      valueSeparator = sep;
      return INSTANCE;
   }

   static {
      reset();
   }
}
