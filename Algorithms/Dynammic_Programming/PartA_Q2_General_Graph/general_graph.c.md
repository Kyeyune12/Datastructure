# general_graph.c

**Part A, Question 2, in C.** What if BCOST(l) is **not** ready when BCOST(j) is computed (the vertices are not numbered in stage order)? Same algorithm and same output as [GeneralGraph.java](GeneralGraph.java.md).

**Written answer:** [answer.md](answer.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
gcc general_graph.c -o general_graph
./general_graph
```

## The re-design

1. **Topological sort** (Kahn's algorithm, `topoSort`): repeatedly take a vertex with no incoming edges. This produces an order where every edge goes forward.
2. If all N vertices were placed, the graph is a **DAG**: run the same BCOST rule as Q1, but in topological order.
3. If some vertices were never placed, there is a **cycle**: fall back to **Bellman-Ford** (relax every edge N - 1 times), then do one extra pass. If anything still improves, there is a **negative cycle**.

## What is inside

| Function | What it does |
| --- | --- |
| `topoSort(e, m, order)` | Kahn's algorithm with a simple array queue. Returns how many vertices it placed (less than N means a cycle) |
| `shortestPath(e, m, s, t)` | Picks the method, runs it and **prints** the method and the result |
| `naiveBcost(e, m, s, t)` | The unchanged Q1 loop, used to show it gives the wrong answer on a relabelled graph |
| `main` | Runs the three demos A, B and C |

## The three demos

| Demo | Graph | Result |
| --- | --- | --- |
| A | Slide-20 graph with the vertex numbers shuffled | Naive Q1 loop prints INF (wrong). The re-design gives 16 |
| B | Original graph + edge 10 -> 7 (cost 1): a cycle 7 -> 10 -> 7 | Topological sort fails, Bellman-Ford gives 16 |
| C | Same, but the extra edge costs -6: cycle weight 3 - 6 = -3 | Bellman-Ford reports a negative cycle |

## Complexity

- Topological sort: O(n · e) as written (it scans the edge list for each vertex taken from the queue). Θ(n + e) with adjacency lists.
- DAG case: O(n · e) as written, Θ(n + e) with lists.
- Bellman-Ford: **Θ(n · e)**, since it does n - 1 passes over all e edges.

## How the C file is written

Same team style as every C file in this repo:

- `#define` for sizes (`N`, `E`, `INF`) and `struct Edge { int from; int to; int cost; }` for one edge.
- The graph is one global array `edges[]`; the result tables are global arrays, indexed from 1 so they match the lecture (index 0 is unused).
- Every `if` and loop has braces, with one statement per line, so each step can be pointed at during the presentation.
- `INF` is 1,000,000. That is far bigger than any real path cost here, and `INF + cost` still fits in an `int`.

## Questions you may be asked

- **How does Kahn's algorithm detect a cycle?** Vertices on a cycle always keep at least one incoming edge, so they never reach in-degree 0 and are never placed. `count < N` means there is a cycle.
- **Why N - 1 passes in Bellman-Ford?** A shortest path without a cycle has at most N - 1 edges, and each pass fixes at least one more edge of it.
- **Why can the extra pass detect a negative cycle?** After N - 1 passes all true shortest distances are final. If an edge can still lower a distance, going round a cycle keeps making it cheaper, so the cycle must be negative.
- **Why not always use Bellman-Ford?** It is slower. On a DAG the topological-order method does each edge once.
