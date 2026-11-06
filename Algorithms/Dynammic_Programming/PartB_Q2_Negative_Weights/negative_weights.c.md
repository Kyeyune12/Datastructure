# negative_weights.c

**Part B, Question 2, in C.** All-pairs shortest paths when some edges have **negative** weights. Same algorithm and same output as [NegativeWeights.java](NegativeWeights.java.md).

**Written answer:** [answer.md](answer.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
gcc negative_weights.c -o negative_weights
./negative_weights
```

## The key facts

- **Floyd-Warshall still works** with negative edges, as long as there is no **negative cycle**.
- A negative cycle shows up after the algorithm as a **negative number on the diagonal**: A(i, i) < 0 means i is on (or can reach) a negative cycle.
- **Dijkstra can fail**: it is greedy and fixes a vertex's distance forever once chosen, but a negative edge found later could still make it cheaper.

## What is inside

| Function | What it does |
| --- | --- |
| `init`, `addEdge`, `printMatrix`, `printPath` | Same as in [floyd.c](../PartB_Q1_All_Pairs_Floyd/floyd.c.md) |
| `floyd()` | The triple loop, then returns 1 if any `A[i][i] < 0` |
| `dijkstra(size, s, t, w)` | Plain O(n²) Dijkstra, to show it gets the wrong answer |

## The three demos

| Demo | Result |
| --- | --- |
| 1) Edges 1->2 (2), 1->3 (5), 3->2 (-4) | Dijkstra says dist(1, 2) = 2 (**wrong**). Floyd says 1 (correct, via 1 -> 3 -> 2) |
| 2) 5-vertex graph with negative edges, no negative cycle | Full correct matrix, `Negative cycle? NO` |
| 3) Cycle 1 -> 2 -> 3 -> 1 with total weight -2 | Diagonal goes negative, `Negative cycle? YES`, vertices 1 2 3 listed |

## Complexity

- Floyd: **Θ(n³)** time, Θ(n²) space. The diagonal check adds Θ(n).
- Dijkstra (matrix version): Θ(n²).

## How the C file is written

Same team style as every C file in this repo:

- `#define` for sizes (`N`, `E`, `INF`) and `struct Edge { int from; int to; int cost; }` for one edge.
- The graph is one global array `edges[]`; the result tables are global arrays, indexed from 1 so they match the lecture (index 0 is unused).
- Every `if` and loop has braces, with one statement per line, so each step can be pointed at during the presentation.
- `INF` is 1,000,000. That is far bigger than any real path cost here, and `INF + cost` still fits in an `int`.

## Questions you may be asked

- **Why does Dijkstra fail in demo 1?** It finalises vertex 2 with distance 2 before it looks at vertex 3. The edge 3 -> 2 (-4) would make it 1, but vertex 2 is already "done".
- **Why is a shortest path undefined with a negative cycle?** You can go round the cycle again and again, and each lap makes the total smaller, without limit.
- **Real example?** Currency exchange: if converting round a loop of currencies leaves you with more money than you started with, that is a negative cycle (an arbitrage opportunity).
