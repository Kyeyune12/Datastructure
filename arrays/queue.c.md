# queue.c

A **placeholder** for a queue. The file contains only an `#include`, a `#define` and a commented-out struct. There is no queue code yet.

**Language:** C · **Status:** unfinished · **Finished queue implementation:** [QueueArrayAndLinkedList.java](../Data_Structures/Queues/QueueArrayAndLinkedList.java.md)

## What is in the file

```c
#include <stdio.h>

#define SIZE = 5

//struct Point{
//int x = 8;
// int y = 5;
//};
```

## Problems to fix

| Line | Problem | Fix |
| --- | --- | --- |
| `#define SIZE = 5` | `#define` is plain text substitution, so `SIZE` would become `= 5`. As soon as `SIZE` is used (e.g. `int q[SIZE];` becomes `int q[= 5];`) it is a syntax error | `#define SIZE 5`, with no `=` |
| `struct Point { int x = 8; … }` (commented out) | C doesn't allow default values inside a struct definition | Declare `int x; int y;`, then initialise when creating it: `struct Point p = {8, 5};` |

## A circular array queue to complete the file

This matches the design of the Java `CircularArrayQueue`:

```c
#include <stdio.h>
#include <stdbool.h>

#define SIZE 5

struct Queue {
    int data[SIZE];
    int head;       /* index of the oldest item        */
    int size;       /* number of items currently held  */
};

void initQueue(struct Queue *q) { q->head = 0; q->size = 0; }
bool isEmpty(const struct Queue *q) { return q->size == 0; }
bool isFull(const struct Queue *q)  { return q->size == SIZE; }

bool enqueue(struct Queue *q, int value) {
    if (isFull(q)) return false;
    q->data[(q->head + q->size) % SIZE] = value;   /* wraps around */
    q->size++;
    return true;
}

bool dequeue(struct Queue *q, int *out) {
    if (isEmpty(q)) return false;
    *out = q->data[q->head];
    q->head = (q->head + 1) % SIZE;
    q->size--;
    return true;
}

int main(void) {
    struct Queue q; initQueue(&q);
    for (int v = 10; v <= 30; v += 10) enqueue(&q, v);
    int x;
    while (dequeue(&q, &x)) printf("%d ", x);      /* 10 20 30 */
    printf("\n");
    return 0;
}
```

Every operation is O(1). Keeping `size` is what tells "empty" from "full".
