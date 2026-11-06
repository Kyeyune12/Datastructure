#include <stdio.h>
#include <stdbool.h>

#define MAX 5

// A fixed-size stack (LIFO) stored in an array. top = index of the top item, -1 = empty.
// Build: gcc adts.c -o adts
// Run:   ./adts

struct Stack{
    int arr[MAX];
    int top;
};

void push (struct Stack* s, int value){
    if (s->top == MAX - 1) {
        printf("Stack overflow!\n");
        return;
    }
    s->arr[++(s->top)] = value;
}

int pop (struct Stack* s){
    if (s->top == -1){
        printf("Stack underflow!\n");
        return -1;
    }
    return s->arr[(s->top)--];
}

int peekStack (struct Stack* s){
    if (s->top == -1) {
        return -1;
    }
    return s->arr[s->top];
}

bool isStackEmpty (struct Stack* s){
    return s->top == -1;
}

int main(){
    printf("***Welcome to ADTS***");

    struct Stack s;
    s.top = -1;


    push(&s, 12); push(&s, 10); push(&s,13);
    printf("\ntop item (Peek): %d\n", peekStack(&s));
    printf("Popped: %d\n", pop(&s));
    printf("\ntop item (Peek): %d\n", peekStack(&s));

    return 0;
}
