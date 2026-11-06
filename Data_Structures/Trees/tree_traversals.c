// Tree traversals (C version of TreeTraversals.java)
// Example tree from "Tree Traversal, Backtracking and Branch and Bound.pdf":
//   A is the root; B and C are its children;
//   D and E are the children of B; F is the right child of C.
//
// Level order uses a QUEUE (FIFO); DFS uses recursion or a STACK (LIFO).
//
// Build: gcc -std=c11 -Wall -Wextra tree_traversals.c -o trees && ./trees
#include <stdio.h>
#include <stdlib.h>

struct Node {
    char label;
    struct Node *left, *right;
};

static struct Node *new_node(char label) {
    struct Node *n = calloc(1, sizeof(struct Node));
    if (n == NULL) exit(1);
    n->label = label;
    return n;
}

static int visited = 0;   // how many nodes this traversal has printed so far

static void visit(const struct Node *v) {
    printf(visited++ ? " %c" : "%c", v->label);   // no trailing space
}

// ---------- breadth-first: queue ----------
static void level_order(struct Node *root) {
    struct Node *queue[64];
    int head = 0, tail = 0;
    if (root != NULL) queue[tail++] = root;

    while (head < tail) {
        struct Node *v = queue[head++];               // oldest discovered node
        visit(v);
        if (v->left != NULL) queue[tail++] = v->left;
        if (v->right != NULL) queue[tail++] = v->right;
    }
}

// Same traversal, but prints the queue after every step.
static void level_order_trace(struct Node *root) {
    struct Node *queue[64];
    int head = 0, tail = 0, step = 1;
    if (root != NULL) queue[tail++] = root;
    while (head < tail) {
        struct Node *v = queue[head++];
        if (v->left != NULL) queue[tail++] = v->left;
        if (v->right != NULL) queue[tail++] = v->right;
        printf("  step %d: visit %c   queue after: ", step++, v->label);
        if (head == tail) printf("(empty)");
        for (int i = head; i < tail; i++) printf(i > head ? ", %c" : "%c", queue[i]->label);
        printf("\n");
    }
}

// ---------- depth-first: recursion ----------
static void preorder(const struct Node *v)  { if (!v) return; visit(v); preorder(v->left); preorder(v->right); }
static void inorder(const struct Node *v)   { if (!v) return; inorder(v->left); visit(v); inorder(v->right); }
static void postorder(const struct Node *v) { if (!v) return; postorder(v->left); postorder(v->right); visit(v); }

// ---------- depth-first without recursion: explicit stack ----------
static void preorder_iterative(struct Node *root) {
    struct Node *stack[64];
    int top = -1;
    if (root != NULL) stack[++top] = root;
    while (top >= 0) {
        struct Node *v = stack[top--];
        visit(v);
        if (v->right != NULL) stack[++top] = v->right;
        if (v->left != NULL) stack[++top] = v->left;   // pushed last, visited next
    }
}

static int height(const struct Node *v) {
    if (v == NULL) return 0;
    int l = height(v->left), r = height(v->right);
    return 1 + (l > r ? l : r);
}
static int count_nodes(const struct Node *v) { return v == NULL ? 0 : 1 + count_nodes(v->left) + count_nodes(v->right); }

static void free_tree(struct Node *v) { if (!v) return; free_tree(v->left); free_tree(v->right); free(v); }

int main(void) {
    printf("*** Tree traversals ***\n");
    struct Node *a = new_node('A'), *b = new_node('B'), *c = new_node('C');
    struct Node *d = new_node('D'), *e = new_node('E'), *f = new_node('F');
    a->left = b;  a->right = c;
    b->left = d;  b->right = e;
    c->right = f;

    printf("Nodes: %d, height (levels): %d\n", count_nodes(a), height(a));
    printf("Level order trace (queue):\n");
    level_order_trace(a);

    printf("Level order:         "); visited = 0; level_order(a);         printf("\n");
    printf("Preorder:            "); visited = 0; preorder(a);            printf("\n");
    printf("Inorder:             "); visited = 0; inorder(a);             printf("\n");
    printf("Postorder:           "); visited = 0; postorder(a);           printf("\n");
    printf("Preorder (stack):    "); visited = 0; preorder_iterative(a);  printf("\n");

    free_tree(a);
    return 0;
}
