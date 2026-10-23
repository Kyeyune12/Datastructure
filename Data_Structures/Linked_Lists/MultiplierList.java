/**
 * javac MultiplierList.java && java MultiplierList
 * School multiplication, base 10, LSD first. Returns a fresh canonical list;
 * inputs are not changed. Null means zero; zero returns one node containing 0.
 * Negative k, cyclic lists and digits outside 0..9 are rejected. Redundant most
 * significant zeros are accepted and removed from output. long intermediates
 * safely hold 9*Integer.MAX_VALUE + carry. No conversion of the entire number
 * into an int/long, so the number of input digits is not machine-word limited.
 * After i digits: k*processedInput = processedOutput + carry*10^i.
 * Time O(n+log10(k+1)), auxiliary O(1) excluding new output nodes.
 */
public final class MultiplierList {
    private MultiplierList() { }
    public static final class ListNode {
        public final int digit;
        public ListNode next;
        public ListNode(int digit) {
            if (digit<0 || digit>9) throw new IllegalArgumentException("digit must be 0..9");
            this.digit=digit;
        }
    }
    public static ListNode multiplyLinkedList(ListNode head, int k) {
        if (k<0) throw new IllegalArgumentException("multiplier must be non-negative");
        ListNode slow=head,fast=head;
        while (fast!=null && fast.next!=null) {
            slow=slow.next; fast=fast.next.next;
            if (slow==fast) throw new IllegalArgumentException("cyclic input");
        }
        ListNode lastSignificant=null;
        for (ListNode p=head; p!=null; p=p.next) {
            if (p.digit<0 || p.digit>9) throw new IllegalArgumentException("invalid digit");
            if (p.digit!=0) lastSignificant=p;
        }
        if (lastSignificant==null || k==0) return new ListNode(0);
        ListNode dummy=new ListNode(0),tail=dummy,lastNonZero=null;
        long carry=0;
        for (ListNode p=head; p!=null; p=p.next) {
            long value=(long)p.digit*k+carry;
            tail.next=new ListNode((int)(value%10)); tail=tail.next;
            if (tail.digit!=0) lastNonZero=tail;
            carry=value/10;
            if (p==lastSignificant) break; // Do not allocate redundant high-order zeros.
        }
        while (carry>0) {
            tail.next=new ListNode((int)(carry%10)); tail=tail.next;
            if (tail.digit!=0) lastNonZero=tail;
            carry/=10;
        }
        if (lastNonZero==null) { dummy.next.next=null; return dummy.next; }
        lastNonZero.next=null;
        return dummy.next;
    }
    public static void main(String[] args) {
        ListNode head=new ListNode(9); head.next=new ListNode(9);
        ListNode result=multiplyLinkedList(head,5);
        System.out.print("99 * 5, LSD first: ");
        for (ListNode p=result; p!=null; p=p.next) System.out.print(p.digit+" -> ");
        System.out.println("null");
    }
}
