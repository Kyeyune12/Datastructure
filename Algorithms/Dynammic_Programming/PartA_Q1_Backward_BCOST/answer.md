# Part A – Assignment 1: Backward approach (BCOST)

**Question:** Develop the algorithm and implement it with the assumption that BCOST(l) is available while computing BCOST(j). Work out the time–space complexity.

## Idea

BCOST(j) is the cost of the cheapest path from the source **s** to vertex **j**. The last edge on that path comes from some predecessor **l** of j, so:

```
BCOST(s) = 0
BCOST(j) = min { BCOST(l) + c(l, j) }   over all edges <l, j>
D(j)     = the l that gives the minimum
```

The assumption "BCOST(l) is available when computing BCOST(j)" holds because the vertices are numbered stage by stage (every edge goes from a smaller number to a bigger one). So we simply compute j = 2, 3, …, n in order.

## Algorithm (BGraph)

```
Algorithm BGraph(G, k, n, p)
// G has n vertices in k stages, s = 1, t = n
{
    bcost[1] := 0
    for j := 2 to n do
    {
        bcost[j] := ∞
        for each l such that <l, j> is an edge do
            if bcost[l] + c[l, j] < bcost[j] then
            {
                bcost[j] := bcost[l] + c[l, j]
                d[j] := l
            }
    }
    // rebuild the decision sequence
    p[1] := 1;  p[k] := n
    for j := k - 1 down to 2 do  p[j] := d[p[j + 1]]
}
```

## Hand trace on the slide-20 graph

| j | BCOST(j) | from |
|---|---|---|
| 1 | 0 | – |
| 2 | 9 | 1 |
| 3 | 7 | 1 |
| 4 | 3 | 1 |
| 5 | 2 | 1 |
| 6 | min(9+4, 7+2) = **9** | 3 |
| 7 | min(9+2, 7+7, 2+11) = **11** | 2 |
| 8 | min(9+1, 3+11, 2+8) = **10** | 2 |
| 9 | min(9+6, 11+4) = **15** | 6 |
| 10 | min(9+5, 11+3, 10+5) = **14** | 6 |
| 11 | 10+6 = **16** | 8 |
| 12 | min(15+4, 14+2, 16+5) = **16** | 10 |

**Shortest distance = 16**, path **1 → 3 → 6 → 10 → 12**.
(1 → 2 → 7 → 10 → 12 also costs 16 — a tie. The forward approach in Q3 finds that one.)

## Complexity

Let |V| = n vertices and |E| = e edges.

- **Time:** each vertex is visited once, and each edge <l, j> is examined exactly once (when j is processed). Building predecessor lists is also O(n + e). Total **Θ(n + e)**. With an adjacency *matrix* instead of lists it becomes Θ(n²).
- **Space:** bcost[ ] and d[ ] are O(n); the predecessor lists are O(n + e). So **O(n + e)** total, or **O(n)** extra beyond the graph itself.

## Files

- `bcost.c` – C version (`gcc -O2 -o bcost bcost.c && ./bcost`)
- `BCost.java` – Java version (`javac BCost.java && java BCost`)
- `output.txt` – program output (identical for both versions)
