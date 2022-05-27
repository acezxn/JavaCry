package org.apache.commons.cli;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.cli.OptionValidator;

public class Option implements Cloneable, Serializable {

   public static final int UNINITIALIZED = -1;
   public static final int UNLIMITED_VALUES = -2;
   private static final long serialVersionUID = 1L;
   private final String option;
   private String longOption;
   private String argName;
   private String description;
   private boolean required;
   private boolean optionalArg;
   private int argCount;
   private Class<?> type;
   private List<String> values;
   private char valuesep;


   public static Option.Builder builder() {
      return builder((String)null);
   }

   public static Option.Builder builder(String option) {
      return new Option.Builder(option, null);
   }

   private Option(Option.Builder builder) {
      this.argCount = -1;
      this.type = String.class;
      this.values = new ArrayList();
      this.argName = builder.argName;
      this.description = builder.description;
      this.longOption = builder.longOption;
      this.argCount = builder.argCount;
      this.option = builder.option;
      this.optionalArg = builder.optionalArg;
      this.required = builder.required;
      this.type = builder.type;
      this.valuesep = builder.valueSeparator;
   }

   public Option(String option, boolean hasArg, String description) throws IllegalArgumentException {
      this(option, (String)null, hasArg, description);
   }

   public Option(String option, String description) throws IllegalArgumentException {
      this(option, (String)null, false, description);
   }

   public Option(String option, String longOption, boolean hasArg, String description) throws IllegalArgumentException {
      this.argCount = -1;
      this.type = String.class;
      this.values = new ArrayList();
      this.option = OptionValidator.validate(option);
      this.longOption = longOption;
      if(hasArg) {
         this.argCount = 1;
      }

      this.description = description;
   }

   boolean acceptsArg() {
      return (this.hasArg() || this.hasArgs() || this.hasOptionalArg()) && (this.argCount <= 0 || this.values.size() < this.argCount);
   }

   private void add(String value) {
      if(!this.acceptsArg()) {
         throw new RuntimeException("Cannot add value, list full.");
      } else {
         this.values.add(value);
      }
   }

   @Deprecated
   public boolean addValue(String value) {
      throw new UnsupportedOperationException("The addValue method is not intended for client use. Subclasses should use the addValueForProcessing method instead. ");
   }

   void addValueForProcessing(String value) {
      if(this.argCount == -1) {
         throw new RuntimeException("NO_ARGS_ALLOWED");
      } else {
         this.processValue(value);
      }
   }

   void clearValues() {
      this.values.clear();
   }

   public Object clone() {
      try {
         Option cnse = (Option)super.clone();
         cnse.values = new ArrayList(this.values);
         return cnse;
      } catch (CloneNotSupportedException var2) {
         throw new RuntimeException("A CloneNotSupportedException was thrown: " + var2.getMessage());
      }
   }

   public boolean equals(Object obj) {
      if(this == obj) {
         return true;
      } else if(!(obj instanceof Option)) {
         return false;
      } else {
         Option other = (Option)obj;
         return Objects.equals(this.longOption, other.longOption) && Objects.equals(this.option, other.option);
      }
   }

   public String getArgName() {
      return this.argName;
   }

   public int getArgs() {
      return this.argCount;
   }

   public String getDescription() {
      return this.description;
   }

   public int getId() {
      return this.getKey().charAt(0);
   }

   String getKey() {
      return this.option == null?this.longOption:this.option;
   }

   public String getLongOpt() {
      return this.longOption;
   }

   public String getOpt() {
      return this.option;
   }

   public Object getType() {
      return this.type;
   }

   public String getValue() {
      return this.hasNoValues()?null:(String)this.values.get(0);
   }

   public String getValue(int index) throws IndexOutOfBoundsException {
      return this.hasNoValues()?null:(String)this.values.get(index);
   }

   public String getValue(String defaultValue) {
      String value = this.getValue();
      return value != null?value:defaultValue;
   }

   public String[] getValues() {
      return this.hasNoValues()?null:(String[])this.values.toArray(new String[this.values.size()]);
   }

   public char getValueSeparator() {
      return this.valuesep;
   }

   public List<String> getValuesList() {
      return this.values;
   }

   public boolean hasArg() {
      return this.argCount > 0 || this.argCount == -2;
   }

   public boolean hasArgName() {
      return this.argName != null && !this.argName.isEmpty();
   }

   public boolean hasArgs() {
      return this.argCount > 1 || this.argCount == -2;
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.longOption, this.option});
   }

   public boolean hasLongOpt() {
      return this.longOption != null;
   }

   private boolean hasNoValues() {
      return this.values.isEmpty();
   }

   public boolean hasOptionalArg() {
      return this.optionalArg;
   }

   public boolean hasValueSeparator() {
      return this.valuesep > 0;
   }

   public boolean isRequired() {
      return this.required;
   }

   private void processValue(String value) {
      if(this.hasValueSeparator()) {
         char sep = this.getValueSeparator();

         for(int index = value.indexOf(sep); index != -1 && this.values.size() != this.argCount - 1; index = value.indexOf(sep)) {
            this.add(value.substring(0, index));
            value = value.substring(index + 1);
         }
      }

      this.add(value);
   }

   boolean requiresArg() {
      return this.optionalArg?false:(this.argCount == -2?this.values.isEmpty():this.acceptsArg());
   }

   public void setArgName(String argName) {
      this.argName = argName;
   }

   public void setArgs(int num) {
      this.argCount = num;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public void setLongOpt(String longOpt) {
      this.longOption = longOpt;
   }

   public void setOptionalArg(boolean optionalArg) {
      this.optionalArg = optionalArg;
   }

   public void setRequired(boolean required) {
      this.required = required;
   }

   public void setType(Class<?> type) {
      this.type = type;
   }

   @Deprecated
   public void setType(Object type) {
      this.setType((Class)type);
   }

   public void setValueSeparator(char sep) {
      this.valuesep = sep;
   }

   public String toString() {
      StringBuilder buf = (new StringBuilder()).append("[ option: ");
      buf.append(this.option);
      if(this.longOption != null) {
         buf.append(" ").append(this.longOption);
      }

      buf.append(" ");
      if(this.hasArgs()) {
         buf.append("[ARG...]");
      } else if(this.hasArg()) {
         buf.append(" [ARG]");
      }

      buf.append(" :: ").append(this.description);
      if(this.type != null) {
         buf.append(" :: ").append(this.type);
      }

      buf.append(" ]");
      return buf.toString();
   }

   // $FF: synthetic method
   Option(Option.Builder x0, Object x1) {
      this(x0);
   }

   public static final class Builder {

      private String option;
      private String description;
      private String longOption;
      private String argName;
      private boolean required;
      private boolean optionalArg;
      private int argCount;
      private Class<?> type;
      private char valueSeparator;


      private Builder(String option) throws IllegalArgumentException {
         this.argCount = -1;
         this.type = String.class;
         this.option(option);
      }

      public Option.Builder argName(String argName) {
         this.argName = argName;
         return this;
      }

      public Option build() {
         if(this.option == null && this.longOption == null) {
            throw new IllegalArgumentException("Either opt or longOpt must be specified");
         } else {
            return new Option(this, null);
         }
      }

      public Option.Builder desc(String description) {
         this.description = description;
         return this;
      }

      public Option.Builder hasArg() {
         return this.hasArg(true);
      }

      public Option.Builder hasArg(boolean hasArg) {
         this.argCount = hasArg?1:-1;
         return this;
      }

      public Option.Builder hasArgs() {
         this.argCount = -2;
         return this;
      }

      public Option.Builder longOpt(String longOpt) {
         this.longOption = longOpt;
         return this;
      }

      public Option.Builder numberOfArgs(int numberOfArgs) {
         this.argCount = numberOfArgs;
         return this;
      }

      public Option.Builder option(String option) throws IllegalArgumentException {
         this.option = OptionValidator.validate(option);
         return this;
      }

      public Option.Builder optionalArg(boolean isOptional) {
         this.optionalArg = isOptional;
         return this;
      }

      public Option.Builder required() {
         return this.required(true);
      }

      public Option.Builder required(boolean required) {
         this.required = required;
         return this;
      }

      public Option.Builder type(Class<?> type) {
         this.type = type;
         return this;
      }

      public Option.Builder valueSeparator() {
         return this.valueSeparator('=');
      }

      public Option.Builder valueSeparator(char sep) {
         this.valueSeparator = sep;
         return this;
      }

      // $FF: synthetic method
      Builder(String x0, Object x1) throws IllegalArgumentException {
         this(x0);
      }
   }
}
