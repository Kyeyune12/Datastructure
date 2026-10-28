/**
 * javac QueueArrayAndLinkedList.java && java QueueArrayAndLinkedList
 *
 * A queue is FIFO: the FIRST item added is the FIRST item removed
 * (like people in a line at the bank).
 *
 * Two versions with the same operations:
 *   1. CircularArrayQueue - items live in an array; 'front' moves forward
 *      and wraps back to 0 at the end of the array (using % capacity).
 *   2. LinkedQueue - items live in linked nodes; add at the tail,
 *      remove from the head.
 *
 * enqueue, dequeue and peek are all O(1).
 */
public class QueueArrayAndLinkedList {

    // ================= 1. Queue using a circular array =================

    static class CircularArrayQueue {
        private int[] data;
        private int front;   // index of the oldest item (the next one to leave)
        private int size;    // how many items are in the queue

        CircularArrayQueue(int capacity) {
            data = new int[capacity];
        }

        boolean isEmpty() {
            return size == 0;
        }

        boolean isFull() {
            return size == data.length;
        }

        // add a value at the back
        void enqueue(int value) {
            if (isFull()) {
                throw new IllegalStateException("full queue");
            }
            // the back of the queue is 'size' steps after the front;
            // % data.length wraps it back to 0 when it passes the end
            int rear = (front + size) % data.length;
            data[rear] = value;
            size++;
        }

        // look at the front value without removing it
        int peek() {
            if (isEmpty()) {
                throw new IllegalStateException("empty queue");
            }
            return data[front];
        }

        // remove and return the front value
        int dequeue() {
            int value = peek();
            front = (front + 1) % data.length;   // move front forward, wrapping around
            size--;
            return value;
        }
    }

    // ================= 2. Queue using a linked list =================

    static class LinkedQueue {

        private static class Node {
            int value;
            Node next;

            Node(int value) {
                this.value = value;
            }
        }

        private Node head;   // front of the queue: items leave from here
        private Node tail;   // back of the queue: items join here
        private int size;

        boolean isEmpty() {
            return size == 0;
        }

        // add a value at the back
        void enqueue(int value) {
            Node newNode = new Node(value);

            if (tail == null) {
                head = newNode;        // queue was empty
            } else {
                tail.next = newNode;   // old back node points to the new one
            }
            tail = newNode;
            size++;
        }

        // look at the front value without removing it
        int peek() {
            if (isEmpty()) {
                throw new IllegalStateException("empty queue");
            }
            return head.value;
        }

        // remove and return the front value
        int dequeue() {
            int value = peek();
            head = head.next;

            if (head == null) {
                tail = null;           // queue is now empty
            }
            size--;
            return value;
        }
    }

    public static void main(String[] args) {
        CircularArrayQueue a = new CircularArrayQueue(3);
        LinkedQueue b = new LinkedQueue();

        // add 10, 20, 30 to both queues
        a.enqueue(10);
        b.enqueue(10);

        a.enqueue(20);
        b.enqueue(20);

        a.enqueue(30);
        b.enqueue(30);

        System.out.println("Array queue full: " + a.isFull());

        // remove 10 from both
        System.out.println(a.dequeue() + " = " + b.dequeue());

        // add 40: in the array queue this wraps around to index 0
        a.enqueue(40);
        b.enqueue(40);
        System.out.println("Peek: " + a.peek() + ", " + b.peek());

        // empty both queues: items come out in the order they went in
        while (!a.isEmpty()) {
            int fromArray = a.dequeue();
            int fromLinked = b.dequeue();
            System.out.println(fromArray + " = " + fromLinked);
        }
    }
}
