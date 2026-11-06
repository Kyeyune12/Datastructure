#include <stdio.h>

// Part B - Question 1
// All-pairs shortest paths (Floyd-Warshall), exactly the slide's loop:
//
//   for k = 1 to n
//     for i = 1 to n
//       for j = 1 to n
//         A(i,j) = min[ A(i,j), A(i,k) + A(k,j) ]
//
// A^k(i,j) = shortest i->j path using only intermediate vertices {1..k}.
// A next[][] matrix is kept alongside so the actual paths can be printed.
//
// Example 1: textbook 3-vertex graph, showing A0, A1, A2, A3.
// Example 2: the slide-20 multistage graph (all 12 x 12 distances).
//
// Build: gcc floyd.c -o floyd
// Run:   ./floyd

#define MAXN 13         // vertices are numbered 1..12, so we need 13 slots
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

// Adds a direct edge u -> v with cost w
void addEdge(int u, int v, int w) {
    A[u][v] = w;
    next[u][v] = v;
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
    printf("\n");
}

// The Floyd-Warshall triple loop. If showSteps is 1, print A after every k.
void floyd(int showSteps) {
    for (int k = 1; k <= n; k++) {          // allow vertex k as a stop-over
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                // is "i -> k -> j" shorter than the best i -> j so far?
                if (A[i][k] != INF && A[k][j] != INF && A[i][k] + A[k][j] < A[i][j]) {
                    A[i][j] = A[i][k] + A[k][j];
                    next[i][j] = next[i][k];   // first step is now towards k
                }
            }
        }

        if (showSteps) {
            printf("A%d (via vertices 1..%d):\n", k, k);
            printMatrix();
        }
    }
}

// Prints the shortest path from i to j by following next[][]
void printPath(int i, int j) {
    if (A[i][j] == INF) {
        printf("  %2d -> %2d : no path\n", i, j);
        return;
    }

    printf("  %2d -> %2d : cost %3d   path %d", i, j, A[i][j], i);
    int x = i;
    while (x != j) {
        x = next[x][j];
        printf(" -> %d", x);
    }
    printf("\n");
}

int main() {
    printf("===== Example 1: 3-vertex graph =====\n");
    printf("Edges: 1->2 (4), 1->3 (11), 2->1 (6), 2->3 (2), 3->1 (3)\n\n");

    init(3);
    addEdge(1, 2, 4);
    addEdge(1, 3, 11);
    addEdge(2, 1, 6);
    addEdge(2, 3, 2);
    addEdge(3, 1, 3);

    printf("A0 (direct edges):\n");
    printMatrix();
    floyd(1);

    for (int i = 1; i <= 3; i++) {
        for (int j = 1; j <= 3; j++) {
            if (i != j) {
                printPath(i, j);
            }
        }
    }

    printf("\n===== Example 2: slide-20 multistage graph =====\n\n");

    // Each edge is {from, to, cost}
    int edges[21][3] = {
        {1, 2, 9}, {1, 3, 7}, {1, 4, 3}, {1, 5, 2}, {2, 6, 4}, {2, 7, 2}, {2, 8, 1},
        {3, 6, 2}, {3, 7, 7}, {4, 8, 11}, {5, 7, 11}, {5, 8, 8}, {6, 9, 6}, {6, 10, 5},
        {7, 9, 4}, {7, 10, 3}, {8, 10, 5}, {8, 11, 6}, {9, 12, 4}, {10, 12, 2}, {11, 12, 5}
    };

    init(12);
    for (int i = 0; i < 21; i++) {
        addEdge(edges[i][0], edges[i][1], edges[i][2]);
    }
    floyd(0);

    printf("Final A (all-pairs distances):\n");
    printMatrix();

    printPath(1, 12);
    printPath(2, 12);
    printPath(3, 10);
    printPath(5, 9);
    printPath(4, 9);
    return 0;
}
