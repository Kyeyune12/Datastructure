/*
 * Part B - Assignment 2
 * All-pairs shortest paths with NEGATIVE edge weights.
 *
 * Floyd-Warshall still works with negative edges, as long as there is
 * no NEGATIVE CYCLE. A negative cycle shows up after the algorithm as
 * a negative diagonal entry: A(i,i) < 0  ->  i lies on a negative cycle,
 * and any pair whose path can pass through it has no shortest path.
 *
 * Demos:
 *   1) Why greedy Dijkstra is not enough: it gets a wrong answer.
 *   2) Floyd-Warshall on a 5-vertex graph with negative edges (no neg cycle).
 *   3) Floyd-Warshall detecting a negative cycle.
 *
 * Compile: javac NegativeWeights.java     Run: java NegativeWeights
 */
public class NegativeWeights {
    static final int INF = Integer.MAX_VALUE / 4;   // "infinity" (no path)

    int n;          // number of vertices
    int[][] A;      // A[i][j] = shortest known distance from i to j
    int[][] next;   // next[i][j] = first vertex to go to on the way from i to j

    // Starts with no edges: distance 0 to yourself, INF to everyone else
    NegativeWeights(int n) {
        this.n = n;
        A = new int[n + 1][n + 1];
        next = new int[n + 1][n + 1];

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                if (i == j) {
                    A[i][j] = 0;
                    next[i][j] = j;
                } else {
                    A[i][j] = INF;
                    next[i][j] = 0;
                }
            }
        }
    }

    // Adds a direct edge u -> v with cost w (w may be negative)
    void addEdge(int u, int v, int w) {
        A[u][v] = w;
        next[u][v] = v;
    }

    // Runs Floyd-Warshall; returns true if a negative cycle exists
    boolean floyd() {
        for (int k = 1; k <= n; k++) {
            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= n; j++) {
                    if (A[i][k] < INF && A[k][j] < INF && A[i][k] + A[k][j] < A[i][j]) {
                        A[i][j] = A[i][k] + A[k][j];
                        next[i][j] = next[i][k];
                    }
                }
            }
        }

        // going from i back to i should cost 0;
        // if it costs less than 0, i is on a negative cycle
        for (int i = 1; i <= n; i++) {
            if (A[i][i] < 0) {
                return true;
            }
        }
        return false;
    }

    // Prints the matrix A with row and column numbers
    void printMatrix() {
        System.out.print("      ");
        for (int j = 1; j <= n; j++) {
            System.out.printf("%5d", j);
        }
        System.out.println();

        for (int i = 1; i <= n; i++) {
            System.out.printf("%5d ", i);
            for (int j = 1; j <= n; j++) {
                if (A[i][j] >= INF) {
                    System.out.print("  INF");
                } else {
                    System.out.printf("%5d", A[i][j]);
                }
            }
            System.out.println();
        }
    }

    // Prints the shortest path from i to j by following next[][]
    void printPath(int i, int j) {
        String path = "" + i;
        int x = i;
        while (x != j) {
            x = next[x][j];
            path = path + " -> " + x;
        }
        System.out.printf("  %d -> %d : cost %3d   path %s%n", i, j, A[i][j], path);
    }

    // Plain O(n^2) Dijkstra on an adjacency matrix w.
    // Greedy: once a vertex is "done", its distance is never changed again.
    // That is exactly why it can fail with negative edges.
    static int dijkstra(int n, int s, int t, int[][] w) {
        int[] dist = new int[n + 1];
        boolean[] done = new boolean[n + 1];

        for (int v = 1; v <= n; v++) {
            dist[v] = INF;
        }
        dist[s] = 0;

        for (int round = 0; round < n; round++) {
            // pick the closest vertex that is not done yet
            int u = -1;
            for (int v = 1; v <= n; v++) {
                if (!done[v] && (u == -1 || dist[v] < dist[u])) {
                    u = v;
                }
            }
            if (dist[u] == INF) {
                break;   // everything left is unreachable
            }

            done[u] = true;   // greedy: u's distance is now final forever

            // update the neighbours of u that are not done yet
            for (int v = 1; v <= n; v++) {
                if (!done[v] && u != v && w[u][v] < INF && dist[u] + w[u][v] < dist[v]) {
                    dist[v] = dist[u] + w[u][v];
                }
            }
        }
        return dist[t];
    }

    public static void main(String[] args) {
        // ---------- 1) Dijkstra fails with a negative edge ----------
        System.out.println("===== 1) Dijkstra fails with a negative edge =====");
        System.out.println("Edges: 1->2 (2), 1->3 (5), 3->2 (-4)");

        int[][] w = new int[4][4];
        for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 3; j++) {
                if (i == j) {
                    w[i][j] = 0;
                } else {
                    w[i][j] = INF;
                }
            }
        }
        w[1][2] = 2;
        w[1][3] = 5;
        w[3][2] = -4;
        System.out.println("  Dijkstra   dist(1 -> 2) = " + dijkstra(3, 1, 2, w) + "   (WRONG)");

        NegativeWeights f0 = new NegativeWeights(3);
        f0.addEdge(1, 2, 2);
        f0.addEdge(1, 3, 5);
        f0.addEdge(3, 2, -4);
        f0.floyd();
        System.out.println("  Floyd      dist(1 -> 2) = " + f0.A[1][2] + "   (correct: 1 -> 3 -> 2)\n");

        // ---------- 2) Negative edges, but no negative cycle ----------
        System.out.println("===== 2) Floyd-Warshall with negative edges, no negative cycle =====");
        System.out.println("Edges: 1->2 (3), 1->3 (8), 1->5 (-4), 2->4 (1), 2->5 (7),");
        System.out.println("       3->2 (4), 4->1 (2), 4->3 (-5), 5->4 (6)\n");

        NegativeWeights g = new NegativeWeights(5);
        g.addEdge(1, 2, 3);
        g.addEdge(1, 3, 8);
        g.addEdge(1, 5, -4);
        g.addEdge(2, 4, 1);
        g.addEdge(2, 5, 7);
        g.addEdge(3, 2, 4);
        g.addEdge(4, 1, 2);
        g.addEdge(4, 3, -5);
        g.addEdge(5, 4, 6);

        boolean neg = g.floyd();
        g.printMatrix();
        System.out.println("Negative cycle? " + (neg ? "YES" : "NO"));
        g.printPath(1, 3);
        g.printPath(1, 2);
        g.printPath(5, 2);
        g.printPath(3, 1);

        // ---------- 3) Negative cycle ----------
        System.out.println("\n===== 3) Negative cycle detection =====");
        System.out.println("Edges: 1->2 (1), 2->3 (-2), 3->1 (-1), 3->4 (2)   [cycle 1->2->3->1 = -2]\n");

        NegativeWeights h = new NegativeWeights(4);
        h.addEdge(1, 2, 1);
        h.addEdge(2, 3, -2);
        h.addEdge(3, 1, -1);
        h.addEdge(3, 4, 2);

        neg = h.floyd();
        h.printMatrix();
        System.out.println("Negative cycle? " + (neg ? "YES" : "NO"));

        // list every vertex whose diagonal entry is negative
        String onCycle = "";
        for (int i = 1; i <= 4; i++) {
            if (h.A[i][i] < 0) {
                onCycle = onCycle + " " + i;
            }
        }
        System.out.println("Vertices on / reaching a negative cycle (A(i,i) < 0):" + onCycle);
        System.out.println("=> distances among these vertices are undefined (can go to -infinity).");
    }
}
