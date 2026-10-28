/*
 * DIVIDE & CONQUER PROBLEM 2: Strassen's Matrix Multiplication
 *
 * Normal matrix multiplication of two n x n matrices splits them into
 * four quarters and needs 8 smaller multiplications.
 * Strassen found a clever trick that needs only 7 multiplications
 * (plus some extra additions/subtractions, which are cheap).
 * Fewer multiplications at every level = faster for big matrices.
 *
 * Divide & Conquer idea:
 *   DIVIDE : split A and B into four (n/2 x n/2) quarters each.
 *   CONQUER: compute 7 products M1..M7 recursively.
 *   COMBINE: add/subtract the M's to build the four quarters of the answer.
 *
 * Note: this simple version needs n to be a power of 2 (1, 2, 4, 8, ...).
 *
 * How to run:
 *   javac StrassenMatrix.java
 *   java StrassenMatrix
 */
public class StrassenMatrix {

    // C = A + B
    static int[][] add(int[][] A, int[][] B) {
        int n = A.length;
        int[][] C = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = A[i][j] + B[i][j];
            }
        }
        return C;
    }

    // C = A - B
    static int[][] subtract(int[][] A, int[][] B) {
        int n = A.length;
        int[][] C = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = A[i][j] - B[i][j];
            }
        }
        return C;
    }

    // Copy one quarter of matrix M, starting at (row, col), into a new matrix
    static int[][] quarter(int[][] M, int row, int col) {
        int half = M.length / 2;
        int[][] Q = new int[half][half];
        for (int i = 0; i < half; i++) {
            for (int j = 0; j < half; j++) {
                Q[i][j] = M[row + i][col + j];
            }
        }
        return Q;
    }

    // Paste quarter Q into matrix M, starting at (row, col)
    static void paste(int[][] Q, int[][] M, int row, int col) {
        for (int i = 0; i < Q.length; i++) {
            for (int j = 0; j < Q.length; j++) {
                M[row + i][col + j] = Q[i][j];
            }
        }
    }

    static int[][] strassen(int[][] A, int[][] B) {
        int n = A.length;

        // BASE CASE: 1 x 1 matrix, just multiply the two numbers
        if (n == 1) {
            return new int[][] { { A[0][0] * B[0][0] } };
        }

        int half = n / 2;

        // DIVIDE: split A and B into quarters
        //   A = | A11 A12 |     B = | B11 B12 |
        //       | A21 A22 |         | B21 B22 |
        int[][] A11 = quarter(A, 0, 0);
        int[][] A12 = quarter(A, 0, half);
        int[][] A21 = quarter(A, half, 0);
        int[][] A22 = quarter(A, half, half);

        int[][] B11 = quarter(B, 0, 0);
        int[][] B12 = quarter(B, 0, half);
        int[][] B21 = quarter(B, half, 0);
        int[][] B22 = quarter(B, half, half);

        // CONQUER: Strassen's 7 products (instead of 8)
        int[][] M1 = strassen(add(A11, A22), add(B11, B22));
        int[][] M2 = strassen(add(A21, A22), B11);
        int[][] M3 = strassen(A11, subtract(B12, B22));
        int[][] M4 = strassen(A22, subtract(B21, B11));
        int[][] M5 = strassen(add(A11, A12), B22);
        int[][] M6 = strassen(subtract(A21, A11), add(B11, B12));
        int[][] M7 = strassen(subtract(A12, A22), add(B21, B22));

        // COMBINE: build the four quarters of the result C
        int[][] C11 = add(subtract(add(M1, M4), M5), M7);
        int[][] C12 = add(M3, M5);
        int[][] C21 = add(M2, M4);
        int[][] C22 = add(subtract(add(M1, M3), M2), M6);

        int[][] C = new int[n][n];
        paste(C11, C, 0, 0);
        paste(C12, C, 0, half);
        paste(C21, C, half, 0);
        paste(C22, C, half, half);
        return C;
    }

    // Ordinary multiplication, used only to check Strassen's answer
    static int[][] normalMultiply(int[][] A, int[][] B) {
        int n = A.length;
        int[][] C = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        return C;
    }

    // true if every cell of A equals the same cell of B
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

    static void print(String title, int[][] M) {
        System.out.println(title);
        for (int[] row : M) {
            for (int value : row) {
                System.out.printf("%5d", value);
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void main(String[] args) {
        int[][] A = {
            {1, 2, 3, 4},
            {5, 6, 7, 8},
            {9, 1, 2, 3},
            {4, 5, 6, 7}
        };
        int[][] B = {
            {1, 0, 2, 1},
            {0, 1, 0, 2},
            {3, 1, 1, 0},
            {2, 2, 0, 1}
        };

        print("Matrix A:", A);
        print("Matrix B:", B);

        int[][] C = strassen(A, B);
        print("A x B using Strassen:", C);

        int[][] check = normalMultiply(A, B);
        print("A x B using normal multiplication (to check):", check);

        boolean same = equal(C, check);
        if (same) {
            System.out.println("Both results match!");
        } else {
            System.out.println("Results do NOT match.");
        }
    }
}
