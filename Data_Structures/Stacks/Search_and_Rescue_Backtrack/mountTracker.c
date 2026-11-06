#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdbool.h>

#define NAME_LEN 30

struct Node {
    char junctionName[NAME_LEN];
    struct Node* next;
};

struct Stack {
    struct Node* top;
};

void push(struct Stack* s, char* junctionName) {
    struct Node* newNode = (struct Node*) malloc(sizeof(struct Node));
    if (newNode == NULL) {
        printf("Memory allocation failed! Cannot log junction.\n");
        return;
    }
    strcpy(newNode->junctionName, junctionName);
    newNode->next = s->top;
    s->top = newNode;
    printf("Advanced to %s.\n", junctionName);
}

char* pop(struct Stack* s) {
    if (s->top == NULL) {
        printf("Already at trailhead. No further junctions to retrace.\n");
        return NULL;
    }
    struct Node* temp = s->top;
    static char junction[NAME_LEN];
    strcpy(junction, temp->junctionName);
    s->top = temp->next;
    free(temp);
    return junction;
}

char* peekStack(struct Stack* s) {
    if (s->top == NULL) {
        return NULL;
    }
    return s->top->junctionName;
}

bool isStackEmpty(struct Stack* s) {
    return s->top == NULL;
}

void printTrail(struct Stack* s) {
    if (isStackEmpty(s)) {
        printf("Currently at trailhead. No junctions logged.\n");
        return;
    }
    printf("Current trail (most recent first): ");
    struct Node* current = s->top;
    while (current != NULL) {
        printf("%s", current->junctionName);
        if (current->next != NULL) printf(" -> ");
        current = current->next;
    }
    printf("\n");
}

int main() {
    struct Stack s;
    s.top = NULL;
    int choice;
    char junctionName[NAME_LEN];

    printf("*** Search & Rescue Backtrack Assistant ***\n");

    while (1) {
        printf("\n1. Advance to next junction (push)\n");
        printf("2. Backtrack one step (pop)\n");
        printf("3. View current position / trail so far\n");
        printf("4. Exit\n");
        printf("Enter choice: ");
        scanf("%d", &choice);

        if (choice == 1) {
            printf("Enter junction name: ");
            scanf("%s", junctionName);
            push(&s, junctionName);

        } else if (choice == 2) {
            char* junction = pop(&s);
            if (junction != NULL) {
                printf("Dead end. Backtracking... left %s.\n", junction);
                char* current = peekStack(&s);
                if (current != NULL) {
                    printf("Now at %s.\n", current);
                } else {
                    printf("Now at Trailhead.\n");
                }
            }

        } else if (choice == 3) {
            printTrail(&s);

        } else if (choice == 4) {
            printf("Exiting Search & Rescue Assistant.\n");
            break;

        } else {
            printf("Invalid choice. Please enter 1-4.\n");
        }
    }

    return 0;
}