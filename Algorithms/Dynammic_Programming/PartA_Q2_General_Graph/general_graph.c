#include <stdio.h>

// Part A - Question 2
// General graph: BCOST(l) may NOT be ready when BCOST(j) is computed.
//
// Re-design:
//   1. Topologically sort the vertices (Kahn's algorithm).
//   2. If the graph is a DAG, run the same BCOST rule but visit the
//      vertices in TOPOLOGICAL order instead of numeric order. Every
//      predecessor l of j then comes before j, so BCOST(l) is ready.
//   3. If the sort fails (the graph has a cycle) there is no order that
//      works, so fall back to Bellman-Ford: relax every edge n-1 times,
//      then do one more pass to detect a negative cycle.
//
// Demos:
//   A) Slide-20 graph with vertices RELABELLED (not in stage order):
//      the naive Q1 loop gives a wrong answer, the re-design is correct.
//   B) Original graph + edge 10->7 (cost 1): a positive cycle -> Bellman-Ford.
//   C) Original graph + edge 10->7 (cost -6): negative cycle -> reported.
//
// Build: gcc general_graph.c -o general_graph
// Run:   ./general_graph

#define N 12            // number of vertices
#define E 21            // number of edges in the slide-20 graph
#define INF 1000000     // "infinity" (no path yet)

struct Edge {
    int from;
    int to;
    int cost;
};

struct Edge base[E] = {
    {1, 2, 9}, {1, 3, 7}, {1, 4, 3}, {1, 5, 2},
    {2, 6, 4}, {2, 7, 2}, {2, 8, 1},
    {3, 6, 2}, {3, 7, 7},
    {4, 8, 11},
    {5, 7, 11}, {5, 8, 8},
    {6, 9, 6}, {6, 10, 5},
    {7, 9, 4}, {7, 10, 3},
    {8, 10, 5}, {8, 11, 6},
    {9, 12, 4}, {10, 12, 2}, {11, 12, 5}
};

// ---------- Step 1: topological sort (Kahn's algorithm) ----------
// Repeatedly take a vertex with no incoming edges, add it to the order,
// and "remove" its outgoing edges.
// Returns how many vertices were placed. If that is less than N, there is a cycle.
int topoSort(struct Edge e[], int m, int order[]) {
    int indeg[N + 1];   // indeg[v] = number of edges coming INTO v
    for (int v = 1; v <= N; v++) {
        indeg[v] = 0;
    }
    for (int i = 0; i < m; i++) {
        indeg[e[i].to]++;
    }

    // a simple array queue: add at 'tail', remove at 'head'
    int queue[N];
    int head = 0;
    int tail = 0;

    // start with every vertex that has no incoming edges
    for (int v = 1; v <= N; v++) {
        if (indeg[v] == 0) {
            queue[tail] = v;
            tail++;
        }
    }

    int count = 0;
    while (head < tail) {
        int u = queue[head];
        head++;

        order[count] = u;
        count++;

        // remove every edge out of u; a vertex whose count reaches 0 is ready
        for (int i = 0; i < m; i++) {
            if (e[i].from == u) {
                indeg[e[i].to]--;
                if (indeg[e[i].to] == 0) {
                    queue[tail] = e[i].to;
                    tail++;
                }
            }
        }
    }
    return count;
}

// ---------- Steps 2 and 3: shortest path, then print the result ----------
void shortestPath(struct Edge e[], int m, int s, int t) {
    int bcost[N + 1];
    int d[N + 1];
    int order[N];

    for (int v = 1; v <= N; v++) {
        bcost[v] = INF;
        d[v] = 0;
    }
    bcost[s] = 0;

    if (topoSort(e, m, order) == N) {
        // DAG: same BCOST rule as Q1, but in topological order
        printf("  Method: DAG -> BCOST in topological order\n");

        for (int k = 0; k < N; k++) {
            int j = order[k];
            for (int i = 0; i < m; i++) {
                int l = e[i].from;
                if (e[i].to == j && bcost[l] != INF && bcost[l] + e[i].cost < bcost[j]) {
                    bcost[j] = bcost[l] + e[i].cost;
                    d[j] = l;
                }
            }
        }
    } else {
        // Cycle: Bellman-Ford. Check every edge, n - 1 times.
        printf("  Method: cycle found -> Bellman-Ford\n");

        for (int pass = 1; pass < N; pass++) {
            for (int i = 0; i < m; i++) {
                int from = e[i].from;
                int to = e[i].to;
                if (bcost[from] != INF && bcost[from] + e[i].cost < bcost[to]) {
                    bcost[to] = bcost[from] + e[i].cost;
                    d[to] = from;
                }
            }
        }

        // one more pass: if anything still gets cheaper, there is a negative cycle
        for (int i = 0; i < m; i++) {
            int from = e[i].from;
            int to = e[i].to;
            if (bcost[from] != INF && bcost[from] + e[i].cost < bcost[to]) {
                printf("  Result: NEGATIVE CYCLE reachable - shortest path undefined\n\n");
                return;
            }
        }
    }

    if (bcost[t] == INF) {
        printf("  Result: no path from %d to %d\n\n", s, t);
        return;
    }

    // walk back from t to s, storing the vertices, then print them in reverse
    int path[N + 1];
    int len = 0;
    int x = t;
    path[len] = x;
    len++;
    while (x != s) {
        x = d[x];
        path[len] = x;
        len++;
    }

    printf("  Shortest %d -> %d = %d   Path: ", s, t, bcost[t]);
    for (int i = len - 1; i >= 0; i--) {
        printf("%d", path[i]);
        if (i > 0) {
            printf(" -> ");
        }
    }
    printf("\n\n");
}

// The Q1 algorithm, unchanged: assumes numeric order == stage order.
// Used in demo A to show that it fails on a relabelled graph.
int naiveBcost(struct Edge e[], int m, int s, int t) {
    int bcost[N + 1];
    for (int v = 1; v <= N; v++) {
        bcost[v] = INF;
    }
    bcost[s] = 0;

    for (int j = 1; j <= N; j++) {
        if (j == s) {
            continue;
        }
        for (int i = 0; i < m; i++) {
            int l = e[i].from;
            if (e[i].to == j && bcost[l] != INF && bcost[l] + e[i].cost < bcost[j]) {
                bcost[j] = bcost[l] + e[i].cost;
            }
        }
    }
    return bcost[t];
}

int main() {
    struct Edge g[E + 1];   // room for the slide-20 edges plus one extra edge

    // A) relabelled graph: vertex 'old' gets the name newLabel[old]
    int newLabel[13] = {0, 7, 3, 12, 1, 9, 2, 11, 5, 10, 4, 8, 6};
    for (int i = 0; i < E; i++) {
        g[i].from = newLabel[base[i].from];
        g[i].to = newLabel[base[i].to];
        g[i].cost = base[i].cost;
    }
    int s = newLabel[1];
    int t = newLabel[12];
    printf("A) Relabelled slide-20 graph (source is now %d, target is now %d)\n", s, t);

    int naive = naiveBcost(g, E, s, t);
    if (naive == INF) {
        printf("  Naive Q1 loop:  INF  (WRONG - predecessors not ready)\n");
    } else if (naive == 16) {
        printf("  Naive Q1 loop:  %d  (correct by luck)\n", naive);
    } else {
        printf("  Naive Q1 loop:  %d  (WRONG)\n", naive);
    }
    printf("  Re-designed algorithm:\n");
    shortestPath(g, E, s, t);

    // B) positive cycle 7 -> 10 -> 7: copy the graph and add edge 10 -> 7 (cost 1)
    for (int i = 0; i < E; i++) {
        g[i] = base[i];
    }
    g[E].from = 10;
    g[E].to = 7;
    g[E].cost = 1;
    printf("B) Original graph + edge 10->7 (cost 1)  [cycle 7->10->7]\n");
    shortestPath(g, E + 1, 1, 12);

    // C) negative cycle 7 -> 10 -> 7 with total weight 3 + (-6) = -3
    g[E].cost = -6;
    printf("C) Original graph + edge 10->7 (cost -6) [cycle weight -3]\n");
    shortestPath(g, E + 1, 1, 12);

    return 0;
}
