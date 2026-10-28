/**
 * javac MultiplierList.java && java MultiplierList
 *
 * Multiplies a number stored in a linked list by a whole number k.
 *
 * The digits are stored BACKWARDS (least significant digit first):
 *   99  is stored as  9 -> 9
 *   495 is stored as  5 -> 9 -> 4
 *
 * Storing it backwards lets us work like school multiplication:
 * start with the ones digit, multiply, write down one digit, carry the rest.
 */
public class MultiplierList {

    // One digit of the number
    static class Node {
        int digit;
        Node next;

        Node(int digit) {
            this.digit = digit;
        }
    }

    // Returns a NEW list holding (number in head) * k
    static Node multiply(Node head, int k) {
        // anything times 0 is 0
        if (head == null || k == 0) {
            return new Node(0);
        }

        // 'dummy' is a placeholder in front of the answer,
        // so we never have to check "is the answer list still empty?"
        Node dummy = new Node(0);
        Node tail = dummy;
        int carry = 0;

        // multiply every digit and pass the carry on to the next digit
        Node current = head;
        while (current != null) {
            int value = current.digit * k + carry;

            tail.next = new Node(value % 10);   // the last digit stays here
            tail = tail.next;
            carry = value / 10;                 // the rest is carried on

            current = current.next;
        }

        // whatever carry is left becomes new digits at the end
        while (carry > 0) {
            tail.next = new Node(carry % 10);
            tail = tail.next;
            carry = carry / 10;
        }

        return dummy.next;   // skip the placeholder
    }

    public static void main(String[] args) {
        // build 99 (stored as 9 -> 9)
        Node head = new Node(9);
        head.next = new Node(9);

        Node result = multiply(head, 5);   // 99 * 5 = 495, stored as 5 -> 9 -> 4

        System.out.print("99 * 5, LSD first: ");
        Node current = result;
        while (current != null) {
            System.out.print(current.digit + " -> ");
            current = current.next;
        }
        System.out.println("null");
    }
}
