# Part A – Assignment 4: Can S and t be redefined arbitrarily?

**Question:** Can we redefine S and t arbitrarily and use the algorithm?

## Answer: Yes — with conditions

The DP recurrence never depends on s being vertex 1 or t being vertex n. It only needs:

1. **BCOST(s) = 0 and every other vertex starts at ∞.**
2. **Vertices processed in topological order starting from s.** Vertices before s in that order can't be reached and can be skipped.
3. **No negative cycle** reachable between s and t (otherwise there is no shortest path at all — see Part B Q2).

Things to handle:

| Situation | What the algorithm returns |
|---|---|
| t reachable from s | the correct shortest distance and path |
| t not reachable (e.g. s = 4, t = 9) | ∞ → "no path" |
| t comes before s (e.g. s = 10, t = 2) | ∞ → "no path" (edges only go forward) |
| s = t | 0 |
| s or t in a middle stage | works — the graph between them is just a smaller multistage graph |

So the stage structure isn't essential; what matters is that the graph is acyclic (or, for graphs with cycles, use Bellman-Ford from Q2).

## Generalised algorithm

```
Algorithm ShortestST(G, s, t)
{
    for each v do bcost[v] := ∞
    bcost[s] := 0
    for each j after s in topological order do
        for each edge <l, j> do
            if bcost[l] + c[l, j] < bcost[j] then
                bcost[j] := bcost[l] + c[l, j];  d[j] := l
    if bcost[t] = ∞ then return "no path"
    return bcost[t], path from d[]
}
```

Bonus: one run from s gives the answer for **every** t at once. Running it once per source gives all-pairs distances in O(n(n + e)) for a DAG — this connects to Part B Q1.

## Results (each checked against a brute-force search of every path)

| s → t | cost | path |
|---|---|---|
| 1 → 12 | 16 | 1 → 3 → 6 → 10 → 12 |
| 2 → 12 | 7 | 2 → 7 → 10 → 12 |
| 3 → 10 | 7 | 3 → 6 → 10 |
| 5 → 9 | 15 | 5 → 7 → 9 |
| 2 → 11 | 7 | 2 → 8 → 11 |
| 1 → 7 | 11 | 1 → 2 → 7 |
| 4 → 9 | ∞ | no path |
| 6 → 6 | 0 | 6 |
| 10 → 2 | ∞ | no path |

All matched brute force. The program also prints the full 12 × 12 distance matrix.

## Complexity

Per (s, t) pair: **Θ(n + e)** time, **O(n)** space — same as Q1.

## Files

- `arbitrary_st.c`, `ArbitraryST.java`, `output.txt`
