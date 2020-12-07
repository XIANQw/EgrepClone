package daar.prj1;

import org.junit.jupiter.api.*;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

/*
Testing the strategy with DFA+KMP
 */
class EgrepTest {
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
        Egrep egrep = new Egrep(regex);
        assertEquals(13309, egrep.match(this.textBuffer).length);
    }

    @Test
    void matchRegex1Char() throws Exception {
        String regex = "0";
        Egrep egrep = new Egrep(regex);
        assertEquals(475, egrep.match(this.textBuffer).length);
    }

    @Test
    void matchRegexConcatAlphabetic() throws Exception {
        String regex = "every";
        Egrep egrep = new Egrep(regex);
        assertEquals(15, egrep.match(this.textBuffer).length);
    }

    @Test
    void matchRegexConcat() throws Exception {
        String regex = "1 and ";
        Egrep egrep = new Egrep(regex);
        assertEquals(4, egrep.match(this.textBuffer).length);
    }


    @Test
    void matchSpecChars() throws Exception {
        String regex = "; ";
        Egrep egrep = new Egrep(regex);
        assertEquals(572, egrep.match(this.textBuffer).length);
    }


    @Test
    void notMatchRegex() throws Exception {
        String regex = "hello world";
        Egrep egrep = new Egrep(regex);
        assertEquals(0, egrep.match(this.textBuffer).length);
    }

    @Test
    void matchUselessParenthesis1Char() throws Exception {
        String regex = "(v)";
        Egrep egrep = new Egrep(regex);
        assertEquals(4020, egrep.match(this.textBuffer).length);
    }
}