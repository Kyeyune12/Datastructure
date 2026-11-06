// Circular Linked List (C version of CircularLinkedList.java)
// The last node links back to the first, so there is no NULL at the end.
//
//        +--> [1] --> [2] --> [3] --+
//        |                     tail |
//        +--------------------------+
//
// Only the tail is stored; the head is always tail->next.
// Loops count up to size instead of looking for NULL.
//
// Build: gcc -std=c11 -Wall -Wextra circular_linked_list.c -o circular && ./circular
#include <stdio.h>
#include <stdlib.h>

struct Node {
    int value;
    struct Node *next;
};

struct Ring {
    struct Node *tail;   // last node; tail->next is the first node
    int size;
};

static struct Node *new_node(int value) {
    struct Node *n = malloc(sizeof(struct Node));
    if (n == NULL) { fprintf(stderr, "Out of memory\n"); exit(1); }
    n->value = value;
    n->next = NULL;
    return n;
}

static int is_empty(const struct Ring *r) { return r->tail == NULL; }

static struct Node *node_at(const struct Ring *r, int index) {
    struct Node *cur = r->tail->next;   // start at the head
    for (int i = 0; i < index; i++) cur = cur->next;
    return cur;
}

static void insert_head(struct Ring *r, int value) {
    struct Node *n = new_node(value);
    if (r->tail == NULL) { n->next = n; r->tail = n; }   // points to itself
    else { n->next = r->tail->next; r->tail->next = n; }
    r->size++;
}

static void insert_tail(struct Ring *r, int value) {
    insert_head(r, value);
    r->tail = r->tail->next;   // the new head becomes the tail: ring rotates
}

static void insert_at(struct Ring *r, int index, int value) {
    if (index < 0 || index > r->size) { printf("Invalid position %d\n", index); return; }
    if (index == 0) { insert_head(r, value); return; }
    if (index == r->size) { insert_tail(r, value); return; }
    struct Node *before = node_at(r, index - 1);
    struct Node *n = new_node(value);
    n->next = before->next;
    before->next = n;
    r->size++;
}

static int delete_head(struct Ring *r) {
    if (is_empty(r)) { printf("List is empty!\n"); return -1; }
    struct Node *head = r->tail->next;
    int value = head->value;
    if (r->size == 1) r->tail = NULL;
    else r->tail->next = head->next;
    free(head);
    r->size--;
    return value;
}

static int delete_tail(struct Ring *r) {
    if (is_empty(r)) { printf("List is empty!\n"); return -1; }
    if (r->size == 1) return delete_head(r);
    struct Node *before = node_at(r, r->size - 2);
    int value = r->tail->value;
    before->next = r->tail->next;
    free(r->tail);
    r->tail = before;
    r->size--;
    return value;
}

static int delete_at(struct Ring *r, int index) {
    if (index < 0 || index >= r->size) { printf("Invalid position %d\n", index); return -1; }
    if (index == 0) return delete_head(r);
    if (index == r->size - 1) return delete_tail(r);
    struct Node *before = node_at(r, index - 1);
    struct Node *gone = before->next;
    int value = gone->value;
    before->next = gone->next;
    free(gone);
    r->size--;
    return value;
}

static int search(const struct Ring *r, int value) {
    if (is_empty(r)) return -1;
    struct Node *cur = r->tail->next;
    for (int i = 0; i < r->size; i++, cur = cur->next)
        if (cur->value == value) return i;
    return -1;
}

// Splices 'other' onto the end of 'r' in O(1) by swapping two links.
static void merge_from(struct Ring *r, struct Ring *other) {
    if (r == other || is_empty(other)) return;
    if (is_empty(r)) r->tail = other->tail;
    else {
        struct Node *head = r->tail->next;
        r->tail->next = other->tail->next;   // our tail -> other's head
        other->tail->next = head;            // other's tail -> our head
        r->tail = other->tail;
    }
    r->size += other->size;
    other->tail = NULL;
    other->size = 0;
}

static void display(const struct Ring *r) {
    if (is_empty(r)) { printf("(empty)\n"); return; }
    struct Node *cur = r->tail->next;
    for (int i = 0; i < r->size; i++, cur = cur->next) printf("%d -> ", cur->value);
    printf("(back to %d)\n", r->tail->next->value);
}

int main(void) {
    printf("*** Circular Linked List ***\n");
    struct Ring a = {NULL, 0}, b = {NULL, 0};

    insert_head(&a, 2);
    insert_head(&a, 1);
    insert_tail(&a, 4);
    insert_at(&a, 2, 3);

    insert_tail(&b, 5);
    insert_tail(&b, 6);

    merge_from(&a, &b);
    printf("Merged ring: "); display(&a);
    printf("Donor ring empty: %s\n", is_empty(&b) ? "true" : "false");
    printf("3 is at position %d\n", search(&a, 3));

    printf("Deleted head: %d\n", delete_head(&a));
    printf("Deleted tail: %d\n", delete_tail(&a));
    printf("Deleted [1]:  %d\n", delete_at(&a, 1));
    display(&a);
    return 0;
}
