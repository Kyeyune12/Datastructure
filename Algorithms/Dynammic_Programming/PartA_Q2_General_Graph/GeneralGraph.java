/*
 * Part A - Assignment 2
 * General graph: BCOST(l) may NOT be ready when BCOST(j) is computed.
 *
 * Re-design:
 *   1. Topologically sort the vertices (Kahn's algorithm).
 *   2. If the graph is a DAG, run the same BCOST recurrence but visit the
 *      vertices in TOPOLOGICAL order instead of numeric order. Every
 *      predecessor l of j then comes before j, so BCOST(l) is ready.
 *   3. If the sort fails (the graph has a cycle) there is no order that
 *      works, so fall back to Bellman-Ford: relax every edge n-1 times,
 *      then do one more pass to detect a negative cycle.
 *
 * Demos:
 *   A) Slide-20 graph with vertices RELABELLED (not in stage order):
 *      the naive Q1 loop gives a wrong answer, the re-design is correct.
 *   B) Original graph + edge 10->7 (cost 1): a positive cycle -> Bellman-Ford.
 *   C) Original graph + edge 10->7 (cost -6): negative cycle -> reported.
 *
 * Compile: javac GeneralGraph.java     Run: java GeneralGraph
 */
import java.util.*;

public class GeneralGraph {
    static final int INF = Integer.MAX_VALUE / 4;

    static final int[][] BASE = {
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

    /** Result of the re-designed algorithm. */
    static class Result {
        String method;
        boolean negativeCycle;
        int cost = INF;
        List<Integer> path = new ArrayList<>();
    }

    /** Kahn's algorithm. Returns null if the graph has a cycle. */
    static List<Integer> topoSort(int n, List<int[]> edges) {
        int[] indeg = new int[n + 1];
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i <= n; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) { adj.get(e[0]).add(e[1]); indeg[e[1]]++; }

        Deque<Integer> q = new ArrayDeque<>();
        for (int v = 1; v <= n; v++) if (indeg[v] == 0) q.add(v);
        List<Integer> order = new ArrayList<>();
        while (!q.isEmpty()) {
            int u = q.poll();
            order.add(u);
            for (int v : adj.get(u)) if (--indeg[v] == 0) q.add(v);
        }
        return order.size() == n ? order : null;
    }

    static Result shortestPath(int n, List<int[]> edges, int s, int t) {
        Result r = new Result();
        int[] bcost = new int[n + 1], d = new int[n + 1];
        Arrays.fill(bcost, INF);
        bcost[s] = 0;

        List<List<int[]>> pred = new ArrayList<>();
        for (int i = 0; i <= n; i++) pred.add(new ArrayList<>());
        for (int[] e : edges) pred.get(e[1]).add(e);

        List<Integer> order = topoSort(n, edges);
        if (order != null) {
            r.method = "DAG -> BCOST in topological order";
            for (int j : order)
                for (int[] e : pred.get(j)) {
                    int l = e[0], c = e[2];
                    if (bcost[l] != INF && bcost[l] + c < bcost[j]) {
                        bcost[j] = bcost[l] + c;
                        d[j] = l;
                    }
                }
        } else {
            r.method = "cycle found -> Bellman-Ford";
            for (int pass = 1; pass < n; pass++)
                for (int[] e : edges)
                    if (bcost[e[0]] != INF && bcost[e[0]] + e[2] < bcost[e[1]]) {
                        bcost[e[1]] = bcost[e[0]] + e[2];
                        d[e[1]] = e[0];
                    }
            for (int[] e : edges)
                if (bcost[e[0]] != INF && bcost[e[0]] + e[2] < bcost[e[1]]) {
                    r.negativeCycle = true;
                    return r;
                }
        }

        if (bcost[t] == INF) return r;
        r.cost = bcost[t];
        int x = t;
        r.path.add(x);
        while (x != s && r.path.size() <= n) { x = d[x]; r.path.add(x); }
        Collections.reverse(r.path);
        return r;
    }

    /** The Q1 algorithm, unchanged: assumes numeric order == stage order. */
    static int naiveBcost(int n, List<int[]> edges, int s, int t) {
        int[] bcost = new int[n + 1];
        Arrays.fill(bcost, INF);
        bcost[s] = 0;
        for (int j = 1; j <= n; j++) {
            if (j == s) continue;
            for (int[] e : edges)
                if (e[1] == j && bcost[e[0]] != INF && bcost[e[0]] + e[2] < bcost[j])
                    bcost[j] = bcost[e[0]] + e[2];
        }
        return bcost[t];
    }

    static void report(String title, int n, List<int[]> edges, int s, int t) {
        System.out.println(title);
        Result r = shortestPath(n, edges, s, t);
        System.out.println("  Method: " + r.method);
        if (r.negativeCycle)
            System.out.println("  Result: NEGATIVE CYCLE reachable - shortest path undefined\n");
        else if (r.cost == INF)
            System.out.println("  Result: no path from " + s + " to " + t + "\n");
        else {
            StringJoiner sj = new StringJoiner(" -> ");
            for (int v : r.path) sj.add(String.valueOf(v));
            System.out.println("  Shortest " + s + " -> " + t + " = " + r.cost + "   Path: " + sj + "\n");
        }
    }

    static List<int[]> copyBase() {
        List<int[]> g = new ArrayList<>();
        for (int[] e : BASE) g.add(e.clone());
        return g;
    }

    public static void main(String[] args) {
        // A) relabelled graph: newLabel[old]
        int[] newLabel = {0, 7, 3, 12, 1, 9, 2, 11, 5, 10, 4, 8, 6};
        List<int[]> g = new ArrayList<>();
        for (int[] e : BASE) g.add(new int[]{newLabel[e[0]], newLabel[e[1]], e[2]});
        int s = newLabel[1], t = newLabel[12];
        System.out.println("A) Relabelled slide-20 graph (source is now " + s + ", target is now " + t + ")");
        int naive = naiveBcost(12, g, s, t);
        if (naive >= INF) System.out.println("  Naive Q1 loop:  INF  (WRONG - predecessors not ready)");
        else System.out.println("  Naive Q1 loop:  " + naive + "  (" + (naive == 16 ? "correct by luck" : "WRONG") + ")");
        report("  Re-designed algorithm:", 12, g, s, t);

        // B) positive cycle
        g = copyBase();
        g.add(new int[]{10, 7, 1});
        report("B) Original graph + edge 10->7 (cost 1)  [cycle 7->10->7]", 12, g, 1, 12);

        // C) negative cycle
        g = copyBase();
        g.add(new int[]{10, 7, -6});
        report("C) Original graph + edge 10->7 (cost -6) [cycle weight -3]", 12, g, 1, 12);
    }
}
