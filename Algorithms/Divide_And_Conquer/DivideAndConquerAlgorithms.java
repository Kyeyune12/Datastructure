import java.util.Random;

public final class DivideAndConquerAlgorithms {
    private DivideAndConquerAlgorithms() { }
    private static void requireArray(int[] a) {
        if (a == null) throw new NullPointerException("array");
    }
    public static void mergeSort(int[] a) {
        requireArray(a);
        if (a.length < 2) return;
        mergeSort(a, new int[a.length], 0, a.length);
    }
    private static void mergeSort(int[] a, int[] temp, int lo, int hi) {
        if (hi - lo < 2) return;
        int mid = lo + (hi - lo) / 2;
        mergeSort(a, temp, lo, mid); mergeSort(a, temp, mid, hi);
        int i = lo, j = mid, k = lo;
        while (i < mid && j < hi) temp[k++] = a[i] <= a[j] ? a[i++] : a[j++];
        while (i < mid) temp[k++] = a[i++];
        while (j < hi) temp[k++] = a[j++];
        for (k = lo; k < hi; k++) a[k] = temp[k];
    }
    public static void quickSort(int[] a) { quickSort(a, new Random()); }
    public static void quickSort(int[] a, Random random) {
        requireArray(a);
        if (random == null) throw new NullPointerException("random");
        quickSort(a, 0, a.length - 1, random);
    }
    private static void swap(int[] a, int i, int j) {
        int temporary = a[i]; a[i] = a[j]; a[j] = temporary;
    }
    private static void quickSort(int[] a, int lo, int hi, Random random) {
        while (lo < hi) {
            int pivot = a[lo + random.nextInt(hi - lo + 1)];
            int lt = lo, i = lo, gt = hi;
            while (i <= gt) {
                if (a[i] < pivot) { swap(a, lt, i); lt++; i++; }
                else if (a[i] > pivot) { swap(a, i, gt); gt--; }
                else i++;
            }
            // Recurse only on the smaller side to bound live stack depth.
            if (lt - lo < hi - gt) {
                quickSort(a, lo, lt - 1, random); lo = gt + 1;
            } else {
                quickSort(a, gt + 1, hi, random); hi = lt - 1;
            }
        }
    }
    /** Precondition: a is sorted in nondecreasing order. */
    public static int binarySearch(int[] a, int key) {
        requireArray(a);
        int low = 0, high = a.length;
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (a[mid] == key) return mid;
            if (a[mid] < key) low = mid + 1;
            else high = mid;
        }
        return -1;
    }
    public static int recursiveBinarySearch(int[] a, int key) {
        requireArray(a); return binarySearch(a, key, 0, a.length);
    }
    private static int binarySearch(int[] a, int key, int low, int high) {
        if (low >= high) return -1;
        int mid = low + (high - low) / 2;
        if (a[mid] == key) return mid;
        return a[mid] < key ? binarySearch(a, key, mid + 1, high)
                           : binarySearch(a, key, low, mid);
    }
    private static void print(int[] a) {
        for (int i = 0; i < a.length; i++) {
            if (i > 0) System.out.print(" ");
            System.out.print(a[i]);
        }
        System.out.println();
    }
    public static void main(String[] args) {
        int[] merge = {8,3,2,9,7,1}, quick = {4,2,4,1,3};
        mergeSort(merge); quickSort(quick, new Random(42));
        System.out.print("Merge sort: "); print(merge);
        System.out.print("Quick sort: "); print(quick);
        System.out.println("Index of 7: " + binarySearch(merge, 7));
        System.out.println("Recursive index of 7: " + recursiveBinarySearch(merge, 7));
        System.out.println("Absent 100: " + binarySearch(merge, 100));
    }
}
