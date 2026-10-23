/*
 * Part B - Assignment 1
 * All-pairs shortest paths (Floyd-Warshall), exactly the slide's loop:
 *
 *   for k = 1 to n
 *     for i = 1 to n
 *       for j = 1 to n
 *         A(i,j) = min[ A(i,j), A(i,k) + A(k,j) ]
 *
 * A^k(i,j) = shortest i->j path using only intermediate vertices {1..k}.
 * A next[][] matrix is kept alongside so the actual paths can be printed.
 *
 * Example 1: textbook 3-vertex graph, showing A0, A1, A2, A3.
 * Example 2: the slide-20 multistage graph (all 12 x 12 distances).
 *
 * Compile: gcc -O2 -o floyd floyd.c
 */
#include <stdio.h>

#define MAXN 13
#define INF 1000000000

int A[MAXN][MAXN], nxt[MAXN][MAXN];

void init(int n) {
    for (int i = 1; i <= n; i++)
        for (int j = 1; j <= n; j++) {
            A[i][j] = (i == j) ? 0 : INF;
            nxt[i][j] = (i == j) ? j : 0;
        }
}

void addEdge(int u, int v, int w) { A[u][v] = w; nxt[u][v] = v; }

void printMatrix(const char *name, int n) {
    printf("%s\n      ", name);
    for (int j = 1; j <= n; j++) printf("%5d", j);
    printf("\n");
    for (int i = 1; i <= n; i++) {
        printf("%5d ", i);
        for (int j = 1; j <= n; j++)
            if (A[i][j] >= INF) printf("  INF"); else printf("%5d", A[i][j]);
        printf("\n");
    }
    printf("\n");
}

void floyd(int n, int showSteps) {
    for (int k = 1; k <= n; k++) {
        for (int i = 1; i <= n; i++)
            for (int j = 1; j <= n; j++)
                if (A[i][k] < INF && A[k][j] < INF && A[i][k] + A[k][j] < A[i][j]) {
                    A[i][j] = A[i][k] + A[k][j];
                    nxt[i][j] = nxt[i][k];
                }
        if (showSteps) {
            char name[32];
            sprintf(name, "A%d (via vertices 1..%d):", k, k);
            printMatrix(name, n);
        }
    }
}

void printPath(int i, int j) {
    if (A[i][j] >= INF) { printf("  %2d -> %2d : no path\n", i, j); return; }
    printf("  %2d -> %2d : cost %3d   path %d", i, j, A[i][j], i);
    int x = i;
    while (x != j) { x = nxt[x][j]; printf(" -> %d", x); }
    printf("\n");
}

int main(void) {
    printf("===== Example 1: 3-vertex graph =====\n");
    printf("Edges: 1->2 (4), 1->3 (11), 2->1 (6), 2->3 (2), 3->1 (3)\n\n");
    init(3);
    addEdge(1,2,4); addEdge(1,3,11); addEdge(2,1,6); addEdge(2,3,2); addEdge(3,1,3);
    printMatrix("A0 (direct edges):", 3);
    floyd(3, 1);
    for (int i = 1; i <= 3; i++)
        for (int j = 1; j <= 3; j++) if (i != j) printPath(i, j);

    printf("\n===== Example 2: slide-20 multistage graph =====\n\n");
    int e[][3] = {
        {1,2,9},{1,3,7},{1,4,3},{1,5,2},{2,6,4},{2,7,2},{2,8,1},{3,6,2},{3,7,7},
        {4,8,11},{5,7,11},{5,8,8},{6,9,6},{6,10,5},{7,9,4},{7,10,3},{8,10,5},
        {8,11,6},{9,12,4},{10,12,2},{11,12,5}
    };
    init(12);
    for (int i = 0; i < (int)(sizeof(e) / sizeof(e[0])); i++) addEdge(e[i][0], e[i][1], e[i][2]);
    floyd(12, 0);
    printMatrix("Final A (all-pairs distances):", 12);
    printPath(1, 12); printPath(2, 12); printPath(3, 10); printPath(5, 9); printPath(4, 9);
    return 0;
}
