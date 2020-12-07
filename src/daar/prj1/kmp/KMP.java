package daar.prj1.kmp;

import daar.prj1.Main;
import daar.prj1.IRegexMatcher;

import java.util.Arrays;


/*
Implementation of KMP algorithm for pattern matching
 */
public class KMP implements IRegexMatcher {
    private final String regEx;
    private final int[] carry_over;

    public KMP(String regEx) {
        this.regEx = regEx;
        this.carry_over = table_builder(regEx);
        if (Main.DEBUG) {
            System.out.println("----------------- debug KMP ----------------------");
            System.out.println("  >> KMP Table: ");
            System.out.println(Arrays.toString(this.carry_over));
            System.out.println("----------------- debug KMP ----------------------");
        }
    }

    /*
     Matching the pattern while using the constructed table:
        - start comparing directly from the character that follows the prefix
        (because we are sure that the prefix will match since it is also the suffix of previous substring and that suffix already matched)
     */
    @Override
    public boolean match(String text) {
        int regex_length = this.regEx.length();
        int text_length = text.length();
        // case empty regex
        if (regex_length < 1) {
//            return text_length > 0;
            return true;
        }
        int i = 0; // index for text
        int j = 0; // index for regex
        while (i < text_length) {
            if (this.regEx.charAt(j) == text.charAt(i)) {
                j++;
                i++;
            }
            // matching
            if (j == regex_length) {
                if (Main.DEBUG) {
                    System.out.println("Found at index " + (i - j));
                }
                return true;
            }
            // mismatch after j matches
            if (i < text_length && this.regEx.charAt(j) != text.charAt(i)) {
                // Do not match carry_over[0..carry_over[j-1]] characters
                // they will match anyway
                if (j > 0) {
                    j = this.carry_over[j - 1];
                } else {
                    i = i + 1;
                }
            }
        }
        return false;
    }

    /*
    Computer KMP table:
        - for each substring of the regEx calculate the length of the longest prefix which is also a suffix
    */
    static int[] table_builder(String regEx) {
        int regEx_length = regEx.length();
        int[] carry_over = new int[regEx_length];
        if (regEx_length > 0) {
            carry_over[0] = 0; // always 0
        }
        // calculating carry_over from 1 to (regEx_length - 1)
        int i = 1;
        int previous_len = 0; // length of the previous longest prefix/suffix
        while (i < regEx_length) {
            if (regEx.charAt(i) == regEx.charAt(previous_len)) {
                previous_len++;
                carry_over[i] = previous_len;
                i++;
            } else {
                if (previous_len > 0) {
                    previous_len = carry_over[previous_len - 1];
                } else {
                    carry_over[i] = previous_len;
                    i++;
                }
            }
        }
        return carry_over;
    }
}
