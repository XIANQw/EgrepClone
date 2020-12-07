package daar.prj1;

import daar.prj1.performance_analysis.PerformanceAnalysis;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/*
Testing the Main program
 */
class MainTest {
    private static final String FILEPATH = "./tests/A History of Babylon.txt";
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void testMain() {
        String regex = "anything";
        Main.main(new String[]{regex, FILEPATH});
        assertNotEquals("", outContent.toString());
    }

    @Test
    void testMain1() {
        String regex = "anything";
        Main.main(new String[]{"-c", regex, FILEPATH});
        assertNotEquals("", outContent.toString());
    }

    @Test
    void testMain2() {
        String regex = "anything";
        Main.main(new String[]{"-n", regex, FILEPATH});
        assertNotEquals("", outContent.toString());
    }

    @Test
    void testMain3() {
        String regex = "anything";
        Main.main(new String[]{"-d", regex, FILEPATH});
        assertNotEquals("", outContent.toString());
    }

    @Test
    void testPerformanceAnalysis() {
        assertDoesNotThrow(() -> PerformanceAnalysis.main(null));
    }
}