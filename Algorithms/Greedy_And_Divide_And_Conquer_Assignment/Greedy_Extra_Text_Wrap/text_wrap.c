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
 *   gcc text_wrap.c -o text_wrap
 *   ./text_wrap
 */
#include <stdio.h>
#include <string.h>

#define WIDTH 20   /* maximum characters per line */

/* Prints one line between bars so you can see the width limit */
void printLine(const char *line) {
    printf("|%-*s|\n", WIDTH, line);
}

void printBorder(void) {
    printf("|");
    for (int i = 0; i < WIDTH; i++) printf("-");
    printf("|\n");
}

int main(void) {
    const char *words[] = {
        "Greedy", "algorithms", "make", "the", "best", "choice", "at", "each",
        "step", "and", "never", "go", "back", "to", "change", "it"
    };
    int n = sizeof(words) / sizeof(words[0]);

    char line[200] = "";   /* the line we are currently filling */
    int lineCount = 0;

    printf("Line width: %d characters\n\n", WIDTH);
    printBorder();

    for (int i = 0; i < n; i++) {
        int lineLen = strlen(line);
        int wordLen = strlen(words[i]);

        if (lineLen == 0) {
            /* empty line: the word always goes here */
            strcpy(line, words[i]);
        } else if (lineLen + 1 + wordLen <= WIDTH) {
            /* GREEDY CHOICE: the word fits (with one space), so add it */
            strcat(line, " ");
            strcat(line, words[i]);
        } else {
            /* the word does not fit: finish this line, start a new one */
            printLine(line);
            lineCount++;
            strcpy(line, words[i]);
        }
    }

    /* print the last line that was being filled */
    if (strlen(line) > 0) {
        printLine(line);
        lineCount++;
    }

    printBorder();
    printf("\nTotal lines used: %d\n", lineCount);
    return 0;
}
