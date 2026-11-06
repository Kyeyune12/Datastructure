# KruskalMST.java

**Greedy Problem 2, in Java.** Uses **Kruskal's algorithm** with Union-Find to connect 6 campus buildings using the least total cable. Its output is identical to [kruskal_mst.c](kruskal_mst.c.md).

**Language:** Java · **C version:** [kruskal_mst.c](kruskal_mst.c.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
javac KruskalMST.java
java KruskalMST
```

## The problem in one paragraph

There are 6 buildings (0 Library, 1 Admin, 2 Lab, 3 Hostel, 4 Cafeteria, 5 Hall) and 8 possible cables, each with a cost. We need cables that connect every building at the **lowest total cost**. That set is a **minimum spanning tree (MST)**: no loops, and exactly N − 1 = 5 cables.

## The greedy idea

Sort cables from cheapest to most expensive. Take each one **unless** its two buildings are already connected, because then it would form a loop. Stop at N − 1 cables.

## Walking through the code

### The `Edge` class

```java
static class Edge {
    int from, to, cost;
    Edge(int from, int to, int cost) { ... }
}
```

One possible cable. `from` and `to` are building numbers that index into the `buildings` array of names.

### Union-Find: `parent`, `find`, `union`

```java
static int[] parent;   // parent[i] = the "leader" of building i's group

static int find(int x) {
    while (parent[x] != x) x = parent[x];
    return x;
}

static void union(int a, int b) {
    parent[find(a)] = find(b);
}
```

- Each group of connected buildings has one **leader**, the building that is its own parent.
- `find(x)` climbs parent pointers up to the leader.
- `find(a) == find(b)` means `a` and `b` are already connected, so the edge would make a loop.
- `union(a, b)` merges two groups by pointing `a`'s leader at `b`'s leader.

`parent` is a `static` field, so `find` and `union` can reach it without it being passed around. It is created in `main` with `parent = new int[n]`, and `n` comes from `buildings.length`, so adding a building only means adding a name. The C version needs `#define N 6` instead.

This is basic Union-Find without path compression or union by rank. A `find` can walk a chain several steps long (for example 1 → 2 → 3 → 4 in the trace below), which is acceptable for 6 nodes.

### Sorting edges — the greedy choice

```java
// Bubble sort: keep swapping neighbours until the cheapest edge is first
static void sortByCost(Edge[] edges) {
    for (int i = 0; i < edges.length - 1; i++) {
        for (int j = 0; j < edges.length - 1 - i; j++) {
            if (edges[j].cost > edges[j + 1].cost) {
                Edge temp = edges[j];
                edges[j] = edges[j + 1];
                edges[j + 1] = temp;
            }
        }
    }
}
```

`main` calls `sortByCost(edges)`. Each pass swaps neighbouring edges when the left one is **more** expensive, which sorts in **ascending** order of cost. The test is a strict `>`, so edges with equal cost are never swapped. The sort is therefore **stable**: edges with the same cost keep their original order, so Admin–Hostel (2) stays ahead of Hostel–Cafeteria (2). The C bubble sort works the same way, which is why both programs print the same order.

### The main loop

```java
for (Edge e : edges) {
    if (edgesUsed == n - 1) {
        break;   // tree is complete: n buildings need only n - 1 cables
    }
    if (find(e.from) != find(e.to)) {         // different groups -> no loop
        union(e.from, e.to);
        totalCost += e.cost;
        edgesUsed++;
        System.out.println("  " + buildings[e.from] + " - " + buildings[e.to] + "  (cost " + e.cost + ")");
    } else {
        System.out.println("  [skip] ... would make a loop");
    }
}
```

The C version puts `edgesUsed < N - 1` in the `for` condition. Java uses a for-each loop, so the early exit is a `break` at the top instead. The behaviour is identical: once 5 edges are chosen, the remaining edge (Hostel–Hall, 7) is never looked at.

## Trace

| # | Edge (cost) | Leaders | Action | Total |
|---|---|---|---|---|
| 1 | Admin–Lab (1) | 1 ≠ 2 | take | 1 |
| 2 | Admin–Hostel (2) | 2 ≠ 3 | take | 3 |
| 3 | Hostel–Cafeteria (2) | 3 ≠ 4 | take | 5 |
| 4 | Library–Lab (3) | 0 ≠ 4 | take | 8 |
| 5 | Library–Admin (4) | 4 = 4 | **skip** | 8 |
| 6 | Lab–Hostel (4) | 4 = 4 | **skip** | 8 |
| 7 | Cafeteria–Hall (6) | 4 ≠ 5 | take (5th edge) | **14** |

## Output

```
Cables chosen for the Minimum Spanning Tree:
  Admin - Lab  (cost 1)
  Admin - Hostel  (cost 2)
  Hostel - Cafeteria  (cost 2)
  Library - Lab  (cost 3)
  [skip] Library - Admin would make a loop
  [skip] Lab - Hostel would make a loop
  Cafeteria - Hall  (cost 6)

Total cable cost: 14
```

## Why it is correct

By the **cut property**, the cheapest edge crossing any split of the buildings into two sides is safe to include in an MST. Whenever Kruskal takes an edge, it is the cheapest edge leaving its group, so every choice is safe. [kruskal_mst.c.md](kruskal_mst.c.md) has the full argument and a step-by-step `parent` array trace.

## Complexity

| Part | Time |
|---|---|
| `sortByCost` (bubble sort) | O(m²) |
| m `find` calls (basic Union-Find) | O(m · N) worst case |
| **Total** | **O(m² + m·N)**. With a faster sort (O(m log m)) plus path compression and union by rank, it would be O(m log m) |
| Space | O(N) for `parent` |

## C vs Java differences

| | C | Java |
|---|---|---|
| Edge record | `struct Edge` | `static class Edge` |
| Sorting | bubble sort, O(m²) | the same bubble sort, in `sortByCost` |
| Number of buildings | `#define N 6` | `buildings.length` |
| Merge function name | `unite` | `union` (just a method name in Java) |
| Stop when tree complete | in the `for` condition | `break` inside a for-each |

## What changed (simplified version)

| Before | Now | Why |
|---|---|---|
| `import java.util.Arrays;` | No imports | Only plain arrays and loops are used |
| `Arrays.sort(edges, (a, b) -> a.cost - b.cost)` | A hand-written bubble sort in `sortByCost` | No library call or lambda to explain; matches the C version |
| `int from, to, cost;` on one line | One field per line, each with a comment | Easier to read |
| One-line `for` and `if ... break` without braces | Braces on every loop and `if` | Easier to read |

The output is exactly the same as before.
