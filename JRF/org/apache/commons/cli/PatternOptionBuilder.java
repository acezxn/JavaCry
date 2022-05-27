package org.apache.commons.cli;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Date;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class PatternOptionBuilder {

   public static final Class<String> STRING_VALUE = String.class;
   public static final Class<Object> OBJECT_VALUE = Object.class;
   public static final Class<Number> NUMBER_VALUE = Number.class;
   public static final Class<Date> DATE_VALUE = Date.class;
   public static final Class<?> CLASS_VALUE = Class.class;
   public static final Class<FileInputStream> EXISTING_FILE_VALUE = FileInputStream.class;
   public static final Class<File> FILE_VALUE = File.class;
   public static final Class<File[]> FILES_VALUE = File[].class;
   public static final Class<URL> URL_VALUE = URL.class;


   public static Object getValueClass(char ch) {
      switch(ch) {
      case 35:
         return DATE_VALUE;
      case 36:
      case 38:
      case 39:
      case 40:
      case 41:
      case 44:
      case 45:
      case 46:
      case 48:
      case 49:
      case 50:
      case 51:
      case 52:
      case 53:
      case 54:
      case 55:
      case 56:
      case 57:
      case 59:
      case 61:
      case 63:
      default:
         return null;
      case 37:
         return NUMBER_VALUE;
      case 42:
         return FILES_VALUE;
      case 43:
         return CLASS_VALUE;
      case 47:
         return URL_VALUE;
      case 58:
         return STRING_VALUE;
      case 60:
         return EXISTING_FILE_VALUE;
      case 62:
         return FILE_VALUE;
      case 64:
         return OBJECT_VALUE;
      }
   }

   public static boolean isValueCode(char ch) {
      return ch == 64 || ch == 58 || ch == 37 || ch == 43 || ch == 35 || ch == 60 || ch == 62 || ch == 42 || ch == 47 || ch == 33;
   }

   public static Options parsePattern(String pattern) {
      char opt = 32;
      boolean required = false;
      Class type = null;
      Options options = new Options();

      for(int option = 0; option < pattern.length(); ++option) {
         char ch = pattern.charAt(option);
         if(!isValueCode(ch)) {
            if(opt != 32) {
               Option option1 = Option.builder(String.valueOf(opt)).hasArg(type != null).required(required).type(type).build();
               options.addOption(option1);
               required = false;
               type = null;
               boolean var8 = true;
            }

            opt = ch;
         } else if(ch == 33) {
            required = true;
         } else {
            type = (Class)getValueClass(ch);
         }
      }

      if(opt != 32) {
         Option var9 = Option.builder(String.valueOf(opt)).hasArg(type != null).required(required).type(type).build();
         options.addOption(var9);
      }

      return options;
   }

}
