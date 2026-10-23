/* gcc -std=c11 -Wall -Wextra -Wpedantic -Werror array_shuffle.c -o shuffle
 * ./shuffle
 * Fisher-Yates: choose j uniformly in [0,i], then swap a[i] and a[j].
 * Each permutation has probability 1/n! with independent uniform random draws.
 * rand() is pseudorandom and not cryptographic. ISO C does not promise a quality
 * random source. Rejection sampling removes modulo bias conditional on uniform
 * rand() output; it cannot repair a poor PRNG. Runtime is EXPECTED Theta(n) due
 * to rejection; swaps are exactly n-1, auxiliary space O(1). No finite worst-case
 * rejection bound. Restrict n to RAND_MAX+1 so every needed bound is supported.
 */
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <stdbool.h>

static size_t uniform_below(size_t bound) {
    const uintmax_t range=(uintmax_t)RAND_MAX+1;
    const uintmax_t limit=range-range%(uintmax_t)bound;
    uintmax_t r;
    do { r=(uintmax_t)rand(); } while (r>=limit);
    return (size_t)(r%(uintmax_t)bound);
}
bool shuffleInPlace(int *arr, size_t n) {
    if ((n && !arr) || (uintmax_t)n>(uintmax_t)RAND_MAX+1
        || n>SIZE_MAX/sizeof(int)) return false;
    for (size_t remaining=n; remaining>1; --remaining) {
        size_t i=remaining-1, j=uniform_below(remaining);
        int temp=arr[i]; arr[i]=arr[j]; arr[j]=temp;
    }
    return true;
}
int main(void) {
    int a[]={1,2,3,4,5,6};
    srand(42); /* Seed once, not at each shuffle. */
    if (!shuffleInPlace(a,sizeof(a)/sizeof(a[0]))) return EXIT_FAILURE;
    for (size_t i=0; i<sizeof(a)/sizeof(a[0]); ++i) printf("%d%c",a[i],i==5?'\n':' ');
    return EXIT_SUCCESS;
}
