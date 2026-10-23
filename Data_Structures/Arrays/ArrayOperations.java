import java.util.Objects;

/**
 * Build: javac ArrayOperations.java ; run: java ArrayOperations
 * Prefer Java for coursework and application code: bounds checks, definite
 * initialization and garbage collection reduce memory errors. C is preferable
 * when exact layout, pointer arithmetic or embedded-memory control is required.
 * C requires explicit lifetime/allocation management; invalid indexing is undefined
 * behavior. Java pays runtime/object overhead and gives no raw object addresses.
 *
 * Array axioms: an array object has fixed length, a uniform declared component
 * type, and constant-time indexed access. A dynamic array grows by allocating a
 * replacement. C rectangular arrays have contiguous elements. Java int[][] is an
 * array of references to separate int[] objects: rows may be ragged or null, and
 * a flat multi-dimensional physical layout is NOT guaranteed by the language.
 * Reference arrays can contain different subclasses of their declared type.
 *
 * Flat, zero-based conceptual addresses (s = element size):
 * 1D: B + i*s; 2D row-major: B + (i*cols+j)*s;
 * 2D column-major: B + (j*rows+i)*s;
 * 3D [planes][depth][cols]: B + (i*depth*cols+j*cols+k)*s.
 * Subtract lower bounds first for arrays whose mathematical indexing starts
 * elsewhere. These methods calculate offsets, not Java object addresses.
 * Traversal order does not change the storage layout.
 * All methods are single-threaded; callers must synchronize shared mutations.
 */
public final class ArrayOperations {
    private ArrayOperations() { }
    public static final class Location {
        public final int value, row, col;
        private Location(int value, int row, int col) {
            this.value = value; this.row = row; this.col = col;
        }
        @Override public String toString() {
            return value + " at (" + row + ", " + col + ")";
        }
    }
    private static void index(int i, int n) {
        if (i < 0 || i >= n) throw new IndexOutOfBoundsException("index " + i);
    }
    private static int columns(int[][] a) {
        Objects.requireNonNull(a, "matrix");
        if (a.length == 0) throw new IllegalArgumentException("empty matrix");
        Objects.requireNonNull(a[0], "row");
        int cols = a[0].length;
        if (cols == 0) throw new IllegalArgumentException("empty row");
        for (int[] row : a)
            if (row == null || row.length != cols)
                throw new IllegalArgumentException("matrix must be rectangular");
        return cols;
    }
    public static long address1D(long base, int i, int length, int bytes) {
        index(i, length);
        if (base < 0 || bytes <= 0) throw new IllegalArgumentException("base/size");
        return Math.addExact(base, Math.multiplyExact((long) i, bytes));
    }
    public static long address2D(long base, int i, int j, int rows, int cols,
                                 int bytes, boolean columnMajor) {
        index(i, rows); index(j, cols);
        if (base < 0 || bytes <= 0) throw new IllegalArgumentException("base/size");
        long offset = columnMajor ? (long) j * rows + i : (long) i * cols + j;
        return Math.addExact(base, Math.multiplyExact(offset, bytes));
    }
    public static long address3D(long base, int i, int j, int k,
                                 int planes, int depth, int cols, int bytes) {
        index(i, planes); index(j, depth); index(k, cols);
        if (base < 0 || bytes <= 0) throw new IllegalArgumentException("base/size");
        long offset = Math.addExact(Math.multiplyExact((long) i, depth), j);
        offset = Math.addExact(Math.multiplyExact(offset, cols), k);
        return Math.addExact(base, Math.multiplyExact(offset, bytes));
    }
    /** Concatenation, not sorted merging. Output O(a.length+b.length). */
    public static int[] merge(int[] a, int[] b) {
        Objects.requireNonNull(a, "a"); Objects.requireNonNull(b, "b");
        int[] out = new int[Math.addExact(a.length, b.length)];
        for (int i=0; i<a.length; i++) out[i]=a[i];
        for (int i=0; i<b.length; i++) out[a.length+i]=b[i];
        return out;
    }
    public static void traverse1D(int[] a) {
        Objects.requireNonNull(a, "a");
        for (int i = 0; i < a.length; i++) System.out.println("["+i+"]="+a[i]);
    }
    public static void traverse2D(int[][] a, boolean columnFirst) {
        int cols = columns(a);
        if (columnFirst) {
            for (int j=0; j<cols; j++) for (int i=0; i<a.length; i++)
                System.out.println("["+i+"]["+j+"]="+a[i][j]);
        } else {
            for (int i=0; i<a.length; i++) for (int j=0; j<cols; j++)
                System.out.println("["+i+"]["+j+"]="+a[i][j]);
        }
    }
    public static void traverse3D(int[][][] a) {
        Objects.requireNonNull(a, "a");
        // Validate completely before printing to avoid partial output on bad input.
        if (a.length == 0) throw new IllegalArgumentException("empty cube");
        int cols = columns(a[0]), depth = a[0].length;
        for (int[][] plane : a)
            if (columns(plane) != cols || plane.length != depth)
                throw new IllegalArgumentException("cube must be rectangular");
        for (int i=0; i<a.length; i++) for (int j=0; j<depth; j++)
            for (int k=0; k<cols; k++)
                System.out.println("["+i+"]["+j+"]["+k+"]="+a[i][j][k]);
    }
    /** Ties return the first occurrence in row-major order. */
    public static Location minimum(int[][] a) { return extreme(a, true); }
    public static Location maximum(int[][] a) { return extreme(a, false); }
    private static Location extreme(int[][] a, boolean minimum) {
        int cols = columns(a), r=0, c=0;
        for (int i=0; i<a.length; i++) for (int j=0; j<cols; j++)
            if (minimum ? a[i][j] < a[r][c] : a[i][j] > a[r][c]) { r=i; c=j; }
        return new Location(a[r][c], r, c);
    }
    public static void main(String[] args) {
        int[] merged = merge(new int[]{1,2,3}, new int[]{4,5});
        System.out.println("Merged:");
        traverse1D(merged);
        int[][] matrix = {{3,-4,7},{9,0,9}};
        System.out.println("Row-first:"); traverse2D(matrix, false);
        System.out.println("Column-first:"); traverse2D(matrix, true);
        System.out.println("Min: "+minimum(matrix)+"; max: "+maximum(matrix));
        traverse3D(new int[][][]{{{1,2},{3,4}},{{5,6},{7,8}}});
        System.out.println("1D B=1000 i=3 s=4: "+address1D(1000,3,5,4));
        System.out.println("2D row-major (1,2): "+address2D(1000,1,2,2,3,4,false));
        System.out.println("2D column-major (1,2): "+address2D(1000,1,2,2,3,4,true));
        System.out.println("3D (1,1,1): "+address3D(1000,1,1,1,2,2,2,4));
    }
}
