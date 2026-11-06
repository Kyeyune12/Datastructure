// Backtracking and Branch and Bound - C version of BacktrackingBranchBound.java.
// Reproduces the worked examples in "Tree Traversal, Backtracking and Branch
// and Bound.pdf":
// 1. 4-queens     - backtracking = depth-first search + bounding function
// 2. 0/1 knapsack - branch and bound with three live-node lists:
// FIFO (queue) = breadth-first, LIFO (stack) = depth-first,
// LC (best bound) = best-first
//
// Build: gcc -std=c11 -Wall -Wextra backtracking_branch_bound.c -o bb && ./bb
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// ===== Part 1: n-queens. x[k] = column of the queen in row k (1-based) =====
#define MAXN 8

static int nodes, solutions;
static int first_solution[MAXN + 1], have_first;

static int safe(const int *x, int k) {          // may the queen in row k stay?
    for (int i = 1; i < k; i++)
        if (x[i] == x[k] || abs(x[i] - x[k]) == k - i) return 0;
    return 1;
}

// Returns 1 when the search should stop (first solution found).
static int backtrack(int *x, int k, int n, int stop_at_first) {
    for (int v = 1; v <= n; v++) {
        x[k] = v;
        nodes++;
        if (safe(x, k)) {                       // the bounding function
            if (k == n) {
                solutions++;
                if (!have_first) { memcpy(first_solution, x, sizeof first_solution); have_first = 1; }
                if (stop_at_first) return 1;
            } else if (backtrack(x, k + 1, n, stop_at_first)) {
                return 1;
            }
        }
    }
    return 0;                                   // backtrack to row k-1
}

static void run_backtracking(int n, int stop_at_first) {
    int x[MAXN + 1] = {0};
    nodes = 1;                                  // the root
    solutions = 0;
    have_first = 0;
    backtrack(x, 1, n, stop_at_first);
}

struct Partial { int x[MAXN + 1]; };

static int level_of(const int *x, int n) {
    int k = 0;
    while (k + 1 <= n && x[k + 1] != 0) k++;
    return k;
}

static void run_bfs(int n, int *bfs_nodes, int *max_queue) {
    static struct Partial queue[20000];
    int head = 0, tail = 0;
    memset(&queue[tail++], 0, sizeof(struct Partial));
    *bfs_nodes = 1;
    *max_queue = 0;

    while (head < tail) {
        struct Partial parent = queue[head++];
        int k = level_of(parent.x, n);
        for (int v = 1; v <= n; v++) {
            struct Partial child = parent;
            child.x[k + 1] = v;
            (*bfs_nodes)++;
            if (safe(child.x, k + 1) && k + 1 < n) queue[tail++] = child;
        }
        if (tail - head > *max_queue) *max_queue = tail - head;
    }
}

// Level i decides whether item i is taken. Upper bound of a node = its profit
#define ITEMS 4
static const int P[ITEMS] = {10, 10, 12, 18};
static const int W[ITEMS] = {2, 4, 6, 9};
static const int M = 15;

struct Live {
    int level, profit, weight;
    int x[ITEMS];
    double bound;
};

static double bound_of(int level, int profit, int weight) {
    double b = profit;
    int remaining = M - weight;
    for (int i = level; i < ITEMS; i++) {
        if (W[i] <= remaining) { remaining -= W[i]; b += P[i]; }
        else { b += (double)P[i] * remaining / W[i]; break; }
    }
    return b;
}

enum { FIFO, LIFO, LC };

static void branch_and_bound(int kind, const char *name) {
    struct Live live[100];
    int count = 0, best = 0, expanded = 0, peak = 1;
    int best_x[ITEMS] = {0};

    struct Live root;
    memset(&root, 0, sizeof root);
    root.bound = bound_of(0, 0, 0);
    live[count++] = root;

    while (count > 0) {
        int pick = 0;                           // FIFO: the oldest
        if (kind == LIFO) pick = count - 1;     // newest
        else if (kind == LC)
            for (int i = 1; i < count; i++)
                if (live[i].bound > live[pick].bound) pick = i;   // best bound

        struct Live e = live[pick];
        for (int i = pick; i < count - 1; i++) live[i] = live[i + 1];
        count--;

        if (e.bound <= best) continue;          // killed: cannot beat the best
        expanded++;

        // branch: both children at once; LIFO pushes "take" last so it goes first
        int order[2] = {1, 0};
        if (kind == LIFO) { order[0] = 0; order[1] = 1; }
        for (int t = 0; t < 2; t++) {
            int take = order[t];
            int cp = e.profit + take * P[e.level];
            int cw = e.weight + take * W[e.level];
            if (cw > M) continue;               // infeasible child
            struct Live c = e;
            c.x[e.level] = take;
            c.profit = cp;
            c.weight = cw;
            c.level = e.level + 1;
            if (cp > best) { best = cp; memcpy(best_x, c.x, sizeof best_x); }
            if (c.level < ITEMS) {
                c.bound = bound_of(c.level, cp, cw);
                if (c.bound > best) live[count++] = c;   // promising: keep it live
            }
        }
        if (count > peak) peak = count;
    }

    printf("%-22s expanded %2d, max live %d, best profit %d, x = (", name, expanded, peak, best);
    for (int i = 0; i < ITEMS; i++) printf(i ? ", %d" : "%d", best_x[i]);
    printf(")\n");
}

int main(void) {
    printf("*** Backtracking and Branch and Bound ***\n\n");
    printf("4-queens (state-space tree has 1+4+16+64+256 = 341 nodes)\n");

    run_backtracking(4, 1);
    printf("Backtracking, first solution: %d nodes generated, x = (", nodes);
    for (int i = 1; i <= 4; i++) printf(i > 1 ? ", %d" : "%d", first_solution[i]);
    printf(")\n");

    run_backtracking(4, 0);
    printf("Backtracking, all solutions:  %d nodes generated, %d solutions\n", nodes, solutions);

    int bfs_nodes, max_queue;
    run_bfs(4, &bfs_nodes, &max_queue);
    printf("BFS + same bound:             %d nodes generated, max queue %d\n", bfs_nodes, max_queue);

    printf("\n0/1 knapsack, m = 15, p = (10,10,12,18), w = (2,4,6,9)\n");
    printf("Root bound = %.1f\n", bound_of(0, 0, 0));
    branch_and_bound(FIFO, "FIFO B&B (queue):");
    branch_and_bound(LIFO, "LIFO B&B (stack):");
    branch_and_bound(LC, "LC B&B (best bound):");
    return 0;
}
