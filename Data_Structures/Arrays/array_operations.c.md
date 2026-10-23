# array_operations.c

The C version of the array operations: address formulas, merging, 1D/2D/3D traversal and finding the minimum or maximum. It is written defensively, so every size calculation is checked for overflow and every function reports whether it succeeded.

**Language:** C11 · **Java version:** [ArrayOperations.java](ArrayOperations.java.md) · **Notes:** [MatrixMergeAnalysis.md](MatrixMergeAnalysis.md)

## How to run

```sh
gcc -std=c11 -Wall -Wextra -Wpedantic -Werror array_operations.c -o arrays
./arrays
```

It compiles cleanly even with `-Werror`, which turns every warning into an error.

## Design

- **Flat arrays.** Every function takes a plain `const int *` plus its dimensions, and works out positions itself. For example, the element at row `i`, column `j` is `a[i*cols + j]`. A C `int m[2][3]` is stored as 6 consecutive ints, so it can be passed in this way.
- **`bool` results.** Each function returns `true` on success and `false` on bad input. Results come back through an output pointer such as `size_t *out` or `Location *out`, so an error can never be confused with a real value.
- **Overflow-checked arithmetic.** `product(a, b, &out)` refuses any multiplication that would go past `SIZE_MAX`. All the address and size calculations go through it.

## What is inside

| Function | What it does | Cost |
| --- | --- | --- |
| `product(a, b, out)` *(static)* | Overflow-safe `a * b` for `size_t` | O(1) |
| `address_1d(base, i, length, bytes, out)` | `base + i*bytes`, after checking that `i < length` | O(1) |
| `address_2d(..., column_major, out)` | Turns `(i, j)` into a single position, row-major `i*cols+j` or column-major `j*rows+i`, then uses `address_1d` | O(1) |
| `address_3d(..., out)` | Turns `(i, j, k)` into the position `i*depth*cols + j*cols + k`, then uses `address_1d` | O(1) |
| `merge_arrays(a, na, b, nb, &out, &length)` | `malloc`s a new array holding `a` then `b`. **The caller must `free(*out)`.** Two empty inputs give `NULL` with length 0 | Θ(na + nb) |
| `traverse_1d`, `traverse_2d`, `traverse_3d` | Print every element with its indices. `traverse_2d` can go column-first | Θ(elements) |
| `minimum_2d`, `maximum_2d` | Fill a `Location {value, row, col}` with the smallest or largest element. On a tie, the first in row-major order wins | Θ(rows·cols) |

`extreme()` scans the matrix as one flat array and converts the best position back to a row and column with `best / cols` and `best % cols`.

## Output

```
Merged:
[0]=1
...
[4]=5
Row-first:
[0][0]=3
[0][1]=-4
...
Column-first:
[0][0]=3
[1][0]=9
...
Min -4 at (0,1); max 9 at (1,0)
[0][0][0]=1
...
[1][1][1]=8
1D address: 1012
2D address: 1020
3D address: 1028
```

These are the same results as the Java version. Here only the row-major 2D address is printed: 1000 + (1·3 + 2)·4 = 1020.

## C compared with Java

The comment at the top of the file sums up the trade-off:

- **C** gives exact control of memory layout, `sizeof` and pointer arithmetic. But it does **no bounds checking**: reading past the end of an array is undefined behaviour. You must also manage memory yourself (`malloc`/`free`).
- **Java** checks every index and frees memory automatically. That makes mistakes much less likely, at the cost of some runtime overhead and no access to real addresses.

## Notes

- A raw pointer doesn't carry its length. If a caller passes a buffer that is shorter than the stated dimensions, C cannot detect it.
- `merge_arrays` keeps nothing allocated when it fails, so a failed merge never leaks memory.
