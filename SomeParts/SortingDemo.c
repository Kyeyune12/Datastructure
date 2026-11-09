#include <stdio.h>

// Helper function to print array elements
void printArray(const char* label, int arr[], int size) {
    printf("%-18s", label);
    for (int i = 0; i < size; i++) {
        printf("%d ", arr[i]);
    }
    printf("\n");
}

/*
 * 1. BUBBLE SORT
 * Swaps adjacent elements repeatedly if they are in the wrong order.
 * 
 * Time Complexity:
 *   - Best Case:    O(n)     [When array is already sorted, if optimized with a swapped flag]
 *   - Average Case: O(n^2)   [Typical un-ordered distribution]
 *   - Worst Case:   O(n^2)   [When array is sorted in reverse order]
 * Space Complexity: O(1)     [In-place sorting]
 */
void bubbleSort(int arr[], int size) {
    for (int i = 0; i < size - 1; i++) {
        for (int j = 0; j < size - 1 - i; j++) {
            if (arr[j] > arr[j + 1]) {
                int temp = arr[j];
                arr[j] = arr[j + 1];
                arr[j + 1] = temp;
            }
        }
    }
}

/*
 * 2. SELECTION SORT
 * Finds the minimum element from the unsorted region and places it at the front.
 * 
 * Time Complexity:
 *   - Best Case:    O(n^2)   [Must scan the unsorted region regardless of initial order]
 *   - Average Case: O(n^2)   [Quadratic comparisons]
 *   - Worst Case:   O(n^2)   [Quadratic comparisons]
 * Space Complexity: O(1)     [In-place sorting; performs at most O(n) swaps]
 */
void selectionSort(int arr[], int size) {
    for (int i = 0; i < size - 1; i++) {
        int minIndex = i;
        for (int j = i + 1; j < size; j++) {
            if (arr[j] < arr[minIndex]) {
                minIndex = j; // Track index of smallest element
            }
        }
        // Swap smallest element with current index i
        int temp = arr[minIndex];
        arr[minIndex] = arr[i];
        arr[i] = temp;
    }
}

/*
 * 3. INSERTION SORT
 * Builds the sorted array one element at a time by shifting larger elements to the right.
 * 
 * Time Complexity:
 *   - Best Case:    O(n)     [Array already sorted; inner loop condition fails immediately]
 *   - Average Case: O(n^2)   [Typical un-ordered distribution]
 *   - Worst Case:   O(n^2)   [Array sorted in reverse order; maximum shifts required]
 * Space Complexity: O(1)     [In-place sorting]
 */
void insertionSort(int arr[], int size) {
    for (int i = 1; i < size; i++) {
        int key = arr[i];
        int j = i - 1;

        // Shift elements of arr[0..i-1] that are greater than key to one position ahead
        while (j >= 0 && arr[j] > key) {
            arr[j + 1] = arr[j];
            j--;
        }
        arr[j + 1] = key;
    }
}

/*
 * 4. QUICK SORT
 * Divide and conquer algorithm using a pivot element to partition the array.
 * 
 * Time Complexity:
 *   - Best Case:    O(n log n) [Pivot divides array into roughly equal halves]
 *   - Average Case: O(n log n) [Random/balanced partitions]
 *   - Worst Case:   O(n^2)     [Pivot is always smallest/largest element]
 * Space Complexity: O(log n)   [Auxiliary stack space due to recursive call stack]
 */
int partition(int arr[], int low, int high) {
    int pivot = arr[high]; // Choose last element as pivot
    int i = (low - 1);
    for (int j = low; j < high; j++) {
        if (arr[j] <= pivot) {
            i++;
            int temp = arr[i]; arr[i] = arr[j]; arr[j] = temp;
        }
    }
    int temp = arr[i + 1]; arr[i + 1] = arr[high]; arr[high] = temp;
    return i + 1;
}

void quickSort(int arr[], int low, int high) {
    if (low < high) {
        int pivotIndex = partition(arr, low, high);
        quickSort(arr, low, pivotIndex - 1);  // Sort left side
        quickSort(arr, pivotIndex + 1, high); // Sort right side
    }
}

int main() {
    int size = 5;

    int scores1[] = {45, 12, 85, 32, 8};
    bubbleSort(scores1, size);
    printArray("Bubble Sorted:", scores1, size);

    int scores2[] = {45, 12, 85, 32, 8};
    selectionSort(scores2, size);
    printArray("Selection Sorted:", scores2, size);

    int scores3[] = {45, 12, 85, 32, 8};
    insertionSort(scores3, size);
    printArray("Insertion Sorted:", scores3, size);

    int scores4[] = {45, 12, 85, 32, 8};
    quickSort(scores4, 0, size - 1);
    printArray("Quick Sorted:", scores4, size);

    return 0;
}
