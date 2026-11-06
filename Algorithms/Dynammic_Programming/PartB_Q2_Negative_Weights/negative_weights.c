#include <stdio.h>

// Part B - Question 2
// All-pairs shortest paths with NEGATIVE edge weights.
//
// Floyd-Warshall still works with negative edges, as long as there is
// no NEGATIVE CYCLE. A negative cycle shows up after the algorithm as
// a negative diagonal entry: A(i,i) < 0  ->  i lies on a negative cycle,
// and any pair whose path can pass through it has no shortest path.
//
// Demos:
//   1) Why greedy Dijkstra is not enough: it gets a wrong answer.
//   2) Floyd-Warshall on a 5-vertex graph with negative edges (no neg cycle).
//   3) Floyd-Warshall detecting a negative cycle.
//
// Build: gcc negative_weights.c -o negative_weights
// Run:   ./negative_weights

#define MAXN 8
#define INF 1000000     // "infinity" (no path)

int n;                  // number of vertices in the current graph
int A[MAXN][MAXN];      // A[i][j] = shortest known distance from i to j
int next[MAXN][MAXN];   // next[i][j] = first vertex to go to on the way from i to j

// Starts with no edges: distance 0 to yourself, INF to everyone else
void init(int vertices) {
    n = vertices;
    for (int i = 1; i <= n; i++) {
        for (int j = 1; j <= n; j++) {
            if (i == j) {
                A[i][j] = 0;
                next[i][j] = j;
            } else {
                A[i][j] = INF;
                next[i][j] = 0;
            }
        }
    }
}

// Adds a direct edge u -> v with cost w (w may be negative)
void addEdge(int u, int v, int w) {
    A[u][v] = w;
    next[u][v] = v;
}

// Runs Floyd-Warshall; returns 1 if a negative cycle exists, 0 if not
int floyd() {
    for (int k = 1; k <= n; k++) {
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                if (A[i][k] != INF && A[k][j] != INF && A[i][k] + A[k][j] < A[i][j]) {
                    A[i][j] = A[i][k] + A[k][j];
                    next[i][j] = next[i][k];
                }
            }
        }
    }

    // going from i back to i should cost 0;
    // if it costs less than 0, i is on a negative cycle
    for (int i = 1; i <= n; i++) {
        if (A[i][i] < 0) {
            return 1;
        }
    }
    return 0;
}

// Prints the matrix A with row and column numbers
void printMatrix() {
    printf("      ");
    for (int j = 1; j <= n; j++) {
        printf("%5d", j);
    }
    printf("\n");

    for (int i = 1; i <= n; i++) {
        printf("%5d ", i);
        for (int j = 1; j <= n; j++) {
            if (A[i][j] == INF) {
                printf("  INF");
            } else {
                printf("%5d", A[i][j]);
            }
        }
        printf("\n");
    }
}

// Prints the shortest path from i to j by following next[][]
void printPath(int i, int j) {
    printf("  %d -> %d : cost %3d   path %d", i, j, A[i][j], i);
    int x = i;
    while (x != j) {
        x = next[x][j];
        printf(" -> %d", x);
    }
    printf("\n");
}

// Plain O(n^2) Dijkstra on an adjacency matrix w.
// Greedy: once a vertex is "done", its distance is never changed again.
// That is exactly why it can fail with negative edges.
int dijkstra(int size, int s, int t, int w[MAXN][MAXN]) {
    int dist[MAXN];
    int done[MAXN];

    for (int v = 1; v <= size; v++) {
        dist[v] = INF;
        done[v] = 0;
    }
    dist[s] = 0;

    for (int round = 0; round < size; round++) {
        // pick the closest vertex that is not done yet
        int u = -1;
        for (int v = 1; v <= size; v++) {
            if (!done[v] && (u == -1 || dist[v] < dist[u])) {
                u = v;
            }
        }
        if (dist[u] == INF) {
            break;   // everything left is unreachable
        }

        done[u] = 1;   // greedy: u's distance is now final forever

        // update the neighbours of u that are not done yet
        for (int v = 1; v <= size; v++) {
            if (!done[v] && u != v && w[u][v] != INF && dist[u] + w[u][v] < dist[v]) {
                dist[v] = dist[u] + w[u][v];
            }
        }
    }
    return dist[t];
}

int main() {
    // ---------- 1) Dijkstra fails with a negative edge ----------
    printf("===== 1) Dijkstra fails with a negative edge =====\n");
    printf("Edges: 1->2 (2), 1->3 (5), 3->2 (-4)\n");

    int w[MAXN][MAXN];
    for (int i = 1; i <= 3; i++) {
        for (int j = 1; j <= 3; j++) {
            if (i == j) {
                w[i][j] = 0;
            } else {
                w[i][j] = INF;
            }
        }
    }
    w[1][2] = 2;
    w[1][3] = 5;
    w[3][2] = -4;
    printf("  Dijkstra   dist(1 -> 2) = %d   (WRONG)\n", dijkstra(3, 1, 2, w));

    init(3);
    addEdge(1, 2, 2);
    addEdge(1, 3, 5);
    addEdge(3, 2, -4);
    floyd();
    printf("  Floyd      dist(1 -> 2) = %d   (correct: 1 -> 3 -> 2)\n\n", A[1][2]);

    // ---------- 2) Negative edges, but no negative cycle ----------
    printf("===== 2) Floyd-Warshall with negative edges, no negative cycle =====\n");
    printf("Edges: 1->2 (3), 1->3 (8), 1->5 (-4), 2->4 (1), 2->5 (7),\n");
    printf("       3->2 (4), 4->1 (2), 4->3 (-5), 5->4 (6)\n\n");

    init(5);
    addEdge(1, 2, 3);
    addEdge(1, 3, 8);
    addEdge(1, 5, -4);
    addEdge(2, 4, 1);
    addEdge(2, 5, 7);
    addEdge(3, 2, 4);
    addEdge(4, 1, 2);
    addEdge(4, 3, -5);
    addEdge(5, 4, 6);

    int neg = floyd();
    printMatrix();
    if (neg) {
        printf("Negative cycle? YES\n");
    } else {
        printf("Negative cycle? NO\n");
    }
    printPath(1, 3);
    printPath(1, 2);
    printPath(5, 2);
    printPath(3, 1);

    // ---------- 3) Negative cycle ----------
    printf("\n===== 3) Negative cycle detection =====\n");
    printf("Edges: 1->2 (1), 2->3 (-2), 3->1 (-1), 3->4 (2)   [cycle 1->2->3->1 = -2]\n\n");

    init(4);
    addEdge(1, 2, 1);
    addEdge(2, 3, -2);
    addEdge(3, 1, -1);
    addEdge(3, 4, 2);

    neg = floyd();
    printMatrix();
    if (neg) {
        printf("Negative cycle? YES\n");
    } else {
        printf("Negative cycle? NO\n");
    }

    // list every vertex whose diagonal entry is negative
    printf("Vertices on / reaching a negative cycle (A(i,i) < 0):");
    for (int i = 1; i <= 4; i++) {
        if (A[i][i] < 0) {
            printf(" %d", i);
        }
    }
    printf("\n=> distances among these vertices are undefined (can go to -infinity).\n");
    return 0;
}
