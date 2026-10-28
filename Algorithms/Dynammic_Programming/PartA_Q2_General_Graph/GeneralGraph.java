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
public class GeneralGraph {
    static final int INF = Integer.MAX_VALUE / 4;   // "infinity" (no path yet)

    // Each edge is {from, to, cost}
    static final int[][] BASE = {
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

    // What the re-designed algorithm found
    static class Result {
        String method;           // which method was used
        boolean negativeCycle;   // true if a negative cycle was found
        int cost = INF;          // shortest cost from s to t
        String path = "";        // the path, like "1 -> 2 -> 7"
    }

    // ---------- Step 1: topological sort (Kahn's algorithm) ----------
    // Repeatedly take a vertex with no incoming edges, add it to the order,
    // and "remove" its outgoing edges.
    // Returns the order, or null if the graph has a cycle.
    static int[] topoSort(int n, int[][] edges) {
        // indeg[v] = number of edges coming INTO v
        int[] indeg = new int[n + 1];
        for (int[] e : edges) {
            indeg[e[1]]++;
        }

        // a simple array queue: add at 'tail', remove at 'head'
        int[] queue = new int[n];
        int head = 0;
        int tail = 0;

        // start with every vertex that has no incoming edges
        for (int v = 1; v <= n; v++) {
            if (indeg[v] == 0) {
                queue[tail] = v;
                tail++;
            }
        }

        int[] order = new int[n];
        int count = 0;

        while (head < tail) {
            int u = queue[head];
            head++;

            order[count] = u;
            count++;

            // remove every edge out of u; a vertex whose count reaches 0 is ready
            for (int[] e : edges) {
                if (e[0] == u) {
                    indeg[e[1]]--;
                    if (indeg[e[1]] == 0) {
                        queue[tail] = e[1];
                        tail++;
                    }
                }
            }
        }

        // if some vertices were never ready, they are stuck in a cycle
        if (count == n) {
            return order;
        }
        return null;
    }

    // ---------- Steps 2 and 3: shortest path ----------
    static Result shortestPath(int n, int[][] edges, int s, int t) {
        Result r = new Result();

        int[] bcost = new int[n + 1];
        int[] d = new int[n + 1];
        for (int v = 1; v <= n; v++) {
            bcost[v] = INF;
        }
        bcost[s] = 0;

        int[] order = topoSort(n, edges);

        if (order != null) {
            // DAG: same BCOST rule as Q1, but in topological order
            r.method = "DAG -> BCOST in topological order";

            for (int j : order) {
                for (int[] e : edges) {
                    int l = e[0];
                    int to = e[1];
                    int c = e[2];

                    if (to != j) {
                        continue;   // only edges that END at j
                    }
                    if (bcost[l] != INF && bcost[l] + c < bcost[j]) {
                        bcost[j] = bcost[l] + c;
                        d[j] = l;
                    }
                }
            }
        } else {
            // Cycle: Bellman-Ford. Check every edge, n - 1 times.
            r.method = "cycle found -> Bellman-Ford";

            for (int pass = 1; pass < n; pass++) {
                for (int[] e : edges) {
                    int from = e[0];
                    int to = e[1];
                    int c = e[2];

                    if (bcost[from] != INF && bcost[from] + c < bcost[to]) {
                        bcost[to] = bcost[from] + c;
                        d[to] = from;
                    }
                }
            }

            // one more pass: if anything still gets cheaper, there is a negative cycle
            for (int[] e : edges) {
                int from = e[0];
                int to = e[1];
                int c = e[2];

                if (bcost[from] != INF && bcost[from] + c < bcost[to]) {
                    r.negativeCycle = true;
                    return r;
                }
            }
        }

        if (bcost[t] == INF) {
            return r;   // t cannot be reached
        }
        r.cost = bcost[t];

        // walk back from t to s, adding each vertex to the FRONT of the path
        r.path = "" + t;
        int x = t;
        int steps = 0;
        while (x != s && steps < n) {
            x = d[x];
            r.path = x + " -> " + r.path;
            steps++;
        }
        return r;
    }

    // The Q1 algorithm, unchanged: assumes numeric order == stage order.
    // Used in demo A to show that it fails on a relabelled graph.
    static int naiveBcost(int n, int[][] edges, int s, int t) {
        int[] bcost = new int[n + 1];
        for (int v = 1; v <= n; v++) {
            bcost[v] = INF;
        }
        bcost[s] = 0;

        for (int j = 1; j <= n; j++) {
            if (j == s) {
                continue;
            }
            for (int[] e : edges) {
                int l = e[0];
                int to = e[1];
                int c = e[2];

                if (to == j && bcost[l] != INF && bcost[l] + c < bcost[j]) {
                    bcost[j] = bcost[l] + c;
                }
            }
        }
        return bcost[t];
    }

    // Runs the re-designed algorithm and prints what it found
    static void report(String title, int n, int[][] edges, int s, int t) {
        System.out.println(title);

        Result r = shortestPath(n, edges, s, t);
        System.out.println("  Method: " + r.method);

        if (r.negativeCycle) {
            System.out.println("  Result: NEGATIVE CYCLE reachable - shortest path undefined\n");
        } else if (r.cost == INF) {
            System.out.println("  Result: no path from " + s + " to " + t + "\n");
        } else {
            System.out.println("  Shortest " + s + " -> " + t + " = " + r.cost + "   Path: " + r.path + "\n");
        }
    }

    // Returns a copy of BASE with one extra edge {u, v, w} added at the end
    static int[][] baseWithEdge(int u, int v, int w) {
        int[][] g = new int[BASE.length + 1][];
        for (int i = 0; i < BASE.length; i++) {
            g[i] = BASE[i];
        }
        g[BASE.length] = new int[]{u, v, w};
        return g;
    }

    public static void main(String[] args) {
        // A) relabelled graph: vertex 'old' gets the name newLabel[old]
        int[] newLabel = {0, 7, 3, 12, 1, 9, 2, 11, 5, 10, 4, 8, 6};

        int[][] g = new int[BASE.length][];
        for (int i = 0; i < BASE.length; i++) {
            int from = newLabel[BASE[i][0]];
            int to = newLabel[BASE[i][1]];
            int cost = BASE[i][2];
            g[i] = new int[]{from, to, cost};
        }

        int s = newLabel[1];
        int t = newLabel[12];
        System.out.println("A) Relabelled slide-20 graph (source is now " + s + ", target is now " + t + ")");

        int naive = naiveBcost(12, g, s, t);
        if (naive >= INF) {
            System.out.println("  Naive Q1 loop:  INF  (WRONG - predecessors not ready)");
        } else if (naive == 16) {
            System.out.println("  Naive Q1 loop:  " + naive + "  (correct by luck)");
        } else {
            System.out.println("  Naive Q1 loop:  " + naive + "  (WRONG)");
        }
        report("  Re-designed algorithm:", 12, g, s, t);

        // B) positive cycle 7 -> 10 -> 7
        report("B) Original graph + edge 10->7 (cost 1)  [cycle 7->10->7]", 12, baseWithEdge(10, 7, 1), 1, 12);

        // C) negative cycle 7 -> 10 -> 7 with total weight 3 + (-6) = -3
        report("C) Original graph + edge 10->7 (cost -6) [cycle weight -3]", 12, baseWithEdge(10, 7, -6), 1, 12);
    }
}
