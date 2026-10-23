import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * javac CircularLinkedList.java && java CircularLinkedList
 * A singly circular list stores a tail; head=tail.next. Nonempty traversals stop
 * after size visits, never at null. Insert ends/delete head O(1); delete tail,
 * indexed operations/search O(n). mergeFrom splices two rings in O(1) time and
 * O(1) extra space and empties the donor to avoid shared mutable ownership.
 * Not thread-safe. Private nodes ensure two distinct lists have disjoint rings.
 */
public final class CircularLinkedList {
    private static final class Node {
        final int value; Node next;
        Node(int value) { this.value=value; }
    }
    private Node tail;
    private int size;
    public int size() { return size; }
    public boolean isEmpty() { return size==0; }
    private void room() { if (size==Integer.MAX_VALUE) throw new IllegalStateException("size overflow"); }
    private Node nodeAt(int index) {
        if (index<0 || index>=size) throw new IndexOutOfBoundsException("index "+index);
        Node p=tail.next; for (int i=0; i<index; i++) p=p.next; return p;
    }
    public void insertHead(int value) {
        room(); Node n=new Node(value);
        if (tail==null) { n.next=n; tail=n; }
        else { n.next=tail.next; tail.next=n; }
        size++;
    }
    public void insertTail(int value) { insertHead(value); tail=tail.next; }
    public void insert(int index, int value) {
        if (index<0 || index>size) throw new IndexOutOfBoundsException("index "+index);
        if (index==0) { insertHead(value); return; }
        if (index==size) { insertTail(value); return; }
        room(); Node before=nodeAt(index-1), n=new Node(value);
        n.next=before.next; before.next=n; size++;
    }
    public int deleteHead() {
        if (isEmpty()) throw new NoSuchElementException("empty list");
        Node head=tail.next;
        if (size==1) tail=null; else tail.next=head.next;
        size--; return head.value;
    }
    public int deleteTail() {
        if (isEmpty()) throw new NoSuchElementException("empty list");
        if (size==1) return deleteHead();
        Node before=nodeAt(size-2); int value=tail.value;
        before.next=tail.next; tail=before; size--; return value;
    }
    public int delete(int index) {
        nodeAt(index); // Validate before any mutation.
        if (index==0) return deleteHead();
        if (index==size-1) return deleteTail();
        Node before=nodeAt(index-1); int value=before.next.value;
        before.next=before.next.next; size--; return value;
    }
    public int get(int index) { return nodeAt(index).value; }
    public int search(int value) {
        if (tail==null) return -1;
        Node p=tail.next;
        for (int i=0; i<size; i++,p=p.next) if (p.value==value) return i;
        return -1;
    }
    public int[] traverse() {
        int[] a=new int[size];
        if (tail!=null) { Node p=tail.next; for (int i=0; i<size; i++,p=p.next) a[i]=p.value; }
        return a;
    }
    public void mergeFrom(CircularLinkedList donor) {
        Objects.requireNonNull(donor,"donor");
        if (donor==this) throw new IllegalArgumentException("cannot merge a list with itself");
        int combined=Math.addExact(size,donor.size);
        if (donor.isEmpty()) return;
        if (isEmpty()) tail=donor.tail;
        else {
            Node head=tail.next;
            tail.next=donor.tail.next; donor.tail.next=head; tail=donor.tail;
        }
        size=combined; donor.tail=null; donor.size=0;
    }
    public void clear() { tail=null; size=0; }
    @Override public String toString() { StringBuilder out=new StringBuilder("[");
        int[] values=traverse();
        for (int i=0; i<values.length; i++) {
            if (i>0) out.append(", "); out.append(values[i]);
        }
        return out.append(']').toString(); }
    public static void main(String[] args) {
        CircularLinkedList a=new CircularLinkedList(), b=new CircularLinkedList();
        a.insertHead(2); a.insertHead(1); a.insertTail(4); a.insert(2,3);
        b.insertTail(5); b.insertTail(6); a.mergeFrom(b);
        System.out.println("Merged ring: "+a+"; donor empty: "+b.isEmpty());
        System.out.println("3 at "+a.search(3));
        System.out.println("Deleted "+a.deleteHead()+", "+a.deleteTail()+", "+a.delete(1));
        System.out.println(a);
    }
}
