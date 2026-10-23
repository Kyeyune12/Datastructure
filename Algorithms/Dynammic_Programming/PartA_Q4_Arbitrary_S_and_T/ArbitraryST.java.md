# ArbitraryST.java

**Part A, Question 4.** Can the source S and target t be chosen arbitrarily, and the same algorithm reused? **Yes**, and this program shows it. It runs the BCOST algorithm for many (s, t) pairs, checks each answer against a brute-force search, and prints the full distance matrix.

**Language:** Java · **C version:** [arbitrary_st.c](arbitrary_st.c.md) · **Written answer:** [answer.md](answer.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
javac ArbitraryST.java
java ArbitraryST
```

## The rules that make it work

| Situation | What happens |
| --- | --- |
| Any s | Set BCOST(s) = 0 and every other vertex to ∞, then run forward from s in topological order |
| Vertices s can't reach | Simply stay at ∞ |
| t not reachable from s (e.g. t comes before s) | BCOST(t) = ∞, reported as **no path** |
| s = t | Distance **0** |
| The graph must… | …have no cycles, so that a topological order exists. If it has cycles, see [Q2](../PartA_Q2_General_Graph/GeneralGraph.java.md) |

## How the code is organised

| Member | Purpose |
| --- | --- |
| `bcostFrom(s)` | The Q1 recurrence from any source: `j = s+1 … N` in numeric order (which is a topological order for this graph), scanning the edge list for edges into j |
| `brute(u, t)` | A recursive depth-first search that tries **every** path from u to t and returns the cheapest. It is used only to check the DP |
| `show(x)` | Prints `INF` for ∞ |
| `main` | Tests nine (s, t) pairs against `brute`, then runs `bcostFrom` once from each vertex to print the 12 × 12 distance matrix |

## Output

```
Same algorithm, arbitrary (s, t) pairs
----------------------------------------------------------
  s ->  t | DP cost | brute | path
----------------------------------------------------------
  1 -> 12 |      16 |    16 | 1 -> 3 -> 6 -> 10 -> 12
  2 -> 12 |       7 |     7 | 2 -> 7 -> 10 -> 12
  3 -> 10 |       7 |     7 | 3 -> 6 -> 10
  5 ->  9 |      15 |    15 | 5 -> 7 -> 9
  2 -> 11 |       7 |     7 | 2 -> 8 -> 11
  1 ->  7 |      11 |    11 | 1 -> 2 -> 7
  4 ->  9 |     INF |   INF | no path
  6 ->  6 |       0 |     0 | 6
 10 ->  2 |     INF |   INF | no path
----------------------------------------------------------
All results match brute force: YES

Distance matrix: row = s, column = t  (. = no path)
        1   2   3   4   5   6   7   8   9  10  11  12
   1    0   9   7   3   2   9  11  10  15  14  16  16
   2    .   0   .   .   .   4   2   1   6   5   7   7
   ...
  12    .   .   .   .   .   .   .   .   .   .   .   0
```

The pairs cover the interesting cases:

- a normal pair (1 → 12);
- pairs that start mid-graph (2 → 12, 5 → 9) or end mid-graph (3 → 10);
- s = t (6 → 6);
- unreachable targets: 4 → 9, because 4's only edge leads to 8, which doesn't connect to 9; and 10 → 2, because 2 is in an earlier stage.

The matrix is **upper-triangular**: everything below the diagonal is "no path", because every edge leads to a later stage.

## Complexity

- **`bcostFrom(s)`:** O(n · e) as written, because it scans the whole edge list for each j. Predecessor lists, as in Q1, would make it O(n + e).
- **The full matrix:** one run per source, so O(n² · e) here, or O(n(n + e)) with predecessor lists.
- **`brute`:** tries every path, which can grow exponentially with the number of stages. It is fine for this small graph and used only as a check.

## Notes

- Numeric order works as a topological order only because this graph is numbered by stage. For an arbitrary graph, use the topological sort from Q2.
- Rebuilding the path uses a `LinkedList` with `addFirst`, so the path comes out in source-to-target order without needing to be reversed.
