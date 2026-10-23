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
import java.util.*;

public class ArbitraryST {
    static final int N = 12;
    static final int INF = Integer.MAX_VALUE / 4;

    static final int[][] EDGES = {
        {1,2,9},{1,3,7},{1,4,3},{1,5,2},
        {2,6,4},{2,7,2},{2,8,1},
        {3,6,2},{3,7,7},
        {4,8,11},
        {5,7,11},{5,8,8},
        {6,9,6},{6,10,5},
        {7,9,4},{7,10,3},
        {8,10,5},{8,11,6},
        {9,12,4},{10,12,2},{11,12,5}
    };

    int[] bcost = new int[N + 1];
    int[] d = new int[N + 1];

    /** Generalised BCOST: any source s. */
    void bcostFrom(int s) {
        Arrays.fill(bcost, INF);
        Arrays.fill(d, 0);
        bcost[s] = 0;
        for (int j = s + 1; j <= N; j++)                // topological order
            for (int[] e : EDGES)
                if (e[1] == j && bcost[e[0]] != INF && bcost[e[0]] + e[2] < bcost[j]) {
                    bcost[j] = bcost[e[0]] + e[2];
                    d[j] = e[0];
                }
    }

    /** Brute force: try every path (DFS) - used only for checking. */
    static int brute(int u, int t) {
        if (u == t) return 0;
        int best = INF;
        for (int[] e : EDGES)
            if (e[0] == u) {
                int rest = brute(e[1], t);
                if (rest != INF) best = Math.min(best, e[2] + rest);
            }
        return best;
    }

    static String show(int x) { return x == INF ? "INF" : String.valueOf(x); }

    public static void main(String[] args) {
        int[][] pairs = { {1,12}, {2,12}, {3,10}, {5,9}, {2,11},
                          {1,7}, {4,9}, {6,6}, {10,2} };
        ArbitraryST g = new ArbitraryST();
        boolean allOk = true;

        System.out.println("Same algorithm, arbitrary (s, t) pairs");
        System.out.println("----------------------------------------------------------");
        System.out.println("  s ->  t | DP cost | brute | path");
        System.out.println("----------------------------------------------------------");
        for (int[] p : pairs) {
            int s = p[0], t = p[1];
            g.bcostFrom(s);
            int bf = brute(s, t);
            String path;
            if (g.bcost[t] == INF) path = "no path";
            else {
                LinkedList<Integer> list = new LinkedList<>();
                int x = t;
                list.addFirst(x);
                while (x != s) { x = g.d[x]; list.addFirst(x); }
                StringJoiner sj = new StringJoiner(" -> ");
                for (int v : list) sj.add(String.valueOf(v));
                path = sj.toString();
            }
            System.out.printf("%3d -> %2d | %7s | %5s | %s%n", s, t, show(g.bcost[t]), show(bf), path);
            if (g.bcost[t] != bf) allOk = false;
        }
        System.out.println("----------------------------------------------------------");
        System.out.println("All results match brute force: " + (allOk ? "YES" : "NO") + "\n");

        System.out.println("Distance matrix: row = s, column = t  (. = no path)");
        StringBuilder head = new StringBuilder("     ");
        for (int t = 1; t <= N; t++) head.append(String.format("%4d", t));
        System.out.println(head);
        for (int s = 1; s <= N; s++) {
            g.bcostFrom(s);
            StringBuilder row = new StringBuilder(String.format("%4d ", s));
            for (int t = 1; t <= N; t++)
                row.append(g.bcost[t] == INF ? "   ." : String.format("%4d", g.bcost[t]));
            System.out.println(row);
        }
    }
}
