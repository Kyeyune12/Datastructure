import java.util.NoSuchElementException;

/**
 * javac StackArrayAndLinkedList.java && java StackArrayAndLinkedList
 * LIFO: last pushed, first popped. Both implementations reject underflow with
 * NoSuchElementException, and expose size/isEmpty/peek/push/pop/clear.
 * Array stack doubles contiguous logical storage: push amortized O(1), worst
 * O(n) on resize; pop/peek O(1), storage O(capacity). It never shrinks, avoiding
 * resize thrashing. Linked stack uses one allocation per push: O(1) operations,
 * O(n) node storage. JVM does not expose physical array addresses. Not thread-safe.
 * isFull denotes configured/representational capacity, not available heap RAM;
 * an allocation can fail earlier with OutOfMemoryError.
 */
public final class StackArrayAndLinkedList {
    private StackArrayAndLinkedList() { }
    public static final class ArrayStack {
        private int[] data;
        private int size;
        public ArrayStack(int initialCapacity) {
            if (initialCapacity<1) throw new IllegalArgumentException("capacity must be positive");
            data=new int[initialCapacity];
        }
        public int size() { return size; }
        public boolean isEmpty() { return size==0; }
        public boolean isFull() { return size==Integer.MAX_VALUE; }
        public void push(int value) {
            if (isFull()) throw new IllegalStateException("size limit");
            if (size==data.length) {
                int capacity=(int)Math.min(Integer.MAX_VALUE,2L*data.length);
                int[] expanded=new int[capacity];
                for (int i=0; i<size; i++) expanded[i]=data[i];
                data=expanded;
            }
            data[size++]=value;
        }
        public int peek() {
            if (isEmpty()) throw new NoSuchElementException("empty stack");
            return data[size-1];
        }
        public int pop() { int value=peek(); data[--size]=0; return value; }
        public void clear() { for (int i=0; i<size; i++) data[i]=0; size=0; }
    }
    public static final class LinkedStack {
        private static final class Node {
            final int value; final Node next;
            Node(int value, Node next) { this.value=value; this.next=next; }
        }
        private Node top;
        private int size;
        public int size() { return size; }
        public boolean isEmpty() { return size==0; }
        public boolean isFull() { return size==Integer.MAX_VALUE; }
        public void push(int value) {
            if (isFull()) throw new IllegalStateException("size limit");
            top=new Node(value,top); size++;
        }
        public int peek() {
            if (isEmpty()) throw new NoSuchElementException("empty stack");
            return top.value;
        }
        public int pop() { int value=peek(); top=top.next; size--; return value; }
        public void clear() { top=null; size=0; }
    }
    public static void main(String[] args) {
        ArrayStack array=new ArrayStack(2); LinkedStack linked=new LinkedStack();
        for (int n : new int[]{10,20,30}) { array.push(n); linked.push(n); }
        System.out.println("Peek: "+array.peek()+", "+linked.peek());
        while (!array.isEmpty()) System.out.println(array.pop()+" = "+linked.pop());
    }
}
