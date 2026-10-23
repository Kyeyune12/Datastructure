# negative_weights.c

**Part B, Question 2, in C.** Floyd–Warshall with **negative edge weights**. It shows Dijkstra failing, Floyd giving correct answers when there is no negative cycle, and a negative cycle being detected through the diagonal. Its output is identical to [NegativeWeights.java](NegativeWeights.java.md).

**Language:** C · **Java version:** [NegativeWeights.java](NegativeWeights.java.md) · **Written answer:** [answer.md](answer.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
gcc -O2 -o negative_weights negative_weights.c
./negative_weights
```

## Key facts

- Negative edges are fine for Floyd–Warshall. **Negative cycles** are not: they make shortest paths undefined.
- After the run, **A(i, i) < 0** means vertex i is on a negative cycle.
- Dijkstra is greedy and never revisits a vertex it has finished, so a negative edge found later can make its answer wrong.

## How the code is organised

| Part | Purpose |
| --- | --- |
| `#define MAXN 8`, `INF 1000000000` | Room for up to 7 vertices, and "infinity" |
| `A[][]`, `nxt[][]`, `init(n)`, `addEdge` | Global matrices, reset with `init` before each demo |
| `int floyd(int n)` | The triple loop. Returns 1 if any `A[i][i] < 0` |
| `printMatrix(n)`, `printPath(i, j)` | Output helpers |
| `dijkstra(n, s, t, w)` | O(n²) Dijkstra on its own weight matrix `w` |
| `main` | Demo 1 (Dijkstra against Floyd), demo 2 (a 5-vertex graph with negative edges), demo 3 (a negative cycle) |

## Output

```
===== 1) Dijkstra fails with a negative edge =====
  Dijkstra   dist(1 -> 2) = 2   (WRONG)
  Floyd      dist(1 -> 2) = 1   (correct: 1 -> 3 -> 2)

===== 2) Floyd-Warshall with negative edges, no negative cycle =====
Negative cycle? NO
  1 -> 3 : cost  -3   path 1 -> 5 -> 4 -> 3

===== 3) Negative cycle detection =====
Negative cycle? YES
Vertices on / reaching a negative cycle (A(i,i) < 0): 1 2 3
```

The full matrices are in [output.txt](output.txt). [NegativeWeights.java.md](NegativeWeights.java.md#output) explains each demo step by step.

## Complexity

- **Floyd:** Θ(n³) time, Θ(n²) space.
- **Dijkstra here:** O(n²).

## Notes

- **When a negative cycle exists, the printed numbers for affected pairs are not real distances.** For example, 1 → 4 shows −1, but it is really unbounded below, because the cycle 1 → 2 → 3 → 1 can be repeated before taking 3 → 4. A pair (i, j) is undefined whenever i can reach the cycle and the cycle can reach j.
- `MAXN` is 8, so these demos are limited to 7 vertices. Raise it for bigger graphs.
