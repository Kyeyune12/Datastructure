#include <stdio.h>

// Part A - Question 4
// Can S and t be redefined arbitrarily and the same algorithm reused?
//
// Yes - for any pair (s, t) in a DAG - with these rules:
//   - Process vertices in topological order starting at s
//     (here numeric order is already topological).
//   - BCOST(s) = 0; every other vertex starts at INF.
//   - Vertices not reachable from s simply stay at INF.
//   - If t comes before s in the order, or is unreachable: "no path".
//   - s == t gives distance 0.
//
// The program runs the algorithm for several (s, t) pairs, checks each
// against a brute-force search of every path, and finally runs it once
// per source to print the full distance matrix.
//
// Build: gcc arbitrary_st.c -o arbitrary_st
// Run:   ./arbitrary_st

#define N 12            // number of vertices
#define E 21            // number of edges
#define INF 1000000     // "infinity" (no path)

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
int d[N + 1];       // d[j] = vertex we came from to reach j

// The same BCOST algorithm as Q1, but for ANY source s
void bcostFrom(int s) {
    for (int j = 1; j <= N; j++) {
        bcost[j] = INF;
        d[j] = 0;
    }
    bcost[s] = 0;

    // vertices before s can never be reached from s, so start at s + 1
    for (int j = s + 1; j <= N; j++) {
        for (int i = 0; i < E; i++) {
            int l = edges[i].from;
            int c = edges[i].cost;

            if (edges[i].to == j && bcost[l] != INF && bcost[l] + c < bcost[j]) {
                bcost[j] = bcost[l] + c;
                d[j] = l;
            }
        }
    }
}

// Brute force: try EVERY path from u to t and keep the cheapest.
// Slow, but obviously correct - used only to check the DP answers.
int brute(int u, int t) {
    if (u == t) {
        return 0;
    }

    int best = INF;
    for (int i = 0; i < E; i++) {
        if (edges[i].from == u) {
            int rest = brute(edges[i].to, t);   // best cost from the next vertex
            if (rest != INF && edges[i].cost + rest < best) {
                best = edges[i].cost + rest;
            }
        }
    }
    return best;
}

// Prints a number, or "INF" if there is no path
void printCost(int x, int width) {
    if (x == INF) {
        printf("%*s", width, "INF");
    } else {
        printf("%*d", width, x);
    }
}

// Prints the path s -> ... -> t (recursion: print the path to d[t] first)
void printPath(int s, int t) {
    if (t == s) {
        printf("%d", s);
        return;
    }
    printPath(s, d[t]);
    printf(" -> %d", t);
}

int main() {
    int pairs[9][2] = {
        {1, 12}, {2, 12}, {3, 10}, {5, 9}, {2, 11},
        {1, 7}, {4, 9}, {6, 6}, {10, 2}
    };
    int allOk = 1;

    printf("Same algorithm, arbitrary (s, t) pairs\n");
    printf("----------------------------------------------------------\n");
    printf("  s ->  t | DP cost | brute | path\n");
    printf("----------------------------------------------------------\n");

    for (int p = 0; p < 9; p++) {
        int s = pairs[p][0];
        int t = pairs[p][1];

        bcostFrom(s);
        int bf = brute(s, t);

        printf("%3d -> %2d | ", s, t);
        printCost(bcost[t], 7);
        printf(" | ");
        printCost(bf, 5);
        printf(" | ");
        if (bcost[t] == INF) {
            printf("no path");
        } else {
            printPath(s, t);
        }
        printf("\n");

        if (bcost[t] != bf) {
            allOk = 0;
        }
    }

    printf("----------------------------------------------------------\n");
    if (allOk) {
        printf("All results match brute force: YES\n\n");
    } else {
        printf("All results match brute force: NO\n\n");
    }

    // Distance matrix: run the algorithm once for every source s
    printf("Distance matrix: row = s, column = t  (. = no path)\n");
    printf("     ");
    for (int t = 1; t <= N; t++) {
        printf("%4d", t);
    }
    printf("\n");

    for (int s = 1; s <= N; s++) {
        bcostFrom(s);
        printf("%4d ", s);
        for (int t = 1; t <= N; t++) {
            if (bcost[t] == INF) {
                printf("   .");
            } else {
                printf("%4d", bcost[t]);
            }
        }
        printf("\n");
    }
    return 0;
}
