/*
 * Part A - Assignment 4
 * Can S and t be redefined arbitrarily and the same algorithm reused?
 *
 * Yes - for any pair (s, t) in a DAG - with these rules:
 *   - Process vertices in topological order starting at s
 *     (here numeric order is already topological).
 *   - BCOST(s) = 0; every other vertex starts at INF.
 *   - Vertices not reachable from s simply stay at INF.
 *   - If t comes before s in the order, or is unreachable: "no path".
 *   - s == t gives distance 0.
 *
 * The program runs the algorithm for several (s, t) pairs, checks each
 * against a brute-force search of every path, and finally runs it once
 * per source to print the full distance matrix.
 *
 * Compile: javac ArbitraryST.java     Run: java ArbitraryST
 */
public class ArbitraryST {
    static final int N = 12;                       // number of vertices
    static final int INF = Integer.MAX_VALUE / 4;  // "infinity" (no path)

    // Each edge is {from, to, cost}
    static final int[][] EDGES = {
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

    static int[] bcost = new int[N + 1];   // bcost[j] = cheapest cost from s to j
    static int[] d = new int[N + 1];       // d[j] = vertex we came from to reach j

    // The same BCOST algorithm as Q1, but for ANY source s
    static void bcostFrom(int s) {
        for (int j = 1; j <= N; j++) {
            bcost[j] = INF;
            d[j] = 0;
        }
        bcost[s] = 0;

        // vertices before s can never be reached from s, so start at s + 1
        for (int j = s + 1; j <= N; j++) {
            for (int[] e : EDGES) {
                int l = e[0];
                int to = e[1];
                int c = e[2];

                if (to == j && bcost[l] != INF && bcost[l] + c < bcost[j]) {
                    bcost[j] = bcost[l] + c;
                    d[j] = l;
                }
            }
        }
    }

    // Brute force: try EVERY path from u to t and keep the cheapest.
    // Slow, but obviously correct - used only to check the DP answers.
    static int brute(int u, int t) {
        if (u == t) {
            return 0;
        }

        int best = INF;
        for (int[] e : EDGES) {
            if (e[0] == u) {
                int rest = brute(e[1], t);   // best cost from the next vertex
                if (rest != INF) {
                    best = Math.min(best, e[2] + rest);
                }
            }
        }
        return best;
    }

    // Shows INF as the word "INF" instead of a huge number
    static String show(int x) {
        if (x == INF) {
            return "INF";
        }
        return "" + x;
    }

    // Walks back from t to s using d[], adding each vertex to the FRONT
    static String path(int s, int t) {
        if (bcost[t] == INF) {
            return "no path";
        }

        String path = "" + t;
        int x = t;
        while (x != s) {
            x = d[x];
            path = x + " -> " + path;
        }
        return path;
    }

    public static void main(String[] args) {
        int[][] pairs = {
            {1, 12}, {2, 12}, {3, 10}, {5, 9}, {2, 11},
            {1, 7}, {4, 9}, {6, 6}, {10, 2}
        };
        boolean allOk = true;

        System.out.println("Same algorithm, arbitrary (s, t) pairs");
        System.out.println("----------------------------------------------------------");
        System.out.println("  s ->  t | DP cost | brute | path");
        System.out.println("----------------------------------------------------------");

        for (int[] p : pairs) {
            int s = p[0];
            int t = p[1];

            bcostFrom(s);
            int bf = brute(s, t);

            System.out.printf("%3d -> %2d | %7s | %5s | %s%n", s, t, show(bcost[t]), show(bf), path(s, t));

            if (bcost[t] != bf) {
                allOk = false;
            }
        }

        System.out.println("----------------------------------------------------------");
        if (allOk) {
            System.out.println("All results match brute force: YES\n");
        } else {
            System.out.println("All results match brute force: NO\n");
        }

        // Distance matrix: run the algorithm once for every source s
        System.out.println("Distance matrix: row = s, column = t  (. = no path)");

        System.out.print("     ");
        for (int t = 1; t <= N; t++) {
            System.out.printf("%4d", t);
        }
        System.out.println();

        for (int s = 1; s <= N; s++) {
            bcostFrom(s);
            System.out.printf("%4d ", s);

            for (int t = 1; t <= N; t++) {
                if (bcost[t] == INF) {
                    System.out.print("   .");
                } else {
                    System.out.printf("%4d", bcost[t]);
                }
            }
            System.out.println();
        }
    }
}
