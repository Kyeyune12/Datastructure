/**
 * javac SinglyLinkedList.java && java SinglyLinkedList
 *
 * A singly linked list: each node holds a value and a link to the NEXT node.
 *
 *   head --> [10] --> [20] --> [30] --> null
 *                               ^
 *                              tail
 *
 * We keep both head (first node) and tail (last node).
 * Insert at head/tail and delete head are O(1); everything else is O(n).
 */
public class SinglyLinkedList {

    // One box in the chain
    private static class Node {
        int value;
        Node next;   // the node after this one (null if this is the last node)

        Node(int value) {
            this.value = value;
        }
    }

    private Node head;   // first node
    private Node tail;   // last node
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

        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current;
    }

    // Adds a value at the front of the list
    public void insertHead(int value) {
        Node newNode = new Node(value);
        newNode.next = head;   // new node points to the old first node
        head = newNode;        // new node becomes the first node

        if (tail == null) {
            tail = newNode;    // list was empty, so it is also the last node
        }
        size++;
    }

    // Adds a value at the end of the list
    public void insertTail(int value) {
        Node newNode = new Node(value);

        if (tail == null) {
            head = newNode;        // list was empty
        } else {
            tail.next = newNode;   // old last node points to the new node
        }
        tail = newNode;            // new node becomes the last node
        size++;
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

        // find the node just BEFORE the position and link the new node after it
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

        int value = head.value;
        head = head.next;      // second node becomes the first

        if (head == null) {
            tail = null;       // list is now empty
        }
        size--;
        return value;
    }

    // Removes and returns the last value
    public int deleteTail() {
        if (isEmpty()) {
            throw new IllegalStateException("empty list");
        }
        if (size == 1) {
            return deleteHead();
        }

        // we have no backward links, so walk to the second-last node
        Node before = nodeAt(size - 2);
        int value = tail.value;
        before.next = null;
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

    // Returns the position of the value, or -1 if it is not in the list
    public int search(int value) {
        Node current = head;
        int index = 0;

        while (current != null) {
            if (current.value == value) {
                return index;
            }
            current = current.next;
            index++;
        }
        return -1;
    }

    // Empties the list
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    // Shows the list like [10, 20, 30]
    @Override
    public String toString() {
        String text = "[";
        Node current = head;

        while (current != null) {
            text += current.value;
            if (current.next != null) {
                text += ", ";
            }
            current = current.next;
        }
        return text + "]";
    }

    public static void main(String[] args) {
        SinglyLinkedList list = new SinglyLinkedList();

        list.insertHead(20);     // [20]
        list.insertHead(10);     // [10, 20]
        list.insertTail(40);     // [10, 20, 40]
        list.insert(2, 30);      // [10, 20, 30, 40]
        System.out.println(list + "; 30 at " + list.search(30));

        int a = list.deleteHead();   // removes 10
        int b = list.deleteTail();   // removes 40
        int c = list.delete(1);      // removes 30
        System.out.println("Deleted " + a + ", " + b + ", " + c);
        System.out.println(list);

        list.clear();
        System.out.println("Empty: " + list.isEmpty());
    }
}
