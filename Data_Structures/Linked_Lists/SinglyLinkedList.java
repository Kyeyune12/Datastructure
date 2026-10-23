import java.util.NoSuchElementException;

/**
 * javac SinglyLinkedList.java && java SinglyLinkedList
 * Zero-based integer list. Invariant: size=0 iff head=tail=null; otherwise
 * tail.next=null and exactly size nodes are reachable from head. Not thread-safe.
 * Head/tail insertion and head deletion O(1); other indexed operations, tail
 * deletion and search O(n). Traversal Theta(n). Storage Theta(n), O(1) working
 * memory except returned traversal arrays (Theta(n)). Nodes are private to
 * preserve ownership; deleted nodes become eligible for garbage collection.
 */
public final class SinglyLinkedList {
    private static final class Node {
        final int value; Node next;
        Node(int value) { this.value=value; }
    }
    private Node head, tail;
    private int size;
    public int size() { return size; }
    public boolean isEmpty() { return size==0; }
    private void room() { if (size==Integer.MAX_VALUE) throw new IllegalStateException("size overflow"); }
    private void check(int i) { if (i<0 || i>=size) throw new IndexOutOfBoundsException("index "+i); }
    private Node nodeAt(int i) {
        Node p=head;
        for (int j=0; j<i; j++) p=p.next;
        return p;
    }
    public void insertHead(int value) {
        room(); Node n=new Node(value); n.next=head; head=n;
        if (size==0) tail=n; size++;
    }
    public void insertTail(int value) {
        room(); Node n=new Node(value);
        if (size==0) head=n; else tail.next=n;
        tail=n; size++;
    }
    /** Valid insertion indices: 0 through size, inclusive. */
    public void insert(int index, int value) {
        if (index<0 || index>size) throw new IndexOutOfBoundsException("index "+index);
        if (index==0) { insertHead(value); return; }
        if (index==size) { insertTail(value); return; }
        room(); Node before=nodeAt(index-1), n=new Node(value);
        n.next=before.next; before.next=n; size++;
    }
    public int deleteHead() {
        if (isEmpty()) throw new NoSuchElementException("empty list");
        int value=head.value; head=head.next;
        if (--size==0) tail=null; return value;
    }
    public int deleteTail() {
        if (isEmpty()) throw new NoSuchElementException("empty list");
        if (size==1) return deleteHead();
        Node before=nodeAt(size-2); int value=tail.value;
        before.next=null; tail=before; size--; return value;
    }
    public int delete(int index) {
        check(index);
        if (index==0) return deleteHead();
        if (index==size-1) return deleteTail();
        Node before=nodeAt(index-1); int value=before.next.value;
        before.next=before.next.next; size--; return value;
    }
    public int get(int index) { check(index); return nodeAt(index).value; }
    /** First index, or -1 if absent. */
    public int search(int value) {
        int i=0;
        for (Node p=head; p!=null; p=p.next, i++) if (p.value==value) return i;
        return -1;
    }
    public int[] traverse() {
        int[] values=new int[size]; int i=0;
        for (Node p=head; p!=null; p=p.next) values[i++]=p.value;
        return values;
    }
    public void clear() { head=null; tail=null; size=0; }
    @Override public String toString() { StringBuilder out=new StringBuilder("[");
        int[] values=traverse();
        for (int i=0; i<values.length; i++) {
            if (i>0) out.append(", "); out.append(values[i]);
        }
        return out.append(']').toString(); }
    public static void main(String[] args) {
        SinglyLinkedList list=new SinglyLinkedList();
        list.insertHead(20); list.insertHead(10); list.insertTail(40); list.insert(2,30);
        System.out.println(list+"; 30 at "+list.search(30));
        System.out.println("Deleted "+list.deleteHead()+", "+list.deleteTail()+", "+list.delete(1));
        System.out.println(list); list.clear(); System.out.println("Empty: "+list.isEmpty());
    }
}
