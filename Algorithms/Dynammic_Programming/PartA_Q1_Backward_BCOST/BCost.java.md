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
| `bcost[]`, `d[]` | The DP table and the decision table (both `static`) |
| `bgraph(s)` | Fills `bcost`/`d` for every vertex |
| `path(j)` | Rebuilds the path to j **recursively**: the path to `d[j]`, followed by `" -> j"`. It stops at the start vertex, whose `d` is 0 |
| `main` | Runs `bgraph(1)` and prints the table, the distance and the path |

The core loop:

```java
for (int j = s + 1; j <= N; j++) {          // stage order
    for (int[] e : EDGES) {
        int l = e[0];
        int to = e[1];
        int c = e[2];

        if (to != j) {
            continue;   // we only want edges that END at j
        }

        // is "go to l, then take edge l -> j" cheaper than what we have?
        if (bcost[l] != INF && bcost[l] + c < bcost[j]) {
            bcost[j] = bcost[l] + c;
            d[j] = l;
        }
    }
}
```

And the path, built recursively:

```java
static String path(int j) {
    if (d[j] == 0) {
        return "" + j;   // j is the start vertex
    }
    return path(d[j]) + " -> " + j;
}
```

For example, `path(12)` = `path(10) + " -> 12"` = `path(6) + " -> 10 -> 12"` = … = `"1 -> 3 -> 6 -> 10 -> 12"`.

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

- **Time O(n · e) as written:** for each vertex j, the loop scans the whole edge list looking for edges that end at j. With 12 vertices and 21 edges this is tiny. Keeping a separate list of incoming edges for each vertex would bring it down to Θ(n + e), at the cost of more code.
- **Space:** O(n) for `bcost` and `d`.
- Rebuilding the path takes O(k) for k stages.

## Notes

- `d[x] == 0` means "no predecessor". This is safe because vertex numbers start at 1.
- `path(j)` stops when `d[j] == 0`, so it always ends.

## What changed (simplified version)

| Before | Now | Why |
| --- | --- | --- |
| `import java.util.ArrayList`, `Collections` and `List` | No imports | Only plain arrays are used |
| A `pred` list of incoming edges, built in a constructor | The loop scans `EDGES` directly and skips edges that don't end at j | No list-of-lists to explain. Costs O(n · e) instead of Θ(n + e), which makes no difference on this graph |
| `bgraph(s, t)` built the path into a `List`, then called `Collections.reverse` | `bgraph(s)` fills the tables, and a short recursive `path(j)` builds the path string | Recursion produces the path in the right order with no reversing |
| An object (`new BCost()`) held the tables | The tables and methods are `static` | Nothing to construct |
| `StringBuilder` to join the path | Plain `String` joining | Easier to read |
| Several statements squeezed onto one line, and loops or `if`s without braces | One statement per line, braces on every loop and `if`, and comments explaining each step | Easier to read and explain |
