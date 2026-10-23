/*
 * Part A - Assignment 2
 * General graph: BCOST(l) may NOT be ready when BCOST(j) is computed.
 *
 * Re-design:
 *   1. Topologically sort the vertices (Kahn's algorithm).
 *   2. If the graph is a DAG, run the same BCOST recurrence but visit the
 *      vertices in TOPOLOGICAL order instead of numeric order. Every
 *      predecessor l of j then comes before j, so BCOST(l) is ready.
 *   3. If the sort fails (the graph has a cycle) there is no order that
 *      works, so fall back to Bellman-Ford: relax every edge n-1 times,
 *      then do one more pass to detect a negative cycle.
 *
 * Demos:
 *   A) Slide-20 graph with vertices RELABELLED (not in stage order):
 *      the naive Q1 loop gives a wrong answer, the re-design is correct.
 *   B) Original graph + edge 10->7 (cost 1): a positive cycle -> Bellman-Ford.
 *   C) Original graph + edge 10->7 (cost -6): negative cycle -> reported.
 *
 * Compile: gcc -O2 -o general_graph general_graph.c
 */
#include <stdio.h>

#define MAXN 32
#define MAXE 128
#define INF  1000000000

typedef struct { int u, v, w; } Edge;

static Edge base[] = {
    {1,2,9},{1,3,7},{1,4,3},{1,5,2},
    {2,6,4},{2,7,2},{2,8,1},
    {3,6,2},{3,7,7},
    {4,8,11},
    {5,7,11},{5,8,8},
    {6,9,6},{6,10,5},
    {7,9,4},{7,10,3},
    {8,10,5},{8,11,6},
    {9,12,4},{10,12,2},{11,12,5}
};
static const int BASE_E = sizeof(base) / sizeof(base[0]);

enum { OK, NO_PATH, NEG_CYCLE };

/* Kahn's algorithm. Returns number of vertices placed in order[]. */
int topoSort(int n, Edge *e, int m, int order[]) {
    int indeg[MAXN] = {0}, queue[MAXN], head = 0, tail = 0, cnt = 0;
    for (int i = 0; i < m; i++) indeg[e[i].v]++;
    for (int v = 1; v <= n; v++) if (indeg[v] == 0) queue[tail++] = v;
    while (head < tail) {
        int u = queue[head++];
        order[cnt++] = u;
        for (int i = 0; i < m; i++)
            if (e[i].u == u && --indeg[e[i].v] == 0) queue[tail++] = e[i].v;
    }
    return cnt;
}

/* Re-designed algorithm. method[] receives a description. */
int shortestPath(int n, Edge *e, int m, int s, int t,
                 int *cost, int path[], int *len, const char **method) {
    int bcost[MAXN], d[MAXN], order[MAXN];
    for (int v = 1; v <= n; v++) { bcost[v] = INF; d[v] = 0; }
    bcost[s] = 0;

    if (topoSort(n, e, m, order) == n) {
        *method = "DAG -> BCOST in topological order";
        for (int k = 0; k < n; k++) {
            int j = order[k];
            for (int i = 0; i < m; i++)               /* edges <l, j> */
                if (e[i].v == j && bcost[e[i].u] != INF &&
                    bcost[e[i].u] + e[i].w < bcost[j]) {
                    bcost[j] = bcost[e[i].u] + e[i].w;
                    d[j] = e[i].u;
                }
        }
    } else {
        *method = "cycle found -> Bellman-Ford";
        for (int pass = 1; pass < n; pass++)
            for (int i = 0; i < m; i++)
                if (bcost[e[i].u] != INF && bcost[e[i].u] + e[i].w < bcost[e[i].v]) {
                    bcost[e[i].v] = bcost[e[i].u] + e[i].w;
                    d[e[i].v] = e[i].u;
                }
        for (int i = 0; i < m; i++)
            if (bcost[e[i].u] != INF && bcost[e[i].u] + e[i].w < bcost[e[i].v])
                return NEG_CYCLE;
    }

    if (bcost[t] == INF) return NO_PATH;
    *cost = bcost[t];
    int tmp[MAXN], k = 0, x = t;
    tmp[k++] = x;
    while (x != s && k <= n) { x = d[x]; tmp[k++] = x; }
    for (int i = 0; i < k; i++) path[i] = tmp[k - 1 - i];
    *len = k;
    return OK;
}

/* The Q1 algorithm, unchanged: assumes numeric order == stage order. */
int naiveBcost(int n, Edge *e, int m, int s, int t) {
    int bcost[MAXN];
    for (int v = 1; v <= n; v++) bcost[v] = INF;
    bcost[s] = 0;
    for (int j = 1; j <= n; j++) {
        if (j == s) continue;
        for (int i = 0; i < m; i++)
            if (e[i].v == j && bcost[e[i].u] != INF && bcost[e[i].u] + e[i].w < bcost[j])
                bcost[j] = bcost[e[i].u] + e[i].w;
    }
    return bcost[t];
}

void report(const char *title, int n, Edge *e, int m, int s, int t) {
    int cost, path[MAXN], len; const char *method = "";
    printf("%s\n", title);
    int r = shortestPath(n, e, m, s, t, &cost, path, &len, &method);
    printf("  Method: %s\n", method);
    if (r == NEG_CYCLE) printf("  Result: NEGATIVE CYCLE reachable - shortest path undefined\n\n");
    else if (r == NO_PATH) printf("  Result: no path from %d to %d\n\n", s, t);
    else {
        printf("  Shortest %d -> %d = %d   Path: ", s, t, cost);
        for (int i = 0; i < len; i++) printf("%d%s", path[i], i + 1 < len ? " -> " : "\n\n");
    }
}

int main(void) {
    Edge g[MAXE];

    /* A) relabelled graph: newLabel[old] */
    int newLabel[13] = {0, 7, 3, 12, 1, 9, 2, 11, 5, 10, 4, 8, 6};
    for (int i = 0; i < BASE_E; i++) {
        g[i].u = newLabel[base[i].u];
        g[i].v = newLabel[base[i].v];
        g[i].w = base[i].w;
    }
    int s = newLabel[1], t = newLabel[12];
    printf("A) Relabelled slide-20 graph (source is now %d, target is now %d)\n", s, t);
    int naive = naiveBcost(12, g, BASE_E, s, t);
    if (naive >= INF) printf("  Naive Q1 loop:  INF  (WRONG - predecessors not ready)\n");
    else              printf("  Naive Q1 loop:  %d  (%s)\n", naive, naive == 16 ? "correct by luck" : "WRONG");
    report("  Re-designed algorithm:", 12, g, BASE_E, s, t);

    /* B) positive cycle */
    for (int i = 0; i < BASE_E; i++) g[i] = base[i];
    g[BASE_E] = (Edge){10, 7, 1};
    report("B) Original graph + edge 10->7 (cost 1)  [cycle 7->10->7]", 12, g, BASE_E + 1, 1, 12);

    /* C) negative cycle */
    g[BASE_E] = (Edge){10, 7, -6};
    report("C) Original graph + edge 10->7 (cost -6) [cycle weight -3]", 12, g, BASE_E + 1, 1, 12);
    return 0;
}
