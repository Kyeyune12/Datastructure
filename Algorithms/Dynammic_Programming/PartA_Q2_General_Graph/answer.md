# Part A – Assignment 2: General graph (BCOST(l) may not be ready)

**Question:** Consider a general graph. BCOST(l) may not be immediately available while computing BCOST(j). Re-design the algorithm.

## Why Q1 breaks

Q1 loops `j = 2..n` and trusts that every predecessor l of j has a smaller number. In a general graph the numbering is arbitrary, so when we reach j, some BCOST(l) may still be ∞ (not computed yet) and BCOST(j) comes out wrong.

The program proves this: the slide-20 graph is **relabelled** (source becomes 7, target becomes 6). The unchanged Q1 loop returns **∞ – wrong**; the re-designed version returns **16 – correct**.

## Re-design

There are two cases.

**Case 1 – the graph is a DAG (no cycles).** A valid order always exists: a **topological order**, where every edge goes from an earlier vertex to a later one. Compute it first (Kahn's algorithm), then run the same BCOST recurrence in that order. Every predecessor of j is now finished before j.

**Case 2 – the graph has a cycle.** No such order exists (on a cycle, each vertex would need to come before the other). Use **Bellman-Ford** instead: relax every edge n − 1 times; one more pass that still improves something means a **negative cycle**, where no shortest path exists.

```
Algorithm GeneralBCOST(G, s, t)
{
    order := TopologicalSort(G)          // Kahn: repeatedly remove in-degree-0 vertices
    for each v do bcost[v] := ∞
    bcost[s] := 0

    if order contains all n vertices then          // G is a DAG
        for each j in order do
            for each edge <l, j> do
                if bcost[l] + c[l, j] < bcost[j] then
                    bcost[j] := bcost[l] + c[l, j];  d[j] := l
    else                                            // G has a cycle
    {
        repeat n - 1 times
            for each edge <u, v> do
                if bcost[u] + c[u, v] < bcost[v] then
                    bcost[v] := bcost[u] + c[u, v];  d[v] := u
        for each edge <u, v> do
            if bcost[u] + c[u, v] < bcost[v] then
                return "negative cycle - no shortest path"
    }
    return bcost[t] and the path rebuilt from d[]
}
```

An alternative for DAGs is **memoised recursion**: `BCOST(j)` calls `BCOST(l)` for each predecessor that hasn't been computed yet. This computes things on demand and is effectively a topological sort done by DFS.

## Results from the program

| Test | Method chosen | Result |
|---|---|---|
| A) relabelled graph, s = 7, t = 6 | DAG → topological order | 16 via 7 → 12 → 2 → 4 → 6 (= old 1 → 3 → 6 → 10 → 12) |
| B) + edge 10→7 (cost 1), positive cycle | Bellman-Ford | 16 via 1 → 3 → 6 → 10 → 12 |
| C) + edge 10→7 (cost −6), negative cycle | Bellman-Ford | negative cycle detected |

## Complexity

| Case | Time | Space |
|---|---|---|
| DAG (topological sort + BCOST) | **Θ(n + e)** with adjacency lists | O(n + e) |
| Graph with cycles (Bellman-Ford) | **O(n · e)** | O(n) extra |

(The demo code scans the edge array per vertex for simplicity, i.e. O(n·e); with adjacency lists, as in the Java version's predecessor lists, the DAG case is Θ(n + e).)

## Files

- `general_graph.c`, `GeneralGraph.java`, `output.txt`
