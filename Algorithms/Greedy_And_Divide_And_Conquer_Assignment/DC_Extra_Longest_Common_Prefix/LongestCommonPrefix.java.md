# LongestCommonPrefix.java

**Extra Divide & Conquer Problem, in Java.** Finds the longest prefix shared by every word in a list (`"inter"` for this data) by solving each half of the list recursively and combining the two answers. Its output is identical to [longest_common_prefix.c](longest_common_prefix.c.md).

**Language:** Java · **C version:** [longest_common_prefix.c](longest_common_prefix.c.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
javac LongestCommonPrefix.java
java LongestCommonPrefix
```

## The problem in one paragraph

For `interview, internet, interval, internal, interact`, find the longest starting string that all of them share. The answer is `"inter"`, because the 6th letters (v, n, v, n, a) differ.

## The divide & conquer idea

- **Divide:** split the list at `mid`.
- **Conquer:** find the common prefix of each half recursively.
- **Combine:** take the common prefix of those two prefixes.
- **Base case:** one word, which is its own prefix.

## Walking through the code

### `commonPrefix` — the combine step

```java
static String commonPrefix(String a, String b) {
    int i = 0;
    while (i < a.length() && i < b.length() && a.charAt(i) == b.charAt(i)) i++;
    return a.substring(0, i);
}
```

Walks both strings until one ends or the letters differ, then returns the matching part. `substring(0, i)` gives characters 0 to i − 1, which are exactly the i matching letters.

### `lcp` — the recursion

```java
static String lcp(String[] words, int low, int high) {
    if (low == high) return words[low];                   // BASE CASE
    int mid = (low + high) / 2;                           // DIVIDE
    String left  = lcp(words, low, mid);                  // CONQUER
    String right = lcp(words, mid + 1, high);
    String result = commonPrefix(left, right);            // COMBINE
    System.out.println("  combine \"" + left + "\" + \"" + right + "\" -> \"" + result + "\"");
    return result;
}
```

Java handles strings comfortably, so `lcp` returns the **prefix string itself**. The combine step is just "common prefix of the two half-answers". There's no need for the C version's length trick, where it returns an `int` and compares one representative word from each half. The two approaches give the same answer.

The `println` inside `lcp` shows each combine as it happens. Combines print bottom-up: the deepest pairs first, the whole list last.

### `main`

Prints the words, calls `lcp(words, 0, words.length − 1)`, and prints the result. `System.out.println("\n")` prints the `"\n"` plus println's own newline, which gives the blank line seen in the output.

## Trace

```
                    lcp(0,4)
               /               \
        lcp(0,2)               lcp(3,4)
        /      \               /      \
   lcp(0,1)  "interval"   "internal" "interact"
   /      \
"interview" "internet"
```

| # | Combine | Result |
|---|---|---|
| 1 | `interview` + `internet` | `inter` (6th letter v ≠ n) |
| 2 | `inter` + `interval` | `inter` |
| 3 | `internal` + `interact` | `inter` (6th letter n ≠ a) |
| 4 | `inter` + `inter` | `inter` |

## Output

```
Words: interview internet interval internal interact 

  combine "interview" + "internet" -> "inter"
  combine "inter" + "interval" -> "inter"
  combine "internal" + "interact" -> "inter"
  combine "inter" + "inter" -> "inter"

Longest common prefix: "inter"
```

## Complexity

n words, shortest word length m.

| | Value |
|---|---|
| Recurrence | T(n) = 2T(n/2) + O(m) |
| Time | **O(n · m)** (n − 1 combines, each comparing and copying at most m characters) |
| Space | O(m log n): one prefix string of at most m characters is held at each level of the O(log n)-deep recursion |

## C vs Java differences

| | C | Java |
|---|---|---|
| `lcp` returns | an `int` length | the prefix `String` |
| Combine | compares `words[low]` with `words[mid+1]`, up to `min(left, right)` letters | `commonPrefix(left, right)` |
| Showing a prefix | `printf("%.*s", len, word)` | the string itself |
| Memory | no strings created | `substring` creates new strings (garbage collected) |

## What changed (simplified version)

This file never had imports and was already written step by step. The only change: the loop that prints the words, `for (String w : words) System.out.print(w + " ");`, now has braces and its body on its own line, like every other loop in the project. The output is the same.
