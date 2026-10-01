#include <stdio.h>
#include <string.h>

struct Node{
    int data;
    char name[12];
    struct Node* next;
};


    int maiin(){

    //creating nodes
    struct Node* head = (struct Node*)malloc(sizeof(struct Node));
    struct Node* first = (struct Node*)malloc(sizeof(struct Node));
    struct Node* second = (struct Node*)malloc(sizeof(struct Node));
    struct Node* third = (struct Node*)malloc (sizeof(struct Node));

    // assigning data and chaining them together
    head->data = 1;
    strcpy(head->name, "head");
    head->next = first;

    first->data = 10;
    first->next = second;

    second->data = 20;
    second->next = third;

    third->data = 30;
    third->next = NULL;

    struct Node* current = head;
    while (current != NULL){
        if(current == head){
            printf("name: %s\n",current->name);
        }
        else {
            printf("Clue data: %d\n", current->data);
        }
        current = current->next;
    }

    free(head);
    free(first);
    free(second);
    free(third);

    return 0;
}
