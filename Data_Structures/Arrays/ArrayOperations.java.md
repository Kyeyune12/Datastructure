# ArrayOperations.java

Basic operations on 1D, 2D and 3D arrays in Java (merging, traversal, finding the minimum and maximum), plus the formulas that give an element's address in memory.

**Language:** Java · **C version of the same ideas:** [array_operations.c](array_operations.c.md) · **Notes:** [MatrixMergeAnalysis.md](MatrixMergeAnalysis.md)

## How to run

```sh
javac ArrayOperations.java
java ArrayOperations
```

## What is inside

All the methods are `static`, so they are called directly without making an object.

| Method | What it does | Cost |
| --- | --- | --- |
| `address1D(base, i, size)` | Address of `a[i]` | O(1) |
| `address2DRowMajor(base, i, j, cols, size)` | Address of `a[i][j]` when rows are stored one after another | O(1) |
| `address2DColumnMajor(base, i, j, rows, size)` | Address of `a[i][j]` when columns are stored one after another | O(1) |
| `address3D(base, i, j, k, depth, cols, size)` | Address of `a[i][j][k]` | O(1) |
| `merge(a, b)` | A new array holding `a` followed by `b`. This joins the arrays end to end; it does not sort them | O(a + b) |
| `traverse1D(a)` | Prints each element as `[i] = value` | O(n) |
| `traverseRowFirst(a)` | Prints a 2D array row by row | O(rows·cols) |
| `traverseColumnFirst(a)` | Prints a 2D array column by column | O(rows·cols) |
| `traverse3D(a)` | Prints every element of a 3D array | O(planes·depth·cols) |
| `printMinAndMax(a)` | Prints the smallest and largest value in a 2D array, and where each one is | O(rows·cols) |

## The address formulas

For base address **B** and element size **s** bytes (all indices start at 0):

| Layout | Address of the element |
| --- | --- |
| 1D `a[i]` | B + i·s |
| 2D row-major `a[i][j]` | B + (i·cols + j)·s |
| 2D column-major `a[i][j]` | B + (j·rows + i)·s |
| 3D `a[i][j][k]` | B + (i·depth·cols + j·cols + k)·s |

**How to read the row-major formula:** to reach `a[i][j]`, skip `i` complete rows (each has `cols` elements), then skip `j` more elements inside the row. Multiply that count by the size of one element and add it to the base address.

Row-major stores a whole row before the next row starts, as C does. Column-major stores whole columns, as Fortran and MATLAB do.

These are *conceptual* addresses for exam-style calculations. Java never shows real memory addresses.

## How `printMinAndMax` works

1. Assume the first element `a[0][0]` is both the smallest and the largest.
2. Visit every element. If it is smaller than the current minimum, remember its value, row and column. Do the same for the maximum.
3. Only a *strictly* smaller or larger value replaces the current one, so on a tie the first one found (in row order) wins.

## Output

```
Merged:
[0] = 1
[1] = 2
[2] = 3
[3] = 4
[4] = 5
Row-first:
[0][0] = 3
[0][1] = -4
[0][2] = 7
[1][0] = 9
[1][1] = 0
[1][2] = 9
Column-first:
[0][0] = 3
[1][0] = 9
[0][1] = -4
[1][1] = 0
[0][2] = 7
[1][2] = 9
Min: -4 at (0, 1)
Max: 9 at (1, 0)
3D:
[0][0][0] = 1
...
[1][1][1] = 8
1D address: 1012
2D row-major address: 1020
2D column-major address: 1020
3D address: 1028
```

How the demo's numbers come about:

- **Merged** is `{1, 2, 3}` joined with `{4, 5}`.
- **Max** is 9, which appears twice. The one at (1, 0) is reported because it is found first.
- **1D:** 1000 + 3·4 = 1012.
- **2D row-major:** 1000 + (1·3 + 2)·4 = 1020. **Column-major:** 1000 + (2·2 + 1)·4 = 1020. They match only by coincidence for this cell; other cells give different answers.
- **3D:** 1000 + (1·2·2 + 1·2 + 1)·4 = 1028.

## What changed (simplified version)

| Before | Now |
| --- | --- |
| A `Location` result class, plus separate `minimum` and `maximum` methods | One `printMinAndMax` method that tracks both in a single pass |
| `address2D(..., columnMajor)` with a true/false switch | Two separate methods, `address2DRowMajor` and `address2DColumnMajor`, so each formula is easy to see |
| `Math.multiplyExact`/`addExact` overflow checks and input validation (`index`, `columns`) | Removed. The formulas are written out plainly |
| `traverse2D(a, columnFirst)` | `traverseRowFirst` and `traverseColumnFirst` |
| One-line loops without braces | Every loop and `if` has braces and sits on its own lines, with a comment explaining each step |
