# general_graph.c

**Part A, Question 2, in C.** Shortest paths when the vertices are *not* in stage order: sort them topologically first, and if the graph has a cycle, use Bellman-Ford (which also detects negative cycles). Its output is identical to [GeneralGraph.java](GeneralGraph.java.md).

**Language:** C · **Java version:** [GeneralGraph.java](GeneralGraph.java.md) · **Written answer:** [answer.md](answer.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
gcc -O2 -o general_graph general_graph.c
./general_graph
```

## The idea

1. **The Q1 loop assumes numbered order is stage order.** Rename the vertices and BCOST(l) may still be ∞ when BCOST(j) needs it.
2. **Fix for a DAG (no cycles):** order the vertices topologically with Kahn's algorithm, then run the same recurrence in that order.
3. **Fix for a graph with a cycle:** no ordering exists, so use **Bellman-Ford**. Relax every edge n − 1 times, then do one more pass; if anything still improves, a **negative cycle** is reachable.

## How the code is organised

| Part | Purpose |
| --- | --- |
| `MAXN 32`, `MAXE 128`, `INF 1000000000` | Fixed limits and "infinity" |
| `Edge { u, v, w }`, `base[]` | The 21 lecture edges |
| `enum { OK, NO_PATH, NEG_CYCLE }` | Return codes from `shortestPath` |
| `topoSort(n, e, m, order)` | Kahn's algorithm with an array used as a queue. Returns how many vertices it placed; fewer than n means there is a cycle |
| `shortestPath(n, e, m, s, t, &cost, path, &len, &method)` | The redesigned algorithm. Sets `method` to a description of the approach it used |
| `naiveBcost(...)` | The unchanged Q1 loop, for comparison |
| `report(...)` | Prints the method and the result |
| `main` | Runs demos A (renamed vertices), B (positive cycle) and C (negative cycle) |

## Output

```
A) Relabelled slide-20 graph (source is now 7, target is now 6)
  Naive Q1 loop:  INF  (WRONG - predecessors not ready)
  Re-designed algorithm:
  Method: DAG -> BCOST in topological order
  Shortest 7 -> 6 = 16   Path: 7 -> 12 -> 2 -> 4 -> 6

B) Original graph + edge 10->7 (cost 1)  [cycle 7->10->7]
  Method: cycle found -> Bellman-Ford
  Shortest 1 -> 12 = 16   Path: 1 -> 3 -> 6 -> 10 -> 12

C) Original graph + edge 10->7 (cost -6) [cycle weight -3]
  Method: cycle found -> Bellman-Ford
  Result: NEGATIVE CYCLE reachable - shortest path undefined
```

See [GeneralGraph.java.md](GeneralGraph.java.md#the-three-demos-and-their-output) for what each demo shows.

## Complexity

This version stores only a flat edge list; it doesn't build adjacency lists. So:

| Step | This C version | With adjacency lists (as in the Java version) |
| --- | --- | --- |
| `topoSort` | **O(n · e)**: every removed vertex scans all edges | O(n + e) |
| BCOST in topological order | **O(n · e)**: scans all edges to find those into j | O(n + e) |
| Bellman-Ford | O(n · e) | O(n · e) |

For a 12-vertex graph the difference is negligible. To match the Java version, build per-vertex lists of outgoing and incoming edges as [bcost.c](../PartA_Q1_Backward_BCOST/bcost.c.md) does.

## C-specific notes

- `main` reuses one `Edge g[MAXE]` array for all three demos. B and C add the extra edge at position `g[BASE_E]`.
- The function reports which method it used by setting a `const char *` through a pointer (`const char **method`).
