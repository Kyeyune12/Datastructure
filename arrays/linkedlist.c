#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Builds a 4-node singly linked list BY HAND, prints it, then frees it.
// Shows what a node is: some data plus a pointer to the next node.
//
// Build: gcc linkedlist.c -o linkedlist
// Run:   ./linkedlist

struct Node {
    int data;
    char name[12];
    struct Node* next;
};

int main() {
    printf("*** Linked list built by hand ***\n");

    // creating nodes (malloc gives us memory for one node each)
    struct Node* head = (struct Node*) malloc(sizeof(struct Node));
    struct Node* first = (struct Node*) malloc(sizeof(struct Node));
    struct Node* second = (struct Node*) malloc(sizeof(struct Node));
    struct Node* third = (struct Node*) malloc(sizeof(struct Node));

    // assigning data and chaining them together
    head->data = 1;
    strcpy(head->name, "head");
    head->next = first;

    first->data = 10;
    first->next = second;

    second->data = 20;
    second->next = third;

    third->data = 30;
    third->next = NULL;   // NULL marks the end of the list

    // walk the list from head until we fall off the end
    struct Node* current = head;
    while (current != NULL) {
        if (current == head) {
            printf("name: %s\n", current->name);
        } else {
            printf("Clue data: %d\n", current->data);
        }
        current = current->next;
    }

    // give the memory back
    free(head);
    free(first);
    free(second);
    free(third);

    return 0;
}
