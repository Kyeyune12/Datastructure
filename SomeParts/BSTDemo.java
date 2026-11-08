public class BSTDemo {
    static class Node {
        int key;
        Node left, right;

        Node(int item) {
            key = item;
            left = right = null;
        }
    }

    static Node insert(Node root, int key) {
        if (root == null) return new Node(key);
        if (key < root.key) root.left = insert(root.left, key);
        else if (key > root.key) root.right = insert(root.right, key);
        return root;
    }

    static void inorder(Node root) {
        if (root != null) {
            inorder(root.left);
            System.out.print(root.key + " ");
            inorder(root.right);
        }
    }

    public static void main(String[] args) {
        Node root = null;
        int[] values = {50, 30, 70, 20, 40};

        for (int v : values) {
            root = insert(root, v);
        }

        System.out.print("Inorder BST Traversal (Sorted): ");
        inorder(root);
        System.out.println();
    }
}
