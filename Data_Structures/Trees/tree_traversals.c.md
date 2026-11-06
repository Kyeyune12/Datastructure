# tree_traversals.c

The **C version** of [`TreeTraversals.java`](TreeTraversals.java.md); same output.

## How to run

```sh
gcc -std=c11 -Wall -Wextra tree_traversals.c -o trees
./trees
```

## What is different from Java

Nodes are `calloc`ed (so `left` and `right` start as `NULL`) and the whole tree is freed with a postorder walk (`free_tree`): the children must be freed before the parent, otherwise the parent's pointers are lost.

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

- **Why does `free_tree` use postorder?** Freeing the parent first would lose the pointers to its children and leak them.
