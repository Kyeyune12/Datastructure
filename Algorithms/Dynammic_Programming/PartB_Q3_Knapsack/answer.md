# Part B – Assignment 3: 0/1 Knapsack using dynamic programming

## Problem

n items, item i has weight wᵢ and profit pᵢ. Knapsack capacity m. Choose xᵢ ∈ {0, 1} to

maximise Σ pᵢxᵢ  subject to  Σ wᵢxᵢ ≤ m.

## DP formulation

**V[i][c]** = best profit using only items 1..i with capacity c.

For item i there are two decisions: leave it, or take it (only if it fits).

```
V[0][c] = 0                                         for all c
V[i][c] = V[i-1][c]                                 if wᵢ > c
V[i][c] = max( V[i-1][c],  pᵢ + V[i-1][c - wᵢ] )    otherwise
```

Answer = **V[n][m]**.

**Which items?** Walk back from (n, m): if V[i][c] ≠ V[i−1][c], item i was taken → xᵢ = 1, c := c − wᵢ. Otherwise xᵢ = 0.

This is valid DP because of the **principle of optimality**: whatever we decide for item n, the remaining items must be chosen optimally for the remaining capacity.

## Algorithm

```
Algorithm Knapsack(w, p, n, m)
{
    for c := 0 to m do V[0][c] := 0
    for i := 1 to n do
        for c := 0 to m do
        {
            V[i][c] := V[i-1][c]
            if w[i] ≤ c and p[i] + V[i-1][c - w[i]] > V[i][c] then
                V[i][c] := p[i] + V[i-1][c - w[i]]
        }
    c := m
    for i := n down to 1 do
        if V[i][c] ≠ V[i-1][c] then { x[i] := 1;  c := c - w[i] }
        else x[i] := 0
    return V[n][m], x
}
```

## Worked example (n = 3, m = 6, w = (2, 3, 4), p = (1, 2, 5))

```
i\c  0  1  2  3  4  5  6
 0   0  0  0  0  0  0  0
 1   0  0  1  1  1  1  1
 2   0  0  1  2  2  3  3
 3   0  0  1  2  5  5  6
```

**Max profit = 6**, x = (1, 0, 1) → take items 1 and 3 (weight 2 + 4 = 6).
Traceback: V[3][6]=6 ≠ V[2][6]=3 → take 3, c = 2; V[2][2] = V[1][2] → skip 2; V[1][2]=1 ≠ 0 → take 1.

## Other results (all verified by brute force over every subset)

| Instance | Max profit | x |
|---|---|---|
| n=4, m=8, w=(2,3,4,5), p=(1,2,5,6) | **8** | (0, 1, 0, 1) |
| n=3, m=50, w=(10,20,30), p=(60,100,120) | **220** | (0, 1, 1) |

The last one is a useful contrast: a greedy pick by profit/weight ratio takes item 1 first and ends at 160; DP finds 220.

## Complexity

- **Time: Θ(n · m)** – one table cell per (item, capacity), O(1) work each. Traceback is O(n).
- **Space: Θ(n · m)** for the full table (needed for traceback). If only the profit is needed, one row of size m + 1 suffices (loop c from m down to wᵢ), giving **O(m)** space.
- This is **pseudo-polynomial**: polynomial in the *value* m, but exponential in the number of bits used to write m. Brute force is Θ(2ⁿ · n).

## Files

- `knapsack.c`, `Knapsack.java`, `output.txt`
