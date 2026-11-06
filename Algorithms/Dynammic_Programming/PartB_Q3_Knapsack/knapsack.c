#include <stdio.h>

// Part B - Question 3
// 0/1 Knapsack using dynamic programming.
//
// V[i][c] = best profit using only items 1..i with capacity c
//
//   V[0][c] = 0
//   V[i][c] = V[i-1][c]                               if w_i > c
//           = max( V[i-1][c], p_i + V[i-1][c - w_i] ) otherwise
//
// Answer = V[n][m]. Chosen items are recovered by walking back:
// if V[i][c] != V[i-1][c], item i was taken (c -= w_i).
//
// Every answer is double-checked against brute force (all 2^n subsets).
//
// Build: gcc knapsack.c -o knapsack
// Run:   ./knapsack

#define MAXN 10   // most items we allow
#define MAXW 64   // biggest capacity we allow

int V[MAXN + 1][MAXW + 1];   // the DP table

// w[] and p[] are 1-based (index 0 unused). Fills take[] with 1 = taken, 0 = not taken.
int knapsack(int n, int m, int w[], int p[], int take[]) {
    // row 0: no items = no profit
    for (int c = 0; c <= m; c++) {
        V[0][c] = 0;
    }

    // fill the table one item at a time
    for (int i = 1; i <= n; i++) {
        for (int c = 0; c <= m; c++) {
            // option 1: skip item i
            V[i][c] = V[i - 1][c];

            // option 2: take item i (only if it fits and gives more profit)
            if (w[i] <= c) {
                int withItem = p[i] + V[i - 1][c - w[i]];
                if (withItem > V[i][c]) {
                    V[i][c] = withItem;
                }
            }
        }
    }

    // walk back from V[n][m] to find which items were taken
    int c = m;
    for (int i = n; i >= 1; i--) {
        if (V[i][c] != V[i - 1][c]) {
            take[i] = 1;     // value changed, so item i was used
            c = c - w[i];    // remove its weight and keep going
        } else {
            take[i] = 0;
        }
    }

    return V[n][m];
}

// Prints the DP table V
void printTable(int n, int m) {
    printf("DP table V[i][c]:\n");

    printf("  i\\c ");
    for (int c = 0; c <= m; c++) {
        printf("%4d", c);
    }
    printf("\n");

    for (int i = 0; i <= n; i++) {
        printf("  %3d ", i);
        for (int c = 0; c <= m; c++) {
            printf("%4d", V[i][c]);
        }
        printf("\n");
    }
}

// Tries every subset of items. Each bit of 'mask' says if an item is in the subset.
int bruteForce(int n, int m, int w[], int p[]) {
    int best = 0;

    for (int mask = 0; mask < (1 << n); mask++) {
        int totalWeight = 0;
        int totalProfit = 0;

        for (int i = 0; i < n; i++) {
            if (mask & (1 << i)) {   // is item i+1 in this subset?
                totalWeight += w[i + 1];
                totalProfit += p[i + 1];
            }
        }

        if (totalWeight <= m && totalProfit > best) {
            best = totalProfit;
        }
    }
    return best;
}

// Runs one example and prints everything about it
void solve(char* title, int n, int m, int w[], int p[], int showTable) {
    printf("===== %s =====\n", title);

    // print the items
    printf("Capacity m = %d\n", m);
    printf("  item: ");
    for (int i = 1; i <= n; i++) {
        printf("%4d", i);
    }
    printf("\n  w   : ");
    for (int i = 1; i <= n; i++) {
        printf("%4d", w[i]);
    }
    printf("\n  p   : ");
    for (int i = 1; i <= n; i++) {
        printf("%4d", p[i]);
    }
    printf("\n\n");

    int take[MAXN + 1];
    int best = knapsack(n, m, w, p, take);
    if (showTable) {
        printTable(n, m);
    }

    // print "(x1, x2, ...)" and add up the weight used
    int usedWeight = 0;
    printf("\nMaximum profit = %d\n", best);
    printf("Items taken (x_i): (");
    for (int i = 1; i <= n; i++) {
        printf("%d", take[i]);
        if (i < n) {
            printf(", ");
        }
        if (take[i] == 1) {
            usedWeight += w[i];
        }
    }
    printf(")\n");

    int bf = bruteForce(n, m, w, p);
    printf("Total weight used = %d of %d\n", usedWeight, m);
    if (bf == best) {
        printf("Brute-force check = %d -> OK\n\n", bf);
    } else {
        printf("Brute-force check = %d -> MISMATCH\n\n", bf);
    }
}

int main() {
    // index 0 of each array is unused, so item 1 is at index 1
    int w1[4] = {0, 2, 3, 4};
    int p1[4] = {0, 1, 2, 5};
    solve("Example 1 (n = 3, m = 6)", 3, 6, w1, p1, 1);

    int w2[5] = {0, 2, 3, 4, 5};
    int p2[5] = {0, 1, 2, 5, 6};
    solve("Example 2 (n = 4, m = 8)", 4, 8, w2, p2, 1);

    int w3[4] = {0, 10, 20, 30};
    int p3[4] = {0, 60, 100, 120};
    solve("Example 3 (n = 3, m = 50)", 3, 50, w3, p3, 0);

    return 0;
}
