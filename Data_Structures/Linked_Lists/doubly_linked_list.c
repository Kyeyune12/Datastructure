// Doubly Linked List (C version of DoublyLinkedList.java)
// Each node links to the NEXT and the PREVIOUS node.
//
//   NULL <-- [10] <--> [20] <--> [30] --> NULL
//            head                tail
//
// Insert/delete at either end is O(1); everything else is O(n).
//
// Build: gcc -std=c11 -Wall -Wextra doubly_linked_list.c -o doubly && ./doubly
#include <stdio.h>
#include <stdlib.h>

struct Node {
    int value;
    struct Node *prev;
    struct Node *next;
};

struct List {
    struct Node *head;
    struct Node *tail;
    int size;
};

static struct Node *new_node(int value) {
    struct Node *n = malloc(sizeof(struct Node));
    if (n == NULL) { fprintf(stderr, "Out of memory\n"); exit(1); }
    n->value = value;
    n->prev = n->next = NULL;
    return n;
}

static int is_empty(const struct List *l) { return l->head == NULL; }

static struct Node *node_at(const struct List *l, int index) {
    struct Node *cur = l->head;
    for (int i = 0; i < index; i++) cur = cur->next;
    return cur;
}

static void insert_head(struct List *l, int value) {
    struct Node *n = new_node(value);
    n->next = l->head;
    if (l->head == NULL) l->tail = n;
    else l->head->prev = n;
    l->head = n;
    l->size++;
}

static void insert_tail(struct List *l, int value) {
    struct Node *n = new_node(value);
    n->prev = l->tail;
    if (l->tail == NULL) l->head = n;
    else l->tail->next = n;
    l->tail = n;
    l->size++;
}

static void insert_at(struct List *l, int index, int value) {
    if (index < 0 || index > l->size) { printf("Invalid position %d\n", index); return; }
    if (index == 0) { insert_head(l, value); return; }
    if (index == l->size) { insert_tail(l, value); return; }
    struct Node *after = node_at(l, index);
    struct Node *before = after->prev;
    struct Node *n = new_node(value);
    n->prev = before; n->next = after;
    before->next = n; after->prev = n;
    l->size++;
}

// Cuts a node out of the list, frees it and returns its value.
static int unlink_node(struct List *l, struct Node *n) {
    if (n->prev == NULL) l->head = n->next; else n->prev->next = n->next;
    if (n->next == NULL) l->tail = n->prev; else n->next->prev = n->prev;
    int value = n->value;
    free(n);
    l->size--;
    return value;
}

static int delete_head(struct List *l) {
    if (is_empty(l)) { printf("List is empty!\n"); return -1; }
    return unlink_node(l, l->head);
}

static int delete_tail(struct List *l) {
    if (is_empty(l)) { printf("List is empty!\n"); return -1; }
    return unlink_node(l, l->tail);   // O(1): tail->prev is the new last node
}

static int delete_at(struct List *l, int index) {
    if (index < 0 || index >= l->size) { printf("Invalid position %d\n", index); return -1; }
    return unlink_node(l, node_at(l, index));
}

static int search(const struct List *l, int value) {
    int index = 0;
    for (struct Node *cur = l->head; cur != NULL; cur = cur->next, index++)
        if (cur->value == value) return index;
    return -1;
}

static void print_forward(const struct List *l) {
    for (struct Node *cur = l->head; cur != NULL; cur = cur->next) printf("%d ", cur->value);
    printf("\n");
}

static void print_backward(const struct List *l) {
    for (struct Node *cur = l->tail; cur != NULL; cur = cur->prev) printf("%d ", cur->value);
    printf("\n");
}

int main(void) {
    printf("*** Doubly Linked List ***\n");
    struct List list = {NULL, NULL, 0};

    insert_head(&list, 20);
    insert_head(&list, 10);
    insert_tail(&list, 40);
    insert_at(&list, 2, 30);

    printf("Forward:  "); print_forward(&list);
    printf("Backward: "); print_backward(&list);
    printf("30 is at position %d\n", search(&list, 30));

    printf("Deleted head: %d\n", delete_head(&list));
    printf("Deleted tail: %d\n", delete_tail(&list));
    printf("Deleted [1]:  %d\n", delete_at(&list, 1));
    printf("Forward:  "); print_forward(&list);
    return 0;
}
