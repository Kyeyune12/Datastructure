# array_operations.c

Basic operations on 1D, 2D and 3D arrays in C: address calculation, merging, traversal, and finding the smallest and largest value. It is the **same program as [ArrayOperations.java](ArrayOperations.java.md)**, with the same function names and the same output.

**Notes:** [MatrixMergeAnalysis.md](MatrixMergeAnalysis.md)

## How to run

```sh
gcc array_operations.c -o arrays
./arrays
```

## What is inside

| Function | What it does | Cost |
| --- | --- | --- |
| `address1D(base, i, size)` | Address of `a[i]` | O(1) |
| `address2DRowMajor(base, i, j, cols, size)` | Address of `a[i][j]` when rows are stored one after another | O(1) |
| `address2DColumnMajor(base, i, j, rows, size)` | Address of `a[i][j]` when columns are stored one after another | O(1) |
| `address3D(base, i, j, k, depth, cols, size)` | Address of `a[i][j][k]` | O(1) |
| `merge(a, na, b, nb, result)` | Copies `a` then `b` into `result` (joins end to end; it does not sort) | O(na + nb) |
| `traverse1D(a, n)` | Prints each element as `[i] = value` | O(n) |
| `traverseRowFirst(a)` / `traverseColumnFirst(a)` | Prints a 2D array row by row / column by column | O(rows · cols) |
| `traverse3D(a)` | Prints every element of a 2 × 2 × 2 array | O(8) |
| `printMinAndMax(a)` | Smallest and largest value in a 2D array and where each one is | O(rows · cols) |

## The address formulas

For base address **B** and element size **s** bytes (indexes start at 0):

| Layout | Address |
| --- | --- |
| 1D `a[i]` | B + i·s |
| 2D row-major `a[i][j]` | B + (i·cols + j)·s |
| 2D column-major `a[i][j]` | B + (j·rows + i)·s |
| 3D `a[i][j][k]` | B + (i·depth·cols + j·cols + k)·s |

Example from the program: `address2DRowMajor(1000, 1, 2, 3, 4)` = 1000 + (1·3 + 2)·4 = **1020**.

## Output (end)

```
Min: -4 at (0, 1)
Max: 9 at (1, 0)
1D address: 1012
2D row-major address: 1020
2D column-major address: 1020
3D address: 1028
```

## Questions you may be asked

- **Which layout does C use?** Row-major: `int m[2][3]` is stored as row 0, then row 1. Fortran and MATLAB use column-major.
- **Why does the max say (1, 0) when 9 also appears at (1, 2)?** The test is `>`, not `>=`, so the first 9 found is kept.
- **Why must the caller pass `result` into `merge`?** A C function cannot return a whole array. The caller makes an array big enough (`na + nb`) and the function fills it.
- **Why is row-first traversal usually faster?** It reads memory in the order it is stored, so the CPU cache is used well.
