# kruskal_mst.c

**Greedy Problem 2, in C.** Uses **Kruskal's algorithm** to connect 6 campus buildings with the least total network cable. Union-Find is used to avoid loops. Its output is identical to [KruskalMST.java](KruskalMST.java.md).

**Language:** C · **Java version:** [KruskalMST.java](KruskalMST.java.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
gcc kruskal_mst.c -o kruskal_mst
./kruskal_mst
```

## The problem

There are 6 buildings, numbered 0–5, and 8 possible cables (edges), each with a cost:

```
Buildings: 0 Library, 1 Admin, 2 Lab, 3 Hostel, 4 Cafeteria, 5 Hall

            Library
           4/     \3
        Admin --1-- Lab
          2 \      / 4
             Hostel
           2/     \7
     Cafeteria --6-- Hall
```

| Edge | Cost |
|---|---|
| Library – Admin | 4 |
| Library – Lab | 3 |
| Admin – Lab | 1 |
| Admin – Hostel | 2 |
| Lab – Hostel | 4 |
| Hostel – Cafeteria | 2 |
| Cafeteria – Hall | 6 |
| Hostel – Hall | 7 |

We want a set of cables that connects **every** building with the **smallest total cost**. Such a set never contains a loop, because a loop always has one cable you could remove while staying connected. A connected set of cables with no loops is a **spanning tree**, and for N buildings it has exactly **N − 1 = 5** cables. The cheapest one is the **minimum spanning tree (MST)**.

## The greedy idea (Kruskal)

1. Sort all edges from cheapest to most expensive.
2. Go through them in that order. **Take** an edge if it joins two buildings that are not yet connected. **Skip** it if they are already connected, because it would close a loop.
3. Stop once N − 1 edges have been taken.

The hard part is step 2: how do we quickly know whether two buildings are already connected? That is what **Union-Find** answers.

## Union-Find in this program

Every building belongs to a **group** (a set of buildings already linked together). Each group has a **leader** (its root). `parent[i]` points from building `i` toward its leader, and a leader points to itself.

```c
int parent[N];

int find(int x) {
    while (parent[x] != x) x = parent[x];   /* follow pointers up to the root */
    return x;
}

void unite(int a, int b) {
    parent[find(a)] = find(b);              /* a's leader now points at b's leader */
}
```

- **`find(x)`** follows parent pointers until it reaches a building that is its own parent. That building is the group's leader.
- **Two buildings are already connected exactly when `find(a) == find(b)`.** Adding the edge `a–b` in that case would make a loop.
- **`unite(a, b)`** merges two groups by hanging one leader under the other.
- At the start, `parent[i] = i` for every building, so each building is in its own group.

This is the simplest form of Union-Find. It has no *path compression* and no *union by rank*, so groups can turn into long chains and `find` can take O(N) steps. That is fine for 6 buildings. The improved versions are covered under complexity below.

## Walking through `main`

```c
struct Edge edges[] = { {0,1,4}, {0,2,3}, {1,2,1}, ... };
int m = sizeof(edges) / sizeof(edges[0]);   /* 8 edges */
sortEdges(edges, m);                          /* STEP 1: cheapest first */
for (int i = 0; i < N; i++) parent[i] = i;   /* everyone alone */
```

`sortEdges` is an ascending **bubble sort** on `cost`. It only swaps when `e[j].cost > e[j+1].cost` (strictly greater), so it is **stable**: edges with equal cost stay in their original order. That's why `Admin – Hostel` (cost 2) is considered before `Hostel – Cafeteria` (also cost 2).

```c
for (int i = 0; i < m && edgesUsed < N - 1; i++) {
    int a = edges[i].from, b = edges[i].to;
    if (find(a) != find(b)) {          /* different groups -> no loop */
        unite(a, b);
        totalCost += edges[i].cost;
        edgesUsed++;
        printf("  %s - %s  (cost %d)\n", ...);
    } else {
        printf("  [skip] %s - %s would make a loop\n", ...);
    }
}
```

The loop has two stopping conditions: it runs out of edges (`i < m`), or the tree is complete (`edgesUsed < N - 1`). The second one is why `Hostel – Hall (7)` is never examined. The tree already had 5 edges by then.

## Trace

Sorted edges: Admin–Lab 1, Admin–Hostel 2, Hostel–Cafeteria 2, Library–Lab 3, Library–Admin 4, Lab–Hostel 4, Cafeteria–Hall 6, Hostel–Hall 7.

`parent` is written as `[p0 p1 p2 p3 p4 p5]`.

| # | Edge (cost) | find(a), find(b) | Action | `parent` after | Total |
|---|---|---|---|---|---|
| 1 | Admin(1)–Lab(2) (1) | 1, 2 | take, `parent[1]=2` | `[0 2 2 3 4 5]` | 1 |
| 2 | Admin(1)–Hostel(3) (2) | 2, 3 | take, `parent[2]=3` | `[0 2 3 3 4 5]` | 3 |
| 3 | Hostel(3)–Cafeteria(4) (2) | 3, 4 | take, `parent[3]=4` | `[0 2 3 4 4 5]` | 5 |
| 4 | Library(0)–Lab(2) (3) | 0, 4 | take, `parent[0]=4` | `[4 2 3 4 4 5]` | 8 |
| 5 | Library(0)–Admin(1) (4) | 4, 4 | **skip** (loop) | unchanged | 8 |
| 6 | Lab(2)–Hostel(3) (4) | 4, 4 | **skip** (loop) | unchanged | 8 |
| 7 | Cafeteria(4)–Hall(5) (6) | 4, 5 | take, `parent[4]=5` | `[4 2 3 4 5 5]` | **14** |
| – | Hostel–Hall (7) | – | not examined: 5 edges already | – | – |

In row 5, `find(1)` walks 1 → 2 → 3 → 4. That's the chain effect described above, and it's why real implementations add path compression.

Final MST: Admin–Lab, Admin–Hostel, Hostel–Cafeteria, Library–Lab, Cafeteria–Hall. **Total cost 14.**

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

## Why greedy is correct (the cut property)

Split the buildings into any two sides. The cheapest edge crossing between the sides belongs to *some* MST. If an MST didn't use it, adding it would create a loop that crosses the split a second time through a more expensive edge. Swapping the two gives a tree that costs no more. When Kruskal takes an edge `a–b`, `a` and `b` are in different groups, so `a`'s group versus everything else is such a split. No cheaper edge can cross it: Kruskal would already have examined that edge, found its ends in different groups, and taken it, which would have merged them. So `a–b` is the cheapest edge across the split and is safe to take.

## Complexity

Let N = buildings (vertices) and m = cables (edges).

| Part | This program | With better tools |
|---|---|---|
| Sort edges | O(m²) bubble sort | O(m log m) with `qsort` |
| Union-Find (m finds) | O(m · N) worst case | almost O(m) with path compression + union by rank |
| **Total** | **O(m² + m·N)** | **O(m log m)** |
| Space | O(N) for `parent` | O(N) |

## Things to notice

- `parent` is a global array, so `find` and `unite` can use it without it being passed in.
- `N` is a `#define`, so `parent[N]` and `buildings[N]` have a fixed size at compile time.
- If the graph were disconnected, the loop would end with fewer than N − 1 edges, giving a minimum spanning *forest*. The program doesn't check for this. Adding `if (edgesUsed < N - 1) printf("Graph is not connected\n");` would.
