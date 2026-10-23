/*
 * Part A - Assignment 3
 * Forward approach (FGraph): single-DESTINATION shortest paths.
 *
 *   COST(t) = 0
 *   COST(i) = min over edges <i, r> of { c(i, r) + COST(r) }
 *   D(i)    = the r that gave the minimum (the best next hop from i)
 *
 * Vertices are processed i = n-1 down to 1 (reverse stage order), so
 * COST(r) is always ready. When the loop finishes, COST(i) is the
 * shortest distance from EVERY vertex i to t - not just from s.
 *
 * The redraft below keeps COST() and D() for all vertices and then
 * prints the full shortest path from every vertex to t by following D().
 *
 * Compile: gcc -O2 -o forward forward.c
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

static int succ[N + 1][N + 1];     /* succ[u][k] = edge index */
static int succCount[N + 1];

void fgraph(int t, int cost[], int d[]) {
    for (int i = 1; i <= N; i++) { cost[i] = INF; d[i] = 0; }
    cost[t] = 0;
    for (int i = t - 1; i >= 1; i--) {               /* reverse stage order */
        for (int k = 0; k < succCount[i]; k++) {
            Edge e = edges[succ[i][k]];
            if (cost[e.v] != INF && e.w + cost[e.v] < cost[i]) {
                cost[i] = e.w + cost[e.v];
                d[i] = e.v;
            }
        }
    }
}

int main(void) {
    int cost[N + 1], d[N + 1];
    for (int i = 0; i < E; i++) succ[edges[i].u][succCount[edges[i].u]++] = i;

    int t = N;
    fgraph(t, cost, d);

    printf("Forward approach - shortest path from EVERY vertex to t = %d\n", t);
    printf("----------------------------------------------------\n");
    printf("  i | COST(i) | D(i) | shortest path i -> t\n");
    printf("----------------------------------------------------\n");
    for (int i = 1; i <= N; i++) {
        printf("%3d | %7d | ", i, cost[i]);
        if (d[i]) printf("%4d | ", d[i]); else printf("   - | ");
        int x = i;
        printf("%d", x);
        while (x != t && d[x]) { x = d[x]; printf(" -> %d", x); }
        printf("\n");
    }
    printf("----------------------------------------------------\n");
    printf("Answer for s = 1: COST(1) = %d\n", cost[1]);
    return 0;
}
