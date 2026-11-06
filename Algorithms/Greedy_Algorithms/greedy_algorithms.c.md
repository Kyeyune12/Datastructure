# greedy_algorithms.c

The **C version** of [`GreedyAlgorithms.java`](GreedyAlgorithms.java.md); same algorithms, same output.

## How to run

```sh
gcc -std=c11 -Wall -Wextra greedy_algorithms.c -o greedy
./greedy
```

## What is different from Java

Arrays of `struct` replace the `Item` and `Activity` classes. The sort works on an array of indices (`order[]`) so the original positions are kept, and the temporary arrays are `malloc`ed and `free`d. Java's `Double.toString` is imitated with `print_double` (`240.0`, `0.6666666666666666`).

## Output

```
Optimal fractional value: 240.0
Item 0: 1.0
Item 1: 1.0
Item 2: 0.6666666666666666
Selected original indices: 0 3 4 
```

## Questions you may be asked

- **Why sort indices instead of the items?** The answer must be reported in the original order (`Item 0`, `Item 1`, ...).
- **Why not `qsort`?** The repository's policy is no library shortcuts for the algorithm being studied.
