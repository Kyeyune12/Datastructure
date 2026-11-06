#include <stdio.h>

// Passing arrays to functions in C.
// swapArray swaps each element with its right-hand neighbour, left to right,
// which carries the FIRST element all the way to the END
// (a rotation one place to the left).
//
// Build: gcc main.c -o main
// Run:   ./main

#define SIZE 6

void display(int a[], int length) {
    for (int i = 0; i < length; i++) {
        printf("%d ", a[i]);
    }
    printf("\n");
}

// Arrays are passed by address, so the changes are seen by main
void swapArray(int array[], int length) {
    for (int i = 0; i < length - 1; i++) {
        int temp = array[i];
        array[i] = array[i + 1];
        array[i + 1] = temp;
    }
}

int main() {
    int array[SIZE] = {11, 3, 5, 7, 8, 1};

    printf("Before: ");
    display(array, SIZE);

    swapArray(array, SIZE);

    printf("After:  ");
    display(array, SIZE);

    return 0;
}
