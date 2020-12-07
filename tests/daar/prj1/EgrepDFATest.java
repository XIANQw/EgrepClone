package daar.prj1;

import org.junit.jupiter.api.*;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

/*
Testing the strategy with DFA only
 */
class EgrepDFATest {
    private static final String FILEPATH = "./tests/A History of Babylon.txt";
    private BufferedReader textBuffer = null;

    @BeforeEach
    void setUp() throws IOException {
        textBuffer = new BufferedReader(new InputStreamReader(new FileInputStream(FILEPATH)));
    }

    @AfterEach
    void tearDown() throws IOException {
        if (textBuffer != null) {
            textBuffer.close();
            textBuffer = null;
        }
    }

    @Test
    void matchRegexEmptyPattern() throws Exception {
        String regex = "";
        EgrepDFA egrepDFA = new EgrepDFA(regex);
        assertEquals(13309, egrepDFA.match(this.textBuffer).length);
    }

    @Test
    void matchRegex1Char() throws Exception {
        String regex = "0";
        EgrepDFA egrepDFA = new EgrepDFA(regex);
        assertEquals(475, egrepDFA.match(this.textBuffer).length);
    }

    @Test
    void matchRegexConcatAlphabetic() throws Exception {
        String regex = "every";
        EgrepDFA egrepDFA = new EgrepDFA(regex);
        assertEquals(15, egrepDFA.match(this.textBuffer).length);
    }

    @Test
    void matchRegexConcat() throws Exception {
        String regex = "1 and ";
        EgrepDFA egrepDFA = new EgrepDFA(regex);
        assertEquals(4, egrepDFA.match(this.textBuffer).length);
    }

    @Test
    void matchRegexAlternative() throws Exception {
        String regex = "(and|or|1 the)";
        EgrepDFA egrepDFA = new EgrepDFA(regex);
        assertEquals(6303, egrepDFA.match(this.textBuffer).length);
    }

    @Test
    void matchRegexDotOnly() throws Exception {
        String regex = ".";
        EgrepDFA egrepDFA = new EgrepDFA(regex);
        assertEquals(11593, egrepDFA.match(this.textBuffer).length);
    }

    @Test
    void matchRegexDot() throws Exception {
        String regex = "any.h";
        EgrepDFA egrepDFA = new EgrepDFA(regex);
        assertEquals(5, egrepDFA.match(this.textBuffer).length);
    }

    @Test
    void matchRegexUniversalOnly() throws Exception {
        String regex = ".*";
        EgrepDFA egrepDFA = new EgrepDFA(regex);
        assertEquals(13309, egrepDFA.match(this.textBuffer).length);
    }

    @Test
    void matchRegex() throws Exception {
        String regex = "(you|we|they) (are|were) .es*cribed as";
        EgrepDFA egrepDFA = new EgrepDFA(regex);
        assertEquals(1, egrepDFA.match(this.textBuffer).length);
    }

    @Test
    void matchRegex2() throws Exception {
        String regex = "S(a|g|r)*on";
        EgrepDFA egrepDFA = new EgrepDFA(regex);
        assertEquals(30, egrepDFA.match(this.textBuffer).length);
    }

    @Test
    void matchSpecChars() throws Exception {
        String regex = "; ";
        EgrepDFA egrepDFA = new EgrepDFA(regex);
        assertEquals(572, egrepDFA.match(this.textBuffer).length);
    }

    @Test
    void matchUselessParenthesis() throws Exception {
        String regex = "(ab)";
        EgrepDFA egrepDFA = new EgrepDFA(regex);
        assertEquals(2381, egrepDFA.match(this.textBuffer).length);
    }

    @Test
    void matchUselessParenthesis1Char() throws Exception {
        String regex = "(v)";
        EgrepDFA egrepDFA = new EgrepDFA(regex);
        assertEquals(4020, egrepDFA.match(this.textBuffer).length);
    }

    @Test
    void notMatchRegex() throws Exception {
        String regex = "hello world";
        EgrepDFA egrepDFA = new EgrepDFA(regex);
        assertEquals(0, egrepDFA.match(this.textBuffer).length);
    }

    @Test
    void wrongPatterns() {
        String[] regexes = {"(a", "|a", "\\(a", "*a", "a)"};
        for (String regex : regexes) {
            assertThrows(Exception.class, () -> {
                EgrepDFA egrepDFA = new EgrepDFA(regex);
                assertEquals(4, egrepDFA.match(this.textBuffer).length);
            });
        }
    }
}