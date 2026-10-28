/*
 * GREEDY PROBLEM 2: Kruskal's Minimum Spanning Tree (MST)
 *
 * Situation: We want to connect 6 campus buildings with network cable.
 * Each possible connection (edge) has a cost (metres of cable).
 *
 * Goal: Connect ALL buildings using the LEAST total cable, with no loops.
 *
 * Greedy idea: Sort all cables from cheapest to most expensive.
 * Pick the cheapest one each time, UNLESS it would create a loop (cycle).
 * Stop when we have (number of buildings - 1) cables.
 *
 * To detect loops we use "Union-Find": every building belongs to a group.
 * If two buildings are already in the same group, joining them makes a loop.
 *
 * How to run:
 *   gcc kruskal_mst.c -o kruskal_mst
 *   ./kruskal_mst
 */
#include <stdio.h>

#define N 6   /* number of buildings */

/* An edge = a possible cable between two buildings */
struct Edge {
    int from, to, cost;
};

int parent[N]; /* parent[i] = the "leader" of building i's group */

/* Find the leader of the group that building x belongs to */
int find(int x) {
    while (parent[x] != x) {
        x = parent[x];
    }
    return x;
}

/* Join the groups of a and b into one group */
void unite(int a, int b) {
    parent[find(a)] = find(b);
}

/* Simple bubble sort: cheapest edge first */
void sortEdges(struct Edge e[], int m) {
    for (int i = 0; i < m - 1; i++) {
        for (int j = 0; j < m - 1 - i; j++) {
            if (e[j].cost > e[j + 1].cost) {
                struct Edge temp = e[j];
                e[j] = e[j + 1];
                e[j + 1] = temp;
            }
        }
    }
}

int main(void) {
    const char *buildings[N] = {"Library", "Admin", "Lab", "Hostel", "Cafeteria", "Hall"};

    struct Edge edges[] = {
        {0, 1, 4},  /* Library - Admin     */
        {0, 2, 3},  /* Library - Lab       */
        {1, 2, 1},  /* Admin - Lab         */
        {1, 3, 2},  /* Admin - Hostel      */
        {2, 3, 4},  /* Lab - Hostel        */
        {3, 4, 2},  /* Hostel - Cafeteria  */
        {4, 5, 6},  /* Cafeteria - Hall    */
        {3, 5, 7}   /* Hostel - Hall       */
    };
    int m = sizeof(edges) / sizeof(edges[0]);

    /* STEP 1 (the greedy choice): sort edges from cheapest to most expensive */
    sortEdges(edges, m);

    /* At the start, every building is its own group */
    for (int i = 0; i < N; i++) parent[i] = i;

    int totalCost = 0;
    int edgesUsed = 0;

    printf("Cables chosen for the Minimum Spanning Tree:\n");

    /* STEP 2: take the cheapest edges that don't make a loop */
    for (int i = 0; i < m && edgesUsed < N - 1; i++) {
        int a = edges[i].from, b = edges[i].to;

        if (find(a) != find(b)) {              /* different groups -> no loop */
            unite(a, b);
            totalCost += edges[i].cost;
            edgesUsed++;
            printf("  %s - %s  (cost %d)\n", buildings[a], buildings[b], edges[i].cost);
        } else {
            printf("  [skip] %s - %s would make a loop\n", buildings[a], buildings[b]);
        }
    }

    printf("\nTotal cable cost: %d\n", totalCost);
    return 0;
}
