/*
 * EXTRA DIVIDE & CONQUER PROBLEM: Longest Common Prefix of a List of Words
 *
 * Situation: We have a list of words, e.g. "interview", "internet",
 * "interval", "internal", "interact".
 *
 * Goal: Find the longest beginning (prefix) that ALL the words share.
 * Here the answer is "inter".
 *
 * Divide & Conquer idea:
 *   DIVIDE : split the list into a left half and a right half.
 *   CONQUER: find the common prefix of each half (by calling ourselves).
 *   COMBINE: the answer is the common prefix of those two results.
 *   BASE CASE: a list with one word -> the word itself is the prefix.
 *
 * How to run:
 *   javac LongestCommonPrefix.java
 *   java LongestCommonPrefix
 */
public class LongestCommonPrefix {

    // COMBINE step: common prefix of just TWO words
    static String commonPrefix(String a, String b) {
        int i = 0;
        while (i < a.length() && i < b.length() && a.charAt(i) == b.charAt(i)) {
            i++;
        }
        return a.substring(0, i);
    }

    // Common prefix of words[low..high]
    static String lcp(String[] words, int low, int high) {
        // BASE CASE: only one word
        if (low == high) {
            return words[low];
        }

        // DIVIDE
        int mid = (low + high) / 2;

        // CONQUER
        String left  = lcp(words, low, mid);
        String right = lcp(words, mid + 1, high);

        // COMBINE
        String result = commonPrefix(left, right);
        System.out.println("  combine \"" + left + "\" + \"" + right + "\" -> \"" + result + "\"");
        return result;
    }

    public static void main(String[] args) {
        String[] words = {"interview", "internet", "interval", "internal", "interact"};

        System.out.print("Words: ");
        for (String w : words) {
            System.out.print(w + " ");
        }
        System.out.println("\n");

        String answer = lcp(words, 0, words.length - 1);

        System.out.println("\nLongest common prefix: \"" + answer + "\"");
    }
}
