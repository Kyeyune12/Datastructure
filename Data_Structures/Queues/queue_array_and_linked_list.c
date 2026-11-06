// Queue (FIFO) - C version of QueueArrayAndLinkedList.java
// The FIRST item added is the FIRST item removed (a line at the bank).
//
// 1. CircularArrayQueue - array; indexes wrap around using % capacity.
// 2. LinkedQueue        - nodes; add at the tail, remove from the head.
//
// enqueue, dequeue and peek are all O(1).
//
// Build: gcc -std=c11 -Wall -Wextra queue_array_and_linked_list.c -o queue && ./queue
#include <stdio.h>
#include <stdlib.h>

// ---------- Version 1: circular array queue ----------
struct ArrayQueue {
    int *data;
    int capacity;
    int front;   // index of the oldest item
    int rear;    // index of the newest item
    int count;
};

static struct ArrayQueue *aqueue_new(int capacity) {
    struct ArrayQueue *q = malloc(sizeof(struct ArrayQueue));
    if (q == NULL) exit(1);
    q->data = malloc(sizeof(int) * (size_t)capacity);
    if (q->data == NULL) exit(1);
    q->capacity = capacity;
    q->front = 0; q->rear = -1; q->count = 0;
    return q;
}
static int aqueue_empty(const struct ArrayQueue *q) { return q->count == 0; }
static int aqueue_full(const struct ArrayQueue *q)  { return q->count == q->capacity; }
static void aqueue_enqueue(struct ArrayQueue *q, int v) {
    if (aqueue_full(q)) { printf("Queue is full!\n"); return; }
    q->rear = (q->rear + 1) % q->capacity;   // wrap to 0 at the end
    q->data[q->rear] = v;
    q->count++;
}
static int aqueue_peek(const struct ArrayQueue *q) { return aqueue_empty(q) ? -1 : q->data[q->front]; }
static int aqueue_dequeue(struct ArrayQueue *q) {
    if (aqueue_empty(q)) { printf("Queue is empty!\n"); return -1; }
    int v = q->data[q->front];
    q->front = (q->front + 1) % q->capacity;
    q->count--;
    return v;
}

// ---------- Version 2: linked queue ----------
struct Node {
    int value;
    struct Node *next;
};

struct LinkedQueue { struct Node *head; struct Node *tail; };

static int lqueue_empty(const struct LinkedQueue *q) { return q->head == NULL; }
static void lqueue_enqueue(struct LinkedQueue *q, int v) {
    struct Node *n = malloc(sizeof(struct Node));
    if (n == NULL) exit(1);
    n->value = v;
    n->next = NULL;
    if (q->tail == NULL) q->head = n; else q->tail->next = n;
    q->tail = n;
}
static int lqueue_peek(const struct LinkedQueue *q) { return lqueue_empty(q) ? -1 : q->head->value; }
static int lqueue_dequeue(struct LinkedQueue *q) {
    if (lqueue_empty(q)) { printf("Queue is empty!\n"); return -1; }
    struct Node *old = q->head;
    int v = old->value;
    q->head = old->next;
    if (q->head == NULL) q->tail = NULL;
    free(old);
    return v;
}

int main(void) {
    printf("*** Queue: circular array vs linked list ***\n");
    struct ArrayQueue *a = aqueue_new(3);
    struct LinkedQueue b = {NULL, NULL};

    aqueue_enqueue(a, 10);  lqueue_enqueue(&b, 10);
    aqueue_enqueue(a, 20);  lqueue_enqueue(&b, 20);
    aqueue_enqueue(a, 30);  lqueue_enqueue(&b, 30);

    printf("Array queue full: %s\n", aqueue_full(a) ? "true" : "false");
    printf("%d = %d\n", aqueue_dequeue(a), lqueue_dequeue(&b));

    aqueue_enqueue(a, 40);  lqueue_enqueue(&b, 40);   // rear wraps to index 0
    printf("Peek: %d, %d\n", aqueue_peek(a), lqueue_peek(&b));

    while (!aqueue_empty(a)) {
        int fromArray = aqueue_dequeue(a);
        int fromLinked = lqueue_dequeue(&b);
        printf("%d = %d\n", fromArray, fromLinked);
    }

    free(a->data);
    free(a);
    return 0;
}
