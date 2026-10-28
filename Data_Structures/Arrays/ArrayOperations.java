/**
 * javac ArrayOperations.java && java ArrayOperations
 *
 * Basic array operations: address calculation, merging, traversal,
 * and finding the smallest and largest value in a 2D array.
 *
 * Address formulas (B = base address, s = size of one element in bytes):
 *   1D:              B + i * s
 *   2D row-major:    B + (i * cols + j) * s
 *   2D column-major: B + (j * rows + i) * s
 *   3D:              B + (i * depth * cols + j * cols + k) * s
 */
public class ArrayOperations {

    // ---------- Address calculation ----------

    // Address of a[i] in a 1D array
    static int address1D(int base, int i, int size) {
        return base + i * size;
    }

    // Address of a[i][j] when the array is stored row by row
    // (skip i full rows, then j elements inside the row)
    static int address2DRowMajor(int base, int i, int j, int cols, int size) {
        return base + (i * cols + j) * size;
    }

    // Address of a[i][j] when the array is stored column by column
    // (skip j full columns, then i elements inside the column)
    static int address2DColumnMajor(int base, int i, int j, int rows, int size) {
        return base + (j * rows + i) * size;
    }

    // Address of a[i][j][k] in a 3D array stored row by row
    static int address3D(int base, int i, int j, int k, int depth, int cols, int size) {
        return base + (i * depth * cols + j * cols + k) * size;
    }

    // ---------- Merging ----------

    // Makes a new array: all of a, followed by all of b
    static int[] merge(int[] a, int[] b) {
        int[] result = new int[a.length + b.length];

        // copy a into the front of result
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i];
        }

        // copy b straight after a
        for (int i = 0; i < b.length; i++) {
            result[a.length + i] = b[i];
        }

        return result;
    }

    // ---------- Traversal ----------

    // Visits every element of a 1D array, left to right
    static void traverse1D(int[] a) {
        for (int i = 0; i < a.length; i++) {
            System.out.println("[" + i + "] = " + a[i]);
        }
    }

    // Visits a 2D array row by row (outer loop = rows)
    static void traverseRowFirst(int[][] a) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                System.out.println("[" + i + "][" + j + "] = " + a[i][j]);
            }
        }
    }

    // Visits a 2D array column by column (outer loop = columns)
    static void traverseColumnFirst(int[][] a) {
        for (int j = 0; j < a[0].length; j++) {
            for (int i = 0; i < a.length; i++) {
                System.out.println("[" + i + "][" + j + "] = " + a[i][j]);
            }
        }
    }

    // Visits every element of a 3D array
    static void traverse3D(int[][][] a) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                for (int k = 0; k < a[i][j].length; k++) {
                    System.out.println("[" + i + "][" + j + "][" + k + "] = " + a[i][j][k]);
                }
            }
        }
    }

    // ---------- Min and max ----------

    // Finds the smallest and largest values and where they are
    static void printMinAndMax(int[][] a) {
        // start by assuming the first element is both the min and the max
        int min = a[0][0];
        int minRow = 0;
        int minCol = 0;

        int max = a[0][0];
        int maxRow = 0;
        int maxCol = 0;

        // check every element and update min/max when we find a better one
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
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

        System.out.println("Min: " + min + " at (" + minRow + ", " + minCol + ")");
        System.out.println("Max: " + max + " at (" + maxRow + ", " + maxCol + ")");
    }

    public static void main(String[] args) {
        System.out.println("Merged:");
        int[] first = {1, 2, 3};
        int[] second = {4, 5};
        traverse1D(merge(first, second));

        int[][] matrix = {
            {3, -4, 7},
            {9, 0, 9}
        };
        System.out.println("Row-first:");
        traverseRowFirst(matrix);
        System.out.println("Column-first:");
        traverseColumnFirst(matrix);
        printMinAndMax(matrix);

        System.out.println("3D:");
        int[][][] cube = {
            {{1, 2}, {3, 4}},
            {{5, 6}, {7, 8}}
        };
        traverse3D(cube);

        System.out.println("1D address: " + address1D(1000, 3, 4));
        System.out.println("2D row-major address: " + address2DRowMajor(1000, 1, 2, 3, 4));
        System.out.println("2D column-major address: " + address2DColumnMajor(1000, 1, 2, 2, 4));
        System.out.println("3D address: " + address3D(1000, 1, 1, 1, 2, 2, 4));
    }
}
