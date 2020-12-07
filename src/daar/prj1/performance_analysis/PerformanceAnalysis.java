package daar.prj1.performance_analysis;

import daar.prj1.Main;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Execute the 3 (Egrep, Egrep Clone with DFA only, Egrep Clone with DFA and KMP)
 * on many files (different size) and different patterns
 * and write the execution time to CSV
 */
public class PerformanceAnalysis {
    private static String nanoToMs(long nano) {
        return String.format("%.02f", (nano / 1000000.0));
    }

    private static void printStream(InputStream in) throws IOException {
        BufferedReader is = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = is.readLine()) != null) {
            System.out.println(line);
        }
    }

    public static void main(String[] args) throws IOException {
//        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        String result_output = "results.csv";
        PrintWriter pw = new PrintWriter(new File("./" + result_output));
        StringBuilder builder = new StringBuilder();
        builder.append("filename,file_size,pattern,egrep,egrep_dfa,egrep_dfa_and_kmp\n");
        String[] file_paths = new String[]{
                "Christ_Remembered_at_his_Table.txt",
                "Prospects_of_the_Church_of_England.txt",
                "The_Christian_serving_his_own_generation.txt",
                "The_Divine_and_Perpetual_Obligation_of_the_Observance_of_the_Sabbath.txt",
                "Man_nth.txt",
                "Post-scriptum_de_ma_vie.txt",
        };
        String[] patterns = new String[]{
                "every", // alphabetic concat only
                "1 and ", // concat only
                "(and|or|1 the)", // alternative only
                "any.h", // dot
                "hello world", // not match
                "(hello world)", // useless parenthesis
                "S(a|g|r)*on", // regex
                "(you|we|they) (are|were) .es*cribed as", // regex
                "", // empty regex
                ".", // dot only
                ".*", // universal only
                "a", // 1 char
        };
        long startTime, stopTime;
        String extra_args = "-c";
        for (String filepath : file_paths) {
            String full_path = "./src/daar/prj1/performance_analysis/" + filepath;
            for (String pattern : patterns) {

                // Unix egrep
                Runtime rt = Runtime.getRuntime();
                String[] cmd = {"/bin/sh", extra_args, "egrep " + extra_args + " \"" + pattern + "\" " + full_path};
                startTime = System.nanoTime();
                Process proc = rt.exec(cmd);
                stopTime = System.nanoTime();
                printStream(proc.getInputStream());
                long original_time = stopTime - startTime;

                // clone: DFA only
                startTime = System.nanoTime();
                Main.main(new String[]{extra_args + "d", pattern, full_path});
                stopTime = System.nanoTime();
                long clone_time = stopTime - startTime;

                // clone: DFA + KMP
                startTime = System.nanoTime();
                Main.main(new String[]{extra_args, pattern, full_path});
                stopTime = System.nanoTime();
                long clone_optimized_time = stopTime - startTime;

                // file size
                long file_size = Files.size(Paths.get(full_path));

                // writing to CSV
                builder.append(filepath).append(",")
                        .append(file_size).append(",")
                        .append(pattern).append(",")
                        .append(nanoToMs(original_time)).append(",")
                        .append(nanoToMs(clone_time)).append(",")
                        .append(nanoToMs(clone_optimized_time)).append("\n");
                System.out.println();
            }
        }
        pw.write(builder.toString());
        pw.close();
    }
}
