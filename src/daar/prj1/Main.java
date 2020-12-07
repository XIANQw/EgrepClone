package daar.prj1;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/*
Entry point
 */
public class Main {
    // args
    public static final boolean DEBUG = false;
    public static boolean LINE_NUMBER = false;
    public static boolean COUNT_LINES = false;
    public static boolean USE_DFA = false;
    private static List<String> file_paths;
    private static String regEx;

    /*
    init main with args from command line
     */
    public static void initArgs(String[] args) throws Exception {
        regEx = null;
        file_paths = new ArrayList<>();
        LINE_NUMBER = false;
        COUNT_LINES = false;
        USE_DFA = false;
        int args_length = args.length;
        if (args_length < 2) {
            throw new Exception("should have 2 or more arguments: regex and 1 path or more  (text files)");
        }
        boolean flagsDone = false;
        for (String arg : args) {
            if (!flagsDone && (arg.length() > 1 && arg.length() < 4) && arg.charAt(0) == '-') {
                flagsDone = true;
                switch (arg.charAt(1)) {
                    case 'c': {
                        COUNT_LINES = true;
                        break;
                    }
                    case 'n': {
                        LINE_NUMBER = true;
                        break;
                    }
                    case 'd': {
                        USE_DFA = true;
                        break;
                    }
                    default: {
                        break;
                    }
                }
            } else {
                if (regEx == null) {
                    regEx = arg;
                } else {
                    file_paths.add(arg);
                }
            }
        }
        if (regEx == null || file_paths.isEmpty()) {
            throw new Exception("Missing arguments");
        }
    }

    /*
    Main program
     */
    public static void main(String[] args) {
        try {
            // read args and init class variables
            initArgs(args);
            Egrep egrep;
            // force the use of DFA only strategy when flag -d is true
            if (!USE_DFA) {
                egrep = new Egrep(Main.regEx);
            } else {
                egrep = new EgrepDFA(Main.regEx);
            }
            for (String filepath : Main.file_paths) {
                try (
                        BufferedReader textBuffer = new BufferedReader(new InputStreamReader(new FileInputStream(filepath)))
                ) {
                    String[] results = egrep.match(textBuffer);
                    if (Main.DEBUG) {
                        System.out.println("------------- Search results ------------------");
                    }
                    if (Main.COUNT_LINES) {
                        System.out.println(results.length);
                    } else {
                        for (String line : results) {
                            System.out.println(line);
                        }
                    }

                } catch (IOException e) {
                    System.err.println("ERROR: " + filepath + " doesn't exist");
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.toString());
        }
    }
}