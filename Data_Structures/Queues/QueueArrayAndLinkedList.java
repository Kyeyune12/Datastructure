import java.util.NoSuchElementException;

/**
 * javac QueueArrayAndLinkedList.java && java QueueArrayAndLinkedList
 * FIFO queues, O(1) enqueue/dequeue/peek. Circular array has fixed positive
 * capacity, head points at the oldest item, and size distinguishes empty/full.
 * Linked queue holds both head and tail; last removal resets both. Its optional
 * capacity limit supports backpressure. Default limit is Integer.MAX_VALUE,
 * but isFull is NOT a heap-availability test: allocation can fail earlier.
 * Array storage O(capacity); linked storage O(size). Not thread-safe.
 */
public final class QueueArrayAndLinkedList {
    private QueueArrayAndLinkedList() { }
    public static final class CircularArrayQueue {
        private final int[] data;
        private int head,size;
        public CircularArrayQueue(int capacity) {
            if (capacity<=0) throw new IllegalArgumentException("positive capacity required");
            data=new int[capacity];
        }
        public int size() { return size; }
        public boolean isEmpty() { return size==0; }
        public boolean isFull() { return size==data.length; }
        public void enqueue(int value) {
            if (isFull()) throw new IllegalStateException("full queue");
            int tail=(int)(((long)head+size)%data.length);
            data[tail]=value; size++;
        }
        public int peek() {
            if (isEmpty()) throw new NoSuchElementException("empty queue");
            return data[head];
        }
        public int dequeue() {
            int value=peek(); data[head]=0;
            head=(head+1)%data.length; size--; return value;
        }
        public void clear() { for (int i=0; i<data.length; i++) data[i]=0; head=0; size=0; }
    }
    public static final class LinkedQueue {
        private static final class Node {
            final int value; Node next;
            Node(int value) { this.value=value; }
        }
        private final int capacity;
        private Node head,tail;
        private int size;
        public LinkedQueue() { this(Integer.MAX_VALUE); }
        public LinkedQueue(int capacity) {
            if (capacity<=0) throw new IllegalArgumentException("positive capacity required");
            this.capacity=capacity;
        }
        public int size() { return size; }
        public boolean isEmpty() { return size==0; }
        public boolean isFull() { return size==capacity; }
        public void enqueue(int value) {
            if (isFull()) throw new IllegalStateException("full queue");
            Node n=new Node(value);
            if (tail==null) head=n; else tail.next=n;
            tail=n; size++;
        }
        public int peek() {
            if (isEmpty()) throw new NoSuchElementException("empty queue");
            return head.value;
        }
        public int dequeue() {
            int value=peek(); head=head.next;
            if (--size==0) tail=null; return value;
        }
        public void clear() { head=null; tail=null; size=0; }
    }
    public static void main(String[] args) {
        CircularArrayQueue a=new CircularArrayQueue(3); LinkedQueue b=new LinkedQueue(3);
        for (int x : new int[]{10,20,30}) { a.enqueue(x); b.enqueue(x); }
        System.out.println("Full: "+a.isFull()+", "+b.isFull());
        System.out.println(a.dequeue()+" = "+b.dequeue());
        a.enqueue(40); b.enqueue(40); // Wrap-around in the array queue.
        System.out.println("Peek: "+a.peek()+", "+b.peek());
        while (!a.isEmpty()) System.out.println(a.dequeue()+" = "+b.dequeue());
    }
}
