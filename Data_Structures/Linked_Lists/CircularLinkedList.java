/*
 * Circular Linked List (singly linked)
 * The last node links back to the first node, so there is no null at the end.
 *
 *        +--> [1] --> [2] --> [3] --+
 *        |                     tail |
 *        +--------------------------+
 *
 * We only store the tail. The head is always tail.next.
 * Because there is no null, loops count up to size instead of looking for null.
 * Insert at head/tail and delete head are O(1); everything else is O(n).
 *
 * Run: javac CircularLinkedList.java && java CircularLinkedList
 */
public class CircularLinkedList {

    // One box in the ring
    static class Node {
        int value;
        Node next;

        Node(int value) {
            this.value = value;
        }
    }

    Node tail;   // last node (tail.next is the first node)
    int size;    // number of nodes

    boolean isEmpty() {
        return tail == null;
    }

    // Walks from the head to the node at the given position
    Node nodeAt(int index) {
        Node current = tail.next;   // start at the head
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current;
    }

    // Adds a value at the front of the ring
    void insertHead(int value) {
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
    void insertTail(int value) {
        // trick: insert at the head, then move tail forward one step,
        // so the new node becomes the tail instead of the head
        insertHead(value);
        tail = tail.next;
    }

    // Adds a value at a position (0 = front, size = end)
    void insert(int index, int value) {
        if (index < 0 || index > size) {
            System.out.println("Invalid position " + index);
            return;
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

    // Removes and returns the first value (-1 if the ring is empty)
    int deleteHead() {
        if (isEmpty()) {
            System.out.println("List is empty!");
            return -1;
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

    // Removes and returns the last value (-1 if the ring is empty)
    int deleteTail() {
        if (isEmpty()) {
            System.out.println("List is empty!");
            return -1;
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

    // Removes and returns the value at a position (-1 if the position is invalid)
    int delete(int index) {
        if (index < 0 || index >= size) {
            System.out.println("Invalid position " + index);
            return -1;
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

    // Returns the position of the value, or -1 if it is not in the ring
    int search(int value) {
        if (isEmpty()) {
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

    // Joins the other ring onto the end of this one in O(1), then empties other
    void mergeFrom(CircularLinkedList other) {
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

        other.tail = null;                 // the other ring is now empty
        other.size = 0;
    }

    // Prints the ring once around, starting at the head
    void display() {
        if (isEmpty()) {
            System.out.println("(empty)");
            return;
        }

        Node current = tail.next;
        for (int i = 0; i < size; i++) {
            System.out.print(current.value + " -> ");
            current = current.next;
        }
        System.out.println("(back to " + tail.next.value + ")");
    }

    public static void main(String[] args) {
        System.out.println("*** Circular Linked List ***");
        CircularLinkedList a = new CircularLinkedList();
        CircularLinkedList b = new CircularLinkedList();

        a.insertHead(2);     // 2
        a.insertHead(1);     // 1 2
        a.insertTail(4);     // 1 2 4
        a.insert(2, 3);      // 1 2 3 4

        b.insertTail(5);     // 5
        b.insertTail(6);     // 5 6

        a.mergeFrom(b);      // a = 1 2 3 4 5 6, b = empty
        System.out.print("Merged ring: ");
        a.display();
        System.out.println("Donor ring empty: " + b.isEmpty());
        System.out.println("3 is at position " + a.search(3));

        System.out.println("Deleted head: " + a.deleteHead());   // 1
        System.out.println("Deleted tail: " + a.deleteTail());   // 6
        System.out.println("Deleted [1]:  " + a.delete(1));      // 3
        a.display();
    }
}
