

// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.cli;

import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.io.Serializable;

public class CommandLine implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final List<String> args;
    private final List<Option> options;
    
    protected CommandLine() {
        this.args = new LinkedList<String>();
        this.options = new ArrayList<Option>();
    }
    
    protected void addArg(final String arg) {
        this.args.add(arg);
    }
    
    protected void addOption(final Option opt) {
        this.options.add(opt);
    }
    
    public List<String> getArgList() {
        return this.args;
    }
    
    public String[] getArgs() {
        final String[] answer = new String[this.args.size()];
        this.args.toArray(answer);
        return answer;
    }
    
    @Deprecated
    public Object getOptionObject(final char opt) {
        return this.getOptionObject(String.valueOf(opt));
    }
    
    @Deprecated
    public Object getOptionObject(final String opt) {
        try {
            return this.getParsedOptionValue(opt);
        }
        catch (ParseException pe) {
            System.err.println("Exception found converting " + opt + " to desired type: " + pe.getMessage());
            return null;
        }
    }
    
    public Properties getOptionProperties(final Option option) {
        final Properties props = new Properties();
        for (final Option processedOption : this.options) {
            if (processedOption.equals(option)) {
                final List<String> values = processedOption.getValuesList();
                if (values.size() >= 2) {
                    props.put(values.get(0), values.get(1));
                }
                else {
                    if (values.size() != 1) {
                        continue;
                    }
                    props.put(values.get(0), "true");
                }
            }
        }
        return props;
    }
    
    public Properties getOptionProperties(final String opt) {
        final Properties props = new Properties();
        for (final Option option : this.options) {
            if (opt.equals(option.getOpt()) || opt.equals(option.getLongOpt())) {
                final List<String> values = option.getValuesList();
                if (values.size() >= 2) {
                    props.put(values.get(0), values.get(1));
                }
                else {
                    if (values.size() != 1) {
                        continue;
                    }
                    props.put(values.get(0), "true");
                }
            }
        }
        return props;
    }
    
    public Option[] getOptions() {
        final Collection<Option> processed = this.options;
        final Option[] optionsArray = new Option[processed.size()];
        return processed.toArray(optionsArray);
    }
    
    public String getOptionValue(final char opt) {
        return this.getOptionValue(String.valueOf(opt));
    }
    
    public String getOptionValue(final char opt, final String defaultValue) {
        return this.getOptionValue(String.valueOf(opt), defaultValue);
    }
    
    public String getOptionValue(final Option option) {
        if (option == null) {
            return null;
        }
        final String[] values = this.getOptionValues(option);
        return (values == null) ? null : values[0];
    }
    
    public String getOptionValue(final Option option, final String defaultValue) {
        final String answer = this.getOptionValue(option);
        return (answer != null) ? answer : defaultValue;
    }
    
    public String getOptionValue(final String opt) {
        return this.getOptionValue(this.resolveOption(opt));
    }
    
    public String getOptionValue(final String opt, final String defaultValue) {
        return this.getOptionValue(this.resolveOption(opt), defaultValue);
    }
    
    public String[] getOptionValues(final char opt) {
        return this.getOptionValues(String.valueOf(opt));
    }
    
    public String[] getOptionValues(final Option option) {
        final List<String> values = new ArrayList<String>();
        for (final Option processedOption : this.options) {
            if (processedOption.equals(option)) {
                values.addAll(processedOption.getValuesList());
            }
        }
        return (String[])(values.isEmpty() ? null : ((String[])values.toArray(new String[values.size()])));
    }
    
    public String[] getOptionValues(final String opt) {
        return this.getOptionValues(this.resolveOption(opt));
    }
    
    public Object getParsedOptionValue(final char opt) throws ParseException {
        return this.getParsedOptionValue(String.valueOf(opt));
    }
    
    public Object getParsedOptionValue(final Option option) throws ParseException {
        if (option == null) {
            return null;
        }
        final String res = this.getOptionValue(option);
        if (res == null) {
            return null;
        }
        return TypeHandler.createValue(res, option.getType());
    }
    
    public Object getParsedOptionValue(final String opt) throws ParseException {
        return this.getParsedOptionValue(this.resolveOption(opt));
    }
    
    public boolean hasOption(final char opt) {
        return this.hasOption(String.valueOf(opt));
    }
    
    public boolean hasOption(final Option opt) {
        return this.options.contains(opt);
    }
    
    public boolean hasOption(final String opt) {
        return this.hasOption(this.resolveOption(opt));
    }
    
    public Iterator<Option> iterator() {
        return this.options.iterator();
    }
    
    private Option resolveOption(String opt) {
        opt = Util.stripLeadingHyphens(opt);
        for (final Option option : this.options) {
            if (opt.equals(option.getOpt()) || opt.equals(option.getLongOpt())) {
                return option;
            }
        }
        return null;
    }
    
    public static final class Builder
    {
        private final CommandLine commandLine;
        
        public Builder() {
            this.commandLine = new CommandLine();
        }
        
        public Builder addArg(final String arg) {
            this.commandLine.addArg(arg);
            return this;
        }
        
        public Builder addOption(final Option opt) {
            this.commandLine.addOption(opt);
            return this;
        }
        
        public CommandLine build() {
            return this.commandLine;
        }
    }
}

