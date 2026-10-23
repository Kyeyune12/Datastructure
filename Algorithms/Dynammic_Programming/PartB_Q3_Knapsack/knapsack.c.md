# knapsack.c

**Part B, Question 3, in C.** The **0/1 knapsack** problem by dynamic programming: it builds the V[i][c] table, finds which items were taken, and checks each answer with brute force. Its output is identical to [Knapsack.java](Knapsack.java.md).

**Language:** C · **Java version:** [Knapsack.java](Knapsack.java.md) · **Written answer:** [answer.md](answer.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
gcc -O2 -o knapsack knapsack.c
./knapsack
```

## The recurrence

```
V[0][c] = 0
V[i][c] = V[i-1][c]                                if w_i > c
        = max( V[i-1][c],  p_i + V[i-1][c - w_i] ) otherwise
```

The answer is V[n][m]. To find the chosen items, work back from V[n][m]: if V[i][c] ≠ V[i−1][c], item i was taken, so c −= wᵢ. [Knapsack.java.md](Knapsack.java.md#the-recurrence) explains this in full.

## How the code is organised

| Part | Purpose |
| --- | --- |
| `#define MAXN 10`, `MAXW 64` | The largest number of items and the largest capacity |
| `int V[MAXN+1][MAXW+1];` | The global DP table |
| `knapsack(n, m, w, p, take, showTable)` | Fills V, optionally prints it, works back to fill `take[]`, and returns V[n][m] |
| `bruteForce(n, m, w, p)` | Tries all 2ⁿ subsets with a bitmask |
| `solve(title, n, m, w, p, showTable)` | Prints the problem and the results, and compares them with brute force |
| `main` | Three examples. Arrays are **1-based**, with index 0 unused, to match the lecture notes |

## Output

```
===== Example 1 (n = 3, m = 6) =====
DP table V[i][c]:
  i\c    0   1   2   3   4   5   6
    0    0   0   0   0   0   0   0
    1    0   0   1   1   1   1   1
    2    0   0   1   2   2   3   3
    3    0   0   1   2   5   5   6

Maximum profit = 6
Items taken (x_i): (1, 0, 1)
Total weight used = 6 of 6
Brute-force check = 6 -> OK
```

The other examples give profit **8** with x = (0, 1, 0, 1), and profit **220** with x = (0, 1, 1). Both are confirmed `OK` by brute force. See [output.txt](output.txt).

## Complexity

- **Θ(n · m)** time and space. This is pseudo-polynomial, because it depends on the size of m.
- **Brute force:** Θ(2ⁿ · n).

## C-specific notes

- **Fixed limits:** n ≤ 10 and m ≤ 64, because the table is a static global array. Example 3 (m = 50) fits. A bigger capacity needs a larger `MAXW`, or a table allocated with `malloc`.
- `solve` calls `bruteForce` twice, once to print it and once to compare. This is harmless, but storing the result in a variable would avoid the repeated work.
- `take[]` is set from the comparison directly: `take[i] = (V[i][c] != V[i-1][c]);`.
