#include <stdio.h>

// Basic array operations: address calculation, merging, traversal,
// and finding the smallest and largest value in a 2D array.
//
// Address formulas (B = base address, s = size of one element in bytes):
//   1D:              B + i * s
//   2D row-major:    B + (i * cols + j) * s
//   2D column-major: B + (j * rows + i) * s
//   3D:              B + (i * depth * cols + j * cols + k) * s
//
// Build: gcc array_operations.c -o arrays
// Run:   ./arrays

#define ROWS 2
#define COLS 3

// ---------- Address calculation ----------

// Address of a[i] in a 1D array
int address1D(int base, int i, int size) {
    return base + i * size;
}

// Address of a[i][j] when the array is stored row by row
// (skip i full rows, then j elements inside the row)
int address2DRowMajor(int base, int i, int j, int cols, int size) {
    return base + (i * cols + j) * size;
}

// Address of a[i][j] when the array is stored column by column
// (skip j full columns, then i elements inside the column)
int address2DColumnMajor(int base, int i, int j, int rows, int size) {
    return base + (j * rows + i) * size;
}

// Address of a[i][j][k] in a 3D array stored row by row
int address3D(int base, int i, int j, int k, int depth, int cols, int size) {
    return base + (i * depth * cols + j * cols + k) * size;
}

// ---------- Merging ----------

// Copies all of a, then all of b, into result (result must hold na + nb values)
void merge(int a[], int na, int b[], int nb, int result[]) {
    for (int i = 0; i < na; i++) {
        result[i] = a[i];
    }
    for (int i = 0; i < nb; i++) {
        result[na + i] = b[i];
    }
}

// ---------- Traversal ----------

// Visits every element of a 1D array, left to right
void traverse1D(int a[], int n) {
    for (int i = 0; i < n; i++) {
        printf("[%d] = %d\n", i, a[i]);
    }
}

// Visits a 2D array row by row (outer loop = rows)
void traverseRowFirst(int a[ROWS][COLS]) {
    for (int i = 0; i < ROWS; i++) {
        for (int j = 0; j < COLS; j++) {
            printf("[%d][%d] = %d\n", i, j, a[i][j]);
        }
    }
}

// Visits a 2D array column by column (outer loop = columns)
void traverseColumnFirst(int a[ROWS][COLS]) {
    for (int j = 0; j < COLS; j++) {
        for (int i = 0; i < ROWS; i++) {
            printf("[%d][%d] = %d\n", i, j, a[i][j]);
        }
    }
}

// Visits every element of a 2 x 2 x 2 array
void traverse3D(int a[2][2][2]) {
    for (int i = 0; i < 2; i++) {
        for (int j = 0; j < 2; j++) {
            for (int k = 0; k < 2; k++) {
                printf("[%d][%d][%d] = %d\n", i, j, k, a[i][j][k]);
            }
        }
    }
}

// ---------- Min and max ----------

// Finds the smallest and largest values and where they are
void printMinAndMax(int a[ROWS][COLS]) {
    // start by assuming the first element is both the min and the max
    int min = a[0][0];
    int minRow = 0;
    int minCol = 0;

    int max = a[0][0];
    int maxRow = 0;
    int maxCol = 0;

    // check every element and update min/max when we find a better one
    for (int i = 0; i < ROWS; i++) {
        for (int j = 0; j < COLS; j++) {
            if (a[i][j] < min) {
                min = a[i][j];
                minRow = i;
                minCol = j;
            }
            if (a[i][j] > max) {
                max = a[i][j];
                maxRow = i;
                maxCol = j;
            }
        }
    }

    printf("Min: %d at (%d, %d)\n", min, minRow, minCol);
    printf("Max: %d at (%d, %d)\n", max, maxRow, maxCol);
}

int main() {
    printf("Merged:\n");
    int first[3] = {1, 2, 3};
    int second[2] = {4, 5};
    int merged[5];
    merge(first, 3, second, 2, merged);
    traverse1D(merged, 5);

    int matrix[ROWS][COLS] = {
        {3, -4, 7},
        {9, 0, 9}
    };
    printf("Row-first:\n");
    traverseRowFirst(matrix);
    printf("Column-first:\n");
    traverseColumnFirst(matrix);
    printMinAndMax(matrix);

    printf("3D:\n");
    int cube[2][2][2] = {
        {{1, 2}, {3, 4}},
        {{5, 6}, {7, 8}}
    };
    traverse3D(cube);

    printf("1D address: %d\n", address1D(1000, 3, 4));
    printf("2D row-major address: %d\n", address2DRowMajor(1000, 1, 2, 3, 4));
    printf("2D column-major address: %d\n", address2DColumnMajor(1000, 1, 2, 2, 4));
    printf("3D address: %d\n", address3D(1000, 1, 1, 1, 2, 2, 4));

    return 0;
}
