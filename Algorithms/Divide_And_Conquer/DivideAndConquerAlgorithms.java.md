# DivideAndConquerAlgorithms.java

The runnable Java program for [`DivideAndConquerProblems.md`](DivideAndConquerProblems.md), now in its own `.java` file instead of a code block.

## How to run

```sh
javac DivideAndConquerAlgorithms.java
java DivideAndConquerAlgorithms
```

## What it does

**Merge sort** (split in halves, sort each, merge: T(n) = 2T(n/2) + O(n) = O(n log n)); **randomized three-way quicksort** (random pivot, split into `<`, `==`, `>`; recurses only on the smaller side so the stack stays O(log n)); **binary search** in a loop and recursively (O(log n)).

## The example

`{8,3,2,9,7,1}` sorts to `1 2 3 7 8 9`; `{4,2,4,1,3}` to `1 2 3 4 4`; 7 is at index 3, 100 is absent (-1).

## Output

```
Merge sort: 1 2 3 7 8 9
Quick sort: 1 2 3 4 4
Index of 7: 3
Recursive index of 7: 3
Absent 100: -1
```

## Questions you may be asked

- **Master theorem for merge sort?** a = 2, b = 2, f(n) = n = n^(log2 2), so case 2 gives Theta(n log n).
- **Why three-way partitioning?** Many equal keys would otherwise cause deep, lopsided recursion.
- **Is binary search divide and conquer?** Strictly it is decrease and conquer: only one half is kept.
