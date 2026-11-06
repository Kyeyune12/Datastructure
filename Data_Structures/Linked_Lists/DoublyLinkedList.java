/*
 * Doubly Linked List
 * Each node links to the NEXT node AND the PREVIOUS node,
 * so the list can be walked forwards and backwards.
 *
 *   null <-- [10] <--> [20] <--> [30] --> null
 *            head                tail
 *
 * Insert/delete at either end is O(1); everything else is O(n).
 *
 * Run: javac DoublyLinkedList.java && java DoublyLinkedList
 */
public class DoublyLinkedList {

    // One box in the chain
    static class Node {
        int value;
        Node prev;   // the node before this one (null for the first node)
        Node next;   // the node after this one (null for the last node)

        Node(int value) {
            this.value = value;
        }
    }

    Node head;   // first node
    Node tail;   // last node
    int size;    // number of nodes

    boolean isEmpty() {
        return head == null;
    }

    // Walks from the head to the node at the given position
    Node nodeAt(int index) {
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current;
    }

    // Adds a value at the front of the list
    void insertHead(int value) {
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
    void insertTail(int value) {
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
    int unlink(Node n) {
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

    // Removes and returns the first value (-1 if the list is empty)
    int deleteHead() {
        if (isEmpty()) {
            System.out.println("List is empty!");
            return -1;
        }
        return unlink(head);
    }

    // Removes and returns the last value (-1 if the list is empty)
    int deleteTail() {
        if (isEmpty()) {
            System.out.println("List is empty!");
            return -1;
        }
        return unlink(tail);   // O(1): tail.prev tells us the new last node
    }

    // Removes and returns the value at a position (-1 if the position is invalid)
    int delete(int index) {
        if (index < 0 || index >= size) {
            System.out.println("Invalid position " + index);
            return -1;
        }
        return unlink(nodeAt(index));
    }

    // Returns the position of the value, or -1 if it is not in the list
    int search(int value) {
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
    void printForward() {
        Node current = head;
        while (current != null) {
            System.out.print(current.value + " ");
            current = current.next;
        }
        System.out.println();
    }

    // Prints from tail to head using the prev links
    void printBackward() {
        Node current = tail;
        while (current != null) {
            System.out.print(current.value + " ");
            current = current.prev;
        }
        System.out.println();
    }

    public static void main(String[] args) {
        System.out.println("*** Doubly Linked List ***");
        DoublyLinkedList list = new DoublyLinkedList();

        list.insertHead(20);     // 20
        list.insertHead(10);     // 10 20
        list.insertTail(40);     // 10 20 40
        list.insert(2, 30);      // 10 20 30 40

        System.out.print("Forward:  ");
        list.printForward();
        System.out.print("Backward: ");
        list.printBackward();
        System.out.println("30 is at position " + list.search(30));

        System.out.println("Deleted head: " + list.deleteHead());   // 10
        System.out.println("Deleted tail: " + list.deleteTail());   // 40
        System.out.println("Deleted [1]:  " + list.delete(1));      // 30
        System.out.print("Forward:  ");
        list.printForward();
    }
}
