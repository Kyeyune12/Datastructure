# divide_and_conquer_algorithms.c

The **C version** of [`DivideAndConquerAlgorithms.java`](DivideAndConquerAlgorithms.java.md); same output.

## How to run

```sh
gcc -std=c11 -Wall -Wextra divide_and_conquer_algorithms.c -o dc
./dc
```

## What is different from Java

Arrays are passed as pointer plus length (C arrays do not know their size). The temporary merge array is `malloc`ed and `free`d. `rand()` replaces `java.util.Random`; the pivot is random, but the sorted result is the same for every seed.

## Output

```
Merge sort: 1 2 3 7 8 9
Quick sort: 1 2 3 4 4
Index of 7: 3
Recursive index of 7: 3
Absent 100: -1
```

## Questions you may be asked

- **Why `lo + (hi - lo) / 2`?** `(lo + hi) / 2` can overflow `int` for huge arrays.
- **Worst case of quicksort?** O(n^2), but a random pivot makes it very unlikely: expected O(n log n).
