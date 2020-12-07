package daar.prj1;

import daar.prj1.kmp.KMP;
import daar.prj1.regex.Automaton;
import daar.prj1.regex.RegEx;
import daar.prj1.regex.RegExTree;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

// Egrep command class
public class Egrep {
    private final String regEx;
    protected final IRegexMatcher regexMatcher;
    protected BufferedReader textBuffer;

    public Egrep(String regEx) throws Exception {
        this(regEx, true);
    }

    protected Egrep(String regEx, boolean optimize) throws Exception {
        this.regEx = regEx;
        if (optimize && isConcatenation(regEx)) {
            // use KMP
            this.regexMatcher = new KMP(regEx);
        } else {
            // use DFA
            if (!this.regEx.equals("")) {
                RegExTree regTree;
                try {
                    regTree = RegEx.parse(this.regEx);
                } catch (Exception e) {
                    throw new Exception("Regular expression \"" + this.regEx + "\" isn't valid");
                }
                Automaton nfa = Automaton.parse(regTree);
                Automaton dfa = Automaton.transformNFAToDFA(nfa);
                Automaton minimalDFA = Automaton.minimize(dfa);
                this.regexMatcher = minimalDFA;

                if (Main.DEBUG) {
                    // debug mode => print intermediate results
                    System.out.println("----------------- debug DFA ----------------------");
                    System.out.println("  >> Tree result: ");
                    System.out.println(regTree.toString() + ".");
                    System.out.println("  >> NFA result: ");
                    System.out.println(Automaton.toString(nfa));
                    System.out.println("  >> DFA result: ");
                    System.out.println(Automaton.toString(dfa));
                    System.out.println("  >> minimal DFA result: ");
                    System.out.println(Automaton.toString(minimalDFA));
                }
            } else {
                this.regexMatcher = null;
            }
        }
    }

    public String[] match(BufferedReader textBuffer) throws Exception {
        this.textBuffer = textBuffer;
        String line;
        int lineNumber = 1;
        List<String> results = new ArrayList<>();
        while ((line = this.textBuffer.readLine()) != null) {
            if (this.regexMatcher == null || this.regexMatcher.match(line)) {
                if (Main.LINE_NUMBER) {
                    results.add(lineNumber + ":" + line);
                } else {
                    results.add(line);
                }
            }
            lineNumber++;
        }
        // converting list to array
        String[] resArray = new String[results.size()];
        results.toArray(resArray);
        return resArray;
    }

    private static boolean isConcatenation(String regEx) {
        // check if regEx is exclusively a concatenation of ASCII letters => use KMP
        for (int i = 0; i < regEx.length(); i++) {
            switch (regEx.charAt(i)) {
                case '(':
                case '.':
                case '*':
                case '|': {
                    return false;
                }
            }
        }
        return true;
    }
}
