# GeneralGraph.java

**Part A, Question 2.** What to do when the vertices are *not* numbered in stage order, so BCOST(l) may not be ready when BCOST(j) needs it. The answer: sort the vertices topologically first, and if the graph has a cycle, switch to Bellman-Ford.

**Language:** Java · **C version:** [general_graph.c](general_graph.c.md) · **Written answer:** [answer.md](answer.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
javac GeneralGraph.java
java GeneralGraph
```

## Why the Q1 loop breaks

Q1 visits j = 1, 2, …, n in numeric order. That works only because every edge ⟨l, j⟩ there has l < j. Rename the vertices and some predecessors get *bigger* numbers than the vertices they lead to. Then, when BCOST(j) is computed, BCOST(l) is still ∞, and the answer comes out wrong or ∞.

## The redesign (`shortestPath`)

1. **Topological sort** using Kahn's algorithm (`topoSort`):
   - count the incoming edges of every vertex;
   - start with a queue of every vertex that has no incoming edges;
   - repeatedly remove a vertex, add it to the order, and decrease the in-degree of its successors, queueing any that reach 0.

   If every vertex ends up in the order, the graph has no cycles. Otherwise it has a cycle, and `topoSort` returns `null`.
2. **No cycles (DAG):** run exactly the Q1 recurrence, but visit the vertices in **topological order**. Every predecessor of j comes before j in that order, so BCOST(l) is always ready.
3. **Cycle:** no ordering can work, so use **Bellman-Ford**:
   - relax every edge n − 1 times;
   - then do **one extra pass**. If any edge can still improve a distance, there is a **negative cycle** reachable from s, and shortest paths are undefined.

`naiveBcost` is the unchanged Q1 loop, kept so the demo can show it failing.

## How the code is organised

| Member | Purpose |
| --- | --- |
| `BASE` | The 21 lecture edges |
| `Result` | Holds the `method` used, `negativeCycle`, the `cost` and the `path` |
| `topoSort(n, edges)` | Kahn's algorithm. Returns the order, or `null` if there is a cycle |
| `shortestPath(n, edges, s, t)` | The redesigned algorithm described above |
| `naiveBcost(n, edges, s, t)` | The Q1 loop, for comparison |
| `report(...)` | Prints the method used and the result |
| `copyBase()` | A fresh copy of the edges, so each demo can add its own edge |

## The three demos and their output

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

- **A** renames the vertices using `newLabel = {0, 7, 3, 12, 1, 9, 2, 11, 5, 10, 4, 8, 6}`, so old vertex 1 becomes 7, old 12 becomes 6, and so on. The naive loop gives ∞. The redesigned version gives the correct 16 along the same route as before, just with new names: 7 → 12 → 2 → 4 → 6 is the old 1 → 3 → 6 → 10 → 12.
- **B** adds the cycle 7 → 10 → 7 with weight 3 + 1 = 4, which is positive. Bellman-Ford handles it and still finds 16.
- **C** gives the cycle weight 3 + (−6) = −3. Going round it again always lowers the cost, so the extra pass detects it.

## Complexity

| Case | Time | Space |
| --- | --- | --- |
| Topological sort | O(n + e) | O(n + e) |
| DAG (BCOST in topological order) | O(n + e) | O(n) |
| Bellman-Ford (graph with a cycle) | O(n · e) | O(n) |

## Notes

- Rebuilding the path is capped at n steps (`r.path.size() <= n`) as a safety net.
- If t can't be reached, `cost` stays ∞ and the report says "no path".
