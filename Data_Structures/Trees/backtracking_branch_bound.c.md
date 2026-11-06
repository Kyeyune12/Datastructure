# backtracking_branch_bound.c

The **C version** of [`BacktrackingBranchBound.java`](BacktrackingBranchBound.java.md); same output.

## How to run

```sh
gcc -std=c11 -Wall -Wextra backtracking_branch_bound.c -o bb
./bb
```

## What is different from Java

Partial solutions are plain structs copied by value (`Live c = e;`), so no `clone()` is needed. The queue and the live list are fixed-size arrays; removing from the middle shifts the later entries left.

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

- **Why can structs be copied with `=`?** A struct containing only arrays and numbers is copied member by member, including its embedded arrays.
