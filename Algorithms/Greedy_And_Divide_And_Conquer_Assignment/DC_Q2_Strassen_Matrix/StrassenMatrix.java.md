# StrassenMatrix.java

**Divide & Conquer Problem 2, in Java.** Multiplies two 4 × 4 matrices with **Strassen's algorithm** (7 recursive products per level instead of 8) and checks the result against ordinary multiplication. Its output is identical to [strassen_matrix.c](strassen_matrix.c.md).

**Language:** Java · **C version:** [strassen_matrix.c](strassen_matrix.c.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
javac StrassenMatrix.java
java StrassenMatrix
```

## The idea in short

Split A and B into four half-size blocks each. Ordinary block multiplication needs **8** half-size products, which gives Θ(n³). Strassen needs only **7**:

```
M1 = (A11 + A22)(B11 + B22)      C11 = M1 + M4 − M5 + M7
M2 = (A21 + A22) B11             C12 = M3 + M5
M3 = A11 (B12 − B22)             C21 = M2 + M4
M4 = A22 (B21 − B11)             C22 = M1 − M2 + M3 + M6
M5 = (A11 + A12) B22
M6 = (A21 − A11)(B11 + B12)
M7 = (A12 − A22)(B21 + B22)
```

`T(n) = 7T(n/2) + Θ(n²) = Θ(n^log₂7) ≈ Θ(n^2.81)`.

[strassen_matrix.c.md](strassen_matrix.c.md) shows why these formulas give the right answer, with a worked check of C12.

## Walking through the code

The big difference from the C version: **Java methods can return arrays, and arrays know their own size.** So every helper creates and returns a new matrix of exactly the right size, with no `MAX` and no `n` parameter.

### Helpers

```java
static int[][] add(int[][] A, int[][] B)        // returns A + B
static int[][] subtract(int[][] A, int[][] B)   // returns A − B
static int[][] quarter(int[][] M, int row, int col)  // returns the half × half block at (row, col)
static void paste(int[][] Q, int[][] M, int row, int col)  // writes block Q into M at (row, col)
static int[][] normalMultiply(int[][] A, int[][] B)  // ordinary Θ(n³) version, for checking
static void print(String title, int[][] M)
```

- The size is always read from the array itself: `int n = A.length;` or `int half = M.length / 2;`.
- `new int[n][n]` fills the array with zeros automatically. That's why `normalMultiply` can do `C[i][j] += ...` without resetting `C[i][j] = 0` first, which the C version has to do.

### `strassen(A, B)`

```java
int n = A.length;

// BASE CASE: 1 x 1 matrix, just multiply the two numbers
if (n == 1) {
    return new int[][] { { A[0][0] * B[0][0] } };
}

int half = n / 2;

// DIVIDE: split A and B into quarters
int[][] A11 = quarter(A, 0, 0);
int[][] A12 = quarter(A, 0, half);
int[][] A21 = quarter(A, half, 0);
int[][] A22 = quarter(A, half, half);
// ... same for B

int[][] M1 = strassen(add(A11, A22), add(B11, B22));               // CONQUER
int[][] M2 = strassen(add(A21, A22), B11);
...
int[][] C11 = add(subtract(add(M1, M4), M5), M7);                  // COMBINE
int[][] C12 = add(M3, M5);
int[][] C21 = add(M2, M4);
int[][] C22 = add(subtract(add(M1, M3), M2), M6);

int[][] C = new int[n][n];
paste(C11, C, 0, 0);
paste(C12, C, 0, half);
paste(C21, C, half, 0);
paste(C22, C, half, half);
return C;
```

- **Base case:** a 1 × 1 matrix is returned as a new 1 × 1 array literal.
- **Nested calls** like `add(subtract(add(M1, M4), M5), M7)` read inside-out: `M1 + M4`, then `− M5`, then `+ M7`. Java returns new arrays, so the expression can be written in one line. C has to go step by step through scratch matrices `T1` and `T2`.
- The arguments to each recursive `strassen` call (for example `add(A11, A22)`) are built on the spot and become garbage after the call. Java's garbage collector frees them.

### `main`

Builds A and B, prints them, computes `strassen(A, B)` and `normalMultiply(A, B)`, and compares them with the helper `equal(C, check)`:

```java
static boolean equal(int[][] A, int[][] B) {
    for (int i = 0; i < A.length; i++) {
        for (int j = 0; j < A.length; j++) {
            if (A[i][j] != B[i][j]) {
                return false;
            }
        }
    }
    return true;
}
```

It checks every cell and stops at the first difference. Plain `==` or `equals` on arrays only checks whether they are the *same object* in memory, so it would say `false` even when every number matches.

## Trace (top level)

| Product | 2 × 2 result |
|---|---|
| M1 | `[[6, 10], [22, 26]]` |
| M2 | `[[11, 4], [10, 12]]` |
| M3 | `[[1, 3], [5, 11]]` |
| M4 | `[[10, 5], [26, 13]]` |
| M5 | `[[4, 6], [12, 14]]` |
| M6 | `[[24, 5], [-3, -4]]` |
| M7 | `[[6, 4], [6, 4]]` |

C11 = `[[18, 13], [42, 29]]`, C12 = `[[5, 9], [17, 25]]`, C21 = `[[21, 9], [36, 25]]`, C22 = `[[20, 14], [14, 21]]`.

In total: 7 × 7 = **49** scalar multiplications instead of 64.

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

| | Value |
|---|---|
| Time | Θ(n^log₂7) ≈ Θ(n^2.81) (Master Theorem case 1: a = 7, b = 2, f(n) = Θ(n²)) |
| Space | Θ(n²): each level allocates blocks of its own size, and the sizes shrink geometrically, so the memory live at any one time is O(n²) |

## Limitations

- n must be a **power of 2**. Otherwise `n / 2` blocks don't tile the matrix. Pad with zeros to fix it.
- Strassen only pays off for large matrices. For small ones, the extra additions and array allocations make it slower than the triple loop.

## C vs Java differences

| | C | Java |
|---|---|---|
| Matrix size | fixed `MAX × MAX` arrays + an `n` parameter | arrays sized exactly; `A.length` gives n |
| Returning results | output parameter `C` | methods `return` new arrays |
| Multi-term sums | step by step through `T1`, `T2` | nested calls `add(subtract(add(...)))` |
| Memory | stack arrays, freed on return | heap arrays, freed by the garbage collector |
| Comparing results | double loop with a `same` flag | double loop in `equal`, returning `false` at the first difference |
| Zeroing in `normalMultiply` | `C[i][j] = 0;` needed | `new int[n][n]` is already zero |

## What changed (simplified version)

| Before | Now | Why |
|---|---|---|
| `java.util.Arrays.deepEquals(C, check)` (a library class named in full instead of imported) | A small `equal(A, B)` method with two loops | No library classes; the comparison is visible |
| Nested loops without braces in `add`, `subtract`, `quarter`, `paste` and `normalMultiply` | Braces on every loop | Easier to read |
| Two quarters declared per line, e.g. `int[][] A11 = quarter(A, 0, 0), A12 = quarter(A, 0, half);` | One quarter per line | Easier to match each name with its position |
| `System.out.println(same ? "..." : "...")` | An `if`/`else` | Easier to read |

The algorithm and the output are exactly the same as before.
