# arbitrary_st.c

**Part A, Question 4, in C.** Can the source s and destination t be **any** pair, with the same algorithm reused? Yes, for a DAG. Same algorithm and same output as [ArbitraryST.java](ArbitraryST.java.md).

**Written answer:** [answer.md](answer.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
gcc arbitrary_st.c -o arbitrary_st
./arbitrary_st
```

## The rules

- BCOST(s) = 0; every other vertex starts at INF.
- Process vertices in topological order starting at s. Here numeric order already is topological, so the loop is `j = s + 1 ... N`.
- A vertex that cannot be reached from s simply stays INF, which means "no path".
- s == t gives distance 0.

## What is inside

| Function | What it does |
| --- | --- |
| `bcostFrom(s)` | The Q1 algorithm for **any** source s |
| `brute(u, t)` | Tries **every** path from u to t by recursion and returns the cheapest. Slow, but obviously correct, so it checks the DP |
| `printCost(x, width)` | Prints a number, or `INF` |
| `printPath(s, t)` | Prints s -> ... -> t by recursion over `d[]` |
| `main` | 9 test pairs (including s == t and an impossible pair), then the full 12 × 12 distance matrix |

## Output

```
All results match brute force: YES
```

Pair (6, 6) prints cost 0, and pair (10, 2) prints `INF` / `no path`. The full output is in [output.txt](output.txt).

## Complexity

- One run of `bcostFrom`: O(n · e) as written.
- The full matrix runs it once per source: O(n² · e).
- `brute` can be exponential in general, which is why it is used only as a check.

## How the C file is written

Same team style as every C file in this repo:

- `#define` for sizes (`N`, `E`, `INF`) and `struct Edge { int from; int to; int cost; }` for one edge.
- The graph is one global array `edges[]`; the result tables are global arrays, indexed from 1 so they match the lecture (index 0 is unused).
- Every `if` and loop has braces, with one statement per line, so each step can be pointed at during the presentation.
- `INF` is 1,000,000. That is far bigger than any real path cost here, and `INF + cost` still fits in an `int`.

## Questions you may be asked

- **Why start the loop at s + 1?** A vertex numbered before s can never be reached from s, because every edge goes from a lower number to a higher one.
- **How do you prove the DP answers are right?** Every pair is compared with `brute`, which checks all possible paths. The program prints `YES` only if all of them match.
- **What would break this?** A cycle, since then there is no topological order. Q2 handles that case.
