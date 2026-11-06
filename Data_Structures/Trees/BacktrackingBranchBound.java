/*
 * Backtracking and Branch and Bound on a state-space tree.
 * Reproduces the worked examples in "Tree Traversal, Backtracking and Branch
 * and Bound.pdf":
 *
 *   1. 4-queens        - backtracking = depth-first search + bounding function
 *   2. 0/1 knapsack    - branch and bound with three live-node lists:
 *                          FIFO (queue)          = breadth-first
 *                          LIFO (stack)          = depth-first
 *                          LC   (best bound)     = best-first
 *
 * The live-node lists are plain arrays, no java.util collections.
 *
 * Run: javac BacktrackingBranchBound.java && java BacktrackingBranchBound
 */
public class BacktrackingBranchBound {

    // =====================================================================
    // Part 1: n-queens. x[k] = column of the queen in row k (1-based).
    // A node (partial placement) is "killed" as soon as two queens attack.
    // =====================================================================
    static int nodes;          // nodes generated, root included
    static int solutions;
    static int[] firstSolution;

    static boolean safe(int[] x, int k) {          // may the queen in row k stay?
        for (int i = 1; i < k; i++) {
            if (x[i] == x[k] || Math.abs(x[i] - x[k]) == k - i) {
                return false;
            }
        }
        return true;
    }

    // Returns true when the search should stop (first solution found).
    static boolean backtrack(int[] x, int k, int n, boolean stopAtFirst) {
        for (int v = 1; v <= n; v++) {             // every candidate column for row k
            x[k] = v;
            nodes++;
            if (safe(x, k)) {                      // the bounding function
                if (k == n) {
                    solutions++;
                    if (firstSolution == null) firstSolution = x.clone();
                    if (stopAtFirst) return true;
                } else if (backtrack(x, k + 1, n, stopAtFirst)) {
                    return true;                   // go one level deeper
                }
            }
        }
        return false;                              // loop ends: backtrack to row k-1
    }

    static void runBacktracking(int n, boolean stopAtFirst) {
        nodes = 1;                                 // the root (no decisions yet)
        solutions = 0;
        firstSolution = null;
        backtrack(new int[n + 1], 1, n, stopAtFirst);
    }

    // BFS + the same bounding function: a queue of surviving partial solutions.
    // A solution can only appear on the LAST level, so BFS must generate every
    // shallower level completely before it can report one. We therefore let the
    // search run until the queue is empty (the whole bounded tree).
    static int bfsNodes, bfsMaxQueue;

    static void runBfs(int n) {
        int[][] queue = new int[200000][];
        int head = 0, tail = 0;
        queue[tail++] = new int[n + 1];            // root: level 0
        bfsNodes = 1;
        bfsMaxQueue = 0;
        boolean found = false;

        while (head < tail) {
            int[] parent = queue[head++];
            int k = levelOf(parent);               // rows already fixed
            for (int v = 1; v <= n; v++) {
                int[] child = parent.clone();
                child[k + 1] = v;
                bfsNodes++;
                if (safe(child, k + 1)) {
                    if (k + 1 == n) found = true;
                    else queue[tail++] = child;
                }
            }
            bfsMaxQueue = Math.max(bfsMaxQueue, tail - head);
        }
        if (!found) bfsNodes = -1;                 // no solution exists
    }

    static int levelOf(int[] x) {
        int k = 0;
        while (k + 1 < x.length && x[k + 1] != 0) k++;
        return k;
    }

    // =====================================================================
    // Part 2: 0/1 knapsack by branch and bound.
    // Level i decides whether item i is taken. The upper bound of a node is
    // its profit + the greedy FRACTIONAL knapsack of the remaining items.
    // Items are already sorted by profit/weight (best first).
    // =====================================================================
    static final int[] P = {10, 10, 12, 18};
    static final int[] W = {2, 4, 6, 9};
    static final int M = 15;

    static class Live {                            // one live node
        int level, profit, weight;
        int[] x;
        double bound;
    }

    static double bound(int level, int profit, int weight) {
        double b = profit;
        int remaining = M - weight;
        for (int i = level; i < P.length; i++) {
            if (W[i] <= remaining) {
                remaining -= W[i];
                b += P[i];
            } else {
                b += (double) P[i] * remaining / W[i];   // fraction of the next item
                break;
            }
        }
        return b;
    }

    static final int FIFO = 0, LIFO = 1, LC = 2;

    static void branchAndBound(int kind, String name) {
        int n = P.length;
        Live[] live = new Live[100];
        int count = 0;

        Live root = new Live();
        root.x = new int[n];
        root.bound = bound(0, 0, 0);
        live[count++] = root;

        int best = 0, expanded = 0, peak = 1;
        int[] bestX = new int[n];

        while (count > 0) {
            // choose the next E-node from the live list
            int pick = 0;
            if (kind == LIFO) {
                pick = count - 1;                  // newest
            } else if (kind == LC) {
                for (int i = 1; i < count; i++) {
                    if (live[i].bound > live[pick].bound) pick = i;   // best bound
                }
            }                                      // FIFO: pick = 0, the oldest
            Live e = live[pick];
            for (int i = pick; i < count - 1; i++) live[i] = live[i + 1];
            count--;

            if (e.bound <= best) continue;         // killed: cannot beat the best
            expanded++;

            // branch: generate BOTH children at once. LIFO pushes "take" last so
            // it is explored first.
            int[] order = (kind == LIFO) ? new int[]{0, 1} : new int[]{1, 0};
            for (int take : order) {
                int cp = e.profit + take * P[e.level];
                int cw = e.weight + take * W[e.level];
                if (cw > M) continue;              // infeasible child
                int[] cx = e.x.clone();
                cx[e.level] = take;
                if (cp > best) {
                    best = cp;
                    bestX = cx;
                }
                if (e.level + 1 < n) {
                    double b = bound(e.level + 1, cp, cw);
                    if (b > best) {                // promising: keep it live
                        Live c = new Live();
                        c.level = e.level + 1;
                        c.profit = cp;
                        c.weight = cw;
                        c.x = cx;
                        c.bound = b;
                        live[count++] = c;
                    }
                }
            }
            if (count > peak) peak = count;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(i > 0 ? ", " : "").append(bestX[i]);
        System.out.println(String.format("%-22s expanded %2d, max live %d, best profit %d, x = (%s)",
                name, expanded, peak, best, sb));
    }

    public static void main(String[] args) {
        System.out.println("*** Backtracking and Branch and Bound ***");

        System.out.println();
        System.out.println("4-queens (state-space tree has 1+4+16+64+256 = 341 nodes)");
        runBacktracking(4, true);
        System.out.println("Backtracking, first solution: " + nodes + " nodes generated, x = "
                + describe(firstSolution));
        runBacktracking(4, false);
        System.out.println("Backtracking, all solutions:  " + nodes + " nodes generated, "
                + solutions + " solutions");
        runBfs(4);
        System.out.println("BFS + same bound:             " + bfsNodes + " nodes generated, max queue "
                + bfsMaxQueue);

        System.out.println();
        System.out.println("0/1 knapsack, m = 15, p = (10,10,12,18), w = (2,4,6,9)");
        System.out.println("Root bound = " + bound(0, 0, 0));
        branchAndBound(FIFO, "FIFO B&B (queue):");
        branchAndBound(LIFO, "LIFO B&B (stack):");
        branchAndBound(LC, "LC B&B (best bound):");
    }

    static String describe(int[] x) {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 1; i < x.length; i++) sb.append(i > 1 ? ", " : "").append(x[i]);
        return sb.append(")").toString();
    }
}
