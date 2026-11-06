// Divide and conquer - C version of DivideAndConquerAlgorithms.java
// merge sort, randomized three-way quicksort, binary search (loop + recursive)
//
// Build: gcc -std=c11 -Wall -Wextra divide_and_conquer_algorithms.c -o dc && ./dc
#include <stdio.h>
#include <stdlib.h>

// ---------- Merge sort: T(n) = 2T(n/2) + O(n) = O(n log n) ----------
static void merge_sort_range(int *a, int *temp, int lo, int hi) {
    if (hi - lo < 2) return;
    int mid = lo + (hi - lo) / 2;
    merge_sort_range(a, temp, lo, mid);
    merge_sort_range(a, temp, mid, hi);
    int i = lo, j = mid, k = lo;
    while (i < mid && j < hi) temp[k++] = a[i] <= a[j] ? a[i++] : a[j++];
    while (i < mid) temp[k++] = a[i++];
    while (j < hi) temp[k++] = a[j++];
    for (k = lo; k < hi; k++) a[k] = temp[k];
}

static void merge_sort(int *a, int n) {
    if (n < 2) return;
    int *temp = malloc(sizeof(int) * (size_t)n);
    if (temp == NULL) exit(1);
    merge_sort_range(a, temp, 0, n);
    free(temp);
}

// ---------- Randomized three-way quicksort: expected O(n log n) ----------
static void swap(int *a, int i, int j) { int t = a[i]; a[i] = a[j]; a[j] = t; }

static void quick_sort_range(int *a, int lo, int hi) {
    while (lo < hi) {
        int pivot = a[lo + rand() % (hi - lo + 1)];
        int lt = lo, i = lo, gt = hi;
        while (i <= gt) {                       // split into < pivot, == pivot, > pivot
            if (a[i] < pivot) { swap(a, lt, i); lt++; i++; }
            else if (a[i] > pivot) { swap(a, i, gt); gt--; }
            else i++;
        }
        // recurse on the smaller side only: keeps stack depth O(log n)
        if (lt - lo < hi - gt) { quick_sort_range(a, lo, lt - 1); lo = gt + 1; }
        else { quick_sort_range(a, gt + 1, hi); hi = lt - 1; }
    }
}

static void quick_sort(int *a, int n) { quick_sort_range(a, 0, n - 1); }

// ---------- Binary search (array must be sorted): O(log n) ----------
static int binary_search(const int *a, int n, int key) {
    int low = 0, high = n;
    while (low < high) {
        int mid = low + (high - low) / 2;
        if (a[mid] == key) return mid;
        if (a[mid] < key) low = mid + 1; else high = mid;
    }
    return -1;
}

static int binary_search_rec(const int *a, int key, int low, int high) {
    if (low >= high) return -1;
    int mid = low + (high - low) / 2;
    if (a[mid] == key) return mid;
    return a[mid] < key ? binary_search_rec(a, key, mid + 1, high)
                        : binary_search_rec(a, key, low, mid);
}

static void print_array(const int *a, int n) {
    for (int i = 0; i < n; i++) printf(i > 0 ? " %d" : "%d", a[i]);
    printf("\n");
}

int main(void) {
    int merge[] = {8, 3, 2, 9, 7, 1}, quick[] = {4, 2, 4, 1, 3};
    srand(42);
    merge_sort(merge, 6);
    quick_sort(quick, 5);
    printf("Merge sort: "); print_array(merge, 6);
    printf("Quick sort: "); print_array(quick, 5);
    printf("Index of 7: %d\n", binary_search(merge, 6, 7));
    printf("Recursive index of 7: %d\n", binary_search_rec(merge, 7, 0, 6));
    printf("Absent 100: %d\n", binary_search(merge, 6, 100));
    return 0;
}
