// Greedy algorithms - C version of GreedyAlgorithms.java
// 1. Fractional knapsack: take items by best value/weight first.
// 2. Activity selection: take the activity that FINISHES earliest.
// Both sort with a hand-written merge sort (no qsort).
//
// Build: gcc -std=c11 -Wall -Wextra greedy_algorithms.c -o greedy && ./greedy
#include <stdio.h>
#include <stdlib.h>

struct Item { int weight, value; };
struct Activity { int start, finish; };

// ---------- Fractional knapsack ----------

// Does item a come before item b? Compare value/weight without division.
static int item_before(const struct Item *items, int a, int b) {
    long left = (long)items[a].value * items[b].weight;
    long right = (long)items[b].value * items[a].weight;
    return left > right || (left == right && a <= b);
}

static void sort_items(const struct Item *items, int *order, int *temp, int lo, int hi) {
    if (hi - lo < 2) return;
    int mid = lo + (hi - lo) / 2;
    sort_items(items, order, temp, lo, mid);
    sort_items(items, order, temp, mid, hi);
    int i = lo, j = mid, k = lo;
    while (i < mid && j < hi)
        temp[k++] = item_before(items, order[i], order[j]) ? order[i++] : order[j++];
    while (i < mid) temp[k++] = order[i++];
    while (j < hi) temp[k++] = order[j++];
    for (k = lo; k < hi; k++) order[k] = temp[k];
}

// Fills fractions[] (one per item) and returns the best total value.
static double fractional_knapsack(const struct Item *items, int n, long capacity, double *fractions) {
    int *order = malloc(sizeof(int) * (size_t)n);
    int *temp = malloc(sizeof(int) * (size_t)n);
    if (order == NULL || temp == NULL) exit(1);
    for (int i = 0; i < n; i++) { order[i] = i; fractions[i] = 0; }
    sort_items(items, order, temp, 0, n);

    long remaining = capacity;
    double value = 0;
    for (int i = 0; i < n && remaining > 0; i++) {
        const struct Item *it = &items[order[i]];
        long taken = remaining < it->weight ? remaining : it->weight;
        double fraction = (double)taken / it->weight;
        fractions[order[i]] = fraction;
        value += fraction * it->value;
        remaining -= taken;
    }
    free(order);
    free(temp);
    return value;
}

// ---------- Activity selection ----------

static int activity_before(const struct Activity *a, int ia, int ib) {
    if (a[ia].finish != a[ib].finish) return a[ia].finish < a[ib].finish;
    if (a[ia].start != a[ib].start) return a[ia].start < a[ib].start;
    return ia <= ib;
}

static void sort_activities(const struct Activity *a, int *order, int *temp, int lo, int hi) {
    if (hi - lo < 2) return;
    int mid = lo + (hi - lo) / 2;
    sort_activities(a, order, temp, lo, mid);
    sort_activities(a, order, temp, mid, hi);
    int i = lo, j = mid, k = lo;
    while (i < mid && j < hi)
        temp[k++] = activity_before(a, order[i], order[j]) ? order[i++] : order[j++];
    while (i < mid) temp[k++] = order[i++];
    while (j < hi) temp[k++] = order[j++];
    for (k = lo; k < hi; k++) order[k] = temp[k];
}

// Writes chosen original indices into selected[]; returns how many.
static int select_activities(const struct Activity *a, int n, int *selected) {
    int *order = malloc(sizeof(int) * (size_t)n);
    int *temp = malloc(sizeof(int) * (size_t)n);
    if (order == NULL || temp == NULL) exit(1);
    for (int i = 0; i < n; i++) order[i] = i;
    sort_activities(a, order, temp, 0, n);

    int count = 0, last_finish = 0;
    for (int i = 0; i < n; i++) {
        const struct Activity *act = &a[order[i]];
        if (count == 0 || act->start >= last_finish) {
            selected[count++] = order[i];
            last_finish = act->finish;
        }
    }
    free(order);
    free(temp);
    return count;
}

// Prints like Java's Double.toString for the values used here.
static void print_double(double x) {
    if (x == (long)x) printf("%.1f", x);
    else printf("%.16g", x);
}

int main(void) {
    struct Item items[] = {{10, 60}, {20, 100}, {30, 120}};
    double fractions[3];
    double total = fractional_knapsack(items, 3, 50, fractions);

    printf("Optimal fractional value: "); print_double(total); printf("\n");
    for (int i = 0; i < 3; i++) {
        printf("Item %d: ", i); print_double(fractions[i]); printf("\n");
    }

    struct Activity acts[] = {{1, 4}, {3, 5}, {0, 6}, {5, 7}, {8, 9}, {5, 9}};
    int selected[6];
    int count = select_activities(acts, 6, selected);
    printf("Selected original indices: ");
    for (int i = 0; i < count; i++) printf("%d ", selected[i]);
    printf("\n");
    return 0;
}
