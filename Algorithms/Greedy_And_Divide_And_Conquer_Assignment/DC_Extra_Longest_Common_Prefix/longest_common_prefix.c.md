# longest_common_prefix.c

**Extra Divide & Conquer Problem, in C.** Finds the longest beginning that every word in a list shares (for example `"inter"`) by splitting the list in halves, solving each half, and combining the results. Its output is identical to [LongestCommonPrefix.java](LongestCommonPrefix.java.md).

**Language:** C · **Java version:** [LongestCommonPrefix.java](LongestCommonPrefix.java.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
gcc longest_common_prefix.c -o longest_common_prefix
./longest_common_prefix
```

## The problem

Given the words

```
interview  internet  interval  internal  interact
```

find the longest **prefix** (starting letters) that **all** of them have. Here the answer is `"inter"`: every word starts with i-n-t-e-r, but the 6th letters (v, n, v, n, a) are not all the same.

## The divide & conquer idea

- **Divide:** split the list into a left half and a right half.
- **Conquer:** find the common prefix of each half recursively.
- **Combine:** the common prefix of the whole list is the common prefix of those two answers.
- **Base case:** a list of one word, whose common prefix is the whole word.

This works because "is a prefix of every word" can be checked half by half: a string is a prefix of all the words exactly when it is a prefix of everything in the left half and of everything in the right half.

## The C trick: work with lengths, not strings

Building new strings in C means allocating memory, copying characters and remembering to `free` them. This program avoids all of that by tracking only a **length**:

> If the common prefix of `words[low..high]` has length L, then **every** word in that range starts with the same L letters. So the prefix is simply the first L letters of `words[low]`.

`lcp` returns an `int`, and the answer is printed at the end as the first `length` letters of `words[0]`.

## Walking through the code

### `commonLength` — the combine step

```c
int commonLength(const char *a, const char *b, int limit) {
    int i = 0;
    while (i < limit && a[i] != '\0' && b[i] != '\0' && a[i] == b[i]) i++;
    return i;
}
```

Counts how many starting letters `a` and `b` share, looking at no more than `limit` letters. The loop stops at the first of these:

- `i < limit` fails: we never need more than the shorter half-answer.
- `a[i] != '\0'` or `b[i] != '\0'` fails: a word has ended.
- `a[i] == b[i]` fails: the letters differ.

### `lcp` — the recursion

```c
int lcp(const char *words[], int low, int high) {
    if (low == high) return strlen(words[low]);            /* BASE CASE */
    int mid = (low + high) / 2;                            /* DIVIDE */
    int left  = lcp(words, low, mid);                      /* CONQUER */
    int right = lcp(words, mid + 1, high);
    int limit = (left < right) ? left : right;             /* COMBINE */
    int result = commonLength(words[low], words[mid + 1], limit);
    printf("  combine \"%.*s\" + \"%.*s\" -> \"%.*s\"\n", ...);
    return result;
}
```

**Why the combine step only compares two words.** The left half's prefix is the first `left` letters of `words[low]`. The right half's prefix is the first `right` letters of `words[mid + 1]`. The common prefix of the two halves therefore can't be longer than `min(left, right)`, and within that limit it's just where `words[low]` and `words[mid + 1]` first differ. One representative word from each half is enough.

**`%.*s`** prints at most the given number of characters from a string. `printf("%.*s", 5, "interview")` prints `inter`. That's how the program shows prefixes without ever building them as separate strings.

### `main`

Prints the words, calls `lcp(words, 0, n − 1)`, then prints the first `length` letters of `words[0]`.

## Trace

The recursion tree (indices 0–4):

```
                    lcp(0,4)  mid = 2
               /                     \
        lcp(0,2) mid = 1           lcp(3,4) mid = 3
        /          \                /        \
   lcp(0,1)      lcp(2,2)      lcp(3,3)   lcp(4,4)
   /      \      "interval"    "internal" "interact"
lcp(0,0) lcp(1,1)
"interview" "internet"
```

Combines happen bottom-up, in the order the program prints them:

| # | Call | left | right | limit | Compare | Result |
|---|---|---|---|---|---|---|
| 1 | lcp(0,1) | 9 (interview) | 8 (internet) | 8 | interview / internet: differ at index 5 (v vs n) | 5 → `inter` |
| 2 | lcp(0,2) | 5 | 8 (interval) | 5 | interview / interval: first 5 letters equal | 5 → `inter` |
| 3 | lcp(3,4) | 8 (internal) | 8 (interact) | 8 | internal / interact: differ at index 5 (n vs a) | 5 → `inter` |
| 4 | lcp(0,4) | 5 | 5 | 5 | interview / internal: first 5 letters equal | 5 → `inter` |

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

Let n = number of words and m = length of the shortest word.

| | Value |
|---|---|
| Recurrence | T(n) = 2T(n/2) + O(m) |
| Time | **O(n · m)**. There are n − 1 combine steps, each comparing at most m letters (the base cases also run `strlen`) |
| Extra space | **O(log n)** for the recursion stack. No strings are copied, thanks to the length trick |

The simple method (compare every word with the first one, letter by letter) is also O(n · m). Divide and conquer doesn't make this problem faster. It shows the technique on a clean example, and its two halves could be solved in parallel.

## Notes

- If any word were empty, its `strlen` would be 0, the limit would become 0, and the answer would be `""`. That's correct.
- The same function is used in [route_summarization.c](../DC_Extra_Route_Summarization/route_summarization.c.md), with IP addresses written as 32-bit binary strings instead of words.
