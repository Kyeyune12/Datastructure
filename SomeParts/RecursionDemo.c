#include <stdio.h>

// Situation: Counting down campus floors to reach the ground floor (Base Case)
void countdown(int floor) {
    // BASE CASE: When we reach ground floor, stop recursing
    if (floor == 0) {
        printf("  [Base Case Reached] Ground floor! Opening doors.\n");
        return;
    }

    printf("  Descending from floor %d...\n", floor);
    
    // RECURSIVE STEP: Call function with a smaller problem
    countdown(floor - 1); 
}

int main() {
    printf("--- C Recursion Demo ---\n");
    countdown(3);
    return 0;
}
