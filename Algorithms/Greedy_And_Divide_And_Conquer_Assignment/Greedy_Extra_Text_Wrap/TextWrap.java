/*
 * EXTRA GREEDY PROBLEM: Text Line Wrapping
 *
 * Situation: We have a paragraph of words and a screen that can only show
 * a fixed number of characters per line (the line WIDTH).
 *
 * Goal: Break the text into lines so that no line is longer than WIDTH,
 * using as few lines as possible.
 *
 * Greedy idea: Put as many words as possible on the current line.
 * If the next word does not fit, start a new line with it.
 * (Words on the same line are separated by one space.)
 *
 * How to run:
 *   javac TextWrap.java
 *   java TextWrap
 */
public class TextWrap {

    public static void main(String[] args) {
        String text = "Greedy algorithms make the best choice at each step "
                    + "and never go back to change it";
        int width = 20; // maximum characters per line

        String[] words = text.split(" ");

        String line = "";     // the line we are currently filling
        int lineCount = 0;

        System.out.println("Line width: " + width + " characters\n");
        System.out.println("|" + "-".repeat(width) + "|");

        for (String word : words) {
            if (line.isEmpty()) {
                // empty line: the word always goes here
                line = word;
            } else if (line.length() + 1 + word.length() <= width) {
                // GREEDY CHOICE: the word fits (with one space), so add it
                line = line + " " + word;
            } else {
                // the word does not fit: finish this line, start a new one
                printLine(line, width);
                lineCount++;
                line = word;
            }
        }

        // print the last line that was being filled
        if (!line.isEmpty()) {
            printLine(line, width);
            lineCount++;
        }

        System.out.println("|" + "-".repeat(width) + "|");
        System.out.println("\nTotal lines used: " + lineCount);
    }

    // Prints one line between bars so you can see the width limit
    static void printLine(String line, int width) {
        System.out.println("|" + line + " ".repeat(Math.max(0, width - line.length())) + "|");
    }
}
