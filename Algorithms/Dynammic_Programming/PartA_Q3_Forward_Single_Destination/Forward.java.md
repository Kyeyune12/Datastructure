# Forward.java

**Part A, Question 3.** The **forward approach** (the textbook's *FGraph*). One pass gives the shortest distance from **every** vertex to the target t, so the program prints a shortest path from each vertex to t.

**Language:** Java · **C version:** [forward.c](forward.c.md) · **Written answer:** [answer.md](answer.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
javac Forward.java
java Forward
```

## The recurrence

COST(i) is the cheapest cost **from i to the target t**:

```
COST(t) = 0
COST(i) = min over every edge <i, r> of { c(i, r) + COST(r) }
D(i)    = the r that gave the minimum      (the best next step from i)
```

The vertices are processed **i = t−1 down to 1**, in reverse stage order. Every edge goes from a smaller number to a larger one, so COST(r) is always already final.

## Why one pass serves every vertex

The loop computes COST(i) for *every* i, not just for the source. When it finishes:

- `cost[i]` is the shortest distance from i to t, for all i;
- following `d[]` from any vertex gives its shortest path to t.

So the forward approach naturally solves **single-destination** shortest paths. The source is just one of the rows: COST(1) = 16.

## How the code is organised

| Member | Purpose |
| --- | --- |
| `EDGES`, `N`, `INF` | The lecture graph, as in Q1 |
| `succ` | `List<List<int[]>>`, where `succ.get(u)` lists the `{v, w}` of every edge **out of** u |
| `fgraph(t)` | The DP loop, filling `cost[]` and `d[]` for every vertex |
| `pathFrom(i, t)` | Follows `d[]` from i to t and returns the path as a string, e.g. `"3 -> 6 -> 10 -> 12"` |
| `main` | Runs `fgraph(12)`, then prints one row per vertex |

## Output

```
Forward approach - shortest path from EVERY vertex to t = 12
----------------------------------------------------
  i | COST(i) | D(i) | shortest path i -> t
----------------------------------------------------
  1 |      16 |    2 | 1 -> 2 -> 7 -> 10 -> 12
  2 |       7 |    7 | 2 -> 7 -> 10 -> 12
  3 |       9 |    6 | 3 -> 6 -> 10 -> 12
  4 |      18 |    8 | 4 -> 8 -> 10 -> 12
  5 |      15 |    8 | 5 -> 8 -> 10 -> 12
  6 |       7 |   10 | 6 -> 10 -> 12
  7 |       5 |   10 | 7 -> 10 -> 12
  8 |       7 |   10 | 8 -> 10 -> 12
  9 |       4 |   12 | 9 -> 12
 10 |       2 |   12 | 10 -> 12
 11 |       5 |   12 | 11 -> 12
 12 |       0 |    - | 12
----------------------------------------------------
Answer for s = 1: COST(1) = 16
```

**Why the path from 1 differs from Q1's.** From vertex 1, going through 2 (9 + 7) and going through 3 (7 + 9) both cost 16. The edge to 2 is examined first and the test is a strict `<`, so this program reports 1 → 2 → 7 → 10 → 12, while the backward approach in Q1 reports 1 → 3 → 6 → 10 → 12. Both are correct: the graph has two shortest paths.

## Complexity

- **Time Θ(n + e):** each vertex once, and each outgoing edge once.
- **Space:** O(n) for `cost` and `d`, plus O(n + e) for the successor lists.
- Printing every path costs O(n · k) for k stages.
