/*
 * Project A: Search & Rescue Trail Backtrack Assistant
 * Data Structure: Stack (LIFO)
 * Underlying Structure: Linked List
 * Language: Java
 *
 * Core LIFO operations (required by the assignment):
 *   push(), pop(), peek(), isEmpty()
 *
 * Scenario-driven extensions (built on top of the core operations):
 *   depth()          - how many junctions deep the rescuer currently is
 *   emergencyRecall() - retreat all the way to the trailhead, reusing pop()
 *   alreadyVisited()  - read-only search through the current path, without popping
 *
 * Note: isFull() is intentionally NOT implemented. The assignment only
 * requires isFull() for array-based structures (Section 4). Adding an
 * artificial capacity limit here would contradict the whole reason this
 * project uses a Linked List in the first place - unbounded trail depth.
 */

public class NavigationStack {

    /* ---------- Node definition ---------- */
    static class Node {
        String junction;
        Node next;

        Node(String junction, Node next) {
            this.junction = junction;
            this.next = next;
        }
    }

    /* ---------- Stack state ---------- */
    private Node top;   // head of the linked list = the rescuer's current position

    public NavigationStack() {
        this.top = null;
    }

    /* ---------- Core stack operations ---------- */

    public boolean isEmpty() {
        return top == null;
    }

    /** push: step deeper into a newly discovered junction */
    public void push(String junction) {
        Node newNode = new Node(junction, top);
        top = newNode;
        System.out.println("[OK] Moved into junction \"" + junction + "\". Path recorded.");
    }

    /** pop: dead end reached - retreat to the previous junction */
    public String pop() {
        if (isEmpty()) {
            System.out.println("[WARNING] Already at the trailhead. No further junction to retreat to.");
            return null;
        }
        String junction = top.junction;
        top = top.next;   // unlink the head node
        return junction;
    }

    /** peek: view current position without moving */
    public String peek() {
        if (isEmpty()) {
            System.out.println("[WARNING] Rescuer is at the trailhead. No active junction.");
            return null;
        }
        return top.junction;
    }

    /** display: print the full explored path, deepest junction first */
    public void display() {
        if (isEmpty()) {
            System.out.println("[INFO] No path explored yet. Rescuer is at the trailhead.");
            return;
        }
        System.out.println("--- Explored Path (Current -> Trailhead) ---");
        Node current = top;
        while (current != null) {
            System.out.println((current == top ? "[HERE] " : "       ") + current.junction);
            current = current.next;
        }
        System.out.println("---------------------------------------------");
    }

    /* ---------- Scenario-driven extensions ---------- */

    /** depth: how many junctions deep the rescuer currently is (full traversal) */
    public int depth() {
        int count = 0;
        Node current = top;
        while (current != null) {
            count++;
            current = current.next;
        }
        return count;
    }

    /** emergencyRecall: retreat all the way to the trailhead, reusing pop() */
    public void emergencyRecall() {
        if (isEmpty()) {
            System.out.println("[INFO] Already at the trailhead. Nothing to recall from.");
            return;
        }
        System.out.println("[EMERGENCY RECALL] Retreating to trailhead...");
        while (!isEmpty()) {
            String junction = pop();   // reuses the core pop() operation
            System.out.println("   ...passed back through " + junction);
        }
        System.out.println("[EMERGENCY RECALL] Rescuer is back at the trailhead.");
    }

    /** alreadyVisited: read-only search through the current path, does NOT modify top */
    public boolean alreadyVisited(String junction) {
        Node current = top;   // temporary pointer - top itself never moves
        while (current != null) {
            if (current.junction.equalsIgnoreCase(junction)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    /* ---------- Menu-driven main ---------- */

    // Reads one line typed by the user, one character at a time.
    // (This replaces Scanner, so no import is needed.)
    //   System.in.read() returns the next character as a number,
    //   or -1 when there is no more input.
    static String readLine() {
        String line = "";
        try {
            int c = System.in.read();
            while (c != -1 && c != '\n') {   // stop at the end of the line
                if (c != '\r') {             // Windows adds \r before \n: skip it
                    line = line + (char) c;
                }
                c = System.in.read();
            }
        } catch (Exception e) {
            // could not read input: return what we have so far
        }
        return line.trim();
    }

    public static void main(String[] args) {
        NavigationStack rescue = new NavigationStack();
        int choice = 0;

        System.out.println("=========================================");
        System.out.println(" SEARCH & RESCUE BACKTRACK ASSISTANT (Stack)");
        System.out.println("=========================================");

        do {
            System.out.println("\n1. Move to a new junction (push)");
            System.out.println("2. Dead end - retreat to previous junction (pop)");
            System.out.println("3. View current position (peek)");
            System.out.println("4. Display full explored path");
            System.out.println("5. Check if back at trailhead (isEmpty)");
            System.out.println("6. Check current depth (depth)");
            System.out.println("7. Emergency recall - retreat to trailhead");
            System.out.println("8. Check if a junction was already visited");
            System.out.println("9. Exit");
            System.out.print("Choose an option: ");

            try {
                choice = Integer.parseInt(readLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1: {
                    System.out.print("Enter junction name/ID: ");
                    String junction = readLine();
                    rescue.push(junction);
                    break;
                }
                case 2: {
                    String junction = rescue.pop();
                    if (junction != null) {
                        System.out.println("[RETREAT] Backtracked from \"" + junction + "\".");
                    }
                    break;
                }
                case 3: {
                    String junction = rescue.peek();
                    if (junction != null) {
                        System.out.println("[CURRENT] Rescuer is at: " + junction);
                    }
                    break;
                }
                case 4:
                    rescue.display();
                    break;
                case 5:
                    System.out.println("isEmpty (at trailhead): " + rescue.isEmpty());
                    break;
                case 6:
                    System.out.println("[DEPTH] Rescuer is " + rescue.depth() + " junction(s) deep.");
                    break;
                case 7:
                    rescue.emergencyRecall();
                    break;
                case 8: {
                    System.out.print("Enter junction name to check: ");
                    String junction = readLine();
                    boolean visited = rescue.alreadyVisited(junction);
                    System.out.println("[CHECK] \"" + junction + "\" already visited on this path: " + visited);
                    break;
                }
                case 9:
                    System.out.println("\nExiting Search & Rescue Backtrack Assistant. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1-9.");
            }

        } while (choice != 9);

    }
}
