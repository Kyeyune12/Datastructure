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
 * Compile: javac Knapsack.java     Run: java Knapsack
 */
public class Knapsack {

    static int[][] V;   // the DP table

    // w[] and p[] are 1-based (index 0 unused). Fills take[] with 1 = taken, 0 = not taken.
    static int knapsack(int n, int m, int[] w, int[] p, int[] take, boolean showTable) {
        V = new int[n + 1][m + 1];   // row 0 is all zeros: no items = no profit

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

        if (showTable) {
            printTable(n, m);
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
    static void printTable(int n, int m) {
        System.out.println("DP table V[i][c]:");

        System.out.print("  i\\c ");
        for (int c = 0; c <= m; c++) {
            System.out.printf("%4d", c);
        }
        System.out.println();

        for (int i = 0; i <= n; i++) {
            System.out.printf("  %3d ", i);
            for (int c = 0; c <= m; c++) {
                System.out.printf("%4d", V[i][c]);
            }
            System.out.println();
        }
    }

    // Tries every subset of items. Each bit of 'mask' says if an item is in the subset.
    static int bruteForce(int n, int m, int[] w, int[] p) {
        int best = 0;

        for (int mask = 0; mask < (1 << n); mask++) {
            int totalWeight = 0;
            int totalProfit = 0;

            for (int i = 0; i < n; i++) {
                boolean itemInSubset = (mask & (1 << i)) != 0;
                if (itemInSubset) {
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
    static void solve(String title, int n, int m, int[] w, int[] p, boolean showTable) {
        System.out.println("===== " + title + " =====");

        // print the items
        String items = "  item: ";
        String weights = "  w   : ";
        String profits = "  p   : ";
        for (int i = 1; i <= n; i++) {
            items += String.format("%4d", i);
            weights += String.format("%4d", w[i]);
            profits += String.format("%4d", p[i]);
        }
        System.out.println("Capacity m = " + m);
        System.out.println(items);
        System.out.println(weights);
        System.out.println(profits);
        System.out.println();

        int[] take = new int[n + 1];
        int best = knapsack(n, m, w, p, take, showTable);

        // build "(x1, x2, ...)" and add up the weight used
        int usedWeight = 0;
        String x = "(";
        for (int i = 1; i <= n; i++) {
            x += take[i];
            if (i < n) {
                x += ", ";
            } else {
                x += ")";
            }
            if (take[i] == 1) {
                usedWeight += w[i];
            }
        }

        int bf = bruteForce(n, m, w, p);

        System.out.println("\nMaximum profit = " + best);
        System.out.println("Items taken (x_i): " + x);
        System.out.println("Total weight used = " + usedWeight + " of " + m);
        System.out.println("Brute-force check = " + bf + " -> " + (bf == best ? "OK" : "MISMATCH") + "\n");
    }

    public static void main(String[] args) {
        // index 0 of each array is unused, so item 1 is at index 1
        solve("Example 1 (n = 3, m = 6)", 3, 6,
              new int[]{0, 2, 3, 4},
              new int[]{0, 1, 2, 5}, true);

        solve("Example 2 (n = 4, m = 8)", 4, 8,
              new int[]{0, 2, 3, 4, 5},
              new int[]{0, 1, 2, 5, 6}, true);

        solve("Example 3 (n = 3, m = 50)", 3, 50,
              new int[]{0, 10, 20, 30},
              new int[]{0, 60, 100, 120}, false);
    }
}
