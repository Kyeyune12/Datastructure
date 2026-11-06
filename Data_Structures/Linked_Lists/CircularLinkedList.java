/**
 * javac CircularLinkedList.java && java CircularLinkedList
 *
 * A singly circular linked list: the last node links back to the first node,
 * so there is no null at the end.
 *
 *        +--> [1] --> [2] --> [3] --+
 *        |                     tail |
 *        +--------------------------+
 *
 * We only store the tail. The head is always tail.next.
 * Because there is no null, loops count up to size instead of looking for null.
 * Insert at head/tail and delete head are O(1); everything else is O(n).
 */
public class CircularLinkedList {

    // One box in the ring
    private static class Node {
        int value;
        Node next;

        Node(int value) {
            this.value = value;
        }
    }

    private Node tail;   // last node (tail.next is the first node)
    private int size;    // number of nodes

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    // Walks from the head to the node at the given position
    private Node nodeAt(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index " + index);
        }

        Node current = tail.next;   // start at the head
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current;
    }

    // Adds a value at the front of the ring
    public void insertHead(int value) {
        Node newNode = new Node(value);

        if (tail == null) {
            newNode.next = newNode;    // only node: it points to itself
            tail = newNode;
        } else {
            newNode.next = tail.next;  // new node points to the old head
            tail.next = newNode;       // tail now points to the new head
        }
        size++;
    }

    // Adds a value at the end of the ring
    public void insertTail(int value) {
        // trick: insert at the head, then move tail forward one step,
        // so the new node becomes the tail instead of the head
        insertHead(value);
        tail = tail.next;
    }

    // Adds a value at a position (0 = front, size = end)
    public void insert(int index, int value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("index " + index);
        }
        if (index == 0) {
            insertHead(value);
            return;
        }
        if (index == size) {
            insertTail(value);
            return;
        }

        Node before = nodeAt(index - 1);
        Node newNode = new Node(value);
        newNode.next = before.next;
        before.next = newNode;
        size++;
    }

    // Removes and returns the first value
    public int deleteHead() {
        if (isEmpty()) {
            throw new IllegalStateException("empty list");
        }

        Node head = tail.next;
        if (size == 1) {
            tail = null;            // ring is now empty
        } else {
            tail.next = head.next;  // tail skips over the old head
        }
        size--;
        return head.value;
    }

    // Removes and returns the last value
    public int deleteTail() {
        if (isEmpty()) {
            throw new IllegalStateException("empty list");
        }
        if (size == 1) {
            return deleteHead();
        }

        Node before = nodeAt(size - 2);   // second-last node
        int value = tail.value;
        before.next = tail.next;          // second-last now points to the head
        tail = before;
        size--;
        return value;
    }

    // Removes and returns the value at a position
    public int delete(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index " + index);
        }
        if (index == 0) {
            return deleteHead();
        }
        if (index == size - 1) {
            return deleteTail();
        }

        Node before = nodeAt(index - 1);
        int value = before.next.value;
        before.next = before.next.next;   // skip over the deleted node
        size--;
        return value;
    }

    public int get(int index) {
        return nodeAt(index).value;
    }

    // Returns the position of the value, or -1 if it is not in the ring
    public int search(int value) {
        if (tail == null) {
            return -1;
        }

        Node current = tail.next;
        for (int i = 0; i < size; i++) {   // stop after size steps, not at null
            if (current.value == value) {
                return i;
            }
            current = current.next;
        }
        return -1;
    }

    // Copies the values into an array, head first
    public int[] traverse() {
        int[] values = new int[size];
        if (tail == null) {
            return values;
        }

        Node current = tail.next;
        for (int i = 0; i < size; i++) {
            values[i] = current.value;
            current = current.next;
        }
        return values;
    }

    // Joins the other ring onto the end of this one in O(1), then empties other
    public void mergeFrom(CircularLinkedList other) {
        if (other == this || other.isEmpty()) {
            return;
        }

        if (isEmpty()) {
            tail = other.tail;
        } else {
            Node head = tail.next;
            tail.next = other.tail.next;   // our tail -> other's head
            other.tail.next = head;        // other's tail -> our head
            tail = other.tail;             // other's tail is now the last node
        }
        size += other.size;
        other.clear();
    }

    // Empties the ring
    public void clear() {
        tail = null;
        size = 0;
    }

    // Shows the ring like [1, 2, 3]
    @Override
    public String toString() {
        int[] values = traverse();
        String text = "[";

        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                text += ", ";
            }
            text += values[i];
        }
        return text + "]";
    }

    public static void main(String[] args) {
        CircularLinkedList a = new CircularLinkedList();
        CircularLinkedList b = new CircularLinkedList();

        a.insertHead(2);     // [2]
        a.insertHead(1);     // [1, 2]
        a.insertTail(4);     // [1, 2, 4]
        a.insert(2, 3);      // [1, 2, 3, 4]

        b.insertTail(5);     // [5]
        b.insertTail(6);     // [5, 6]

        a.mergeFrom(b);      // a = [1, 2, 3, 4, 5, 6], b = []
        System.out.println("Merged ring: " + a + "; donor empty: " + b.isEmpty());
        System.out.println("3 at " + a.search(3));

        int x = a.deleteHead();   // removes 1
        int y = a.deleteTail();   // removes 6
        int z = a.delete(1);      // removes 3
        System.out.println("Deleted " + x + ", " + y + ", " + z);
        System.out.println(a);
    }
}
