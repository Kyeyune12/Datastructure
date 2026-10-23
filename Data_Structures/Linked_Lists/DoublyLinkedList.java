import java.util.NoSuchElementException;

/**
 * javac DoublyLinkedList.java && java DoublyLinkedList
 * head.prev=null, tail.next=null; x.next.prev=x and x.prev.next=x where defined.
 * End insertion/deletion O(1); indexed lookup O(min(i,n-1-i)); search O(n).
 * Two links per node trade memory for backward traversal and O(1) unlinking
 * once a node is located. Private nodes prevent external invariant corruption.
 * Integer values, zero-based indices, no sentinel data values; not thread-safe.
 */
public final class DoublyLinkedList {
    private static final class Node {
        final int value; Node prev,next;
        Node(int value) { this.value=value; }
    }
    private Node head,tail;
    private int size;
    public int size() { return size; }
    public boolean isEmpty() { return size==0; }
    private void room() { if (size==Integer.MAX_VALUE) throw new IllegalStateException("size overflow"); }
    private Node nodeAt(int index) {
        if (index<0 || index>=size) throw new IndexOutOfBoundsException("index "+index);
        if (index<size/2) {
            Node p=head; for (int i=0; i<index; i++) p=p.next; return p;
        }
        Node p=tail; for (int i=size-1; i>index; i--) p=p.prev; return p;
    }
    public void insertHead(int value) {
        room(); Node n=new Node(value); n.next=head;
        if (head==null) tail=n; else head.prev=n;
        head=n; size++;
    }
    public void insertTail(int value) {
        room(); Node n=new Node(value); n.prev=tail;
        if (tail==null) head=n; else tail.next=n;
        tail=n; size++;
    }
    public void insert(int index, int value) {
        if (index<0 || index>size) throw new IndexOutOfBoundsException("index "+index);
        if (index==0) { insertHead(value); return; }
        if (index==size) { insertTail(value); return; }
        room(); Node after=nodeAt(index), n=new Node(value);
        n.prev=after.prev; n.next=after; after.prev.next=n; after.prev=n; size++;
    }
    private int unlink(Node n) {
        if (n.prev==null) head=n.next; else n.prev.next=n.next;
        if (n.next==null) tail=n.prev; else n.next.prev=n.prev;
        size--; return n.value;
    }
    public int deleteHead() {
        if (isEmpty()) throw new NoSuchElementException("empty list");
        return unlink(head);
    }
    public int deleteTail() {
        if (isEmpty()) throw new NoSuchElementException("empty list");
        return unlink(tail);
    }
    public int delete(int index) { return unlink(nodeAt(index)); }
    public int get(int index) { return nodeAt(index).value; }
    public int search(int value) {
        int i=0; for (Node p=head; p!=null; p=p.next,i++) if (p.value==value) return i;
        return -1;
    }
    public int[] traverseForward() {
        int[] a=new int[size]; int i=0;
        for (Node p=head; p!=null; p=p.next) a[i++]=p.value; return a;
    }
    public int[] traverseBackward() {
        int[] a=new int[size]; int i=0;
        for (Node p=tail; p!=null; p=p.prev) a[i++]=p.value; return a;
    }
    public void clear() { head=null; tail=null; size=0; }
    @Override public String toString() { StringBuilder out=new StringBuilder("[");
        int[] values=traverseForward();
        for (int i=0; i<values.length; i++) {
            if (i>0) out.append(", "); out.append(values[i]);
        }
        return out.append(']').toString(); }
    public static void main(String[] args) {
        DoublyLinkedList list=new DoublyLinkedList();
        list.insertHead(20); list.insertHead(10); list.insertTail(40); list.insert(2,30);
        System.out.println("Forward: "+list);
        System.out.print("Backward: ");
        for (int value : list.traverseBackward()) System.out.print(value+" ");
        System.out.println();
        System.out.println("30 at "+list.search(30));
        System.out.println("Deleted "+list.deleteHead()+", "+list.deleteTail()+", "+list.delete(1));
        System.out.println(list);
    }
}
