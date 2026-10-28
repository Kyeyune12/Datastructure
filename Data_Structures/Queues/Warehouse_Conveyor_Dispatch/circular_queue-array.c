#include <stdio.h>
#include <string.h>
 
#define CAPACITY 5  

//single item on the belt
typedef struct {
    int  packageId;
    int numItems;
    char destination[30];
} Package;

typedef struct {
    Package items[CAPACITY];
    int front;
    int rear;
    int count;
} ConveyorQueue;

void initQueue(ConveyorQueue *q) {
    q->front = 0;
    q->rear  = -1;
    q->count = 0;
}

/* isEmpty: belt has nothing on it */
int isEmpty(ConveyorQueue *q) {
    return q->count == 0;
}

int isFull(ConveyorQueue *q) {
    return q->count == CAPACITY;
}

void displayQueue(ConveyorQueue *q) {
    if (isEmpty(q)) {
        printf("  [BELT EMPTY] Conveyor belt is empty.\n");
        return;
    }

    printf("  [BELT STATUS] Queue contents from front to rear:\n");
    int i;
    for (i = 0; i < q->count; i++) {
        int index = (q->front + i) % CAPACITY;
        printf("    Slot %d: Package #%d -> %s\n",
               index, q->items[index].packageId, q->items[index].destination);
    }
}

//ADDITION OF PACKAGES
void enqueue(ConveyorQueue *q, int id, const char *destination) {
    if (isFull(q)) {
        printf("  [BELT FULL] Cannot load package #%d. "
               "Conveyor belt has reached its %d-package capacity.\n",
               id, CAPACITY);
        return;
    }

    q->rear = (q->rear + 1) % CAPACITY;      /* wrap-around logic */
    q->items[q->rear].packageId = id;
    strncpy(q->items[q->rear].destination, destination, sizeof(q->items[q->rear].destination) - 1);
    q->items[q->rear].destination[sizeof(q->items[q->rear].destination) - 1] = '\0';
    q->count++; 
 
    printf("  [LOADED] Package #%d -> %s placed on belt at slot %d.\n",
           id, destination, q->rear);
}

//REMOVAL OF PACKAGES 
int dequeue(ConveyorQueue *q, Package *dispatched) {
    if (isEmpty(q)) {
        printf("  [BELT EMPTY] Nothing to dispatch - the conveyor belt is empty.\n");
        return 0;   /* signal failure to caller */
    }
 
    *dispatched = q->items[q->front];        /* grab item before we move on */
    q->front = (q->front + 1) % CAPACITY;    /* wrap-around logic */
    q->count--;
 
    return 1;   /* signal success */
}

int peekFront(ConveyorQueue *q, Package *next) {
    if (isEmpty(q)) return 0;
    *next = q->items[q->front];
    return 1;
}

void printMenu(void) {
    printf("\n===== WAREHOUSE CONVEYOR BELT DISPATCH SYSTEM =====\n");
    printf("1. Load package onto belt   (enqueue)\n");
    printf("2. Dispatch next package    (dequeue)\n");
    printf("3. Peek next package        (front)\n");
    printf("4. View entire belt         (display)\n");
    printf("5. Check isEmpty / isFull\n");
    printf("6. Exit\n");
    printf("Choose an option: ");
}

int main(void) {
    ConveyorQueue belt;
    initQueue(&belt);
 
    int choice;
    char destination[30];
    Package result;
    int nextId = 1; /* auto-generate package IDs for convenience */
 
    while (1) {
        printMenu();
        if (scanf("%d", &choice) != 1) {
            /* handle bad (non-numeric) input so the program never crashes */
            printf("Invalid input. Please enter a number.\n");
            while (getchar() != '\n'); /* clear bad input from the buffer */
            continue;
        }
 
        switch (choice) {
            case 1:
                printf("  Enter destination bay for this package (no spaces): ");
                scanf("%29s", destination);
                enqueue(&belt, nextId, destination);
                nextId++;
                break;
 
            case 2:
                if (dequeue(&belt, &result))
                    printf("  [DISPATCHED] Package #%d -> %s has left the belt.\n",
                           result.packageId, result.destination);
                break;
 
            case 3:
                if (peekFront(&belt, &result))
                    printf("  Next to dispatch: Package #%d -> %s\n",
                           result.packageId, result.destination);
                else
                    printf("  Belt is empty, nothing to peek at.\n");
                break;
 
            case 4:
                displayQueue(&belt);
                break;
 
            case 5:
                printf("  isEmpty: %s   isFull: %s\n",
                       isEmpty(&belt) ? "YES" : "NO",
                       isFull(&belt)  ? "YES" : "NO");
                break;
 
            case 6:
                printf("Exiting. Goodbye!\n");
                return 0;
 
            default:
                printf("Invalid option, choose between 1 and 6.\n");
        }
    }
 
    return 0;
}