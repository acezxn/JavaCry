package org.apache.commons.cli;

import org.apache.commons.cli.ParseException;

public class UnrecognizedOptionException extends ParseException {

   private static final long serialVersionUID = -252504690284625623L;
   private final String option;


   public UnrecognizedOptionException(String message) {
      this(message, (String)null);
   }

   public UnrecognizedOptionException(String message, String option) {
      super(message);
      this.option = option;
   }

   public String getOption() {
      return this.option;
   }
}
