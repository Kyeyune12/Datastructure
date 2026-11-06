# knapsack.c

**Part B, Question 3, in C.** **0/1 knapsack** with dynamic programming: each item is taken whole or left behind. Same algorithm and same output as [Knapsack.java](Knapsack.java.md).

**Written answer:** [answer.md](answer.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
gcc knapsack.c -o knapsack
./knapsack
```

## The rule

```
V[i][c] = best profit using only items 1..i with capacity c

V[0][c] = 0
V[i][c] = V[i-1][c]                                 if w_i > c   (doesn't fit)
        = max( V[i-1][c],  p_i + V[i-1][c - w_i] )  otherwise   (skip it, or take it)
```

The answer is V[n][m]. To find **which** items were taken, walk back from V[n][m]: if V[i][c] differs from V[i-1][c], item i was taken, so subtract its weight and continue.

## What is inside

| Function | What it does |
| --- | --- |
| `knapsack(n, m, w, p, take)` | Fills the table `V`, walks back to fill `take[]`, returns the best profit |
| `printTable(n, m)` | Prints `V` |
| `bruteForce(n, m, w, p)` | Tries all 2^n subsets (each bit of `mask` = one item) to double-check the DP |
| `solve(...)` | Prints one example: items, table, best profit, items taken, weight used, brute-force check |

The arrays `w[]` and `p[]` start at index 1, like the notes; index 0 is unused.

## Results

| Example | Best profit | Items taken |
| --- | --- | --- |
| n = 3, m = 6, w = (2, 3, 4), p = (1, 2, 5) | **6** | (1, 0, 1) |
| n = 4, m = 8, w = (2, 3, 4, 5), p = (1, 2, 5, 6) | **8** | (0, 1, 0, 1) |
| n = 3, m = 50, w = (10, 20, 30), p = (60, 100, 120) | **220** | (0, 1, 1) |

Every example prints `Brute-force check ... OK`.

## Complexity

- **Time Θ(n · m)**: one table cell per item and capacity.
- **Space Θ(n · m)** for the table.
- Brute force is Θ(2^n · n), which is why DP is used.

## How the C file is written

Same team style as every C file in this repo:

- `#define` for sizes (`MAXN`, `MAXW`).
- The table `V` is a global 2D array; `w[]` and `p[]` are indexed from 1 so they match the lecture.
- Every `if` and loop has braces, with one statement per line, so each step can be pointed at during the presentation.

## Questions you may be asked

- **Why not greedy (best profit per kilo first)?** That works for *fractional* knapsack (see the bandwidth problem), but not 0/1. Example 3: greedy takes item 1 (6 per unit) and item 2, for 160; the DP finds 220.
- **Why is it called pseudo-polynomial?** The time depends on the *value* m, not just the number of items. A huge capacity means a huge table.
- **How do you know which items were taken?** If a cell differs from the cell above it, the item on that row must have been taken.
