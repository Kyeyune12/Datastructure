/*
 * Queue (FIFO): the FIRST item added is the FIRST item removed
 * (like people in a line at the bank).
 *
 * Two versions with the same operations:
 *   1. CircularArrayQueue - items live in an array; 'front' moves forward
 *      and wraps back to 0 at the end of the array (using % capacity).
 *   2. LinkedQueue - items live in linked nodes; add at the tail,
 *      remove from the head.
 *
 * enqueue, dequeue and peek are all O(1).
 *
 * Run: javac QueueArrayAndLinkedList.java && java QueueArrayAndLinkedList
 */
public class QueueArrayAndLinkedList {

    // ================= 1. Queue using a circular array =================

    static class CircularArrayQueue {
        int[] data;
        int front = 0;   // index of the oldest item (the next one to leave)
        int rear = -1;   // index of the newest item
        int count = 0;   // how many items are in the queue

        CircularArrayQueue(int capacity) {
            data = new int[capacity];
        }

        boolean isEmpty() {
            return count == 0;
        }

        boolean isFull() {
            return count == data.length;
        }

        // add a value at the back
        void enqueue(int value) {
            if (isFull()) {
                System.out.println("Queue is full!");
                return;
            }
            rear = (rear + 1) % data.length;   // move rear forward, wrapping to 0 at the end
            data[rear] = value;
            count++;
        }

        // look at the front value without removing it (-1 if empty)
        int peek() {
            if (isEmpty()) {
                return -1;
            }
            return data[front];
        }

        // remove and return the front value (-1 if empty)
        int dequeue() {
            if (isEmpty()) {
                System.out.println("Queue is empty!");
                return -1;
            }
            int value = data[front];
            front = (front + 1) % data.length;   // move front forward, wrapping to 0 at the end
            count--;
            return value;
        }
    }

    // ================= 2. Queue using a linked list =================

    static class Node {
        int value;
        Node next;

        Node(int value) {
            this.value = value;
        }
    }

    static class LinkedQueue {
        Node head;   // front of the queue: items leave from here
        Node tail;   // back of the queue: items join here

        boolean isEmpty() {
            return head == null;
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
        }

        // look at the front value without removing it (-1 if empty)
        int peek() {
            if (isEmpty()) {
                return -1;
            }
            return head.value;
        }

        // remove and return the front value (-1 if empty)
        int dequeue() {
            if (isEmpty()) {
                System.out.println("Queue is empty!");
                return -1;
            }
            int value = head.value;
            head = head.next;

            if (head == null) {
                tail = null;           // queue is now empty
            }
            return value;
        }
    }

    public static void main(String[] args) {
        System.out.println("*** Queue: circular array vs linked list ***");
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
