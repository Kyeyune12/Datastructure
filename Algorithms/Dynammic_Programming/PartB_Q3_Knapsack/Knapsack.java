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

    static int[][] V;

    /** w[] and p[] are 1-based (index 0 unused). Fills take[]. */
    static int knapsack(int n, int m, int[] w, int[] p, int[] take, boolean showTable) {
        V = new int[n + 1][m + 1];
        for (int i = 1; i <= n; i++)
            for (int c = 0; c <= m; c++) {
                V[i][c] = V[i - 1][c];                               // skip item i
                if (w[i] <= c && p[i] + V[i - 1][c - w[i]] > V[i][c])
                    V[i][c] = p[i] + V[i - 1][c - w[i]];             // take item i
            }

        if (showTable) {
            StringBuilder sb = new StringBuilder("DP table V[i][c]:\n  i\\c ");
            for (int c = 0; c <= m; c++) sb.append(String.format("%4d", c));
            System.out.println(sb);
            for (int i = 0; i <= n; i++) {
                sb = new StringBuilder(String.format("  %3d ", i));
                for (int c = 0; c <= m; c++) sb.append(String.format("%4d", V[i][c]));
                System.out.println(sb);
            }
        }

        for (int i = n, c = m; i >= 1; i--) {
            take[i] = (V[i][c] != V[i - 1][c]) ? 1 : 0;
            if (take[i] == 1) c -= w[i];
        }
        return V[n][m];
    }

    static int bruteForce(int n, int m, int[] w, int[] p) {
        int best = 0;
        for (int mask = 0; mask < (1 << n); mask++) {
            int tw = 0, tp = 0;
            for (int i = 0; i < n; i++)
                if ((mask & (1 << i)) != 0) { tw += w[i + 1]; tp += p[i + 1]; }
            if (tw <= m && tp > best) best = tp;
        }
        return best;
    }

    static void solve(String title, int n, int m, int[] w, int[] p, boolean showTable) {
        System.out.println("===== " + title + " =====");
        StringBuilder items = new StringBuilder("  item: "), ws = new StringBuilder("  w   : "), ps = new StringBuilder("  p   : ");
        for (int i = 1; i <= n; i++) {
            items.append(String.format("%4d", i));
            ws.append(String.format("%4d", w[i]));
            ps.append(String.format("%4d", p[i]));
        }
        System.out.println("Capacity m = " + m + "\n" + items + "\n" + ws + "\n" + ps + "\n");

        int[] take = new int[n + 1];
        int best = knapsack(n, m, w, p, take, showTable);
        int tw = 0;
        StringBuilder x = new StringBuilder("(");
        for (int i = 1; i <= n; i++) {
            x.append(take[i]).append(i < n ? ", " : ")");
            if (take[i] == 1) tw += w[i];
        }
        int bf = bruteForce(n, m, w, p);
        System.out.println("\nMaximum profit = " + best);
        System.out.println("Items taken (x_i): " + x);
        System.out.println("Total weight used = " + tw + " of " + m);
        System.out.println("Brute-force check = " + bf + " -> " + (bf == best ? "OK" : "MISMATCH") + "\n");
    }

    public static void main(String[] args) {
        solve("Example 1 (n = 3, m = 6)", 3, 6, new int[]{0, 2, 3, 4}, new int[]{0, 1, 2, 5}, true);
        solve("Example 2 (n = 4, m = 8)", 4, 8, new int[]{0, 2, 3, 4, 5}, new int[]{0, 1, 2, 5, 6}, true);
        solve("Example 3 (n = 3, m = 50)", 3, 50, new int[]{0, 10, 20, 30}, new int[]{0, 60, 100, 120}, false);
    }
}
