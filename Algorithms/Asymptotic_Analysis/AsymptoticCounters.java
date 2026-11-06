/*
 * Asymptotic counters: runs every loop from LoopAnalysis.md and
 * ReadingExerciseAlgorithm.md with a counter and checks that the number of
 * "units" really equals the formula derived in those notes.
 *
 * Cost model (same as the notes): initialisation = 1, every condition test
 * (including the final failed one) = 1, every update = 1, each simple body
 * statement = 1.
 *
 * Run: javac AsymptoticCounters.java && java AsymptoticCounters
 */
public class AsymptoticCounters {

    static long ops;            // the unit counter
    static int failures = 0;

    static long floorLog2Plus1(int n) {      // L = floor(log2 n) + 1  (n >= 1)
        long l = 0;
        for (int x = n; x > 0; x /= 2) l++;
        return l;
    }

    // ---- LoopAnalysis.md ----
    static long whileLoop(int n) {            // 3n + 2
        ops = 0;
        int i = 0; ops++;
        for (;;) {
            ops++; if (!(i < n)) break;             // condition test
            ops++;                                  // sum += i
            i++; ops++;                             // update
        }
        return ops;
    }

    static long logLoop(int n) {              // 3L + 2
        ops = 0;
        int i = n; ops++;
        while (true) {
            ops++;                            // condition test
            if (!(i > 0)) break;
            ops++;                            // count++
            i /= 2; ops++;                    // update
        }
        return ops;
    }

    static long nested(int n) {               // 3n^2 + 4n + 2
        ops = 0;
        int i = 0; ops++;
        for (;;) {
            ops++; if (!(i < n)) break;
            int j = 0; ops++;
            for (;;) {
                ops++; if (!(j < n)) break;
                ops++;                        // count++
                j++; ops++;
            }
            i++; ops++;
        }
        return ops;
    }

    static long triangular(int n) {           // 1.5n^2 + 5.5n + 2
        ops = 0;
        int i = 0; ops++;
        for (;;) {
            ops++; if (!(i < n)) break;
            int j = 0; ops++;
            for (;;) {
                ops++; if (!(j <= i)) break;
                ops++;
                j++; ops++;
            }
            i++; ops++;
        }
        return ops;
    }

    static long forWithHalvingWhile(int n) {  // 3nL + 4n + 2
        ops = 0;
        int i = 0; ops++;
        for (;;) {
            ops++; if (!(i < n)) break;
            int j = n; ops++;
            for (;;) {
                ops++; if (!(j > 0)) break;
                ops++;
                j /= 2; ops++;
            }
            i++; ops++;
        }
        return ops;
    }

    static long sharedPointer(int n) {        // 6n + 3
        ops = 0;
        int j = 0; ops++;
        int i = 0; ops++;
        for (;;) {
            ops++; if (!(i < n)) break;
            for (;;) {
                ops++; if (!(j < n)) break;
                ops++;
                j++; ops++;
            }
            i++; ops++;
        }
        return ops;
    }

    // ---- ReadingExerciseAlgorithm.md ----
    // Linear search: hit at index r -> 3r + 4, miss -> 3n + 3
    static long linearSearch(int[] a, int key) {
        ops = 0;
        int i = 0; ops++;
        for (;;) {
            ops++; if (!(i < a.length)) break;      // loop test
            ops++; if (a[i] == key) { ops++; return ops; }   // equality test, return
            i++; ops++;
        }
        ops++;                                      // return -1
        return ops;
    }

    // Matrix sum: 3rc + 4r + 3
    static long matrixSum(int rows, int cols) {
        ops = 0;
        long sum = 0; ops++;
        int i = 0; ops++;
        for (;;) {
            ops++; if (!(i < rows)) break;
            int j = 0; ops++;
            for (;;) {
                ops++; if (!(j < cols)) break;
                sum += 1; ops++;
                j++; ops++;
            }
            i++; ops++;
        }
        return ops;                                 // the notes' formula has no return unit
    }

    // Binary search, unsuccessful: 5h + 4 ; successful on round h: 5h + 1
    static long binarySearch(int[] a, int key) {
        ops = 0;
        int low = 0; ops++;
        int high = a.length; ops++;
        for (;;) {
            ops++; if (!(low < high)) break;
            int mid = low + (high - low) / 2; ops++;
            ops++; if (a[mid] == key) { ops++; return ops; }
            ops++;
            if (a[mid] < key) low = mid + 1; else high = mid;
            ops++;
        }
        ops++;                                      // return -1
        return ops;
    }

    // Duplicate check on distinct data: 1.5n(n-1) + 4n + 3
    static long duplicateCheck(int[] a) {
        ops = 0;
        int n = a.length;
        int i = 0; ops++;
        for (;;) {
            ops++; if (!(i < n)) break;
            int j = i + 1; ops++;
            for (;;) {
                ops++; if (!(j < n)) break;
                ops++; if (a[i] == a[j]) { ops++; return ops; }
                j++; ops++;
            }
            i++; ops++;
        }
        ops++;
        return ops;
    }

    // Insertion sort: 6n - 4 + 3I  (I = number of inversions = shifts)
    static long insertionSort(int[] a, int[] shiftsOut) {
        ops = 0;
        long shifts = 0;
        int i = 1; ops++;
        for (;;) {
            ops++; if (!(i < a.length)) break;
            int key = a[i]; ops++;
            int j = i - 1; ops++;
            for (;;) {
                ops++;                              // whole predicate counts as one unit
                if (!(j >= 0 && a[j] > key)) break;
                a[j + 1] = a[j]; ops++;
                j--; ops++;
                shifts++;
            }
            a[j + 1] = key; ops++;
            i++; ops++;
        }
        shiftsOut[0] = (int) shifts;
        return ops;
    }

    static void check(String name, int n, long measured, long expected) {
        boolean ok = measured == expected;
        if (!ok) {
            failures++;
            System.out.println("FAIL " + name + " n=" + n + " measured=" + measured + " expected=" + expected);
        }
    }

    static long inversions(int[] a) {
        long c = 0;
        for (int i = 0; i < a.length; i++)
            for (int j = i + 1; j < a.length; j++)
                if (a[i] > a[j]) c++;
        return c;
    }

    public static void main(String[] args) {
        System.out.println("*** Asymptotic counters: measured units vs formula ***");

        for (int n = 0; n <= 200; n++) {
            long L = n >= 1 ? floorLog2Plus1(n) : 0;
            check("while loop 3n+2", n, whileLoop(n), 3L * n + 2);
            check("log loop 3L+2", n, logLoop(n), 3 * L + 2);
            check("nested 3n^2+4n+2", n, nested(n), 3L * n * n + 4L * n + 2);
            check("triangular", n, triangular(n) * 2, 3L * n * n + 11L * n + 4);
            if (n >= 1) check("for+halving 3nL+4n+2", n, forWithHalvingWhile(n), 3L * n * L + 4L * n + 2);
            check("shared pointer 6n+3", n, sharedPointer(n), 6L * n + 3);
        }

        for (int n = 1; n <= 100; n++) {
            int[] a = new int[n];
            for (int i = 0; i < n; i++) a[i] = 2 * i;               // sorted, distinct
            for (int r = 0; r < n; r++) check("linear hit 3r+4", n, linearSearch(a, a[r]), 3L * r + 4);
            check("linear miss 3n+3", n, linearSearch(a, -1), 3L * n + 3);
            check("duplicate distinct", n, duplicateCheck(a), 3L * n * (n - 1) / 2 + 4L * n + 3);

            // binary search: every present key, and absent keys
            for (int k = 0; k < n; k++) {
                long measured = binarySearch(a, a[k]);
                // rounds h = number of probes until a[mid]==key; recompute directly
                int low = 0, high = n, h = 0;
                while (low < high) {
                    int mid = low + (high - low) / 2;
                    h++;
                    if (a[mid] == a[k]) break;
                    if (a[mid] < a[k]) low = mid + 1; else high = mid;
                }
                check("binary hit 5h+1", n, measured, 5L * h + 1);
            }
            for (int k = -1; k <= 2 * n; k += 2) {                  // odd keys are absent
                long measured = binarySearch(a, k);
                int low = 0, high = n, h = 0;
                while (low < high) {
                    int mid = low + (high - low) / 2;
                    h++;
                    if (a[mid] < k) low = mid + 1; else high = mid;
                }
                check("binary miss 5h+4", n, measured, 5L * h + 4);
            }

            // insertion sort on reversed input (worst) and sorted input (best)
            int[] rev = new int[n];
            for (int i = 0; i < n; i++) rev[i] = n - i;
            long inv = inversions(rev);
            int[] sh = new int[1];
            check("insertion worst", n, insertionSort(rev, sh), 6L * n - 4 + 3 * inv);
            check("insertion best", n, insertionSort(a.clone(), sh), 6L * n - 4);
        }

        // matrix traversal
        for (int r = 0; r <= 30; r++)
            for (int c = 0; c <= 30; c++)
                check("matrix 3rc+4r+3", r * 100 + c, matrixSum(r, c), 3L * r * c + 4L * r + 3);

        // show a few values so a human can read the growth
        System.out.println();
        System.out.println("   n | while 3n+2 | nested 3n^2+4n+2 | for+halving");
        int[] sizes = {1, 2, 4, 8, 16, 1024};
        for (int n : sizes) {
            System.out.printf("%4d | %10d | %17d | %11d%n", n, whileLoop(n), nested(n), forWithHalvingWhile(n));
        }
        System.out.println();
        System.out.println(failures == 0 ? "All formulas match the measured counts." : failures + " mismatches!");
    }
}
