/*
 * Singly Linked List
 * Each node holds a value and a link to the NEXT node.
 *
 *   head --> [10] --> [20] --> [30] --> null
 *                               ^
 *                              tail
 *
 * We keep both head (first node) and tail (last node).
 * Insert at head/tail and delete head are O(1); everything else is O(n).
 *
 * Run: javac SinglyLinkedList.java && java SinglyLinkedList
 */
public class SinglyLinkedList {

    // One box in the chain
    static class Node {
        int value;
        Node next;   // the node after this one (null if this is the last node)

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
        newNode.next = head;   // new node points to the old first node
        head = newNode;        // new node becomes the first node

        if (tail == null) {
            tail = newNode;    // list was empty, so it is also the last node
        }
        size++;
    }

    // Adds a value at the end of the list
    void insertTail(int value) {
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

        // find the node just BEFORE the position and link the new node after it
        Node before = nodeAt(index - 1);
        Node newNode = new Node(value);
        newNode.next = before.next;
        before.next = newNode;
        size++;
    }

    // Removes and returns the first value (-1 if the list is empty)
    int deleteHead() {
        if (isEmpty()) {
            System.out.println("List is empty!");
            return -1;
        }

        int value = head.value;
        head = head.next;      // second node becomes the first

        if (head == null) {
            tail = null;       // list is now empty
        }
        size--;
        return value;
    }

    // Removes and returns the last value (-1 if the list is empty)
    int deleteTail() {
        if (isEmpty()) {
            System.out.println("List is empty!");
            return -1;
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

    // Prints the list from head to tail, like 10 -> 20 -> 30 -> null
    void display() {
        Node current = head;
        while (current != null) {
            System.out.print(current.value + " -> ");
            current = current.next;
        }
        System.out.println("null");
    }

    public static void main(String[] args) {
        System.out.println("*** Singly Linked List ***");
        SinglyLinkedList list = new SinglyLinkedList();

        list.insertHead(20);     // 20
        list.insertHead(10);     // 10 20
        list.insertTail(40);     // 10 20 40
        list.insert(2, 30);      // 10 20 30 40
        list.display();
        System.out.println("30 is at position " + list.search(30));

        System.out.println("Deleted head: " + list.deleteHead());   // 10
        System.out.println("Deleted tail: " + list.deleteTail());   // 40
        System.out.println("Deleted [1]:  " + list.delete(1));      // 30
        list.display();

        list.deleteHead();                                          // 20
        System.out.println("Empty: " + list.isEmpty());
        list.deleteHead();                                          // prints "List is empty!"
    }
}
