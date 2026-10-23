# bcost.c

**Part A, Question 1, in C.** The **backward approach** (*BGraph*) on the lecture's 12-vertex multistage graph, assuming BCOST(l) is already computed whenever BCOST(j) needs it. The algorithm and output are identical to [BCost.java](BCost.java.md).

**Language:** C · **Java version:** [BCost.java](BCost.java.md) · **Written answer:** [answer.md](answer.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
gcc -O2 -o bcost bcost.c
./bcost
```

## The recurrence

```
BCOST(s) = 0
BCOST(j) = min over every edge <l, j> of { BCOST(l) + c(l, j) }
D(j)     = the l that gave the minimum
```

BCOST(j) is the cheapest cost **from the source s to j**. This is the textbook's naming; the lecture slides use these names the other way round.

The vertices are numbered stage by stage, so every edge ⟨l, j⟩ has l < j. Looping j = s+1 … n in numeric order therefore always has BCOST(l) ready.

## How the code is organised

| Part | Purpose |
| --- | --- |
| `#define N 12`, `MAXE 64`, `INF 1000000000` | Graph size, space for the predecessor lists, and "infinity" (INF + weight still fits in an `int`) |
| `typedef struct { int u, v, w; } Edge;` | One directed, weighted edge |
| `edges[]`, `E` | The 21 lecture edges, and how many there are (`sizeof(edges)/sizeof(edges[0])`) |
| `pred[N+1][MAXE]`, `predCount[]` | `pred[v][k]` is the position in `edges[]` of the k-th edge **into** v |
| `buildPredecessors()` | Fills `pred` in one pass over the edges |
| `bgraph(s, t, bcost, d, path, &len)` | The DP. Returns BCOST(t) and writes the path into `path[]`, with its length in `len` |
| `main` | Calls `bgraph(1, 12, …)` and prints the table, the distance and the path |

The core loop:

```c
for (int j = s + 1; j <= N; j++)                     /* stage order */
    for (int k = 0; k < predCount[j]; k++) {         /* each incoming edge once */
        Edge e = edges[pred[j][k]];
        if (bcost[e.u] != INF && bcost[e.u] + e.w < bcost[j]) {
            bcost[j] = bcost[e.u] + e.w;
            d[j] = e.u;
        }
    }
```

**Rebuilding the path:** follow `d[]` backwards from t into a temporary array, then copy it into `path[]` in reverse so it reads source-to-target.

## Output

Same as the Java version:

```
  j | BCOST(j) | D(j)
  1 |        0 |    -
  2 |        9 |    1
  ...
 12 |       16 |   10
Shortest distance 1 -> 12 = 16
Path: 1 -> 3 -> 6 -> 10 -> 12
```

See [output.txt](output.txt) for the full table. On a tie (BCOST(10) through 6 or through 7, both 14), the strict `<` keeps the first one, so the equally short **1 → 2 → 7 → 10 → 12** isn't printed.

## Complexity

- **Time Θ(n + e)**, since each edge is examined once through `pred`.
- **Space O(n)** for `bcost` and `d`, plus the fixed-size `pred` table.

## C-specific notes

- All arrays have fixed sizes set by `#define`. A vertex with more than `MAXE` incoming edges would overflow `pred`, which is fine for this graph but worth knowing.
- Arrays are indexed from 1 to match the lecture, so they are declared with size `N + 1` and index 0 is unused.
