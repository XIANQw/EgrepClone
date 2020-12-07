package daar.prj1;


public class EgrepDFA extends Egrep {
    public EgrepDFA(String regEx) throws Exception {
        // force the use of DFA
        super(regEx, false);
    }
}