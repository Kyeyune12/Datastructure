# floyd.c

**Part B, Question 1, in C.** **All-pairs shortest paths** with Floyd-Warshall, written exactly like the slide's triple loop. Same algorithm and same output as [Floyd.java](Floyd.java.md).

**Written answer:** [answer.md](answer.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
gcc floyd.c -o floyd
./floyd
```

## The rule

```
for k = 1 to n
  for i = 1 to n
    for j = 1 to n
      A(i,j) = min[ A(i,j), A(i,k) + A(k,j) ]
```

After round k, A(i, j) is the shortest i -> j path that only stops at vertices 1..k on the way.

## What is inside

| Part | Purpose |
| --- | --- |
| `n`, `A[][]`, `next[][]` | Number of vertices, the distance matrix, and the first step on the best i -> j path |
| `init(vertices)` | 0 on the diagonal, INF everywhere else |
| `addEdge(u, v, w)` | Sets `A[u][v] = w` and `next[u][v] = v` |
| `floyd(showSteps)` | The triple loop. With `showSteps = 1` it prints A1, A2, A3 ... |
| `printMatrix()` | Prints A with row and column numbers |
| `printPath(i, j)` | Follows `next[][]` from i until it reaches j |

When going through k is shorter, the first step of the new path is the first step towards k:

```c
A[i][j] = A[i][k] + A[k][j];
next[i][j] = next[i][k];
```

## Examples

1. The textbook 3-vertex graph, showing A0, A1, A2 and A3 step by step.
2. The slide-20 multistage graph: all 12 × 12 distances, plus 5 sample paths (1 -> 12 costs 16).

## Complexity

- **Time Θ(n³)**: three nested loops of n.
- **Space Θ(n²)** for `A` and `next`.

## How the C file is written

Same team style as every C file in this repo:

- `#define` for sizes (`N`, `E`, `INF`) and `struct Edge { int from; int to; int cost; }` for one edge.
- The graph is one global array `edges[]`; the result tables are global arrays, indexed from 1 so they match the lecture (index 0 is unused).
- Every `if` and loop has braces, with one statement per line, so each step can be pointed at during the presentation.
- `INF` is 1,000,000. That is far bigger than any real path cost here, and `INF + cost` still fits in an `int`.

## Questions you may be asked

- **Why must k be the OUTER loop?** A(i, j) for round k uses A(i, k) and A(k, j) from round k - 1. With k outside, every pair is updated with "stop-over k" before moving on to k + 1.
- **Why not run Dijkstra n times?** You can if there are no negative edges. Floyd is shorter to write, handles negative edges, and is fast enough for small dense graphs.
- **How does `next[][]` rebuild the path?** It stores only the first step. From that vertex we look up the first step again, until we reach j.
