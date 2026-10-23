# Part B – Assignment 1: All-pairs shortest paths (Floyd-Warshall)

**Question:** Implement the all-pairs shortest path algorithm:

```
for k := 1 to n
    for i := 1 to n
        for j := 1 to n
            A(i, j) := min[ A(i, j), A(i, k) + A(k, j) ]
```

## Idea

Define **A^k(i, j)** = length of the shortest i → j path whose intermediate vertices all come from {1, 2, …, k}.

- A⁰ = the cost matrix (direct edges; 0 on the diagonal; ∞ where there is no edge).
- Going from A^(k−1) to A^k: the best path either doesn't use vertex k (`A(i, j)`), or goes through k once (`A(i, k) + A(k, j)`). Take the minimum.
- A^n is the answer for all pairs.

The update can be done in one matrix in place, because row k and column k don't change during iteration k (A(k, k) = 0).

To print actual paths, a `next[i][j]` matrix stores the first hop on the best i → j path. When the min is improved via k, set `next[i][j] := next[i][k]`.

## Example 1 – 3-vertex graph, step by step

Edges: 1→2 (4), 1→3 (11), 2→1 (6), 2→3 (2), 3→1 (3)

```
A0          A1          A2          A3
0  4 11     0  4 11     0  4  6     0  4  6
6  0  2     6  0  2     6  0  2     5  0  2
3  ∞  0     3  7  0     3  7  0     3  7  0
```

- A1: 3→2 improved to 7 (via 1: 3 + 4)
- A2: 1→3 improved to 6 (via 2: 4 + 2)
- A3: 2→1 improved to 5 (via 3: 2 + 3)

## Example 2 – slide-20 multistage graph

The program prints the full 12 × 12 distance matrix. Highlights: 1 → 12 = **16** (same as Part A), 2 → 12 = 7, 3 → 10 = 7, 5 → 9 = 15, 4 → 9 = no path.

## Complexity

- **Time: Θ(n³)** – three nested loops of n, constant work inside. Independent of the number of edges.
- **Space: Θ(n²)** – the matrix A (plus next[ ][ ] for paths). In-place update means no extra matrix per k.

Comparison: running the single-source DAG algorithm n times costs O(n(n + e)), which is better for sparse DAGs, but Floyd-Warshall works for **any** directed graph, including cycles and negative edges (Q2).

## Files

- `floyd.c`, `Floyd.java`, `output.txt`
