/*
 * Stack (LIFO): the LAST item pushed is the FIRST item popped
 * (like a pile of plates - you take from the top).
 *
 * Two versions with the same operations:
 *   1. ArrayStack  - items live in an array; 'top' is the index of the top item.
 *   2. LinkedStack - items live in linked nodes; 'top' is the first node.
 *
 * push, pop and peek are all O(1).
 *
 * Run: javac StackArrayAndLinkedList.java && java StackArrayAndLinkedList
 */
public class StackArrayAndLinkedList {

    // ================= 1. Stack using an array =================

    static class ArrayStack {
        int[] data;
        int top = -1;   // index of the top item; -1 means the stack is empty

        ArrayStack(int capacity) {
            data = new int[capacity];
        }

        boolean isEmpty() {
            return top == -1;
        }

        // full when the top item is in the last slot of the array
        boolean isFull() {
            return top == data.length - 1;
        }

        // put a value on top
        void push(int value) {
            if (isFull()) {
                System.out.println("Stack overflow!");
                return;
            }
            top = top + 1;
            data[top] = value;
        }

        // look at the top value without removing it (-1 if empty)
        int peek() {
            if (isEmpty()) {
                return -1;
            }
            return data[top];
        }

        // remove and return the top value (-1 if empty)
        int pop() {
            if (isEmpty()) {
                System.out.println("Stack underflow!");
                return -1;
            }
            int value = data[top];
            top = top - 1;   // the old slot will simply be overwritten by the next push
            return value;
        }
    }

    // ================= 2. Stack using a linked list =================

    static class Node {
        int value;
        Node next;   // the node underneath this one

        Node(int value, Node next) {
            this.value = value;
            this.next = next;
        }
    }

    static class LinkedStack {
        Node top;   // top of the stack; null means empty

        boolean isEmpty() {
            return top == null;
        }

        // put a value on top: the new node points down to the old top
        // (no isFull - a linked stack only runs out when memory runs out)
        void push(int value) {
            top = new Node(value, top);
        }

        // look at the top value without removing it (-1 if empty)
        int peek() {
            if (isEmpty()) {
                return -1;
            }
            return top.value;
        }

        // remove and return the top value: move top down one node (-1 if empty)
        int pop() {
            if (isEmpty()) {
                System.out.println("Stack underflow!");
                return -1;
            }
            int value = top.value;
            top = top.next;
            return value;
        }
    }

    public static void main(String[] args) {
        System.out.println("*** Stack: array vs linked list ***");
        ArrayStack array = new ArrayStack(5);
        LinkedStack linked = new LinkedStack();

        // push 10, 20, 30 onto both stacks
        array.push(10);
        linked.push(10);

        array.push(20);
        linked.push(20);

        array.push(30);
        linked.push(30);

        System.out.println("Peek: " + array.peek() + ", " + linked.peek());

        // pop everything: comes out in reverse order (30, 20, 10)
        while (!array.isEmpty()) {
            int fromArray = array.pop();
            int fromLinked = linked.pop();
            System.out.println(fromArray + " = " + fromLinked);
        }

        array.pop();   // prints "Stack underflow!"
    }
}
