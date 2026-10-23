# floyd.c

**Part B, Question 1, in C.** All-pairs shortest paths with **Floyd–Warshall** (the slide's k-i-j loop), with a `nxt[][]` table for printing paths. Its output is identical to [Floyd.java](Floyd.java.md).

**Language:** C · **Java version:** [Floyd.java](Floyd.java.md) · **Written answer:** [answer.md](answer.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
gcc -O2 -o floyd floyd.c
./floyd
```

## The algorithm

```
for k = 1 to n
  for i = 1 to n
    for j = 1 to n
      A(i,j) = min[ A(i,j), A(i,k) + A(k,j) ]
```

After round k, A(i, j) is the shortest i → j path whose in-between vertices are all numbered ≤ k. A full explanation is in [Floyd.java.md](Floyd.java.md#the-algorithm).

## How the code is organised

| Part | Purpose |
| --- | --- |
| `#define MAXN 13`, `INF 1000000000` | Room for up to 12 vertices (index 0 unused) and "infinity" |
| `int A[MAXN][MAXN], nxt[MAXN][MAXN];` | Global distance and next-step matrices |
| `init(n)` | Resets the first n rows and columns: 0 on the diagonal, `INF` elsewhere. Lets the program reuse the globals for the second example |
| `addEdge(u, v, w)` | `A[u][v] = w; nxt[u][v] = v;` |
| `printMatrix(name, n)` | Prints A, showing `INF` for ∞ |
| `floyd(n, showSteps)` | The triple loop. If `showSteps` is set, it builds a title such as `"A2 (via vertices 1..2):"` with `sprintf` and prints A after each round |
| `printPath(i, j)` | Follows `nxt` from i to j |
| `main` | Example 1 (3 vertices, round by round), then example 2 (the lecture graph, final matrix and five paths) |

## Output

```
A0 (direct edges):
          1    2    3
    1     0    4   11
    2     6    0    2
    3     3  INF    0
...
A3 (via vertices 1..3):
          1    2    3
    1     0    4    6
    2     5    0    2
    3     3    7    0

   1 ->  3 : cost   6   path 1 -> 2 -> 3
   ...
===== Example 2: slide-20 multistage graph =====
   1 -> 12 : cost  16   path 1 -> 3 -> 6 -> 10 -> 12
   4 ->  9 : no path
```

The full output, with every matrix, is in [output.txt](output.txt).

## Complexity

- **Θ(n³) time.**
- **Θ(n²) space.** The matrices are fixed at 13 × 13 in this program.

## C-specific notes

- Using global arrays keeps the function signatures simple. The cost is that only one graph can be held at a time, which is why `init` must run before each example.
- To handle more than 12 vertices, raise `MAXN`.
