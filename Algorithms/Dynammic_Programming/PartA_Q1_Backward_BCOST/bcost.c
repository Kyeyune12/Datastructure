#include <stdio.h>

// Part A - Question 1
// Backward approach (BGraph) for the multistage graph on slide 20,
// ASSUMING BCOST(l) is already available while computing BCOST(j).
//
// The assumption holds because vertices are numbered in stage order:
// every edge <l, j> has l < j, so looping j = 2..n is enough.
//
//   BCOST(s) = 0
//   BCOST(j) = min over edges <l, j> of { BCOST(l) + c(l, j) }
//   D(j)     = the l that gave the minimum (to rebuild the path)
//
// Build: gcc bcost.c -o bcost
// Run:   ./bcost

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

int bcost[N + 1];   // bcost[j] = cheapest cost from s to j
int d[N + 1];       // d[j] = vertex we came from to reach j cheaply

// Fills bcost[] and d[] for source s
void bgraph(int s) {
    // at the start, nothing is reachable except s itself
    for (int j = 1; j <= N; j++) {
        bcost[j] = INF;
        d[j] = 0;
    }
    bcost[s] = 0;

    // visit vertices in stage order, so every BCOST(l) is ready before BCOST(j)
    for (int j = s + 1; j <= N; j++) {
        for (int i = 0; i < E; i++) {
            int l = edges[i].from;
            int c = edges[i].cost;

            if (edges[i].to != j) {
                continue;   // we only want edges that END at j
            }

            // is "go to l, then take edge l -> j" cheaper than what we have?
            if (bcost[l] != INF && bcost[l] + c < bcost[j]) {
                bcost[j] = bcost[l] + c;
                d[j] = l;
            }
        }
    }
}

// Prints the path by following d[] back from j to the start (recursion)
// Example: path(12) prints path(10) then " -> 12"
void printPath(int j) {
    if (d[j] == 0) {
        printf("%d", j);   // j is the start vertex
        return;
    }
    printPath(d[j]);
    printf(" -> %d", j);
}

int main() {
    bgraph(1);

    printf("Backward approach (BGraph) - slide 20 multistage graph\n");
    printf("-----------------------------\n");
    printf("  j | BCOST(j) | D(j)\n");
    printf("-----------------------------\n");

    for (int j = 1; j <= N; j++) {
        if (d[j] == 0) {
            printf("%3d | %8d |    -\n", j, bcost[j]);
        } else {
            printf("%3d | %8d | %4d\n", j, bcost[j], d[j]);
        }
    }

    printf("-----------------------------\n");
    printf("Shortest distance 1 -> 12 = %d\n", bcost[N]);
    printf("Path: ");
    printPath(N);
    printf("\n");
    return 0;
}
