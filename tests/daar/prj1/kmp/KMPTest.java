package daar.prj1.kmp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
Testing the implementation of KMP
 */
class KMPTest {
    @Test
    void matchRegex() {
        String[] regexes = {"jmia$", "acedca", "gzizj", "dzdd", "a", "$", ""};
        String text = "jmia$gn eqdtdbpsfp acedca cbinpfr &dzd&dzdd \\\"iydcgzizj";
        for (String regex : regexes) {
            KMP kmp = new KMP(regex);
            assertTrue(kmp.match(text));
        }
    }

    @Test
    void notMatchRegex() {
        String[] regexes = {"jmia$ ", "aced.a", "ggzizj", "dzdd\\"};
        String text = "jmia$gn eqdtdbpsfp acedca cbinpfr &dzd&dzdd \\\"iydcgzizj";
        for (String regex : regexes) {
            KMP kmp = new KMP(regex);
            assertFalse(kmp.match(text));
        }
    }

    @Test
    void KMPtableBuilder() {
        String regex = "ABCDABD";
        int[] kmp_table = {0, 0, 0, 0, 1, 2, 0};
        assertArrayEquals(kmp_table, KMP.table_builder(regex));
    }
}