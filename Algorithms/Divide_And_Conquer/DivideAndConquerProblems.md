# Divide and conquer: recurrences, proofs and runnable code

Divide and conquer splits a problem into smaller instances, solves them, and combines the results. The split must reduce problem size, and a base case must stop recursion. Binary search keeps only one subproblem and is often more precisely called **decrease and conquer**; it uses the same recurrence-analysis framework.

## 1. Master Theorem: applicable form and limitations

For the standard form

$$T(n)=aT(n/b)+f(n),\qquad a\ge1,\ b>1,$$

compare $f(n)$ with $n^{\log_b a}$. With nonnegative costs and suitable base cases:

1. If $f(n)=O(n^{\log_b a-\epsilon})$ for some $\epsilon>0$, then $T(n)=\Theta(n^{\log_b a})$.
2. If $f(n)=\Theta(n^{\log_b a}\log^k n)$ for fixed $k\ge0$, then $T(n)=\Theta(n^{\log_b a}\log^{k+1}n)$.
3. If $f(n)=\Omega(n^{\log_b a+\epsilon})$ and $a f(n/b)\le c f(n)$ for some $c<1$ eventually (regularity), then $T(n)=\Theta(f(n))$.

Floors and ceilings do not change the standard results here. This theorem does not directly handle uneven quicksort partitions, $T(n-1)$, arbitrary variable branching factors, or every function in the gaps between the cases. Do not force a recurrence into the wrong case.

## 2. Merge sort

### Algorithm and worked trace

1. Split `[lo,hi)` at `mid=lo+(hi-lo)/2`.
2. Recursively sort the two halves.
3. Compare their smallest unconsumed elements, moving the smaller into a temporary array.
4. Copy leftovers, then copy the merged range back.

For `[8,3,2,9,7,1]`:

- Split into `[8,3,2]` and `[9,7,1]`.
- Recursively obtain `[2,3,8]` and `[1,7,9]`.
- Compare 2 vs 1: output 1; 2 vs 7: output 2; 3 vs 7: output 3; 8 vs 7: output 7; 8 vs 9: output 8; append 9.
- Result: `[1,2,3,7,8,9]`.

During merging, the output prefix contains the smallest consumed elements in sorted order. Choosing from the left half on equality preserves original relative order, making merge sort **stable**.

### Derivation

$$T(n)=2T(n/2)+cn+d.$$

Here $a=2$, $b=2$, $n^{\log_2 2}=n$, and $f(n)=\Theta(n)$, so Master Theorem case 2 gives:

$$T(n)=\Theta(n\log n).$$

Alternatively, there are $\log_2 n$ levels, with total $\Theta(n)$ merging at each level. For powers of two under the simplified recurrence $T(n)=2T(n/2)+cn$, $T(1)=d$, expansion gives $T(n)=cn\log_2 n+dn$ exactly under that recurrence.

The implementation always merges, so best, average and worst are $\Theta(n\log n)$ for growing $n$. It allocates one $n$-element buffer and uses $O(\log n)$ recursion stack: $O(n)$ auxiliary space overall. Output is written back into the supplied array, but this does **not** make the algorithm in-place in the strict constant-extra-space sense.

## 3. Randomized three-way quicksort

### Partition invariant

Choose a random pivot value from the current interval. Maintain four regions:

- `[lo,lt)` contains values less than the pivot.
- `[lt,i)` contains values equal to the pivot.
- `[i,gt]` is unclassified.
- `(gt,hi]` contains values greater than the pivot.

If `a[i]<pivot`, swap with `a[lt]`, advance both. If greater, swap with `a[gt]`, decrement `gt`, and inspect the newly arrived `a[i]` again. If equal, advance `i`. When `i>gt`, the unknown region is empty. Recurse only on the less-than and greater-than regions; equal values are already in their final group.

For `[4,2,4,1,3]` with pivot 4, one valid partition is `[2,1,3 | 4,4]`. Sorting the left region finishes the array. The actual seeded random program may choose a different first pivot, with the same sorted result.

### Time recurrence

For distinct keys and partition sizes $k$ and $n-k-1$:

$$T(n)=T(k)+T(n-k-1)+\Theta(n).$$

Balanced partitions give $2T(n/2)+\Theta(n)=\Theta(n\log n)$, a Master Theorem case-2 calculation. Repeatedly extreme pivots give:

$$T(n)=T(n-1)+cn=\Theta(n^2),$$

because the sum is proportional to $n+(n-1)+\cdots+1$. The Master Theorem does not apply to that $n-1$ recurrence.

With independently uniform pivot ranks for distinct keys, expectation obeys:

$$E[T(n)]=\frac{2}{n}\sum_{k=0}^{n-1}E[T(k)]+\Theta(n),$$

yielding $\Theta(n\log n)$ expected time. This is an expectation over pivot choices for a fixed input, not a deterministic worst-case guarantee. Pseudorandom choices approximate this model. A fixed seed makes a demonstration reproducible, not immune to adversarial inputs. The familiar comparison formula $2(n+1)H_n-4n$ belongs to the classic single-pivot comparison model; this three-way code may perform two value comparisons per classification, so that exact constant formula should not be claimed for it.

Three-way partitioning handles duplicates efficiently. An all-equal array is scanned once: $\Theta(n)$ time. Thus “best case $\Theta(n\log n)$” needs a distinct-key qualification; this implementation has a linear all-equal best case.

### Stack safety and storage

A naive recursive quicksort can use $O(n)$ stack in its worst case. This program recursively sorts only the **smaller** partition and iterates on the larger. Each recursive descent is at most half the prior range, so live recursion depth is $O(\log n)$ even if total runtime degenerates to quadratic. Partitioning itself uses $O(1)$ working memory. It is unstable: swapping can reverse the order of equal-key records.

Randomness uses the standard `Random` number generator only. All partitioning, swaps, recursion and array operations are implemented explicitly; there is no library sort or collection container.

## 4. Binary search

Maintain the invariant: if the target is present and has not been returned, some occurrence lies in `[low,high)`. Compare with the middle. Sorted ascending order makes the half that cannot contain the target safely discardable.

For `[2,5,8,12,16,23,38]`, search 16:

1. `[0,7)`, midpoint 3 gives 12; discard indices through 3.
2. `[4,7)`, midpoint 5 gives 23; discard indices 5 and above.
3. `[4,5)`, midpoint 4 gives 16; return 4.

Worst-case recurrence:

$$T(n)=T(n/2)+\Theta(1).$$

Here $a=1$, $b=2$, $n^{\log_2 1}=1$, so case 2 gives $\Theta(\log n)$. Best case is a midpoint hit, $\Theta(1)$. Average successful search is $\Theta(\log n)$ under uniform key selection. The iterative version uses $O(1)$ auxiliary space; the recursive version uses $O(\log n)$ stack.

Both methods return any matching index, or -1 if absent. Duplicates need a modified lower/upper-bound algorithm when the first/last occurrence is required. Sortedness is a documented precondition, not checked by scanning on each search: such a scan would turn the operation into $\Theta(n)$.

## 5. Complete runnable implementation: `DivideAndConquerAlgorithms.java`

Save this block with the named filename, then run:

```sh
javac DivideAndConquerAlgorithms.java
java DivideAndConquerAlgorithms
```

```java
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
```

Expected output:

```text
Merge sort: 1 2 3 7 8 9
Quick sort: 1 2 3 4 4
Index of 7: 3
Recursive index of 7: 3
Absent 100: -1
```

## 6. Comparison and implementation boundaries

| Algorithm | Time | Auxiliary storage | Mutation / constraint |
|---|---|---|---|
| Merge sort | $\Theta(n\log n)$ in all ordinary cases | $O(n)$ | Changes array; stable |
| Three-way randomized quicksort | Expected $O(n\log n)$, worst $\Theta(n^2)$, all equal $\Theta(n)$ | $O(\log n)$ here | Changes array; unstable |
| Iterative binary search | Worst $\Theta(\log n)$, best $\Theta(1)$ | $O(1)$ | Requires sorted input; read-only |
| Recursive binary search | Worst $\Theta(\log n)$, best $\Theta(1)$ | $O(\log n)$ | Requires sorted input; read-only |

Empty and singleton arrays are valid. Null arrays are rejected. The implementations use safe midpoint arithmetic, explicit copying loops, custom merge routines and explicit swaps. Sorting results do not claim a record-level stability test because the runnable examples sort primitive integers; stability of merge sort follows from its tie rule and would carry over to an equivalent key-based record implementation.
