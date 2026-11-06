// Multiplier list (C version of MultiplierList.java)
// Multiplies a number stored in a linked list by a whole number k.
// Digits are stored BACKWARDS (least significant first): 99 is 9 -> 9.
//
// Build: gcc -std=c11 -Wall -Wextra multiplier_list.c -o multiplier && ./multiplier
#include <stdio.h>
#include <stdlib.h>

struct Node {
    int digit;
    struct Node *next;
};

static struct Node *new_node(int digit) {
    struct Node *n = malloc(sizeof(struct Node));
    if (n == NULL) { fprintf(stderr, "Out of memory\n"); exit(1); }
    n->digit = digit;
    n->next = NULL;
    return n;
}

static void free_list(struct Node *head) {
    while (head != NULL) { struct Node *next = head->next; free(head); head = next; }
}

// Returns a NEW list holding (number in head) * k
static struct Node *multiply(const struct Node *head, int k) {
    if (head == NULL || k == 0) return new_node(0);

    struct Node dummy = {0, NULL};   // placeholder so the answer list is never "empty"
    struct Node *tail = &dummy;
    int carry = 0;

    for (const struct Node *cur = head; cur != NULL; cur = cur->next) {
        int value = cur->digit * k + carry;
        tail->next = new_node(value % 10);
        tail = tail->next;
        carry = value / 10;
    }
    while (carry > 0) {            // leftover carry becomes extra digits
        tail->next = new_node(carry % 10);
        tail = tail->next;
        carry /= 10;
    }
    return dummy.next;
}

int main(void) {
    struct Node *head = new_node(9);
    head->next = new_node(9);              // 99, stored as 9 -> 9

    struct Node *result = multiply(head, 5);      // 495, stored as 5 -> 9 -> 4

    printf("99 * 5, LSD first: ");
    for (struct Node *cur = result; cur != NULL; cur = cur->next) printf("%d -> ", cur->digit);
    printf("null\n");

    free_list(head);
    free_list(result);
    return 0;
}
