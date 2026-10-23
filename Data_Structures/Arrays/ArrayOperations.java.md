# ArrayOperations.java

Basic operations on 1D, 2D and 3D arrays in Java (merging, traversal and finding the minimum or maximum), plus the formulas that give an element's address in memory.

**Language:** Java · **C version of the same ideas:** [array_operations.c](array_operations.c.md) · **Notes:** [MatrixMergeAnalysis.md](MatrixMergeAnalysis.md)

## How to run

```sh
javac ArrayOperations.java
java ArrayOperations
```

## What is inside

The class is `final` and its constructor is private, so it is only a collection of `static` methods and can never be instantiated.

| Member | What it does | Cost |
| --- | --- | --- |
| `Location` | A small result class that holds a `value` and the `row` and `col` where it was found. Its `toString()` prints `-4 at (0, 1)` | — |
| `index(i, n)` *(private)* | Throws `IndexOutOfBoundsException` unless 0 ≤ i < n | O(1) |
| `columns(a)` *(private)* | Checks that a 2D array is non-empty and rectangular (every row the same length), then returns the number of columns | O(rows) |
| `address1D(base, i, length, bytes)` | Address of `a[i]`, i.e. `base + i*bytes` | O(1) |
| `address2D(base, i, j, rows, cols, bytes, columnMajor)` | Address of `a[i][j]` in row-major or column-major order | O(1) |
| `address3D(base, i, j, k, planes, depth, cols, bytes)` | Address of `a[i][j][k]` | O(1) |
| `merge(a, b)` | A new array holding `a` followed by `b`. This joins the arrays end to end; it does not sort them | O(a + b) |
| `traverse1D(a)` | Prints each element as `[i]=value` | O(n) |
| `traverse2D(a, columnFirst)` | Prints every element, row by row or column by column | O(rows·cols) |
| `traverse3D(a)` | Prints every element of a 3D array, after checking that every plane has the same shape | O(planes·depth·cols) |
| `minimum(a)` / `maximum(a)` | The smallest or largest value in a 2D array and where it is. On a tie, the first one in row-major order wins | O(rows·cols) |

## The address formulas

For base address **B** and element size **s** bytes (all indices start at 0):

| Layout | Address of the element |
| --- | --- |
| 1D `a[i]` | B + i·s |
| 2D row-major `a[i][j]` | B + (i·cols + j)·s |
| 2D column-major `a[i][j]` | B + (j·rows + i)·s |
| 3D `a[i][j][k]`, shape [planes][depth][cols] | B + (i·depth·cols + j·cols + k)·s |

Row-major means a whole row is stored before the next row starts, as C does. Column-major stores whole columns, as Fortran and MATLAB do. The methods use `Math.multiplyExact` and `Math.addExact`, so a result too large for a `long` throws an exception instead of silently wrapping round to a wrong number.

These are *conceptual* addresses for exam-style calculations. Java never shows real memory addresses. A Java `int[][]` is actually an array of separate row arrays, which need not sit next to each other in memory.

## Output

```
Merged:
[0]=1
[1]=2
[2]=3
[3]=4
[4]=5
Row-first:
[0][0]=3
[0][1]=-4
[0][2]=7
[1][0]=9
[1][1]=0
[1][2]=9
Column-first:
[0][0]=3
[1][0]=9
[0][1]=-4
[1][1]=0
[0][2]=7
[1][2]=9
Min: -4 at (0, 1); max: 9 at (1, 0)
[0][0][0]=1
...
[1][1][1]=8
1D B=1000 i=3 s=4: 1012
2D row-major (1,2): 1020
2D column-major (1,2): 1020
3D (1,1,1): 1028
```

How the demo's numbers come about:

- **Merged** is `{1,2,3}` joined with `{4,5}`.
- **Max** is 9, which appears twice. The one at (1, 0) is reported because it comes first in row-major order.
- **1D:** 1000 + 3·4 = 1012.
- **2D, row-major and column-major (both 1020):** row-major gives 1000 + (1·3 + 2)·4 = 1020. Column-major gives 1000 + (2·2 + 1)·4 = 1020. They match only by coincidence for this cell of a 2×3 array; other cells give different answers.
- **3D:** in a 2×2×2 array, 1000 + (1·4 + 1·2 + 1)·4 = 1028.

## Notes

- Bad input fails straight away with a clear exception: a `null` array, an empty matrix, a ragged matrix, an index out of range, or overflow.
- `traverse3D` checks the whole cube *before* printing anything, so bad input never produces half an output.
- Visiting the elements column-first changes only the order of visits. It does not change how the array is stored.
