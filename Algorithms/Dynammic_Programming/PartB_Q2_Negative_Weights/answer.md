# Part B – Assignment 2: Consider negative weights also

## Short answer

- **Floyd-Warshall works with negative edge weights**, as long as there is **no negative cycle**. Nothing in the recurrence `A(i,j) = min[A(i,j), A(i,k) + A(k,j)]` assumes weights are positive.
- **If a negative cycle exists, shortest paths are undefined** for any pair whose path can touch the cycle — you can loop forever and drive the cost to −∞.
- **Detection is free:** after the algorithm, **A(i, i) < 0** for any vertex i on a negative cycle. Normally A(i, i) = 0.
- **Greedy algorithms like Dijkstra fail** with negative edges, because they finalise a vertex the moment it is the closest, and a later negative edge can still make it cheaper.

## Why the DP stays correct

The correctness argument only needs: a shortest path never repeats a vertex. That's true when there's no negative cycle (removing a cycle of weight ≥ 0 never makes a path longer). So the A^k definition still holds and A^n is still correct.

With a negative cycle, repeating vertices *does* help, the "simple path" assumption breaks, and the values in A just reflect some walks — they are not meaningful shortest distances. That's why we check the diagonal.

Note for **undirected** graphs: a single negative edge {u, v} is already a negative cycle (u → v → u), so undirected graphs with negative edges have no shortest paths through that edge.

## Algorithm with detection

```
Algorithm AllPathsNeg(cost, A, n)
{
    A := cost                        // A(i,i) = 0, ∞ where no edge
    for k := 1 to n
        for i := 1 to n
            for j := 1 to n
                if A(i,k) + A(k,j) < A(i,j) then A(i,j) := A(i,k) + A(k,j)
    for i := 1 to n
        if A(i,i) < 0 then return "negative cycle through vertex i"
    return A
}
```

(In code, skip the update if A(i,k) or A(k,j) is ∞, so ∞ + negative doesn't produce a bogus value.)

## Results

**1) Dijkstra fails** – edges 1→2 (2), 1→3 (5), 3→2 (−4)
Dijkstra says dist(1→2) = 2 (**wrong**). Floyd says 1, via 1 → 3 → 2 (**correct**).

**2) Negative edges, no negative cycle** (5 vertices)

```
      1   2   3   4   5
1     0   1  -3   2  -4
2     3   0  -4   1  -1
3     7   4   0   5   3
4     2  -1  -5   0  -2
5     8   5   1   6   0
```

Diagonal is all 0 → no negative cycle. Example: 1 → 3 costs −3 via 1 → 5 → 4 → 3.

**3) Negative cycle** – 1→2 (1), 2→3 (−2), 3→1 (−1): cycle weight −2.
Diagonal A(1,1) = −2, A(2,2) = −2, A(3,3) = −4 → **negative cycle detected** at vertices 1, 2, 3.

## Complexity

Unchanged: **Θ(n³) time, Θ(n²) space**. The detection pass adds only O(n).

For single-source with negative edges, **Bellman-Ford** (O(n·e)) is the usual choice — it's what Part A Q2 falls back to.

## Files

- `negative_weights.c`, `NegativeWeights.java`, `output.txt`
