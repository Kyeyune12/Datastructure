/*
 * Part A - Assignment 1
 * Backward approach (BGraph) for the multistage graph on slide 20,
 * ASSUMING BCOST(l) is already available while computing BCOST(j).
 *
 * The assumption holds because vertices are numbered in stage order:
 * every edge <l, j> has l < j, so looping j = 2..n is enough.
 *
 *   BCOST(s) = 0
 *   BCOST(j) = min over edges <l, j> of { BCOST(l) + c(l, j) }
 *   D(j)     = the l that gave the minimum (to rebuild the path)
 *
 * Compile: javac BCost.java
 * Run:     java BCost
 */
public class BCost {
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

    static int[] bcost = new int[N + 1];   // bcost[j] = cheapest cost from s to j
    static int[] d = new int[N + 1];       // d[j] = vertex we came from to reach j cheaply

    // Fills bcost[] and d[] for source s
    static void bgraph(int s) {
        // at the start, nothing is reachable except s itself
        for (int j = 1; j <= N; j++) {
            bcost[j] = INF;
            d[j] = 0;
        }
        bcost[s] = 0;

        // visit vertices in stage order, so every BCOST(l) is ready before BCOST(j)
        for (int j = s + 1; j <= N; j++) {
            for (int[] e : EDGES) {
                int l = e[0];
                int to = e[1];
                int c = e[2];

                if (to != j) {
                    continue;   // we only want edges that END at j
                }

                // is "go to l, then take edge l -> j" cheaper than what we have?
                if (bcost[l] != INF && bcost[l] + c < bcost[j]) {
                    bcost[j] = bcost[l] + c;
                    d[j] = l;
                }
            }
        }
    }

    // Builds the path by following d[] back from j to the start.
    // Example: path(12) = path(10) + " -> 12" = ... = "1 -> 2 -> 7 -> 10 -> 12"
    static String path(int j) {
        if (d[j] == 0) {
            return "" + j;   // j is the start vertex
        }
        return path(d[j]) + " -> " + j;
    }

    public static void main(String[] args) {
        bgraph(1);

        System.out.println("Backward approach (BGraph) - slide 20 multistage graph");
        System.out.println("-----------------------------");
        System.out.println("  j | BCOST(j) | D(j)");
        System.out.println("-----------------------------");

        for (int j = 1; j <= N; j++) {
            String dj;
            if (d[j] == 0) {
                dj = "-";
            } else {
                dj = "" + d[j];
            }
            System.out.printf("%3d | %8d | %4s%n", j, bcost[j], dj);
        }

        System.out.println("-----------------------------");
        System.out.println("Shortest distance 1 -> 12 = " + bcost[N]);
        System.out.println("Path: " + path(N));
    }
}
