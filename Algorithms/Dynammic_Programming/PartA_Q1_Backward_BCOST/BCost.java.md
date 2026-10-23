# BCost.java

**Part A, Question 1.** The **backward approach** (the textbook's *BGraph*) on the lecture's 12-vertex multistage graph. It assumes BCOST(l) has already been computed whenever BCOST(j) needs it.

**Language:** Java · **C version:** [bcost.c](bcost.c.md) · **Written answer:** [answer.md](answer.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
javac BCost.java
java BCost
```

## The recurrence

BCOST(j) is the cheapest cost of getting **from the source s to j**:

```
BCOST(s) = 0
BCOST(j) = min over every edge <l, j> of { BCOST(l) + c(l, j) }
D(j)     = the l that gave the minimum      (used to rebuild the path)
```

The lecture slides use these names the other way round. Here, following the textbook (Horowitz–Sahni), **BCOST** is measured *from the source*.

## Why the assumption holds

The vertices are numbered stage by stage (1 | 2–5 | 6–8 | 9–11 | 12), so **every edge ⟨l, j⟩ has l < j**. Looping `j = s+1, s+2, …, n` in numeric order therefore always reaches a vertex after all of its predecessors, which means BCOST(l) is final by the time BCOST(j) needs it.

## How the code is organised

| Member | Purpose |
| --- | --- |
| `N = 12`, `INF = Integer.MAX_VALUE / 4` | The number of vertices and "infinity". INF is a quarter of the largest `int`, so `INF + weight` can't overflow |
| `EDGES` | The 21 edges of the lecture graph, as `{u, v, w}` triples |
| `pred` | `List<List<int[]>>`, where `pred.get(v)` lists the `{u, w}` of every edge **into** v. It is built once, in the constructor |
| `bcost[]`, `d[]` | The DP table and the decision table |
| `bgraph(s, t)` | Fills `bcost`/`d` for every vertex, then follows `d[]` back from t and reverses the result to give the path |
| `main` | Runs `bgraph(1, 12)` and prints the table, the distance and the path |

The core loop:

```java
for (int j = s + 1; j <= N; j++)                 // stage order
    for (int[] e : pred.get(j)) {                // each incoming edge, once
        int l = e[0], c = e[1];
        if (bcost[l] != INF && bcost[l] + c < bcost[j]) {
            bcost[j] = bcost[l] + c;
            d[j] = l;
        }
    }
```

## Output

```
Backward approach (BGraph) - slide 20 multistage graph
-----------------------------
  j | BCOST(j) | D(j)
-----------------------------
  1 |        0 |    -
  2 |        9 |    1
  3 |        7 |    1
  4 |        3 |    1
  5 |        2 |    1
  6 |        9 |    3
  7 |       11 |    2
  8 |       10 |    2
  9 |       15 |    6
 10 |       14 |    6
 11 |       16 |    8
 12 |       16 |   10
-----------------------------
Shortest distance 1 -> 12 = 16
Path: 1 -> 3 -> 6 -> 10 -> 12
```

**Why only one of the two shortest paths is shown.** BCOST(10) can be reached through 6 (9 + 5 = 14) or through 7 (11 + 3 = 14). The test is a strict `<`, so the first one examined (6) is kept, and the equally short path **1 → 2 → 7 → 10 → 12** isn't printed. To list every shortest path, store all the l values that tie in `D(j)`.

## Complexity

- **Time Θ(n + e):** each vertex is visited once, and each edge is examined once through the predecessor lists.
- **Space:** O(n) for `bcost` and `d`, plus O(n + e) for the predecessor lists.
- Rebuilding the path takes O(k) for k stages.

## Notes

- `d[x] == 0` means "no predecessor". This is safe because vertex numbers start at 1.
- The path loop stops at s, or when `d[x] == 0`, so an unreachable t can't make it loop forever.
