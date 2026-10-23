/*
 * Part A - Assignment 4
 * Can S and t be redefined arbitrarily and the same algorithm reused?
 *
 * Yes - for any pair (s, t) in a DAG - with these rules:
 *   - Process vertices in topological order starting at s
 *     (here numeric order is already topological).
 *   - BCOST(s) = 0; every other vertex starts at INF.
 *   - Vertices not reachable from s simply stay at INF.
 *   - If t comes before s in the order, or is unreachable: "no path".
 *   - s == t gives distance 0.
 *
 * The program runs the algorithm for several (s, t) pairs, checks each
 * against a brute-force search of every path, and finally runs it once
 * per source to print the full distance matrix.
 *
 * Compile: gcc -O2 -o arbitrary_st arbitrary_st.c
 */
#include <stdio.h>

#define N 12
#define INF 1000000000

typedef struct { int u, v, w; } Edge;

static Edge edges[] = {
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
static const int E = sizeof(edges) / sizeof(edges[0]);

/* Generalised BCOST: any source s. Fills bcost[] and d[] for all vertices. */
void bcostFrom(int s, int bcost[], int d[]) {
    for (int v = 1; v <= N; v++) { bcost[v] = INF; d[v] = 0; }
    bcost[s] = 0;
    for (int j = s + 1; j <= N; j++)                 /* topological order */
        for (int i = 0; i < E; i++)
            if (edges[i].v == j && bcost[edges[i].u] != INF &&
                bcost[edges[i].u] + edges[i].w < bcost[j]) {
                bcost[j] = bcost[edges[i].u] + edges[i].w;
                d[j] = edges[i].u;
            }
}

/* Brute force: try every path (DFS) - used only for checking. */
int bruteBest;
void dfs(int u, int t, int cost) {
    if (u == t) { if (cost < bruteBest) bruteBest = cost; return; }
    for (int i = 0; i < E; i++)
        if (edges[i].u == u) dfs(edges[i].v, t, cost + edges[i].w);
}

int main(void) {
    int pairs[][2] = { {1,12}, {2,12}, {3,10}, {5,9}, {2,11},
                       {1,7}, {4,9}, {6,6}, {10,2} };
    int P = sizeof(pairs) / sizeof(pairs[0]);
    int bcost[N + 1], d[N + 1], allOk = 1;

    printf("Same algorithm, arbitrary (s, t) pairs\n");
    printf("----------------------------------------------------------\n");
    printf("  s ->  t | DP cost | brute | path\n");
    printf("----------------------------------------------------------\n");
    for (int p = 0; p < P; p++) {
        int s = pairs[p][0], t = pairs[p][1];
        bcostFrom(s, bcost, d);
        bruteBest = INF;
        dfs(s, t, 0);

        printf("%3d -> %2d | ", s, t);
        if (bcost[t] == INF) printf("    INF | ");
        else                 printf("%7d | ", bcost[t]);
        if (bruteBest == INF) printf("  INF | ");
        else                  printf("%5d | ", bruteBest);

        if (bcost[t] == INF) printf("no path\n");
        else {
            int tmp[N + 1], k = 0, x = t;
            tmp[k++] = x;
            while (x != s) { x = d[x]; tmp[k++] = x; }
            for (int i = k - 1; i >= 0; i--) printf("%d%s", tmp[i], i ? " -> " : "\n");
        }
        if (bcost[t] != bruteBest) allOk = 0;
    }
    printf("----------------------------------------------------------\n");
    printf("All results match brute force: %s\n\n", allOk ? "YES" : "NO");

    printf("Distance matrix: row = s, column = t  (. = no path)\n     ");
    for (int t = 1; t <= N; t++) printf("%4d", t);
    printf("\n");
    for (int s = 1; s <= N; s++) {
        bcostFrom(s, bcost, d);
        printf("%4d ", s);
        for (int t = 1; t <= N; t++)
            if (bcost[t] == INF) printf("   .");
            else                 printf("%4d", bcost[t]);
        printf("\n");
    }
    return 0;
}
