# Knapsack.java

**Part B, Question 3.** The **0/1 knapsack** problem solved with dynamic programming. Each item is either taken whole or left out, and the program reports the best profit and which items to take. Every answer is checked against brute force.

**Language:** Java · **C version:** [knapsack.c](knapsack.c.md) · **Written answer:** [answer.md](answer.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
javac Knapsack.java
java Knapsack
```

## The problem

There are n items: item i has weight wᵢ and profit pᵢ, and the knapsack holds at most m. Choose xᵢ ∈ {0, 1} (leave or take each item) to **maximise Σ pᵢxᵢ** subject to **Σ wᵢxᵢ ≤ m**.

## The recurrence

V[i][c] is the best profit using only items 1..i with capacity c:

```
V[0][c] = 0                                        (no items, no profit)
V[i][c] = V[i-1][c]                                if w_i > c   (item i doesn't fit)
        = max( V[i-1][c],  p_i + V[i-1][c - w_i] ) otherwise
              skip item i     take item i
```

**Principle of optimality:** whatever is decided about item i, the decisions about items 1..i−1 must be the best possible for the capacity that remains.

**Finding which items were taken:** start at V[n][m] and work back. If V[i][c] ≠ V[i−1][c], item i must have been taken, so record xᵢ = 1 and set c −= wᵢ. Otherwise xᵢ = 0.

## How the code is organised

| Member | Purpose |
| --- | --- |
| `static int[][] V` | The DP table, (n+1) × (m+1) |
| `knapsack(n, m, w, p, take, showTable)` | Fills V, optionally prints it, works back to fill `take[]`, and returns V[n][m]. Arrays are **1-based**: `w[0]` and `p[0]` are unused |
| `bruteForce(n, m, w, p)` | Tries all 2ⁿ subsets using a bitmask. Used only as a check |
| `solve(title, …)` | Prints the problem, runs the DP and the check, and prints the profit, the x vector and the weight used |
| `main` | Three examples |

## Output

Example 1 (n = 3, m = 6, w = (2, 3, 4), p = (1, 2, 5)):

```
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

Working back from V[3][6] = 6:

- V[3][6] ≠ V[2][6] (6 against 3), so item 3 was taken and c becomes 6 − 4 = 2.
- V[2][2] = V[1][2] = 1, so item 2 wasn't taken.
- V[1][2] ≠ V[0][2] (1 against 0), so item 1 was taken and c becomes 0.

All three examples:

| Example | n, m | w | p | Profit | x |
| --- | --- | --- | --- | --- | --- |
| 1 | 3, 6 | (2, 3, 4) | (1, 2, 5) | **6** | (1, 0, 1) |
| 2 | 4, 8 | (2, 3, 4, 5) | (1, 2, 5, 6) | **8** | (0, 1, 0, 1) |
| 3 | 3, 50 | (10, 20, 30) | (60, 100, 120) | **220** | (0, 1, 1) |

Example 3 is the classic case where picking items **greedily by profit/weight ratio fails**. Greedy takes item 1 first (ratio 6), then item 2, for a total of 160. The best answer, 220, skips item 1.

## Complexity

- **Time Θ(n · m)** and **space Θ(n · m)** for the table.
- This is **pseudo-polynomial**: it grows with the *value* of m, not with the number of digits needed to write m. That is why the knapsack problem is still NP-hard.
- **`bruteForce`:** Θ(2ⁿ · n), fine for small n only.
- Working back to find the chosen items takes O(n).

## Notes

- If only the best profit is needed, a single row of size m + 1 is enough, looping c from m *downwards* so each item is used at most once. The full table is kept here so the chosen items can be recovered.
