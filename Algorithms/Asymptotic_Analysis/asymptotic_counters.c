// Asymptotic counters - C version of AsymptoticCounters.java.
// Runs every loop from LoopAnalysis.md and ReadingExerciseAlgorithm.md with a
// unit counter and checks the count equals the formula derived in the notes.
//
// Cost model: initialisation = 1, every condition test (including the final
// failed one) = 1, every update = 1, each simple body statement = 1.
//
// Build: gcc -std=c11 -Wall -Wextra asymptotic_counters.c -o counters && ./counters
#include <stdio.h>
#include <stdlib.h>

static long ops;          // the unit counter
static int failures = 0;

#define TICK() (ops++)

static long floor_log2_plus1(int n) { long l = 0; for (int x = n; x > 0; x /= 2) l++; return l; }

// ---- LoopAnalysis.md ----
static long while_loop(int n) {                 // 3n + 2
    ops = 0; int i = 0; TICK();
    for (;;) { TICK(); if (!(i < n)) break; TICK(); i++; TICK(); }
    return ops;
}
static long log_loop(int n) {                   // 3L + 2
    ops = 0; int i = n; TICK();
    for (;;) { TICK(); if (!(i > 0)) break; TICK(); i /= 2; TICK(); }
    return ops;
}
static long nested(int n) {                     // 3n^2 + 4n + 2
    ops = 0; int i = 0; TICK();
    for (;;) {
        TICK(); if (!(i < n)) break;
        int j = 0; TICK();
        for (;;) { TICK(); if (!(j < n)) break; TICK(); j++; TICK(); }
        i++; TICK();
    }
    return ops;
}
static long triangular(int n) {                 // 1.5n^2 + 5.5n + 2
    ops = 0; int i = 0; TICK();
    for (;;) {
        TICK(); if (!(i < n)) break;
        int j = 0; TICK();
        for (;;) { TICK(); if (!(j <= i)) break; TICK(); j++; TICK(); }
        i++; TICK();
    }
    return ops;
}
static long for_with_halving_while(int n) {     // 3nL + 4n + 2
    ops = 0; int i = 0; TICK();
    for (;;) {
        TICK(); if (!(i < n)) break;
        int j = n; TICK();
        for (;;) { TICK(); if (!(j > 0)) break; TICK(); j /= 2; TICK(); }
        i++; TICK();
    }
    return ops;
}
static long shared_pointer(int n) {             // 6n + 3
    ops = 0; int j = 0; TICK(); int i = 0; TICK();
    for (;;) {
        TICK(); if (!(i < n)) break;
        for (;;) { TICK(); if (!(j < n)) break; TICK(); j++; TICK(); }
        i++; TICK();
    }
    return ops;
}

// ---- ReadingExerciseAlgorithm.md ----
static long linear_search(const int *a, int n, int key) {   // hit 3r+4, miss 3n+3
    ops = 0; int i = 0; TICK();
    for (;;) {
        TICK(); if (!(i < n)) break;
        TICK(); if (a[i] == key) { TICK(); return ops; }
        i++; TICK();
    }
    TICK();
    return ops;
}
static long matrix_sum(int rows, int cols) {                // 3rc + 4r + 3
    ops = 0; long sum = 0; TICK(); int i = 0; TICK();
    for (;;) {
        TICK(); if (!(i < rows)) break;
        int j = 0; TICK();
        for (;;) { TICK(); if (!(j < cols)) break; sum += 1; TICK(); j++; TICK(); }
        i++; TICK();
    }
    return ops;
}
static long binary_search(const int *a, int n, int key) {   // miss 5h+4, hit 5h+1
    ops = 0; int low = 0; TICK(); int high = n; TICK();
    for (;;) {
        TICK(); if (!(low < high)) break;
        int mid = low + (high - low) / 2; TICK();
        TICK(); if (a[mid] == key) { TICK(); return ops; }
        TICK();
        if (a[mid] < key) low = mid + 1; else high = mid;
        TICK();
    }
    TICK();
    return ops;
}
static long duplicate_check(const int *a, int n) {         // 1.5n(n-1) + 4n + 3
    ops = 0; int i = 0; TICK();
    for (;;) {
        TICK(); if (!(i < n)) break;
        int j = i + 1; TICK();
        for (;;) {
            TICK(); if (!(j < n)) break;
            TICK(); if (a[i] == a[j]) { TICK(); return ops; }
            j++; TICK();
        }
        i++; TICK();
    }
    TICK();
    return ops;
}
static long insertion_sort(int *a, int n) {                // 6n - 4 + 3I
    ops = 0; int i = 1; TICK();
    for (;;) {
        TICK(); if (!(i < n)) break;
        int key = a[i]; TICK();
        int j = i - 1; TICK();
        for (;;) {
            TICK();
            if (!(j >= 0 && a[j] > key)) break;
            a[j + 1] = a[j]; TICK();
            j--; TICK();
        }
        a[j + 1] = key; TICK();
        i++; TICK();
    }
    return ops;
}

static long inversions(const int *a, int n) {
    long c = 0;
    for (int i = 0; i < n; i++) for (int j = i + 1; j < n; j++) if (a[i] > a[j]) c++;
    return c;
}

static void check(const char *name, int n, long measured, long expected) {
    if (measured != expected) {
        failures++;
        printf("FAIL %s n=%d measured=%ld expected=%ld\n", name, n, measured, expected);
    }
}

int main(void) {
    printf("*** Asymptotic counters: measured units vs formula ***\n");

    for (int n = 0; n <= 200; n++) {
        long L = n >= 1 ? floor_log2_plus1(n) : 0;
        check("while loop 3n+2", n, while_loop(n), 3L * n + 2);
        check("log loop 3L+2", n, log_loop(n), 3 * L + 2);
        check("nested 3n^2+4n+2", n, nested(n), 3L * n * n + 4L * n + 2);
        check("triangular", n, triangular(n) * 2, 3L * n * n + 11L * n + 4);
        if (n >= 1) check("for+halving 3nL+4n+2", n, for_with_halving_while(n), 3L * n * L + 4L * n + 2);
        check("shared pointer 6n+3", n, shared_pointer(n), 6L * n + 3);
    }

    for (int n = 1; n <= 100; n++) {
        int *a = malloc(sizeof(int) * (size_t)n), *rev = malloc(sizeof(int) * (size_t)n);
        if (a == NULL || rev == NULL) return 1;
        for (int i = 0; i < n; i++) a[i] = 2 * i;
        for (int r = 0; r < n; r++) check("linear hit 3r+4", n, linear_search(a, n, a[r]), 3L * r + 4);
        check("linear miss 3n+3", n, linear_search(a, n, -1), 3L * n + 3);
        check("duplicate distinct", n, duplicate_check(a, n), 3L * n * (n - 1) / 2 + 4L * n + 3);

        for (int k = 0; k < n; k++) {
            long measured = binary_search(a, n, a[k]);
            int low = 0, high = n, h = 0;
            while (low < high) {
                int mid = low + (high - low) / 2; h++;
                if (a[mid] == a[k]) break;
                if (a[mid] < a[k]) low = mid + 1; else high = mid;
            }
            check("binary hit 5h+1", n, measured, 5L * h + 1);
        }
        for (int k = -1; k <= 2 * n; k += 2) {
            long measured = binary_search(a, n, k);
            int low = 0, high = n, h = 0;
            while (low < high) {
                int mid = low + (high - low) / 2; h++;
                if (a[mid] < k) low = mid + 1; else high = mid;
            }
            check("binary miss 5h+4", n, measured, 5L * h + 4);
        }

        for (int i = 0; i < n; i++) rev[i] = n - i;
        long inv = inversions(rev, n);
        check("insertion worst", n, insertion_sort(rev, n), 6L * n - 4 + 3 * inv);
        check("insertion best", n, insertion_sort(a, n), 6L * n - 4);
        free(a); free(rev);
    }

    for (int r = 0; r <= 30; r++)
        for (int c = 0; c <= 30; c++)
            check("matrix 3rc+4r+3", r * 100 + c, matrix_sum(r, c), 3L * r * c + 4L * r + 3);

    printf("\n   n | while 3n+2 | nested 3n^2+4n+2 | for+halving\n");
    int sizes[] = {1, 2, 4, 8, 16, 1024};
    for (int k = 0; k < 6; k++) {
        int n = sizes[k];
        printf("%4d | %10ld | %17ld | %11ld\n", n, while_loop(n), nested(n), for_with_halving_while(n));
    }
    printf("\n%s\n", failures == 0 ? "All formulas match the measured counts." : "Mismatches found!");
    return failures != 0;
}
