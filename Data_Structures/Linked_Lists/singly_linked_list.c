// Singly Linked List (C version of SinglyLinkedList.java)
// Each node holds a value and a link to the NEXT node.
//
//   head --> [10] --> [20] --> [30] --> NULL
//                               ^
//                              tail
//
// Insert at head/tail and delete head are O(1); everything else is O(n).
//
// Build: gcc -std=c11 -Wall -Wextra singly_linked_list.c -o singly && ./singly
#include <stdio.h>
#include <stdlib.h>

struct Node {
    int value;
    struct Node *next;   // the node after this one (NULL if last)
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
    n->next = NULL;
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
    l->head = n;
    if (l->tail == NULL) l->tail = n;   // list was empty
    l->size++;
}

static void insert_tail(struct List *l, int value) {
    struct Node *n = new_node(value);
    if (l->tail == NULL) l->head = n;
    else l->tail->next = n;
    l->tail = n;
    l->size++;
}

static void insert_at(struct List *l, int index, int value) {
    if (index < 0 || index > l->size) { printf("Invalid position %d\n", index); return; }
    if (index == 0) { insert_head(l, value); return; }
    if (index == l->size) { insert_tail(l, value); return; }
    struct Node *before = node_at(l, index - 1);
    struct Node *n = new_node(value);
    n->next = before->next;
    before->next = n;
    l->size++;
}

static int delete_head(struct List *l) {
    if (is_empty(l)) { printf("List is empty!\n"); return -1; }
    struct Node *old = l->head;
    int value = old->value;
    l->head = old->next;
    if (l->head == NULL) l->tail = NULL;
    free(old);
    l->size--;
    return value;
}

static int delete_tail(struct List *l) {
    if (is_empty(l)) { printf("List is empty!\n"); return -1; }
    if (l->size == 1) return delete_head(l);
    struct Node *before = node_at(l, l->size - 2);   // no backward links: walk
    int value = l->tail->value;
    free(l->tail);
    before->next = NULL;
    l->tail = before;
    l->size--;
    return value;
}

static int delete_at(struct List *l, int index) {
    if (index < 0 || index >= l->size) { printf("Invalid position %d\n", index); return -1; }
    if (index == 0) return delete_head(l);
    if (index == l->size - 1) return delete_tail(l);
    struct Node *before = node_at(l, index - 1);
    struct Node *gone = before->next;
    int value = gone->value;
    before->next = gone->next;
    free(gone);
    l->size--;
    return value;
}

static int search(const struct List *l, int value) {
    int index = 0;
    for (struct Node *cur = l->head; cur != NULL; cur = cur->next, index++)
        if (cur->value == value) return index;
    return -1;
}

static void display(const struct List *l) {
    for (struct Node *cur = l->head; cur != NULL; cur = cur->next) printf("%d -> ", cur->value);
    printf("null\n");
}

int main(void) {
    printf("*** Singly Linked List ***\n");
    struct List list = {NULL, NULL, 0};

    insert_head(&list, 20);
    insert_head(&list, 10);
    insert_tail(&list, 40);
    insert_at(&list, 2, 30);
    display(&list);
    printf("30 is at position %d\n", search(&list, 30));

    printf("Deleted head: %d\n", delete_head(&list));
    printf("Deleted tail: %d\n", delete_tail(&list));
    printf("Deleted [1]:  %d\n", delete_at(&list, 1));
    display(&list);

    delete_head(&list);
    printf("Empty: %s\n", is_empty(&list) ? "true" : "false");
    delete_head(&list);
    return 0;
}
