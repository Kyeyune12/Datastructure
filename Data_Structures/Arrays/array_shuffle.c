#include <stdio.h>
#include <stdlib.h>

// Fisher-Yates shuffle: puts the array in a random order.
// Every possible order is equally likely. Time O(n), extra space O(1).
//
// Build: gcc array_shuffle.c -o shuffle
// Run:   ./shuffle

#define SIZE 6

// go from the last element down to the second one,
// and swap each element with a random element at or before it
void shuffle(int arr[], int n) {
    for (int i = n - 1; i > 0; i--) {
        int j = rand() % (i + 1);   // random index from 0 to i

        int temp = arr[i];          // swap arr[i] and arr[j]
        arr[i] = arr[j];
        arr[j] = temp;
    }
}

void printArray(int arr[], int n) {
    for (int i = 0; i < n; i++) {
        printf("%d ", arr[i]);
    }
    printf("\n");
}

int main() {
    int values[SIZE] = {1, 2, 3, 4, 5, 6};

    srand(42);   // seed the random numbers ONCE at the start

    printf("Before: ");
    printArray(values, SIZE);

    shuffle(values, SIZE);

    printf("After:  ");
    printArray(values, SIZE);

    return 0;
}
