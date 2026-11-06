# TextWrap.java

**Extra Greedy Problem, in Java.** Wraps a sentence into lines of at most 20 characters using as few lines as possible, putting as many words as fit on each line. Its output is identical to [text_wrap.c](text_wrap.c.md).

**Language:** Java · **C version:** [text_wrap.c](text_wrap.c.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
javac TextWrap.java
java TextWrap
```

> `"-".repeat(width)` needs **Java 11 or newer**.

## The problem in one paragraph

Fit the sentence *"Greedy algorithms make the best choice at each step and never go back to change it"* onto lines no wider than 20 characters. Words are separated by single spaces and never split. Use the fewest lines.

## The greedy idea

Fill the current line with words for as long as `line length + 1 + word length ≤ width`. When a word doesn't fit, print the line and start a new one with that word.

## Walking through the code

### Getting the words

```java
String text = "Greedy algorithms make the best choice at each step "
            + "and never go back to change it";
int width = 20;
String[] words = text.split(" ");
```

The C version hard-codes an array of words. Java starts from a single sentence and uses `split(" ")` to cut it at each space, giving the same 16 words. To wrap different text, just change the string. (Two spaces in a row would produce an empty "word", so the input should use single spaces.)

### The greedy loop

```java
String line = "";
for (String word : words) {
    if (line.isEmpty()) {
        line = word;                                   // first word on the line
    } else if (line.length() + 1 + word.length() <= width) {
        line = line + " " + word;                      // GREEDY: it fits
    } else {
        printLine(line, width);                        // doesn't fit: close the line
        lineCount++;
        line = word;                                   // start the next line
    }
}
```

The three cases match the C program:

1. **Empty line:** the word goes in without a leading space.
2. **Fits:** `line + " " + word` makes a **new** string. Java strings are immutable, so the variable `line` is pointed at the new one. C instead edits its buffer in place with `strcat`.
3. **Doesn't fit:** print, count, and restart with the word.

### Flushing the last line

```java
if (!line.isEmpty()) { printLine(line, width); lineCount++; }
```

The final line is never followed by a word that fails to fit, so it has to be printed after the loop.

### `printLine` — showing the width limit

```java
static void printLine(String line, int width) {
    System.out.println("|" + line + " ".repeat(Math.max(0, width - line.length())) + "|");
}
```

Pads the line with spaces up to `width` so the closing `|` lines up. `Math.max(0, …)` keeps the padding from going negative if a single word is longer than `width`. `repeat` with a negative count would throw an exception. The C version gets the same padding from `printf("%-*s")`.

## Trace

| Line | Contents | Length |
|---|---|---|
| 1 | `Greedy algorithms` | 17 (adding "make" → 22, too long) |
| 2 | `make the best choice` | 20 (exactly full; `≤` allows it) |
| 3 | `at each step and` | 16 (adding "never" → 22) |
| 4 | `never go back to` | 16 (adding "change" → 23) |
| 5 | `change it` | 9 (flushed after the loop) |

[text_wrap.c.md](text_wrap.c.md) traces it word by word.

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

## Why it is optimal

**Greedy stays ahead:** after every line, greedy has placed at least as many words as any other valid wrapping, because each of its lines is packed as full as possible from its starting word. So it can never need more lines. It does *not* produce the most evenly spaced text. Minimising raggedness needs dynamic programming.

## Complexity

| | Value |
|---|---|
| Time | O(total characters). `line + " " + word` copies the line each time, but a line is at most `width` long, so that's O(width) per word |
| Space | O(total characters) for the `words` array; O(width) for the current line |

For very long texts, a `StringBuilder` would avoid the repeated copying.

## C vs Java differences

| | C | Java |
|---|---|---|
| Input | array of word literals | one sentence + `split(" ")` |
| Current line | `char line[200]`, edited with `strcpy` / `strcat` | immutable `String`, rebuilt with `+` |
| Length | `strlen(line)` (scans the string) | `line.length()` (stored, O(1)) |
| Padding | `printf("|%-*s|", WIDTH, line)` | `" ".repeat(...)` with `Math.max(0, ...)` |
| Width | `#define WIDTH 20` | local `int width = 20` |
