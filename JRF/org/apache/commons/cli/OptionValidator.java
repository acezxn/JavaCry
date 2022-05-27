package org.apache.commons.cli;


final class OptionValidator {

   private static boolean isValidChar(char c) {
      return Character.isJavaIdentifierPart(c);
   }

   private static boolean isValidOpt(char c) {
      return isValidChar(c) || c == 63 || c == 64;
   }

   static String validate(String option) throws IllegalArgumentException {
      if(option == null) {
         return null;
      } else {
         if(option.length() == 1) {
            char ch = option.charAt(0);
            if(!isValidOpt(ch)) {
               throw new IllegalArgumentException("Illegal option name \'" + ch + "\'");
            }
         } else {
            char[] var5 = option.toCharArray();
            int var2 = var5.length;

            for(int var3 = 0; var3 < var2; ++var3) {
               char ch1 = var5[var3];
               if(!isValidChar(ch1)) {
                  throw new IllegalArgumentException("The option \'" + option + "\' contains an illegal character : \'" + ch1 + "\'");
               }
            }
         }

         return option;
      }
   }
}
