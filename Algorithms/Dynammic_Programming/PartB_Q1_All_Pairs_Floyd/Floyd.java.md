# Floyd.java

**Part B, Question 1.** All-pairs shortest paths with **Floyd–Warshall**, written exactly as the slide's triple loop. It also keeps a `next[][]` table so the actual paths can be printed, not just their lengths.

**Language:** Java · **C version:** [floyd.c](floyd.c.md) · **Written answer:** [answer.md](answer.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
javac Floyd.java
java Floyd
```

## The algorithm

```
for k = 1 to n
  for i = 1 to n
    for j = 1 to n
      A(i,j) = min[ A(i,j), A(i,k) + A(k,j) ]
```

**What A^k means:** after round k, A(i, j) is the shortest i → j path that passes only through vertices numbered **1 … k** along the way.

**Why each round is correct:** the best such path either doesn't use k (so its length is A(i, j) from the previous round), or uses k exactly once (so its length is A(i, k) + A(k, j)). This is the principle of optimality: both halves must themselves be shortest paths.

**Starting point:** A⁰ is the cost matrix: 0 on the diagonal, the edge weight where there is an edge, ∞ elsewhere. After round n, A holds every shortest distance.

## How the code is organised

| Member | Purpose |
| --- | --- |
| `A[i][j]` | The distance matrix, updated in place |
| `next[i][j]` | The first step after i on the best i → j path found so far, or 0 if there is none |
| `Floyd(n)` | Sets up A⁰: 0 on the diagonal, `INF` elsewhere |
| `addEdge(u, v, w)` | Sets `A[u][v] = w` and `next[u][v] = v` |
| `run(showSteps)` | The triple loop. Whenever going through k is shorter, it also sets `next[i][j] = next[i][k]`, so the path to j now starts the same way as the path to k. Optionally prints A^k after each round |
| `print(name)` | Prints the matrix, showing `INF` for ∞ |
| `printPath(i, j)` | Follows `next` from i until it reaches j |

The check `A[i][k] < INF && A[k][j] < INF` stops the program adding two ∞ values together.

## Output

**Example 1** is a 3-vertex graph, shown round by round:

```
Edges: 1->2 (4), 1->3 (11), 2->1 (6), 2->3 (2), 3->1 (3)

A0 (direct edges):        A1 (via 1):            A2 (via 1..2):         A3 (via 1..3):
     0    4   11               0    4   11             0    4    6             0    4    6
     6    0    2               6    0    2             6    0    2             5    0    2
     3  INF    0               3    7    0             3    7    0             3    7    0

   1 ->  3 : cost   6   path 1 -> 2 -> 3
   2 ->  1 : cost   5   path 2 -> 3 -> 1
   3 ->  2 : cost   7   path 3 -> 1 -> 2
```

The program prints each matrix separately; they are shown side by side here to save space. Each round improves exactly the entries that benefit from the new middle vertex:

- k = 1 improves 3 → 2, from ∞ to 3 + 4 = 7;
- k = 2 improves 1 → 3, from 11 to 4 + 2 = 6;
- k = 3 improves 2 → 1, from 6 to 2 + 3 = 5.

**Example 2** is the lecture's 12-vertex graph. The program prints the full 12 × 12 matrix and some paths:

```
   1 -> 12 : cost  16   path 1 -> 3 -> 6 -> 10 -> 12
   2 -> 12 : cost   7   path 2 -> 7 -> 10 -> 12
   3 -> 10 : cost   7   path 3 -> 6 -> 10
   5 ->  9 : cost  15   path 5 -> 7 -> 9
   4 ->  9 : no path
```

These match the backward and forward approaches in Part A.

## Complexity

- **Time Θ(n³):** three nested loops, with no shortcuts.
- **Space Θ(n²)** for `A` and `next`. Updating in place is safe because in round k, row k and column k never change (A(k, k) = 0).

## Notes

- If the graph has two edges between the same pair of vertices, the last `addEdge` wins. Keep the smaller weight if that matters.
- For negative weights and negative cycles, see [NegativeWeights.java](../PartB_Q2_Negative_Weights/NegativeWeights.java.md).
