#include <stdio.h>
#include <string.h>

// Project B: Warehouse Conveyor Dispatch
// Data structure: Queue (FIFO) in a fixed-size CIRCULAR array.
//
// Packages are loaded at the rear (enqueue) and dispatched from the front
// (dequeue) in the order they arrived. front and rear wrap around with
// (index + 1) % CAPACITY, so no element is ever shifted.
//
// Build: gcc circular_queue-array.c -o conveyor
// Run:   ./conveyor

#define CAPACITY 5
#define NAME_LEN 30

// One package on the belt
struct Package {
    int id;
    char destination[NAME_LEN];
};

// The belt itself
struct Queue {
    struct Package items[CAPACITY];
    int front;   // index of the next package to leave
    int rear;    // index of the last package that joined
    int count;   // how many packages are on the belt
};

int isEmpty(struct Queue* q) {
    return q->count == 0;
}

int isFull(struct Queue* q) {
    return q->count == CAPACITY;
}

// Add a package at the rear
void enqueue(struct Queue* q, int id, char* destination) {
    if (isFull(q)) {
        printf("Belt is full! Cannot load package #%d.\n", id);
        return;
    }
    q->rear = (q->rear + 1) % CAPACITY;   // move rear forward, wrap to 0 at the end
    q->items[q->rear].id = id;
    strcpy(q->items[q->rear].destination, destination);
    q->count++;
    printf("Loaded package #%d -> %s at slot %d.\n", id, destination, q->rear);
}

// Remove the package at the front. Returns 1 on success, 0 if the belt is empty.
int dequeue(struct Queue* q, struct Package* out) {
    if (isEmpty(q)) {
        printf("Belt is empty! Nothing to dispatch.\n");
        return 0;
    }
    *out = q->items[q->front];              // copy the package out first
    q->front = (q->front + 1) % CAPACITY;   // move front forward, wrap to 0 at the end
    q->count--;
    return 1;
}

// Look at the front package without removing it
int peek(struct Queue* q, struct Package* out) {
    if (isEmpty(q)) {
        return 0;
    }
    *out = q->items[q->front];
    return 1;
}

// Print every package from front to rear
void display(struct Queue* q) {
    if (isEmpty(q)) {
        printf("Belt is empty.\n");
        return;
    }
    printf("Belt (front to rear):\n");
    for (int i = 0; i < q->count; i++) {
        int index = (q->front + i) % CAPACITY;
        printf("  Slot %d: Package #%d -> %s\n", index, q->items[index].id, q->items[index].destination);
    }
}

int main() {
    struct Queue belt;
    belt.front = 0;
    belt.rear = -1;
    belt.count = 0;

    struct Package p;
    char destination[NAME_LEN];
    int nextId = 1;   // package IDs are given out automatically
    int choice;

    printf("*** Warehouse Conveyor Dispatch ***\n");

    while (1) {
        printf("\n1. Load package (enqueue)\n");
        printf("2. Dispatch next package (dequeue)\n");
        printf("3. Next package (peek)\n");
        printf("4. View the belt\n");
        printf("5. isEmpty / isFull\n");
        printf("6. Exit\n");
        printf("Enter choice: ");

        if (scanf("%d", &choice) != 1) {
            printf("Please enter a number.\n");
            while (getchar() != '\n');   // clear the bad input
            continue;
        }

        if (choice == 1) {
            printf("Enter destination (no spaces): ");
            scanf("%29s", destination);
            enqueue(&belt, nextId, destination);
            nextId++;

        } else if (choice == 2) {
            if (dequeue(&belt, &p)) {
                printf("Dispatched package #%d -> %s.\n", p.id, p.destination);
            }

        } else if (choice == 3) {
            if (peek(&belt, &p)) {
                printf("Next to dispatch: package #%d -> %s\n", p.id, p.destination);
            } else {
                printf("Belt is empty.\n");
            }

        } else if (choice == 4) {
            display(&belt);

        } else if (choice == 5) {
            printf("isEmpty: %d   isFull: %d   (1 = yes, 0 = no)\n", isEmpty(&belt), isFull(&belt));

        } else if (choice == 6) {
            printf("Exiting. Goodbye!\n");
            break;

        } else {
            printf("Invalid choice. Please enter 1-6.\n");
        }
    }

    return 0;
}
