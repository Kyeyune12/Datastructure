# TreeTraversals.java

Code for question 1 of [`Tree Traversal, Backtracking and Branch and Bound.pdf`](Tree%20Traversal%2C%20Backtracking%20and%20Branch%20and%20Bound.pdf): every traversal of the example tree A-F, using plain arrays as the queue and the stack.

## How to run

```sh
javac TreeTraversals.java
java TreeTraversals
```

## The tree

```
       A
     /   \
    B     C
   / \     \
  D   E     F
```

## What it shows

- **Level order (breadth-first)** uses a **queue**. The trace prints the queue after each step and matches the table in the PDF.
- **Preorder / inorder / postorder (depth-first)** use recursion, i.e. the call stack.
- **Iterative preorder** is the same loop as level order with the queue replaced by a **stack** (right child pushed first so the left is visited first). This is the key idea: BFS and DFS differ only in the container.

## Output

```
*** Tree traversals ***
Nodes: 6, height (levels): 3
Level order trace (queue):
  step 1: visit A   queue after: B, C
  step 2: visit B   queue after: C, D, E
  step 3: visit C   queue after: D, E, F
  step 4: visit D   queue after: E, F
  step 5: visit E   queue after: F
  step 6: visit F   queue after: (empty)
Level order:         A B C D E F
Preorder:            A B D E C F
Inorder:             D B E A C F
Postorder:           D E B F C A
Preorder (stack):    A B D E C F
```

## Questions you may be asked

- **Time and space?** All traversals are O(n) time. BFS needs O(w) extra space (widest level), DFS needs O(h) (height).
- **Which traversal sorts a binary search tree?** Inorder.
- **Which one deletes a tree safely?** Postorder: children are freed before the parent.
- **Why no `visited` marks?** A tree has exactly one path to each node, so no node can be reached twice. A graph needs the marks.
