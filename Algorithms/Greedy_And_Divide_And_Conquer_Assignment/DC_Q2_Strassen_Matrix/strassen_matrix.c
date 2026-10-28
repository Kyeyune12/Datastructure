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
 * Note: this simple version needs n to be a power of 2 (1, 2, 4, 8, ...),
 * and n must not be bigger than MAX.
 *
 * How to run:
 *   gcc strassen_matrix.c -o strassen_matrix
 *   ./strassen_matrix
 */
#include <stdio.h>

#define MAX 4   /* size of the matrices in this example */

/* C = A + B  (only the top-left n x n part is used) */
void add(int n, int A[MAX][MAX], int B[MAX][MAX], int C[MAX][MAX]) {
    for (int i = 0; i < n; i++)
        for (int j = 0; j < n; j++)
            C[i][j] = A[i][j] + B[i][j];
}

/* C = A - B */
void subtract(int n, int A[MAX][MAX], int B[MAX][MAX], int C[MAX][MAX]) {
    for (int i = 0; i < n; i++)
        for (int j = 0; j < n; j++)
            C[i][j] = A[i][j] - B[i][j];
}

/* Copy the quarter of M starting at (row, col) into Q */
void quarter(int half, int M[MAX][MAX], int row, int col, int Q[MAX][MAX]) {
    for (int i = 0; i < half; i++)
        for (int j = 0; j < half; j++)
            Q[i][j] = M[row + i][col + j];
}

/* Paste quarter Q into M starting at (row, col) */
void paste(int half, int Q[MAX][MAX], int M[MAX][MAX], int row, int col) {
    for (int i = 0; i < half; i++)
        for (int j = 0; j < half; j++)
            M[row + i][col + j] = Q[i][j];
}

void strassen(int n, int A[MAX][MAX], int B[MAX][MAX], int C[MAX][MAX]) {
    /* BASE CASE: 1 x 1 matrix, just multiply the two numbers */
    if (n == 1) {
        C[0][0] = A[0][0] * B[0][0];
        return;
    }

    int half = n / 2;
    int A11[MAX][MAX], A12[MAX][MAX], A21[MAX][MAX], A22[MAX][MAX];
    int B11[MAX][MAX], B12[MAX][MAX], B21[MAX][MAX], B22[MAX][MAX];
    int M1[MAX][MAX], M2[MAX][MAX], M3[MAX][MAX], M4[MAX][MAX];
    int M5[MAX][MAX], M6[MAX][MAX], M7[MAX][MAX];
    int T1[MAX][MAX], T2[MAX][MAX]; /* temporary results */

    /* DIVIDE: split A and B into quarters
     *   A = | A11 A12 |     B = | B11 B12 |
     *       | A21 A22 |         | B21 B22 |
     */
    quarter(half, A, 0, 0, A11);    quarter(half, A, 0, half, A12);
    quarter(half, A, half, 0, A21); quarter(half, A, half, half, A22);
    quarter(half, B, 0, 0, B11);    quarter(half, B, 0, half, B12);
    quarter(half, B, half, 0, B21); quarter(half, B, half, half, B22);

    /* CONQUER: Strassen's 7 products (instead of 8) */
    add(half, A11, A22, T1); add(half, B11, B22, T2); strassen(half, T1, T2, M1); /* M1 = (A11+A22)(B11+B22) */
    add(half, A21, A22, T1);                          strassen(half, T1, B11, M2); /* M2 = (A21+A22)B11       */
    subtract(half, B12, B22, T2);                     strassen(half, A11, T2, M3); /* M3 = A11(B12-B22)       */
    subtract(half, B21, B11, T2);                     strassen(half, A22, T2, M4); /* M4 = A22(B21-B11)       */
    add(half, A11, A12, T1);                          strassen(half, T1, B22, M5); /* M5 = (A11+A12)B22       */
    subtract(half, A21, A11, T1); add(half, B11, B12, T2); strassen(half, T1, T2, M6); /* M6 = (A21-A11)(B11+B12) */
    subtract(half, A12, A22, T1); add(half, B21, B22, T2); strassen(half, T1, T2, M7); /* M7 = (A12-A22)(B21+B22) */

    /* COMBINE: build the four quarters of C */
    int C11[MAX][MAX], C12[MAX][MAX], C21[MAX][MAX], C22[MAX][MAX];

    add(half, M1, M4, T1); subtract(half, T1, M5, T2); add(half, T2, M7, C11); /* C11 = M1+M4-M5+M7 */
    add(half, M3, M5, C12);                                                   /* C12 = M3+M5       */
    add(half, M2, M4, C21);                                                   /* C21 = M2+M4       */
    add(half, M1, M3, T1); subtract(half, T1, M2, T2); add(half, T2, M6, C22); /* C22 = M1+M3-M2+M6 */

    paste(half, C11, C, 0, 0);
    paste(half, C12, C, 0, half);
    paste(half, C21, C, half, 0);
    paste(half, C22, C, half, half);
}

/* Ordinary multiplication, used only to check Strassen's answer */
void normalMultiply(int n, int A[MAX][MAX], int B[MAX][MAX], int C[MAX][MAX]) {
    for (int i = 0; i < n; i++)
        for (int j = 0; j < n; j++) {
            C[i][j] = 0;
            for (int k = 0; k < n; k++)
                C[i][j] += A[i][k] * B[k][j];
        }
}

void print(const char *title, int n, int M[MAX][MAX]) {
    printf("%s\n", title);
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) printf("%5d", M[i][j]);
        printf("\n");
    }
    printf("\n");
}

int main(void) {
    int n = 4;
    int A[MAX][MAX] = {
        {1, 2, 3, 4},
        {5, 6, 7, 8},
        {9, 1, 2, 3},
        {4, 5, 6, 7}
    };
    int B[MAX][MAX] = {
        {1, 0, 2, 1},
        {0, 1, 0, 2},
        {3, 1, 1, 0},
        {2, 2, 0, 1}
    };
    int C[MAX][MAX], check[MAX][MAX];

    print("Matrix A:", n, A);
    print("Matrix B:", n, B);

    strassen(n, A, B, C);
    print("A x B using Strassen:", n, C);

    normalMultiply(n, A, B, check);
    print("A x B using normal multiplication (to check):", n, check);

    int same = 1;
    for (int i = 0; i < n; i++)
        for (int j = 0; j < n; j++)
            if (C[i][j] != check[i][j]) same = 0;

    printf(same ? "Both results match!\n" : "Results do NOT match.\n");
    return 0;
}
