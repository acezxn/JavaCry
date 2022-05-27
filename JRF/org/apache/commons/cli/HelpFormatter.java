

// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.cli;

import java.io.Serializable;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.StringReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Comparator;

public class HelpFormatter
{
    public static final int DEFAULT_WIDTH = 74;
    public static final int DEFAULT_LEFT_PAD = 1;
    public static final int DEFAULT_DESC_PAD = 3;
    public static final String DEFAULT_SYNTAX_PREFIX = "usage: ";
    public static final String DEFAULT_OPT_PREFIX = "-";
    public static final String DEFAULT_LONG_OPT_PREFIX = "--";
    public static final String DEFAULT_LONG_OPT_SEPARATOR = " ";
    public static final String DEFAULT_ARG_NAME = "arg";
    @Deprecated
    public int defaultWidth;
    @Deprecated
    public int defaultLeftPad;
    @Deprecated
    public int defaultDescPad;
    @Deprecated
    public String defaultSyntaxPrefix;
    @Deprecated
    public String defaultNewLine;
    @Deprecated
    public String defaultOptPrefix;
    @Deprecated
    public String defaultLongOptPrefix;
    @Deprecated
    public String defaultArgName;
    protected Comparator<Option> optionComparator;
    private String longOptSeparator;
    
    public HelpFormatter() {
        this.defaultWidth = 74;
        this.defaultLeftPad = 1;
        this.defaultDescPad = 3;
        this.defaultSyntaxPrefix = "usage: ";
        this.defaultNewLine = System.getProperty("line.separator");
        this.defaultOptPrefix = "-";
        this.defaultLongOptPrefix = "--";
        this.defaultArgName = "arg";
        this.optionComparator = new OptionComparator();
        this.longOptSeparator = " ";
    }
    
    private void appendOption(final StringBuffer buff, final Option option, final boolean required) {
        if (!required) {
            buff.append("[");
        }
        if (option.getOpt() != null) {
            buff.append("-").append(option.getOpt());
        }
        else {
            buff.append("--").append(option.getLongOpt());
        }
        if (option.hasArg() && (option.getArgName() == null || !option.getArgName().isEmpty())) {
            buff.append((option.getOpt() == null) ? this.longOptSeparator : " ");
            buff.append("<").append((option.getArgName() != null) ? option.getArgName() : this.getArgName()).append(">");
        }
        if (!required) {
            buff.append("]");
        }
    }
    
    private void appendOptionGroup(final StringBuffer buff, final OptionGroup group) {
        if (!group.isRequired()) {
            buff.append("[");
        }
        final List<Option> optList = new ArrayList<Option>(group.getOptions());
        if (this.getOptionComparator() != null) {
            Collections.sort(optList, this.getOptionComparator());
        }
        final Iterator<Option> it = optList.iterator();
        while (it.hasNext()) {
            this.appendOption(buff, it.next(), true);
            if (it.hasNext()) {
                buff.append(" | ");
            }
        }
        if (!group.isRequired()) {
            buff.append("]");
        }
    }
    
    protected String createPadding(final int len) {
        final char[] padding = new char[len];
        Arrays.fill(padding, ' ');
        return new String(padding);
    }
    
    protected int findWrapPos(final String text, final int width, final int startPos) {
        int pos = text.indexOf(10, startPos);
        if (pos != -1 && pos <= width) {
            return pos + 1;
        }
        pos = text.indexOf(9, startPos);
        if (pos != -1 && pos <= width) {
            return pos + 1;
        }
        if (startPos + width >= text.length()) {
            return -1;
        }
        for (pos = startPos + width; pos >= startPos; --pos) {
            final char c = text.charAt(pos);
            if (c == ' ' || c == '\n') {
                break;
            }
            if (c == '\r') {
                break;
            }
        }
        if (pos > startPos) {
            return pos;
        }
        pos = startPos + width;
        return (pos == text.length()) ? -1 : pos;
    }
    
    public String getArgName() {
        return this.defaultArgName;
    }
    
    public int getDescPadding() {
        return this.defaultDescPad;
    }
    
    public int getLeftPadding() {
        return this.defaultLeftPad;
    }
    
    public String getLongOptPrefix() {
        return this.defaultLongOptPrefix;
    }
    
    public String getLongOptSeparator() {
        return this.longOptSeparator;
    }
    
    public String getNewLine() {
        return this.defaultNewLine;
    }
    
    public Comparator<Option> getOptionComparator() {
        return this.optionComparator;
    }
    
    public String getOptPrefix() {
        return this.defaultOptPrefix;
    }
    
    public String getSyntaxPrefix() {
        return this.defaultSyntaxPrefix;
    }
    
    public int getWidth() {
        return this.defaultWidth;
    }
    
    public void printHelp(final int width, final String cmdLineSyntax, final String header, final Options options, final String footer) {
        this.printHelp(width, cmdLineSyntax, header, options, footer, false);
    }
    
    public void printHelp(final int width, final String cmdLineSyntax, final String header, final Options options, final String footer, final boolean autoUsage) {
        final PrintWriter pw = new PrintWriter(System.out);
        this.printHelp(pw, width, cmdLineSyntax, header, options, this.getLeftPadding(), this.getDescPadding(), footer, autoUsage);
        pw.flush();
    }
    
    public void printHelp(final PrintWriter pw, final int width, final String cmdLineSyntax, final String header, final Options options, final int leftPad, final int descPad, final String footer) {
        this.printHelp(pw, width, cmdLineSyntax, header, options, leftPad, descPad, footer, false);
    }
    
    public void printHelp(final PrintWriter pw, final int width, final String cmdLineSyntax, final String header, final Options options, final int leftPad, final int descPad, final String footer, final boolean autoUsage) {
        if (cmdLineSyntax == null || cmdLineSyntax.isEmpty()) {
            throw new IllegalArgumentException("cmdLineSyntax not provided");
        }
        if (autoUsage) {
            this.printUsage(pw, width, cmdLineSyntax, options);
        }
        else {
            this.printUsage(pw, width, cmdLineSyntax);
        }
        if (header != null && !header.isEmpty()) {
            this.printWrapped(pw, width, header);
        }
        this.printOptions(pw, width, options, leftPad, descPad);
        if (footer != null && !footer.isEmpty()) {
            this.printWrapped(pw, width, footer);
        }
    }
    
    public void printHelp(final String cmdLineSyntax, final Options options) {
        this.printHelp(this.getWidth(), cmdLineSyntax, null, options, null, false);
    }
    
    public void printHelp(final String cmdLineSyntax, final Options options, final boolean autoUsage) {
        this.printHelp(this.getWidth(), cmdLineSyntax, null, options, null, autoUsage);
    }
    
    public void printHelp(final String cmdLineSyntax, final String header, final Options options, final String footer) {
        this.printHelp(cmdLineSyntax, header, options, footer, false);
    }
    
    public void printHelp(final String cmdLineSyntax, final String header, final Options options, final String footer, final boolean autoUsage) {
        this.printHelp(this.getWidth(), cmdLineSyntax, header, options, footer, autoUsage);
    }
    
    public void printOptions(final PrintWriter pw, final int width, final Options options, final int leftPad, final int descPad) {
        final StringBuffer sb = new StringBuffer();
        this.renderOptions(sb, width, options, leftPad, descPad);
        pw.println(sb.toString());
    }
    
    public void printUsage(final PrintWriter pw, final int width, final String cmdLineSyntax) {
        final int argPos = cmdLineSyntax.indexOf(32) + 1;
        this.printWrapped(pw, width, this.getSyntaxPrefix().length() + argPos, this.getSyntaxPrefix() + cmdLineSyntax);
    }
    
    public void printUsage(final PrintWriter pw, final int width, final String app, final Options options) {
        final StringBuffer buff = new StringBuffer(this.getSyntaxPrefix()).append(app).append(" ");
        final Collection<OptionGroup> processedGroups = new ArrayList<OptionGroup>();
        final List<Option> optList = new ArrayList<Option>(options.getOptions());
        if (this.getOptionComparator() != null) {
            Collections.sort(optList, this.getOptionComparator());
        }
        final Iterator<Option> it = optList.iterator();
        while (it.hasNext()) {
            final Option option = it.next();
            final OptionGroup group = options.getOptionGroup(option);
            if (group != null) {
                if (!processedGroups.contains(group)) {
                    processedGroups.add(group);
                    this.appendOptionGroup(buff, group);
                }
            }
            else {
                this.appendOption(buff, option, option.isRequired());
            }
            if (it.hasNext()) {
                buff.append(" ");
            }
        }
        this.printWrapped(pw, width, buff.toString().indexOf(32) + 1, buff.toString());
    }
    
    public void printWrapped(final PrintWriter pw, final int width, final int nextLineTabStop, final String text) {
        final StringBuffer sb = new StringBuffer(text.length());
        this.renderWrappedTextBlock(sb, width, nextLineTabStop, text);
        pw.println(sb.toString());
    }
    
    public void printWrapped(final PrintWriter pw, final int width, final String text) {
        this.printWrapped(pw, width, 0, text);
    }
    
    protected StringBuffer renderOptions(final StringBuffer sb, final int width, final Options options, final int leftPad, final int descPad) {
        final String lpad = this.createPadding(leftPad);
        final String dpad = this.createPadding(descPad);
        int max = 0;
        final List<StringBuffer> prefixList = new ArrayList<StringBuffer>();
        final List<Option> optList = options.helpOptions();
        if (this.getOptionComparator() != null) {
            Collections.sort(optList, this.getOptionComparator());
        }
        for (final Option option : optList) {
            final StringBuffer optBuf = new StringBuffer();
            if (option.getOpt() == null) {
                optBuf.append(lpad).append("   ").append(this.getLongOptPrefix()).append(option.getLongOpt());
            }
            else {
                optBuf.append(lpad).append(this.getOptPrefix()).append(option.getOpt());
                if (option.hasLongOpt()) {
                    optBuf.append(',').append(this.getLongOptPrefix()).append(option.getLongOpt());
                }
            }
            if (option.hasArg()) {
                final String argName = option.getArgName();
                if (argName != null && argName.isEmpty()) {
                    optBuf.append(' ');
                }
                else {
                    optBuf.append(option.hasLongOpt() ? this.longOptSeparator : " ");
                    optBuf.append("<").append((argName != null) ? option.getArgName() : this.getArgName()).append(">");
                }
            }
            prefixList.add(optBuf);
            max = ((optBuf.length() > max) ? optBuf.length() : max);
        }
        int x = 0;
        final Iterator<Option> it = optList.iterator();
        while (it.hasNext()) {
            final Option option2 = it.next();
            final StringBuilder optBuf2 = new StringBuilder(prefixList.get(x++).toString());
            if (optBuf2.length() < max) {
                optBuf2.append(this.createPadding(max - optBuf2.length()));
            }
            optBuf2.append(dpad);
            final int nextLineTabStop = max + descPad;
            if (option2.getDescription() != null) {
                optBuf2.append(option2.getDescription());
            }
            this.renderWrappedText(sb, width, nextLineTabStop, optBuf2.toString());
            if (it.hasNext()) {
                sb.append(this.getNewLine());
            }
        }
        return sb;
    }
    
    protected StringBuffer renderWrappedText(final StringBuffer sb, final int width, int nextLineTabStop, String text) {
        int pos = this.findWrapPos(text, width, 0);
        if (pos == -1) {
            sb.append(this.rtrim(text));
            return sb;
        }
        sb.append(this.rtrim(text.substring(0, pos))).append(this.getNewLine());
        if (nextLineTabStop >= width) {
            nextLineTabStop = 1;
        }
        final String padding = this.createPadding(nextLineTabStop);
        while (true) {
            text = padding + text.substring(pos).trim();
            pos = this.findWrapPos(text, width, 0);
            if (pos == -1) {
                break;
            }
            if (text.length() > width && pos == nextLineTabStop - 1) {
                pos = width;
            }
            sb.append(this.rtrim(text.substring(0, pos))).append(this.getNewLine());
        }
        sb.append(text);
        return sb;
    }
    
    private Appendable renderWrappedTextBlock(final StringBuffer sb, final int width, final int nextLineTabStop, final String text) {
        try {
            final BufferedReader in = new BufferedReader(new StringReader(text));
            boolean firstLine = true;
            String line;
            while ((line = in.readLine()) != null) {
                if (!firstLine) {
                    sb.append(this.getNewLine());
                }
                else {
                    firstLine = false;
                }
                this.renderWrappedText(sb, width, nextLineTabStop, line);
            }
        }
        catch (IOException ex) {}
        return sb;
    }
    
    protected String rtrim(final String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        int pos;
        for (pos = s.length(); pos > 0 && Character.isWhitespace(s.charAt(pos - 1)); --pos) {}
        return s.substring(0, pos);
    }
    
    public void setArgName(final String name) {
        this.defaultArgName = name;
    }
    
    public void setDescPadding(final int padding) {
        this.defaultDescPad = padding;
    }
    
    public void setLeftPadding(final int padding) {
        this.defaultLeftPad = padding;
    }
    
    public void setLongOptPrefix(final String prefix) {
        this.defaultLongOptPrefix = prefix;
    }
    
    public void setLongOptSeparator(final String longOptSeparator) {
        this.longOptSeparator = longOptSeparator;
    }
    
    public void setNewLine(final String newline) {
        this.defaultNewLine = newline;
    }
    
    public void setOptionComparator(final Comparator<Option> comparator) {
        this.optionComparator = comparator;
    }
    
    public void setOptPrefix(final String prefix) {
        this.defaultOptPrefix = prefix;
    }
    
    public void setSyntaxPrefix(final String prefix) {
        this.defaultSyntaxPrefix = prefix;
    }
    
    public void setWidth(final int width) {
        this.defaultWidth = width;
    }
    
    private static class OptionComparator implements Comparator<Option>, Serializable
    {
        private static final long serialVersionUID = 5305467873966684014L;
        
        @Override
        public int compare(final Option opt1, final Option opt2) {
            return opt1.getKey().compareToIgnoreCase(opt2.getKey());
        }
    }
}

