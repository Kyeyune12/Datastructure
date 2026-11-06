# bcost.c

**Part A, Question 1, in C.** The **backward approach** (BGraph) on the lecture's 12-vertex multistage graph (slide 20), assuming BCOST(l) is ready whenever BCOST(j) needs it. Same algorithm and same output as [BCost.java](BCost.java.md).

**Written answer:** [answer.md](answer.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
gcc bcost.c -o bcost
./bcost
```

## The rule

```
BCOST(s) = 0
BCOST(j) = min over every edge <l, j> of { BCOST(l) + c(l, j) }
D(j)     = the l that gave the minimum   (used to rebuild the path)
```

The vertices are numbered stage by stage, so every edge <l, j> has l < j. Looping j = s+1 ... n therefore always finds BCOST(l) already worked out.

## What is inside

| Part | Purpose |
| --- | --- |
| `struct Edge edges[E]` | The 21 edges of the slide-20 graph |
| `bcost[]`, `d[]` | Cheapest cost from s to j, and the vertex we came from |
| `bgraph(s)` | The DP: fills `bcost[]` and `d[]` |
| `printPath(j)` | Prints the path to j by **recursion**: print the path to `d[j]` first, then `-> j` |
| `main` | Runs `bgraph(1)` and prints the table, the distance and the path |

The core loop:

```c
for (int j = s + 1; j <= N; j++) {          // stage order
    for (int i = 0; i < E; i++) {
        int l = edges[i].from;
        int c = edges[i].cost;
        if (edges[i].to != j) {
            continue;                       // only edges that END at j
        }
        if (bcost[l] != INF && bcost[l] + c < bcost[j]) {
            bcost[j] = bcost[l] + c;
            d[j] = l;
        }
    }
}
```

## Output

```
Shortest distance 1 -> 12 = 16
Path: 1 -> 3 -> 6 -> 10 -> 12
```

The full table is in [output.txt](output.txt).

## Complexity

- **Time O(n · e) as written:** for every vertex j the loop scans the whole edge list. Keeping a list of incoming edges for each vertex would make it Θ(n + e), which is the figure in [answer.md](answer.md). With 12 vertices and 21 edges the difference is not noticeable, and the simple loop is much easier to explain.
- **Space O(n)** for `bcost[]` and `d[]`.

## How the C file is written

Same team style as every C file in this repo:

- `#define` for sizes (`N`, `E`, `INF`) and `struct Edge { int from; int to; int cost; }` for one edge.
- The graph is one global array `edges[]`; the result tables are global arrays, indexed from 1 so they match the lecture (index 0 is unused).
- Every `if` and loop has braces, with one statement per line, so each step can be pointed at during the presentation.
- `INF` is 1,000,000. That is far bigger than any real path cost here, and `INF + cost` still fits in an `int`.

## Questions you may be asked

- **Why can you loop j in numeric order?** Because every edge goes from a lower number to a higher number, so all of j's predecessors are finished first.
- **Why `bcost[l] != INF` before adding?** It skips vertices that cannot be reached yet, so we never treat "infinity + cost" as a real path.
- **Why does `printPath` stop when `d[j] == 0`?** Only the source has no predecessor, so `d[j] == 0` means "we are back at the start".
- **There are two shortest paths of cost 16. Why does it print this one?** The test is a strict `<`, so on a tie the first edge found is kept.
