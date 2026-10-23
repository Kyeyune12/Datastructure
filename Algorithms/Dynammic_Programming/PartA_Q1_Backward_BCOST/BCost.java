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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BCost {
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
    List<List<int[]>> pred = new ArrayList<>();   // pred[v] = list of {u, w}

    BCost() {
        for (int i = 0; i <= N; i++) pred.add(new ArrayList<>());
        for (int[] e : EDGES) pred.get(e[1]).add(new int[]{e[0], e[2]});
    }

    List<Integer> bgraph(int s, int t) {
        for (int j = 1; j <= N; j++) { bcost[j] = INF; d[j] = 0; }
        bcost[s] = 0;

        for (int j = s + 1; j <= N; j++) {          // stage order
            for (int[] e : pred.get(j)) {           // each edge once
                int l = e[0], c = e[1];
                if (bcost[l] != INF && bcost[l] + c < bcost[j]) {
                    bcost[j] = bcost[l] + c;
                    d[j] = l;
                }
            }
        }

        List<Integer> path = new ArrayList<>();
        int x = t;
        path.add(x);
        while (x != s && d[x] != 0) { x = d[x]; path.add(x); }
        Collections.reverse(path);
        return path;
    }

    public static void main(String[] args) {
        BCost g = new BCost();
        List<Integer> path = g.bgraph(1, N);

        System.out.println("Backward approach (BGraph) - slide 20 multistage graph");
        System.out.println("-----------------------------");
        System.out.println("  j | BCOST(j) | D(j)");
        System.out.println("-----------------------------");
        for (int j = 1; j <= N; j++) {
            String dj = g.d[j] == 0 ? "-" : String.valueOf(g.d[j]);
            System.out.printf("%3d | %8d | %4s%n", j, g.bcost[j], dj);
        }
        System.out.println("-----------------------------");
        System.out.println("Shortest distance 1 -> 12 = " + g.bcost[N]);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) sb.append(path.get(i)).append(i + 1 < path.size() ? " -> " : "");
        System.out.println("Path: " + sb);
    }
}
