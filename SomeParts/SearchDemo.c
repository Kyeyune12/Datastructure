#include <stdio.h>

// 1. LINEAR SEARCH: Check every element
int linearSearch(int arr[], int size, int target) {
    for (int i = 0; i < size; i++) {
        if (arr[i] == target) return i; // Found at index i
    }
    return -1; // Not found
}

// 2. BINARY SEARCH: Requires sorted array, cuts search space in half
int binarySearch(int arr[], int left, int right, int target) {
    while (left <= right) {
        int mid = left + (right - left) / 2;
        if (arr[mid] == target) return mid;
        if (arr[mid] < target) left = mid + 1;   // Search right half
        else right = mid - 1;                     // Search left half
    }
    return -1;
}

int main() {
    int sortedScores[] = {10, 25, 40, 60, 90};
    int size = 5;

    printf("Linear search for 60 found at index: %d\n", linearSearch(sortedScores, size, 60));
    printf("Binary search for 40 found at index: %d\n", binarySearch(sortedScores, 0, size - 1, 40));
    return 0;
}
