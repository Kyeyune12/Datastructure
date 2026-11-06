#include <stdio.h>
#include <stdbool.h>

// A simple queue (FIFO) stored in an array: the FIRST value in is the FIRST out.
// Same style as the stack in adts.c.
// front and rear wrap around with % SIZE, so the array is used in a circle.
//
// Build: gcc queue.c -o queue
// Run:   ./queue

#define SIZE 5

struct Queue {
    int arr[SIZE];
    int front;   // index of the next value to leave
    int rear;    // index of the last value that joined
    int count;   // how many values are in the queue
};

bool isQueueEmpty(struct Queue* q) {
    return q->count == 0;
}

bool isQueueFull(struct Queue* q) {
    return q->count == SIZE;
}

void enqueue(struct Queue* q, int value) {
    if (isQueueFull(q)) {
        printf("Queue overflow!\n");
        return;
    }
    q->rear = (q->rear + 1) % SIZE;   // wrap back to 0 after the last slot
    q->arr[q->rear] = value;
    q->count++;
}

int dequeue(struct Queue* q) {
    if (isQueueEmpty(q)) {
        printf("Queue underflow!\n");
        return -1;
    }
    int value = q->arr[q->front];
    q->front = (q->front + 1) % SIZE;   // wrap back to 0 after the last slot
    q->count--;
    return value;
}

int peekQueue(struct Queue* q) {
    if (isQueueEmpty(q)) {
        return -1;
    }
    return q->arr[q->front];
}

int main() {
    printf("***Welcome to the Queue ADT***\n");

    struct Queue q;
    q.front = 0;
    q.rear = -1;
    q.count = 0;

    enqueue(&q, 12); enqueue(&q, 10); enqueue(&q, 13);
    printf("front item (Peek): %d\n", peekQueue(&q));
    printf("Dequeued: %d\n", dequeue(&q));
    printf("front item (Peek): %d\n", peekQueue(&q));

    return 0;
}
