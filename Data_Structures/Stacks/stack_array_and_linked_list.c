// Stack (LIFO) - C version of StackArrayAndLinkedList.java
// The LAST item pushed is the FIRST item popped (a pile of plates).
//
// 1. ArrayStack  - items in an array; 'top' is the index of the top item.
// 2. LinkedStack - items in linked nodes; 'top' is the first node.
//
// push, pop and peek are all O(1).
//
// Build: gcc -std=c11 -Wall -Wextra stack_array_and_linked_list.c -o stack && ./stack
#include <stdio.h>
#include <stdlib.h>

// ---------- Version 1: array stack ----------
struct ArrayStack {
    int *data;
    int capacity;
    int top;          // -1 means empty
};

static struct ArrayStack *astack_new(int capacity) {
    struct ArrayStack *s = malloc(sizeof(struct ArrayStack));
    if (s == NULL) exit(1);
    s->data = malloc(sizeof(int) * (size_t)capacity);
    if (s->data == NULL) exit(1);
    s->capacity = capacity;
    s->top = -1;
    return s;
}
static int astack_empty(const struct ArrayStack *s) { return s->top == -1; }
static int astack_full(const struct ArrayStack *s)  { return s->top == s->capacity - 1; }
static void astack_push(struct ArrayStack *s, int v) {
    if (astack_full(s)) { printf("Stack overflow!\n"); return; }
    s->data[++s->top] = v;
}
static int astack_peek(const struct ArrayStack *s) { return astack_empty(s) ? -1 : s->data[s->top]; }
static int astack_pop(struct ArrayStack *s) {
    if (astack_empty(s)) { printf("Stack underflow!\n"); return -1; }
    return s->data[s->top--];
}

// ---------- Version 2: linked stack ----------
struct Node {
    int value;
    struct Node *next;   // the node underneath this one
};

struct LinkedStack { struct Node *top; };

static int lstack_empty(const struct LinkedStack *s) { return s->top == NULL; }
static void lstack_push(struct LinkedStack *s, int v) {
    struct Node *n = malloc(sizeof(struct Node));
    if (n == NULL) exit(1);
    n->value = v;
    n->next = s->top;
    s->top = n;
}
static int lstack_peek(const struct LinkedStack *s) { return lstack_empty(s) ? -1 : s->top->value; }
static int lstack_pop(struct LinkedStack *s) {
    if (lstack_empty(s)) { printf("Stack underflow!\n"); return -1; }
    struct Node *old = s->top;
    int v = old->value;
    s->top = old->next;
    free(old);
    return v;
}

int main(void) {
    printf("*** Stack: array vs linked list ***\n");
    struct ArrayStack *array = astack_new(5);
    struct LinkedStack linked = {NULL};

    astack_push(array, 10);  lstack_push(&linked, 10);
    astack_push(array, 20);  lstack_push(&linked, 20);
    astack_push(array, 30);  lstack_push(&linked, 30);

    printf("Peek: %d, %d\n", astack_peek(array), lstack_peek(&linked));

    while (!astack_empty(array)) {
        int fromArray = astack_pop(array);
        int fromLinked = lstack_pop(&linked);
        printf("%d = %d\n", fromArray, fromLinked);
    }
    astack_pop(array);   // prints "Stack underflow!"

    free(array->data);
    free(array);
    return 0;
}
