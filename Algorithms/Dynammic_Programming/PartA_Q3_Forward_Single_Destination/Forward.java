/*
 * Part A - Assignment 3
 * Forward approach (FGraph): single-DESTINATION shortest paths.
 *
 *   COST(t) = 0
 *   COST(i) = min over edges <i, r> of { c(i, r) + COST(r) }
 *   D(i)    = the r that gave the minimum (the best next hop from i)
 *
 * Vertices are processed i = n-1 down to 1 (reverse stage order), so
 * COST(r) is always ready. When the loop finishes, COST(i) is the
 * shortest distance from EVERY vertex i to t - not just from s.
 *
 * The redraft below keeps COST() and D() for all vertices and then
 * prints the full shortest path from every vertex to t by following D().
 *
 * Compile: javac Forward.java     Run: java Forward
 */
public class Forward {
    static final int N = 12;                       // number of vertices
    static final int INF = Integer.MAX_VALUE / 4;  // "infinity" (no path yet)

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

    static int[] cost = new int[N + 1];   // cost[i] = cheapest cost from i to t
    static int[] d = new int[N + 1];      // d[i] = best next vertex after i

    // Fills cost[] and d[] for destination t
    static void fgraph(int t) {
        for (int i = 1; i <= N; i++) {
            cost[i] = INF;
            d[i] = 0;
        }
        cost[t] = 0;

        // go BACKWARDS through the stages, so COST(r) is ready before COST(i)
        for (int i = t - 1; i >= 1; i--) {
            for (int[] e : EDGES) {
                int from = e[0];
                int r = e[1];
                int c = e[2];

                if (from != i) {
                    continue;   // we only want edges that START at i
                }

                // is "take edge i -> r, then the best way from r" cheaper?
                if (cost[r] != INF && c + cost[r] < cost[i]) {
                    cost[i] = c + cost[r];
                    d[i] = r;
                }
            }
        }
    }

    // Follows d[] forward from i until we reach t
    static String pathFrom(int i, int t) {
        String path = "" + i;
        int x = i;

        while (x != t && d[x] != 0) {
            x = d[x];
            path = path + " -> " + x;
        }
        return path;
    }

    public static void main(String[] args) {
        int t = N;
        fgraph(t);

        System.out.println("Forward approach - shortest path from EVERY vertex to t = " + t);
        System.out.println("----------------------------------------------------");
        System.out.println("  i | COST(i) | D(i) | shortest path i -> t");
        System.out.println("----------------------------------------------------");

        for (int i = 1; i <= N; i++) {
            String di;
            if (d[i] == 0) {
                di = "-";
            } else {
                di = "" + d[i];
            }
            System.out.printf("%3d | %7d | %4s | %s%n", i, cost[i], di, pathFrom(i, t));
        }

        System.out.println("----------------------------------------------------");
        System.out.println("Answer for s = 1: COST(1) = " + cost[1]);
    }
}
