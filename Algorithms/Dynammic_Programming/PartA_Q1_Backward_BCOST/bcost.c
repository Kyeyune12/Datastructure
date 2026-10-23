/*
 * Part A - Assignment 1
 * Backward approach (BGraph) for the multistage graph on slide 20,
 * ASSUMING BCOST(l) is already available while computing BCOST(j).
 *
 * The assumption holds because vertices are numbered in stage order:
 * every edge <l, j> has l < j, so looping j = 2..n is enough.
 *
 *   BCOST(s) = 0
 *   BCOST(j) = min over edges <l, j> of { BCOST(l) + c(l, j) }
 *   D(j)     = the l that gave the minimum (to rebuild the path)
 *
 * Compile: gcc -O2 -o bcost bcost.c
 * Run:     ./bcost
 */
#include <stdio.h>

#define N 12
#define MAXE 64
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

/* predecessor lists: pred[v][k] = index into edges[] of the k-th edge into v */
static int pred[N + 1][MAXE];
static int predCount[N + 1];

void buildPredecessors(void) {
    for (int i = 0; i < E; i++) {
        int v = edges[i].v;
        pred[v][predCount[v]++] = i;
    }
}

int bgraph(int s, int t, int bcost[], int d[], int path[], int *len) {
    for (int j = 1; j <= N; j++) { bcost[j] = INF; d[j] = 0; }
    bcost[s] = 0;

    for (int j = s + 1; j <= N; j++) {               /* stage order */
        for (int k = 0; k < predCount[j]; k++) {     /* each edge once */
            Edge e = edges[pred[j][k]];
            if (bcost[e.u] != INF && bcost[e.u] + e.w < bcost[j]) {
                bcost[j] = bcost[e.u] + e.w;
                d[j] = e.u;
            }
        }
    }

    /* walk D() backwards from t, then reverse */
    int tmp[N + 1], n = 0, x = t;
    tmp[n++] = x;
    while (x != s && d[x] != 0) { x = d[x]; tmp[n++] = x; }
    for (int i = 0; i < n; i++) path[i] = tmp[n - 1 - i];
    *len = n;
    return bcost[t];
}

int main(void) {
    int bcost[N + 1], d[N + 1], path[N + 1], len;
    buildPredecessors();
    int best = bgraph(1, N, bcost, d, path, &len);

    printf("Backward approach (BGraph) - slide 20 multistage graph\n");
    printf("-----------------------------\n");
    printf("  j | BCOST(j) | D(j)\n");
    printf("-----------------------------\n");
    for (int j = 1; j <= N; j++) {
        if (d[j]) printf("%3d | %8d | %4d\n", j, bcost[j], d[j]);
        else      printf("%3d | %8d |    -\n", j, bcost[j]);
    }
    printf("-----------------------------\n");
    printf("Shortest distance 1 -> 12 = %d\n", best);
    printf("Path: ");
    for (int i = 0; i < len; i++) printf("%d%s", path[i], i + 1 < len ? " -> " : "\n");
    return 0;
}
