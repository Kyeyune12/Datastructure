# arbitrary_st.c

**Part A, Question 4, in C.** Shows that the same BCOST algorithm works for an arbitrary source s and target t. Each answer is checked against a brute-force search, and the program prints the full distance matrix. Its output is identical to [ArbitraryST.java](ArbitraryST.java.md).

**Language:** C · **Java version:** [ArbitraryST.java](ArbitraryST.java.md) · **Written answer:** [answer.md](answer.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
gcc -O2 -o arbitrary_st arbitrary_st.c
./arbitrary_st
```

## Answer in short

Yes, S and t can be any vertices of a graph without cycles:

- BCOST(s) = 0, and every other vertex starts at ∞;
- vertices s can't reach stay at ∞, so an unreachable t is reported as **no path**;
- s = t gives **0**.

## How the code is organised

| Part | Purpose |
| --- | --- |
| `bcostFrom(s, bcost, d)` | The generalised BCOST. Loops j = s+1 … N (a topological order here), scanning every edge to find those into j |
| `int bruteBest;` + `dfs(u, t, cost)` | Brute-force check. `dfs` follows every path from u, adding up its cost, and updates the global `bruteBest` whenever it reaches t more cheaply |
| `main` | Tests nine (s, t) pairs, printing the DP cost, the brute-force cost and the path, then prints the 12 × 12 matrix |

**Difference from Java:** the Java `brute(u, t)` *returns* the best cost. The C version keeps a running cost as a parameter and stores the best in a global variable. Both explore exactly the same paths.

## Output

```
  s ->  t | DP cost | brute | path
  1 -> 12 |      16 |    16 | 1 -> 3 -> 6 -> 10 -> 12
  2 -> 12 |       7 |     7 | 2 -> 7 -> 10 -> 12
  3 -> 10 |       7 |     7 | 3 -> 6 -> 10
  5 ->  9 |      15 |    15 | 5 -> 7 -> 9
  2 -> 11 |       7 |     7 | 2 -> 8 -> 11
  1 ->  7 |      11 |    11 | 1 -> 2 -> 7
  4 ->  9 |     INF |   INF | no path
  6 ->  6 |       0 |     0 | 6
 10 ->  2 |     INF |   INF | no path
All results match brute force: YES
```

It then prints the distance matrix, which is upper-triangular because every edge goes to a later stage. See [output.txt](output.txt) for the full matrix.

## Complexity

- **`bcostFrom`:** O(n · e), from scanning the edge list per vertex. With predecessor lists it would be O(n + e).
- **Matrix:** n runs, so O(n² · e).
- **`dfs`:** tries every path, which can be exponential. It is a checking tool only.

## C-specific notes

- Each path is rebuilt into a temporary array by following `d[]` from t, then printed backwards so it reads s → t.
- `bruteBest` is reset to `INF` before each pair.
