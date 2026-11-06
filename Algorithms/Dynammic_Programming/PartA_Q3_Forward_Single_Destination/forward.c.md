# forward.c

**Part A, Question 3, in C.** The **forward approach** (FGraph): the shortest path from **every** vertex to one destination t. Same algorithm and same output as [Forward.java](Forward.java.md).

**Written answer:** [answer.md](answer.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
gcc forward.c -o forward
./forward
```

## The rule

```
COST(t) = 0
COST(i) = min over every edge <i, r> of { c(i, r) + COST(r) }
D(i)    = the r that gave the minimum   (the best next hop from i)
```

Vertices are processed from i = t - 1 down to 1 (reverse stage order), so COST(r) is always ready. When the loop ends, COST(i) is the shortest distance from **every** vertex i to t, not just from the source.

## What is inside

| Part | Purpose |
| --- | --- |
| `cost[]`, `d[]` | Cheapest cost from i to t, and the best next vertex after i |
| `fgraph(t)` | The DP: fills `cost[]` and `d[]` |
| `main` | Prints a row per vertex: COST(i), D(i), and the path found by following `d[]` forward until t |

## Output (first rows)

```
  i | COST(i) | D(i) | shortest path i -> t
  1 |      16 |    2 | 1 -> 2 -> 7 -> 10 -> 12
  2 |       7 |    7 | 2 -> 7 -> 10 -> 12
```

The full table is in [output.txt](output.txt).

## Complexity

- **Time O(n · e) as written:** the loop scans the whole edge list for each vertex. With a list of outgoing edges per vertex it is Θ(n + e).
- **Space O(n).**

## How the C file is written

Same team style as every C file in this repo:

- `#define` for sizes (`N`, `E`, `INF`) and `struct Edge { int from; int to; int cost; }` for one edge.
- The graph is one global array `edges[]`; the result tables are global arrays, indexed from 1 so they match the lecture (index 0 is unused).
- Every `if` and loop has braces, with one statement per line, so each step can be pointed at during the presentation.
- `INF` is 1,000,000. That is far bigger than any real path cost here, and `INF + cost` still fits in an `int`.

## Questions you may be asked

- **What is the difference from the backward approach?** Backward measures cost *from the source*; forward measures cost *to the destination*. Forward answers "best route to t" for every starting point in one run.
- **Why does it print 1 -> 2 -> 7 -> 10 -> 12 here but 1 -> 3 -> 6 -> 10 -> 12 in Q1?** Both cost 16. The two methods meet the tie in a different order, and the strict `<` keeps whichever came first.
- **Where is this used for real?** A GPS or a delivery company working out the best route *to* one depot from every location.
