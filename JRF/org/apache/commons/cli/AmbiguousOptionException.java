package org.apache.commons.cli;

import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.cli.UnrecognizedOptionException;

public class AmbiguousOptionException extends UnrecognizedOptionException {

   private static final long serialVersionUID = 5829816121277947229L;
   private final Collection<String> matchingOptions;


   private static String createMessage(String option, Collection<String> matchingOptions) {
      StringBuilder buf = new StringBuilder("Ambiguous option: \'");
      buf.append(option);
      buf.append("\'  (could be: ");
      Iterator it = matchingOptions.iterator();

      while(it.hasNext()) {
         buf.append("\'");
         buf.append((String)it.next());
         buf.append("\'");
         if(it.hasNext()) {
            buf.append(", ");
         }
      }

      buf.append(")");
      return buf.toString();
   }

   public AmbiguousOptionException(String option, Collection<String> matchingOptions) {
      super(createMessage(option, matchingOptions), option);
      this.matchingOptions = matchingOptions;
   }

   public Collection<String> getMatchingOptions() {
      return this.matchingOptions;
   }
}
