/**
 * javac StackArrayAndLinkedList.java && java StackArrayAndLinkedList
 *
 * A stack is LIFO: the LAST item pushed is the FIRST item popped
 * (like a pile of plates - you take from the top).
 *
 * Two versions with the same operations:
 *   1. ArrayStack  - items live in an array; 'top' is the index of the top item.
 *   2. LinkedStack - items live in linked nodes; 'top' is the first node.
 *
 * push, pop and peek are all O(1).
 */
public class StackArrayAndLinkedList {

    // ================= 1. Stack using an array =================

    static class ArrayStack {
        private int[] data;
        private int top = -1;   // index of the top item; -1 means the stack is empty

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
                throw new IllegalStateException("stack is full");
            }
            top = top + 1;
            data[top] = value;
        }

        // look at the top value without removing it
        int peek() {
            if (isEmpty()) {
                throw new IllegalStateException("stack is empty");
            }
            return data[top];
        }

        // remove and return the top value
        int pop() {
            int value = peek();
            top = top - 1;   // the old slot will simply be overwritten by the next push
            return value;
        }
    }

    // ================= 2. Stack using a linked list =================

    static class LinkedStack {

        private static class Node {
            int value;
            Node next;   // the node underneath this one

            Node(int value, Node next) {
                this.value = value;
                this.next = next;
            }
        }

        private Node top;   // top of the stack; null means empty

        boolean isEmpty() {
            return top == null;
        }

        // put a value on top: the new node points down to the old top
        void push(int value) {
            Node newNode = new Node(value, top);
            top = newNode;
        }

        // look at the top value without removing it
        int peek() {
            if (isEmpty()) {
                throw new IllegalStateException("stack is empty");
            }
            return top.value;
        }

        // remove and return the top value: move top down one node
        int pop() {
            int value = peek();
            top = top.next;
            return value;
        }
    }

    public static void main(String[] args) {
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
    }
}
