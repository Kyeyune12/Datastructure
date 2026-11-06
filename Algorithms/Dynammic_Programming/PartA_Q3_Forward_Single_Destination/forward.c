#include <stdio.h>

// Part A - Question 3
// Forward approach (FGraph): shortest path from EVERY vertex to ONE destination t.
//
//   COST(t) = 0
//   COST(i) = min over edges <i, r> of { c(i, r) + COST(r) }
//   D(i)    = the r that gave the minimum (the best next hop from i)
//
// Vertices are processed i = n-1 down to 1 (reverse stage order), so
// COST(r) is always ready. When the loop finishes, COST(i) is the
// shortest distance from EVERY vertex i to t - not just from s.
//
// Build: gcc forward.c -o forward
// Run:   ./forward

#define N 12            // number of vertices
#define E 21            // number of edges
#define INF 1000000     // "infinity" (no path yet)

struct Edge {
    int from;
    int to;
    int cost;
};

struct Edge edges[E] = {
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

int cost[N + 1];   // cost[i] = cheapest cost from i to t
int d[N + 1];      // d[i] = best next vertex after i

// Fills cost[] and d[] for destination t
void fgraph(int t) {
    for (int i = 1; i <= N; i++) {
        cost[i] = INF;
        d[i] = 0;
    }
    cost[t] = 0;

    // go BACKWARDS through the stages, so COST(r) is ready before COST(i)
    for (int i = t - 1; i >= 1; i--) {
        for (int k = 0; k < E; k++) {
            int r = edges[k].to;
            int c = edges[k].cost;

            if (edges[k].from != i) {
                continue;   // we only want edges that START at i
            }

            // is "take edge i -> r, then the best way from r" cheaper?
            if (cost[r] != INF && c + cost[r] < cost[i]) {
                cost[i] = c + cost[r];
                d[i] = r;
            }
        }
    }
}

int main() {
    int t = N;
    fgraph(t);

    printf("Forward approach - shortest path from EVERY vertex to t = %d\n", t);
    printf("----------------------------------------------------\n");
    printf("  i | COST(i) | D(i) | shortest path i -> t\n");
    printf("----------------------------------------------------\n");

    for (int i = 1; i <= N; i++) {
        printf("%3d | %7d | ", i, cost[i]);
        if (d[i] == 0) {
            printf("   - | ");
        } else {
            printf("%4d | ", d[i]);
        }

        // follow d[] forward from i until we reach t
        int x = i;
        printf("%d", x);
        while (x != t && d[x] != 0) {
            x = d[x];
            printf(" -> %d", x);
        }
        printf("\n");
    }

    printf("----------------------------------------------------\n");
    printf("Answer for s = 1: COST(1) = %d\n", cost[1]);
    return 0;
}
