/**
 * javac DoublyLinkedList.java && java DoublyLinkedList
 *
 * A doubly linked list: each node links to the NEXT node AND the PREVIOUS node,
 * so the list can be walked forwards and backwards.
 *
 *   null <-- [10] <--> [20] <--> [30] --> null
 *            head                tail
 *
 * Insert/delete at either end is O(1); everything else is O(n).
 */
public class DoublyLinkedList {

    // One box in the chain
    private static class Node {
        int value;
        Node prev;   // the node before this one (null for the first node)
        Node next;   // the node after this one (null for the last node)

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
        newNode.next = head;

        if (head == null) {
            tail = newNode;          // list was empty
        } else {
            head.prev = newNode;     // old first node points back to the new node
        }
        head = newNode;
        size++;
    }

    // Adds a value at the end of the list
    public void insertTail(int value) {
        Node newNode = new Node(value);
        newNode.prev = tail;

        if (tail == null) {
            head = newNode;          // list was empty
        } else {
            tail.next = newNode;     // old last node points forward to the new node
        }
        tail = newNode;
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

        // the new node goes between 'before' and 'after'
        Node after = nodeAt(index);
        Node before = after.prev;
        Node newNode = new Node(value);

        newNode.prev = before;
        newNode.next = after;
        before.next = newNode;
        after.prev = newNode;
        size++;
    }

    // Removes node n by linking its two neighbours to each other
    private int unlink(Node n) {
        // fix the link coming from the left
        if (n.prev == null) {
            head = n.next;           // n was the first node
        } else {
            n.prev.next = n.next;
        }

        // fix the link coming from the right
        if (n.next == null) {
            tail = n.prev;           // n was the last node
        } else {
            n.next.prev = n.prev;
        }

        size--;
        return n.value;
    }

    public int deleteHead() {
        if (isEmpty()) {
            throw new IllegalStateException("empty list");
        }
        return unlink(head);
    }

    public int deleteTail() {
        if (isEmpty()) {
            throw new IllegalStateException("empty list");
        }
        return unlink(tail);   // O(1): tail.prev tells us the new last node
    }

    public int delete(int index) {
        return unlink(nodeAt(index));
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

    // Prints from head to tail using the next links
    public void printForward() {
        Node current = head;
        while (current != null) {
            System.out.print(current.value + " ");
            current = current.next;
        }
        System.out.println();
    }

    // Prints from tail to head using the prev links
    public void printBackward() {
        Node current = tail;
        while (current != null) {
            System.out.print(current.value + " ");
            current = current.prev;
        }
        System.out.println();
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
        DoublyLinkedList list = new DoublyLinkedList();

        list.insertHead(20);     // [20]
        list.insertHead(10);     // [10, 20]
        list.insertTail(40);     // [10, 20, 40]
        list.insert(2, 30);      // [10, 20, 30, 40]

        System.out.print("Forward: ");
        list.printForward();
        System.out.print("Backward: ");
        list.printBackward();
        System.out.println("30 at " + list.search(30));

        int a = list.deleteHead();   // removes 10
        int b = list.deleteTail();   // removes 40
        int c = list.delete(1);      // removes 30
        System.out.println("Deleted " + a + ", " + b + ", " + c);
        System.out.println(list);
    }
}
