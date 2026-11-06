import java.util.Scanner;

/*
 * Project A: Search & Rescue Trail Backtrack Assistant
 * Data structure: Stack (LIFO), built on a linked list.
 *
 * A rescuer moves deeper into a trail system (push). At a dead end they
 * go back to the junction they came from (pop). The most recent junction
 * is always the one to return to, so a stack is the right structure.
 *
 * Core stack operations: push, pop, peek, isEmpty
 * Extra operations:      depth, emergencyRecall, alreadyVisited
 *
 * There is no isFull(): a linked list grows one node at a time,
 * so the trail can be as deep as memory allows.
 *
 * Run: javac NavigationStack.java && java NavigationStack
 */
public class NavigationStack {

    // One junction on the trail
    static class Node {
        String junction;
        Node next;   // the junction we came from

        Node(String junction, Node next) {
            this.junction = junction;
            this.next = next;
        }
    }

    Node top;   // the rescuer's current position (null = at the trailhead)

    boolean isEmpty() {
        return top == null;
    }

    // push: step deeper into a new junction
    void push(String junction) {
        top = new Node(junction, top);   // new node points down to the old top
        System.out.println("Moved into junction \"" + junction + "\".");
    }

    // pop: dead end reached - go back to the previous junction
    String pop() {
        if (isEmpty()) {
            System.out.println("Already at the trailhead. Nowhere to go back to.");
            return null;
        }
        String junction = top.junction;
        top = top.next;   // the junction underneath becomes the top
        return junction;
    }

    // peek: see the current position without moving
    String peek() {
        if (isEmpty()) {
            return null;
        }
        return top.junction;
    }

    // display: print the whole trail, newest junction first
    void display() {
        if (isEmpty()) {
            System.out.println("No junctions yet. Rescuer is at the trailhead.");
            return;
        }
        System.out.println("Trail (current -> trailhead):");
        Node current = top;
        while (current != null) {
            System.out.println("  " + current.junction);
            current = current.next;
        }
    }

    // depth: how many junctions deep the rescuer is (count the nodes)
    int depth() {
        int count = 0;
        Node current = top;
        while (current != null) {
            count++;
            current = current.next;
        }
        return count;
    }

    // emergencyRecall: go all the way back to the trailhead by popping everything
    void emergencyRecall() {
        System.out.println("EMERGENCY RECALL: going back to the trailhead...");
        while (!isEmpty()) {
            System.out.println("  passed back through " + pop());
        }
        System.out.println("Rescuer is back at the trailhead.");
    }

    // alreadyVisited: search the trail without changing it
    boolean alreadyVisited(String junction) {
        Node current = top;   // a separate pointer, so top itself never moves
        while (current != null) {
            if (current.junction.equalsIgnoreCase(junction)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public static void main(String[] args) {
        NavigationStack rescue = new NavigationStack();
        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        System.out.println("*** Search & Rescue Backtrack Assistant ***");

        while (choice != 8) {
            System.out.println("\n1. Move to a new junction (push)");
            System.out.println("2. Dead end - go back (pop)");
            System.out.println("3. Current position (peek)");
            System.out.println("4. Show the whole trail");
            System.out.println("5. How deep am I? (depth)");
            System.out.println("6. Emergency recall");
            System.out.println("7. Was a junction already visited?");
            System.out.println("8. Exit");
            System.out.print("Enter choice: ");

            if (!scanner.hasNextInt()) {   // the user typed letters, not a number
                scanner.nextLine();
                System.out.println("Please enter a number.");
                continue;
            }
            choice = scanner.nextInt();
            scanner.nextLine();            // throw away the Enter key

            if (choice == 1) {
                System.out.print("Enter junction name: ");
                rescue.push(scanner.nextLine());

            } else if (choice == 2) {
                String left = rescue.pop();
                if (left != null) {
                    System.out.println("Dead end. Left " + left + ".");
                    if (rescue.isEmpty()) {
                        System.out.println("Now at the trailhead.");
                    } else {
                        System.out.println("Now at " + rescue.peek() + ".");
                    }
                }

            } else if (choice == 3) {
                if (rescue.isEmpty()) {
                    System.out.println("At the trailhead.");
                } else {
                    System.out.println("Current position: " + rescue.peek());
                }

            } else if (choice == 4) {
                rescue.display();

            } else if (choice == 5) {
                System.out.println("Depth: " + rescue.depth() + " junction(s).");

            } else if (choice == 6) {
                rescue.emergencyRecall();

            } else if (choice == 7) {
                System.out.print("Enter junction name to check: ");
                String junction = scanner.nextLine();
                System.out.println("Already visited: " + rescue.alreadyVisited(junction));

            } else if (choice == 8) {
                System.out.println("Exiting Search & Rescue Assistant.");

            } else {
                System.out.println("Invalid choice. Please enter 1-8.");
            }
        }

        scanner.close();
    }
}
