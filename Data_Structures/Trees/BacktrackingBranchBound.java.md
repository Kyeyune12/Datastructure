# BacktrackingBranchBound.java

Code for question 2 of [`Tree Traversal, Backtracking and Branch and Bound.pdf`](Tree%20Traversal%2C%20Backtracking%20and%20Branch%20and%20Bound.pdf). It reproduces the worked numbers in the PDF.

## How to run

```sh
javac BacktrackingBranchBound.java
java BacktrackingBranchBound
```

## Part 1: 4-queens (backtracking)

`x[k]` is the column of the queen in row `k`. `backtrack` tries each column, kills the node at once if two queens attack (`safe`), otherwise recurses one row deeper; when the loop ends the function returns, which is the *backtrack*. The root counts as a node. Results: 27 nodes to the first solution (2, 4, 1, 3), 61 nodes for all (2 solutions), against 341 nodes with no pruning. A breadth-first search with the same test also generates 61 nodes and needs a queue of up to 6 partial solutions, while DFS holds only one path.

## Part 2: 0/1 knapsack (branch and bound)

Capacity 15, profits (10,10,12,18), weights (2,4,6,9). The bound of a node is its profit plus the *fractional* greedy fill of the remaining items (38 at the root). The three variants differ only in how the next E-node is picked from the live list: **FIFO** = oldest (queue, BFS), **LIFO** = newest (stack, DFS), **LC** = highest bound (best-first). All find profit 38 with x = (1,1,0,1); FIFO expands 7 nodes, LIFO and LC 5.

## Output

```
*** Backtracking and Branch and Bound ***

4-queens (state-space tree has 1+4+16+64+256 = 341 nodes)
Backtracking, first solution: 27 nodes generated, x = (2, 4, 1, 3)
Backtracking, all solutions:  61 nodes generated, 2 solutions
BFS + same bound:             61 nodes generated, max queue 6

0/1 knapsack, m = 15, p = (10,10,12,18), w = (2,4,6,9)
Root bound = 38.0
FIFO B&B (queue):      expanded  7, max live 5, best profit 38, x = (1, 1, 0, 1)
LIFO B&B (stack):      expanded  5, max live 4, best profit 38, x = (1, 1, 0, 1)
LC B&B (best bound):   expanded  5, max live 4, best profit 38, x = (1, 1, 0, 1)
```

## Questions you may be asked

- **Backtracking vs branch and bound?** Backtracking is always depth-first with a feasibility test and usually lists solutions. Branch and bound creates all children of the E-node at once, may use BFS, DFS or best-first order, and uses a cost bound to optimise.
- **Why is the bound optimistic?** It allows a fraction of an item, which is never worse than the 0/1 answer. A node whose optimistic value cannot beat the best known answer can be killed safely.
- **Why does LC usually expand the fewest nodes?** It always works on the most promising node, so good solutions are found early and more of the tree is pruned.
