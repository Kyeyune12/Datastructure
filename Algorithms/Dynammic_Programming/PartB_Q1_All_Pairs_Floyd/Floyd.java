/*
 * Part B - Assignment 1
 * All-pairs shortest paths (Floyd-Warshall), exactly the slide's loop:
 *
 *   for k = 1 to n
 *     for i = 1 to n
 *       for j = 1 to n
 *         A(i,j) = min[ A(i,j), A(i,k) + A(k,j) ]
 *
 * A^k(i,j) = shortest i->j path using only intermediate vertices {1..k}.
 * A next[][] matrix is kept alongside so the actual paths can be printed.
 *
 * Example 1: textbook 3-vertex graph, showing A0, A1, A2, A3.
 * Example 2: the slide-20 multistage graph (all 12 x 12 distances).
 *
 * Compile: javac Floyd.java     Run: java Floyd
 */
public class Floyd {
    static final int INF = Integer.MAX_VALUE / 4;

    final int n;
    final int[][] A, next;

    Floyd(int n) {
        this.n = n;
        A = new int[n + 1][n + 1];
        next = new int[n + 1][n + 1];
        for (int i = 1; i <= n; i++)
            for (int j = 1; j <= n; j++) {
                A[i][j] = (i == j) ? 0 : INF;
                next[i][j] = (i == j) ? j : 0;
            }
    }

    void addEdge(int u, int v, int w) { A[u][v] = w; next[u][v] = v; }

    void run(boolean showSteps) {
        for (int k = 1; k <= n; k++) {
            for (int i = 1; i <= n; i++)
                for (int j = 1; j <= n; j++)
                    if (A[i][k] < INF && A[k][j] < INF && A[i][k] + A[k][j] < A[i][j]) {
                        A[i][j] = A[i][k] + A[k][j];
                        next[i][j] = next[i][k];
                    }
            if (showSteps) print("A" + k + " (via vertices 1.." + k + "):");
        }
    }

    void print(String name) {
        System.out.println(name);
        StringBuilder sb = new StringBuilder("      ");
        for (int j = 1; j <= n; j++) sb.append(String.format("%5d", j));
        System.out.println(sb);
        for (int i = 1; i <= n; i++) {
            sb = new StringBuilder(String.format("%5d ", i));
            for (int j = 1; j <= n; j++) sb.append(A[i][j] >= INF ? "  INF" : String.format("%5d", A[i][j]));
            System.out.println(sb);
        }
        System.out.println();
    }

    void printPath(int i, int j) {
        if (A[i][j] >= INF) { System.out.printf("  %2d -> %2d : no path%n", i, j); return; }
        StringBuilder sb = new StringBuilder().append(i);
        int x = i;
        while (x != j) { x = next[x][j]; sb.append(" -> ").append(x); }
        System.out.printf("  %2d -> %2d : cost %3d   path %s%n", i, j, A[i][j], sb);
    }

    public static void main(String[] args) {
        System.out.println("===== Example 1: 3-vertex graph =====");
        System.out.println("Edges: 1->2 (4), 1->3 (11), 2->1 (6), 2->3 (2), 3->1 (3)\n");
        Floyd f = new Floyd(3);
        f.addEdge(1,2,4); f.addEdge(1,3,11); f.addEdge(2,1,6); f.addEdge(2,3,2); f.addEdge(3,1,3);
        f.print("A0 (direct edges):");
        f.run(true);
        for (int i = 1; i <= 3; i++)
            for (int j = 1; j <= 3; j++) if (i != j) f.printPath(i, j);

        System.out.println("\n===== Example 2: slide-20 multistage graph =====\n");
        int[][] e = {
            {1,2,9},{1,3,7},{1,4,3},{1,5,2},{2,6,4},{2,7,2},{2,8,1},{3,6,2},{3,7,7},
            {4,8,11},{5,7,11},{5,8,8},{6,9,6},{6,10,5},{7,9,4},{7,10,3},{8,10,5},
            {8,11,6},{9,12,4},{10,12,2},{11,12,5}
        };
        Floyd g = new Floyd(12);
        for (int[] x : e) g.addEdge(x[0], x[1], x[2]);
        g.run(false);
        g.print("Final A (all-pairs distances):");
        g.printPath(1, 12); g.printPath(2, 12); g.printPath(3, 10); g.printPath(5, 9); g.printPath(4, 9);
    }
}
