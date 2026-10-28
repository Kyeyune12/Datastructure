# text_wrap.c

**Extra Greedy Problem, in C.** Breaks a sentence into lines no wider than 20 characters, using as few lines as possible. Each line is filled with as many words as fit. Its output is identical to [TextWrap.java](TextWrap.java.md).

**Language:** C · **Java version:** [TextWrap.java](TextWrap.java.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
gcc text_wrap.c -o text_wrap
./text_wrap
```

## The problem

We have the words

```
Greedy algorithms make the best choice at each step and never go back to change it
```

and a screen that shows at most **WIDTH = 20** characters per line. Words on the same line are separated by exactly one space, and a word is never split. We want the **fewest lines**.

## The greedy idea

Keep adding words to the current line while they fit. When the next word doesn't fit, print the line and start a new one with that word. A line is never revisited once printed.

The test for "fits" is:

```
current length + 1 (the space) + word length ≤ WIDTH
```

## Walking through the code

### Constants and printing helpers

```c
#define WIDTH 20

void printLine(const char *line) { printf("|%-*s|\n", WIDTH, line); }
void printBorder(void) { /* prints |--------------------| */ }
```

`%-*s` means "left-align the string, padded with spaces to a width given as an argument". The `*` takes its value from the `WIDTH` argument. So every line is padded to exactly 20 characters between the `|` bars, which makes the width limit visible in the output.

### The words and the line buffer

```c
const char *words[] = { "Greedy", "algorithms", "make", ... "it" };
int n = sizeof(words) / sizeof(words[0]);   /* 16 words */
char line[200] = "";                          /* the line being filled */
```

`line` starts as an empty string. It's 200 bytes, far more than WIDTH + 1, so it can never overflow with this data. A line only grows beyond WIDTH if a single word is longer than WIDTH (see the notes at the end).

### The greedy loop

```c
for (int i = 0; i < n; i++) {
    int lineLen = strlen(line);
    int wordLen = strlen(words[i]);

    if (lineLen == 0) {
        strcpy(line, words[i]);                    /* first word on an empty line */
    } else if (lineLen + 1 + wordLen <= WIDTH) {
        strcat(line, " ");                         /* GREEDY: it fits, add it */
        strcat(line, words[i]);
    } else {
        printLine(line);                           /* doesn't fit: close the line */
        lineCount++;
        strcpy(line, words[i]);                    /* word starts the next line */
    }
}
```

There are three cases:

1. **Empty line:** the word goes in with no leading space (`strcpy` replaces the contents).
2. **Fits:** append a space, then the word (`strcat` joins onto the end).
3. **Doesn't fit:** print the current line, count it, and start the new line with this word.

### Flushing the last line

```c
if (strlen(line) > 0) { printLine(line); lineCount++; }
```

Inside the loop, a line is only printed when a word *fails* to fit. The final line never meets such a word, so it has to be printed after the loop. Forgetting this step is a classic bug.

## Trace (WIDTH = 20)

| Word | len | Line before | Test | Action | Line after |
|---|---|---|---|---|---|
| Greedy | 6 | "" | empty | start | `Greedy` (6) |
| algorithms | 10 | 6 | 6+1+10 = 17 ≤ 20 | add | `Greedy algorithms` (17) |
| make | 4 | 17 | 17+1+4 = 22 > 20 | **print line 1** | `make` (4) |
| the | 3 | 4 | 8 ≤ 20 | add | `make the` (8) |
| best | 4 | 8 | 13 ≤ 20 | add | `make the best` (13) |
| choice | 6 | 13 | 20 ≤ 20 | add (exactly full) | `make the best choice` (20) |
| at | 2 | 20 | 23 > 20 | **print line 2** | `at` (2) |
| each | 4 | 2 | 7 | add | `at each` (7) |
| step | 4 | 7 | 12 | add | `at each step` (12) |
| and | 3 | 12 | 16 | add | `at each step and` (16) |
| never | 5 | 16 | 22 > 20 | **print line 3** | `never` (5) |
| go | 2 | 5 | 8 | add | `never go` (8) |
| back | 4 | 8 | 13 | add | `never go back` (13) |
| to | 2 | 13 | 16 | add | `never go back to` (16) |
| change | 6 | 16 | 23 > 20 | **print line 4** | `change` (6) |
| it | 2 | 6 | 9 | add | `change it` (9) |
| (end) | | | | **print line 5** | |

Line 2 is exactly 20 characters. The `≤` (not `<`) in the test is what lets it fill completely.

## Output

```
Line width: 20 characters

|--------------------|
|Greedy algorithms   |
|make the best choice|
|at each step and    |
|never go back to    |
|change it           |
|--------------------|

Total lines used: 5
```

## Why greedy gives the fewest lines

The proof is a **"greedy stays ahead"** argument. Compare greedy with any other valid wrapping. After k lines, greedy has placed **at least as many words** as the other wrapping. For k = 1 this holds because greedy packs line 1 as full as possible. Suppose it holds after k lines. The other wrapping's line k + 1 starts at the same word as greedy's or earlier, so it can't reach further than greedy's line k + 1, which is packed as full as possible from its starting point. So greedy never falls behind, and it finishes using no more lines than any other wrapping.

**Fewest lines is not the same as "looks nicest".** Greedy can leave one line very short and the next very full. Word processors like TeX minimise *raggedness* (unused space squared) instead. That problem needs **dynamic programming**: greedy is optimal for the line count, but not for evenness.

## Complexity

| | Value |
|---|---|
| Time | O(total characters). Each word is measured and copied a constant number of times, but `strlen(line)` rescans the line on each step. That's O(WIDTH) per word, still linear for a fixed width |
| Space | O(WIDTH) for the line buffer |

## Notes

- A word longer than WIDTH would go on its own line and stick out past the right bar. The program doesn't split long words.
- `strcpy` and `strcat` don't check buffer sizes. They're safe here because of the 200-byte buffer, but production code would use `snprintf` or track the length explicitly.
