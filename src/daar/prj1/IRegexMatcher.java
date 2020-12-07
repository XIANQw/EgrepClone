package daar.prj1;

// strategy interface => KMP or DFA
public interface IRegexMatcher {
    boolean match(String text);
}
