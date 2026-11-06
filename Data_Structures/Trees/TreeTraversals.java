/*
 * Tree traversals on the example tree from "Tree Traversal, Backtracking and
 * Branch and Bound.pdf":
 *
 *            A
 *          /   \
 *         B     C
 *        / \     \
 *       D   E     F
 *
 * Breadth-first (level order) uses a QUEUE.
 * Depth-first (preorder, inorder, postorder) uses a STACK or recursion.
 * Queue and stack are plain arrays, no java.util collections.
 *
 * Run: javac TreeTraversals.java && java TreeTraversals
 */
public class TreeTraversals {

    static class Node {
        char label;
        Node left, right;

        Node(char label) {
            this.label = label;
        }
    }

    static StringBuilder out;   // collects the visit order of one traversal

    static void visit(Node v) {
        out.append(v.label).append(' ');
    }

    // ---------- breadth-first: queue (FIFO) ----------
    static void levelOrder(Node root, boolean showQueue) {
        Node[] queue = new Node[64];
        int head = 0, tail = 0;        // items live in queue[head .. tail-1]
        if (root != null) queue[tail++] = root;

        int step = 1;
        while (head < tail) {
            Node v = queue[head++];            // oldest discovered node
            visit(v);
            if (v.left != null) queue[tail++] = v.left;
            if (v.right != null) queue[tail++] = v.right;

            if (showQueue) {
                StringBuilder q = new StringBuilder();
                for (int i = head; i < tail; i++) {
                    q.append(q.length() > 0 ? ", " : "").append(queue[i].label);
                }
                System.out.println("  step " + step++ + ": visit " + v.label
                        + "   queue after: " + (q.length() == 0 ? "(empty)" : q));
            }
        }
    }

    // ---------- depth-first: recursion (the call stack) ----------
    static void preorder(Node v) {
        if (v == null) return;
        visit(v);
        preorder(v.left);
        preorder(v.right);
    }

    static void inorder(Node v) {
        if (v == null) return;
        inorder(v.left);
        visit(v);
        inorder(v.right);
    }

    static void postorder(Node v) {
        if (v == null) return;
        postorder(v.left);
        postorder(v.right);
        visit(v);
    }

    // ---------- depth-first without recursion: explicit stack (LIFO) ----------
    // Same loop as level order, queue swapped for a stack; right child pushed first.
    static void preorderIterative(Node root) {
        Node[] stack = new Node[64];
        int top = -1;
        if (root != null) stack[++top] = root;
        while (top >= 0) {
            Node v = stack[top--];             // newest discovered node
            visit(v);
            if (v.right != null) stack[++top] = v.right;
            if (v.left != null) stack[++top] = v.left;    // pushed last, so visited next
        }
    }

    static int height(Node v) {                // empty tree = 0, single node = 1
        if (v == null) return 0;
        int l = height(v.left), r = height(v.right);
        return 1 + (l > r ? l : r);
    }

    static int countNodes(Node v) {
        return v == null ? 0 : 1 + countNodes(v.left) + countNodes(v.right);
    }

    static Node buildExampleTree() {
        Node a = new Node('A'), b = new Node('B'), c = new Node('C');
        Node d = new Node('D'), e = new Node('E'), f = new Node('F');
        a.left = b;  a.right = c;
        b.left = d;  b.right = e;
        c.right = f;
        return a;
    }

    static void run(String name, Runnable r) {
        out = new StringBuilder();
        r.run();
        System.out.println(name + out.toString().trim());
    }

    public static void main(String[] args) {
        System.out.println("*** Tree traversals ***");
        Node root = buildExampleTree();
        System.out.println("Nodes: " + countNodes(root) + ", height (levels): " + height(root));

        System.out.println("Level order trace (queue):");
        out = new StringBuilder();
        levelOrder(root, true);
        System.out.println("Level order:         " + out.toString().trim());

        run("Preorder:            ", () -> preorder(root));
        run("Inorder:             ", () -> inorder(root));
        run("Postorder:           ", () -> postorder(root));
        run("Preorder (stack):    ", () -> preorderIterative(root));
    }
}
