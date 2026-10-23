# NegativeWeights.java

**Part B, Question 2.** All-pairs shortest paths when edges can have **negative weights**. It shows three things:

1. Dijkstra's greedy method gives a wrong answer.
2. Floyd–Warshall still gives correct answers when there is no negative cycle.
3. A negative cycle is detected by a negative value on the diagonal of the result.

**Language:** Java · **C version:** [negative_weights.c](negative_weights.c.md) · **Written answer:** [answer.md](answer.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
javac NegativeWeights.java
java NegativeWeights
```

## Key facts

- **Floyd–Warshall still works with negative edges.** Its recurrence only compares path lengths; it never assumes they are positive.
- **It fails only with a negative cycle.** Going round such a cycle again always makes a path cheaper, so no shortest path exists.
- **Detection:** after the run, **A(i, i) < 0** means vertex i is on a negative cycle. A path from i back to itself should cost 0, but the cycle makes it cost less.
- **Dijkstra fails with negative edges.** It marks the nearest unfinished vertex as "done for good". A negative edge found later could have made that vertex cheaper, but Dijkstra never revisits it.

## How the code is organised

| Member | Purpose |
| --- | --- |
| `A`, `next`, `addEdge` | Same as in [Floyd.java](../PartB_Q1_All_Pairs_Floyd/Floyd.java.md) |
| `floyd()` | The triple loop. Returns `true` if any `A[i][i] < 0` afterwards |
| `printMatrix()`, `printPath(i, j)` | Output helpers |
| `dijkstra(n, s, t, w)` | A plain O(n²) Dijkstra on an adjacency matrix, used only to show it failing |
| `main` | Runs the three demos |

## Output

### 1) Dijkstra fails

```
Edges: 1->2 (2), 1->3 (5), 3->2 (-4)
  Dijkstra   dist(1 -> 2) = 2   (WRONG)
  Floyd      dist(1 -> 2) = 1   (correct: 1 -> 3 -> 2)
```

Dijkstra finalises vertex 2 at distance 2, because 2 is closer than 5. Only afterwards does it reach 3, whose edge of −4 would give 5 − 4 = 1. But vertex 2 is already locked in.

### 2) Negative edges, no negative cycle

A 5-vertex example:

```
          1    2    3    4    5
    1     0    1   -3    2   -4
    2     3    0   -4    1   -1
    3     7    4    0    5    3
    4     2   -1   -5    0   -2
    5     8    5    1    6    0
Negative cycle? NO
  1 -> 3 : cost  -3   path 1 -> 5 -> 4 -> 3
  1 -> 2 : cost   1   path 1 -> 5 -> 4 -> 3 -> 2
```

The diagonal is all 0, so there is no negative cycle and every entry is a true shortest distance. Some shortest paths are longer in edges but cheaper in cost: 1 → 5 → 4 → 3 costs −4 + 6 − 5 = −3, compared with 8 for the direct edge.

### 3) Detecting a negative cycle

```
Edges: 1->2 (1), 2->3 (-2), 3->1 (-1), 3->4 (2)   [cycle 1->2->3->1 = -2]
          1    2    3    4
    1    -2   -1   -3   -1
    2    -3   -2   -4   -2
    3    -3   -2   -4   -2
    4   INF  INF  INF    0
Negative cycle? YES
Vertices on / reaching a negative cycle (A(i,i) < 0): 1 2 3
```

## Complexity

- **Floyd–Warshall:** Θ(n³) time, Θ(n²) space. The negative-cycle check adds Θ(n).
- **Dijkstra, as written here:** O(n²).

## Notes

- **When there is a negative cycle, the numbers in the matrix are not real distances.** They are simply where the loop happened to stop. For example, A(1, 4) = −1 above, but 1 → 4 has no shortest path at all: you can go round the cycle as many times as you like before taking 3 → 4. The rule is that a pair (i, j) has no shortest path when some vertex k on a negative cycle can be reached from i and can itself reach j. That includes 1 → 4, 2 → 4 and 3 → 4 here, not only the pairs among 1, 2 and 3.
- The sums aren't clamped. With a very strongly negative cycle on a large graph, values could keep falling until they overflow an `int`. That is harmless in these small demos.
