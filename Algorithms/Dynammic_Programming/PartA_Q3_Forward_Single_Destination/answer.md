# Part A – Assignment 3: Forward approach = single-destination shortest paths

**Question:** The forward approach gives single-destination shortest paths to all the different nodes. Redraft the algorithm to accomplish this.

## Idea

The forward approach works from the target backwards. COST(i) is the cheapest path from vertex **i to t**:

```
COST(t) = 0
COST(i) = min { c(i, r) + COST(r) }   over all edges <i, r>
D(i)    = the r that gives the minimum  (best next hop from i)
```

Vertices are processed from n − 1 down to 1, so COST(r) is always ready. The key observation: when the loop ends, **COST(i) is already correct for every vertex i**, not just the source. So with one run we get the shortest path from *every* node to the single destination t.

The original FGraph only returns COST(1) and the path from vertex 1. The redraft keeps the whole COST[ ] and D[ ] arrays and prints the full path from every vertex by following D( ).

## Redrafted algorithm

```
Algorithm FGraphAll(G, n, t)
{
    cost[t] := 0
    for i := t - 1 down to 1 do
    {
        cost[i] := ∞
        for each r such that <i, r> is an edge do
            if c[i, r] + cost[r] < cost[i] then
            {
                cost[i] := c[i, r] + cost[r]
                d[i] := r
            }
    }
    // single-destination output: one path per vertex
    for i := 1 to n do
    {
        write(i, cost[i])
        v := i
        while v ≠ t and d[v] defined do { v := d[v];  write(" -> ", v) }
    }
}
```

For a graph whose vertices aren't numbered in stage order, replace "t − 1 down to 1" with **reverse topological order** (same fix as Q2).

## Result on the slide-20 graph

| i | COST(i) | path to t = 12 |
|---|---|---|
| 1 | **16** | 1 → 2 → 7 → 10 → 12 |
| 2 | 7 | 2 → 7 → 10 → 12 |
| 3 | 9 | 3 → 6 → 10 → 12 |
| 4 | 18 | 4 → 8 → 10 → 12 |
| 5 | 15 | 5 → 8 → 10 → 12 |
| 6 | 7 | 6 → 10 → 12 |
| 7 | 5 | 7 → 10 → 12 |
| 8 | 7 | 8 → 10 → 12 |
| 9 | 4 | 9 → 12 |
| 10 | 2 | 10 → 12 |
| 11 | 5 | 11 → 12 |
| 12 | 0 | 12 |

Comparison: the **backward** approach (Q1) is the mirror image — it gives **single-source** shortest paths, from s to every vertex.

## Complexity

Same as Q1: **Θ(n + e)** time, **O(n)** extra space (COST and D arrays). Printing all n paths adds at most O(n · k), where k is the number of stages.

## Files

- `forward.c`, `Forward.java`, `output.txt`
