/*
 * Part B - Assignment 2
 * All-pairs shortest paths with NEGATIVE edge weights.
 *
 * Floyd-Warshall still works with negative edges, as long as there is
 * no NEGATIVE CYCLE. A negative cycle shows up after the algorithm as
 * a negative diagonal entry: A(i,i) < 0  ->  i lies on a negative cycle,
 * and any pair whose path can pass through it has no shortest path.
 *
 * Demos:
 *   1) Why greedy Dijkstra is not enough: it gets a wrong answer.
 *   2) Floyd-Warshall on a 5-vertex graph with negative edges (no neg cycle).
 *   3) Floyd-Warshall detecting a negative cycle.
 *
 * Compile: gcc -O2 -o negative_weights negative_weights.c
 */
#include <stdio.h>

#define MAXN 8
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

/* Returns 1 if a negative cycle exists. */
int floyd(int n) {
    for (int k = 1; k <= n; k++)
        for (int i = 1; i <= n; i++)
            for (int j = 1; j <= n; j++)
                if (A[i][k] < INF && A[k][j] < INF && A[i][k] + A[k][j] < A[i][j]) {
                    A[i][j] = A[i][k] + A[k][j];
                    nxt[i][j] = nxt[i][k];
                }
    for (int i = 1; i <= n; i++) if (A[i][i] < 0) return 1;
    return 0;
}

void printMatrix(int n) {
    printf("      ");
    for (int j = 1; j <= n; j++) printf("%5d", j);
    printf("\n");
    for (int i = 1; i <= n; i++) {
        printf("%5d ", i);
        for (int j = 1; j <= n; j++)
            if (A[i][j] >= INF) printf("  INF"); else printf("%5d", A[i][j]);
        printf("\n");
    }
}

void printPath(int i, int j) {
    printf("  %d -> %d : cost %3d   path %d", i, j, A[i][j], i);
    int x = i;
    while (x != j) { x = nxt[x][j]; printf(" -> %d", x); }
    printf("\n");
}

/* Plain O(n^2) Dijkstra on the current A[][] as an adjacency matrix. */
int dijkstra(int n, int s, int t, int w[MAXN][MAXN]) {
    int dist[MAXN], done[MAXN] = {0};
    for (int v = 1; v <= n; v++) dist[v] = INF;
    dist[s] = 0;
    for (int it = 0; it < n; it++) {
        int u = -1;
        for (int v = 1; v <= n; v++) if (!done[v] && (u == -1 || dist[v] < dist[u])) u = v;
        if (dist[u] == INF) break;
        done[u] = 1;                                  /* greedy: final forever */
        for (int v = 1; v <= n; v++)
            if (!done[v] && w[u][v] < INF && u != v && dist[u] + w[u][v] < dist[v])
                dist[v] = dist[u] + w[u][v];
    }
    return dist[t];
}

int main(void) {
    printf("===== 1) Dijkstra fails with a negative edge =====\n");
    printf("Edges: 1->2 (2), 1->3 (5), 3->2 (-4)\n");
    int w[MAXN][MAXN];
    for (int i = 1; i <= 3; i++) for (int j = 1; j <= 3; j++) w[i][j] = (i == j) ? 0 : INF;
    w[1][2] = 2; w[1][3] = 5; w[3][2] = -4;
    printf("  Dijkstra   dist(1 -> 2) = %d   (WRONG)\n", dijkstra(3, 1, 2, w));
    init(3); addEdge(1,2,2); addEdge(1,3,5); addEdge(3,2,-4);
    floyd(3);
    printf("  Floyd      dist(1 -> 2) = %d   (correct: 1 -> 3 -> 2)\n\n", A[1][2]);

    printf("===== 2) Floyd-Warshall with negative edges, no negative cycle =====\n");
    printf("Edges: 1->2 (3), 1->3 (8), 1->5 (-4), 2->4 (1), 2->5 (7),\n");
    printf("       3->2 (4), 4->1 (2), 4->3 (-5), 5->4 (6)\n\n");
    init(5);
    addEdge(1,2,3); addEdge(1,3,8); addEdge(1,5,-4); addEdge(2,4,1); addEdge(2,5,7);
    addEdge(3,2,4); addEdge(4,1,2); addEdge(4,3,-5); addEdge(5,4,6);
    int neg = floyd(5);
    printMatrix(5);
    printf("Negative cycle? %s\n", neg ? "YES" : "NO");
    printPath(1, 3); printPath(1, 2); printPath(5, 2); printPath(3, 1);

    printf("\n===== 3) Negative cycle detection =====\n");
    printf("Edges: 1->2 (1), 2->3 (-2), 3->1 (-1), 3->4 (2)   [cycle 1->2->3->1 = -2]\n\n");
    init(4);
    addEdge(1,2,1); addEdge(2,3,-2); addEdge(3,1,-1); addEdge(3,4,2);
    neg = floyd(4);
    printMatrix(4);
    printf("Negative cycle? %s\n", neg ? "YES" : "NO");
    printf("Vertices on / reaching a negative cycle (A(i,i) < 0):");
    for (int i = 1; i <= 4; i++) if (A[i][i] < 0) printf(" %d", i);
    printf("\n=> distances among these vertices are undefined (can go to -infinity).\n");
    return 0;
}
