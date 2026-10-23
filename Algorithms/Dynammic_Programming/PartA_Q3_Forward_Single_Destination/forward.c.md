# forward.c

**Part A, Question 3, in C.** The **forward approach** (*FGraph*): one pass computes COST(i), the shortest distance from every vertex i to the target t, and the program prints each vertex's path to t. Its output is identical to [Forward.java](Forward.java.md).

**Language:** C · **Java version:** [Forward.java](Forward.java.md) · **Written answer:** [answer.md](answer.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
gcc -O2 -o forward forward.c
./forward
```

## The recurrence

```
COST(t) = 0
COST(i) = min over every edge <i, r> of { c(i, r) + COST(r) }
D(i)    = the r that gave the minimum      (the best next step)
```

The loop runs i = t−1 down to 1. Every edge goes from a smaller number to a larger one, so COST(r) is ready whenever COST(i) needs it.

## How the code is organised

| Part | Purpose |
| --- | --- |
| `Edge edges[]`, `E`, `N 12`, `INF 1000000000` | The lecture graph and "infinity" |
| `succ[N+1][N+1]`, `succCount[]` | `succ[u][k]` is the position in `edges[]` of the k-th edge **out of** u. They are filled at the top of `main` |
| `fgraph(t, cost, d)` | The DP. Fills `cost[]` and `d[]` for every vertex |
| `main` | Builds `succ`, runs `fgraph(12, …)`, then prints a row per vertex, following `d[]` inline to print each path |

```c
for (int i = t - 1; i >= 1; i--)                    /* reverse stage order */
    for (int k = 0; k < succCount[i]; k++) {
        Edge e = edges[succ[i][k]];
        if (cost[e.v] != INF && e.w + cost[e.v] < cost[i]) {
            cost[i] = e.w + cost[e.v];
            d[i] = e.v;
        }
    }
```

## Output

```
  i | COST(i) | D(i) | shortest path i -> t
  1 |      16 |    2 | 1 -> 2 -> 7 -> 10 -> 12
  2 |       7 |    7 | 2 -> 7 -> 10 -> 12
  3 |       9 |    6 | 3 -> 6 -> 10 -> 12
  ...
 12 |       0 |    - | 12
Answer for s = 1: COST(1) = 16
```

The full table is in [output.txt](output.txt). From vertex 1 there is a tie (through 2 or through 3, both 16). The edge to 2 is examined first, so the program prints 1 → 2 → 7 → 10 → 12. That differs from Q1's 1 → 3 → 6 → 10 → 12, and both are shortest paths.

## Complexity

- **Time Θ(n + e)**, using the successor lists.
- **Space O(n)** for `cost` and `d`, plus the fixed `succ` table.

## C-specific notes

- `succ` is sized `[N+1][N+1]`, so a vertex can have at most N outgoing edges. That is enough for a simple graph (no repeated edges).
- `d[i] == 0` means "no next step", which is why the loop that prints a path stops at t or at 0.
