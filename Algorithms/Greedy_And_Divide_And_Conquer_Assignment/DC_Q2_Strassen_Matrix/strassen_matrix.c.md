# strassen_matrix.c

**Divide & Conquer Problem 2, in C.** Multiplies two 4 × 4 matrices with **Strassen's algorithm**, which uses 7 recursive multiplications per level instead of 8. It then checks the answer against ordinary multiplication. Its output is identical to [StrassenMatrix.java](StrassenMatrix.java.md).

**Language:** C · **Java version:** [StrassenMatrix.java](StrassenMatrix.java.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
gcc strassen_matrix.c -o strassen_matrix
./strassen_matrix
```

## Background: why 7 instead of 8 matters

Split each n × n matrix into four n/2 × n/2 blocks:

```
A = | A11 A12 |    B = | B11 B12 |    C = A·B = | C11 C12 |
    | A21 A22 |        | B21 B22 |              | C21 C22 |
```

The ordinary block formula is `C11 = A11·B11 + A12·B21`, and so on. That's **8** half-size multiplications, giving `T(n) = 8T(n/2) + Θ(n²) = Θ(n³)`. Recursion alone saves nothing.

Strassen (1969) found 7 products, **M1 … M7**, whose sums and differences give all four blocks of C. Additions cost only Θ(n²), while multiplications are recursive and expensive. Saving one at every level gives

```
T(n) = 7T(n/2) + Θ(n²)  →  Θ(n^log₂7) ≈ Θ(n^2.81)
```

## Strassen's formulas (exactly as in the code)

```
M1 = (A11 + A22)(B11 + B22)
M2 = (A21 + A22) B11
M3 = A11 (B12 − B22)
M4 = A22 (B21 − B11)
M5 = (A11 + A12) B22
M6 = (A21 − A11)(B11 + B12)
M7 = (A12 − A22)(B21 + B22)

C11 = M1 + M4 − M5 + M7
C12 = M3 + M5
C21 = M2 + M4
C22 = M1 − M2 + M3 + M6
```

**Check one block.** `C12 = M3 + M5 = A11·B12 − A11·B22 + A11·B22 + A12·B22 = A11·B12 + A12·B22`, which is exactly the ordinary formula. The other three blocks expand the same way, with the extra terms cancelling.

## Walking through the code

### Fixed-size arrays

```c
#define MAX 4
void add(int n, int A[MAX][MAX], int B[MAX][MAX], int C[MAX][MAX]);
```

C can't easily pass 2-D arrays of varying size, so every matrix is declared `MAX × MAX` (4 × 4). The parameter `n` says how much of it, the top-left `n × n` corner, is actually in use. A 2 × 2 block at the second level lives in the corner of a 4 × 4 array.

### Helper functions

| Function | What it does |
|---|---|
| `add(n, A, B, C)` | `C = A + B` on the top-left n × n part |
| `subtract(n, A, B, C)` | `C = A − B` |
| `quarter(half, M, row, col, Q)` | copies the half × half block of M starting at `(row, col)` into Q. Used to cut out A11, A12, … |
| `paste(half, Q, M, row, col)` | the reverse: writes block Q into M at `(row, col)`. Used to assemble C |
| `normalMultiply(n, A, B, C)` | the ordinary triple loop, Θ(n³), used only to **check** Strassen |
| `print(title, n, M)` | prints a matrix with `%5d`, so columns line up |

Results are written into an output parameter (`C`) because C functions can't return arrays.

### `strassen(n, A, B, C)` step by step

**1. Base case**

```c
if (n == 1) { C[0][0] = A[0][0] * B[0][0]; return; }
```

A 1 × 1 matrix is just one number, so multiply directly.

**2. Divide**

```c
int half = n / 2;
quarter(half, A, 0, 0, A11);    quarter(half, A, 0, half, A12);
quarter(half, A, half, 0, A21); quarter(half, A, half, half, A22);
/* same for B */
```

The four blocks start at `(0,0)`, `(0,half)`, `(half,0)` and `(half,half)`.

**3. Conquer — the 7 recursive products**

```c
add(half, A11, A22, T1); add(half, B11, B22, T2); strassen(half, T1, T2, M1);
add(half, A21, A22, T1);                          strassen(half, T1, B11, M2);
...
```

`T1` and `T2` are scratch matrices that hold a sum or difference just long enough to pass it into the recursive call. They're reused for every product, because each `strassen` call finishes before the next line overwrites them.

**4. Combine**

```c
add(half, M1, M4, T1); subtract(half, T1, M5, T2); add(half, T2, M7, C11); /* C11 = M1+M4-M5+M7 */
add(half, M3, M5, C12);
add(half, M2, M4, C21);
add(half, M1, M3, T1); subtract(half, T1, M2, T2); add(half, T2, M6, C22); /* C22 = M1+M3-M2+M6 */
paste(half, C11, C, 0, 0);  paste(half, C12, C, 0, half);
paste(half, C21, C, half, 0); paste(half, C22, C, half, half);
```

Each C operation takes only two operands, so a 4-term sum is built in steps through `T1` and `T2`.

### `main` — run and verify

Runs `strassen` on A and B, runs `normalMultiply` on the same inputs, compares the results element by element, and prints `Both results match!` if every entry is equal.

## Trace (top level, n = 4, half = 2)

Blocks of the inputs:

```
A11 = |1 2|  A12 = |3 4|  A21 = |9 1|  A22 = |2 3|
      |5 6|        |7 8|        |4 5|        |6 7|

B11 = |1 0|  B12 = |2 1|  B21 = |3 1|  B22 = |1 0|
      |0 1|        |0 2|        |2 2|        |0 1|
```

The seven products, each a 2 × 2 Strassen call:

| Product | Result |
|---|---|
| M1 = (A11+A22)(B11+B22) | `[[6, 10], [22, 26]]` |
| M2 = (A21+A22) B11 | `[[11, 4], [10, 12]]` |
| M3 = A11 (B12−B22) | `[[1, 3], [5, 11]]` |
| M4 = A22 (B21−B11) | `[[10, 5], [26, 13]]` |
| M5 = (A11+A12) B22 | `[[4, 6], [12, 14]]` |
| M6 = (A21−A11)(B11+B12) | `[[24, 5], [-3, -4]]` |
| M7 = (A12−A22)(B21+B22) | `[[6, 4], [6, 4]]` |

Combining them:

| Block | Formula | Result |
|---|---|---|
| C11 | M1 + M4 − M5 + M7 | `[[18, 13], [42, 29]]` |
| C12 | M3 + M5 | `[[5, 9], [17, 25]]` |
| C21 | M2 + M4 | `[[21, 9], [36, 25]]` |
| C22 | M1 − M2 + M3 + M6 | `[[20, 14], [14, 21]]` |

Pasted together, these give the final matrix below. Each 2 × 2 call makes 7 calls of size 1, so the whole run does **7 × 7 = 49** scalar multiplications instead of 4³ = 64.

## Output

```
Matrix A:
    1    2    3    4
    5    6    7    8
    9    1    2    3
    4    5    6    7

Matrix B:
    1    0    2    1
    0    1    0    2
    3    1    1    0
    2    2    0    1

A x B using Strassen:
   18   13    5    9
   42   29   17   25
   21    9   20   14
   36   25   14   21

A x B using normal multiplication (to check):
   18   13    5    9
   42   29   17   25
   21    9   20   14
   36   25   14   21

Both results match!
```

## Complexity

| | Strassen | Ordinary |
|---|---|---|
| Recurrence | T(n) = 7T(n/2) + Θ(n²) | T(n) = 8T(n/2) + Θ(n²) |
| Master Theorem | a = 7, b = 2, n^log₂7 ≈ n^2.81 dominates n² → case 1 | n³ dominates → case 1 |
| Time | **Θ(n^2.81)** | Θ(n³) |
| Scalar multiplications | 7^log₂n = n^2.81 (49 for n = 4) | n³ (64 for n = 4) |

**Space in this C version:** every call declares 21 local `MAX × MAX` arrays (4 + 4 quarters, M1–M7, T1–T2, C11–C22) on the stack, and the recursion is log₂n + 1 levels deep. For MAX = 4 that's tiny. For large MAX the stack would overflow, and real implementations allocate blocks of the correct size with `malloc`.

## Limitations and notes

- **n must be a power of 2** (1, 2, 4, 8, …) and **at most MAX**. Other sizes would need padding with zeros up to the next power of 2.
- For small matrices Strassen is actually **slower** than the triple loop, because of all the extra additions and copying. Practical libraries switch to the ordinary method below a cutoff size (often around 64–128).
- With `int` there is no rounding error. With floating-point numbers, Strassen is slightly less numerically stable than ordinary multiplication.
