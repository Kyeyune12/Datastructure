/*
 * Part B - Assignment 3
 * 0/1 Knapsack using dynamic programming.
 *
 * V[i][c] = best profit using only items 1..i with capacity c
 *
 *   V[0][c] = 0
 *   V[i][c] = V[i-1][c]                               if w_i > c
 *           = max( V[i-1][c], p_i + V[i-1][c - w_i] ) otherwise
 *
 * Answer = V[n][m]. Chosen items are recovered by walking back:
 * if V[i][c] != V[i-1][c], item i was taken (c -= w_i).
 *
 * Every answer is double-checked against brute force (all 2^n subsets).
 *
 * Compile: gcc -O2 -o knapsack knapsack.c
 */
#include <stdio.h>

#define MAXN 10
#define MAXW 64

int V[MAXN + 1][MAXW + 1];

int knapsack(int n, int m, const int w[], const int p[], int take[], int showTable) {
    for (int c = 0; c <= m; c++) V[0][c] = 0;
    for (int i = 1; i <= n; i++)
        for (int c = 0; c <= m; c++) {
            V[i][c] = V[i - 1][c];                               /* skip item i */
            if (w[i] <= c && p[i] + V[i - 1][c - w[i]] > V[i][c])
                V[i][c] = p[i] + V[i - 1][c - w[i]];             /* take item i */
        }

    if (showTable) {
        printf("DP table V[i][c]:\n  i\\c ");
        for (int c = 0; c <= m; c++) printf("%4d", c);
        printf("\n");
        for (int i = 0; i <= n; i++) {
            printf("  %3d ", i);
            for (int c = 0; c <= m; c++) printf("%4d", V[i][c]);
            printf("\n");
        }
    }

    for (int i = n, c = m; i >= 1; i--) {
        take[i] = (V[i][c] != V[i - 1][c]);
        if (take[i]) c -= w[i];
    }
    return V[n][m];
}

int bruteForce(int n, int m, const int w[], const int p[]) {
    int best = 0;
    for (int mask = 0; mask < (1 << n); mask++) {
        int tw = 0, tp = 0;
        for (int i = 0; i < n; i++)
            if (mask & (1 << i)) { tw += w[i + 1]; tp += p[i + 1]; }
        if (tw <= m && tp > best) best = tp;
    }
    return best;
}

void solve(const char *title, int n, int m, const int w[], const int p[], int showTable) {
    int take[MAXN + 1] = {0};
    printf("===== %s =====\n", title);
    printf("Capacity m = %d\n  item: ", m);
    for (int i = 1; i <= n; i++) printf("%4d", i);
    printf("\n  w   : ");
    for (int i = 1; i <= n; i++) printf("%4d", w[i]);
    printf("\n  p   : ");
    for (int i = 1; i <= n; i++) printf("%4d", p[i]);
    printf("\n\n");

    int best = knapsack(n, m, w, p, take, showTable);
    int tw = 0;
    printf("\nMaximum profit = %d\nItems taken (x_i): (", best);
    for (int i = 1; i <= n; i++) { printf("%d%s", take[i], i < n ? ", " : ")\n"); if (take[i]) tw += w[i]; }
    printf("Total weight used = %d of %d\n", tw, m);
    printf("Brute-force check = %d -> %s\n\n", bruteForce(n, m, w, p),
           bruteForce(n, m, w, p) == best ? "OK" : "MISMATCH");
}

int main(void) {
    /* index 0 unused so items are 1-based like the notes */
    int w1[] = {0, 2, 3, 4},      p1[] = {0, 1, 2, 5};
    solve("Example 1 (n = 3, m = 6)", 3, 6, w1, p1, 1);

    int w2[] = {0, 2, 3, 4, 5},   p2[] = {0, 1, 2, 5, 6};
    solve("Example 2 (n = 4, m = 8)", 4, 8, w2, p2, 1);

    int w3[] = {0, 10, 20, 30},   p3[] = {0, 60, 100, 120};
    solve("Example 3 (n = 3, m = 50)", 3, 50, w3, p3, 0);
    return 0;
}
