package org.apache.commons.cli;


final class Util {

   static final String[] EMPTY_STRING_ARRAY = new String[0];


   static String stripLeadingAndTrailingQuotes(String str) {
      int length = str.length();
      if(length > 1 && str.startsWith("\"") && str.endsWith("\"") && str.substring(1, length - 1).indexOf(34) == -1) {
         str = str.substring(1, length - 1);
      }

      return str;
   }

   static String stripLeadingHyphens(String str) {
      return str == null?null:(str.startsWith("--")?str.substring(2):(str.startsWith("-")?str.substring(1):str));
   }

}
