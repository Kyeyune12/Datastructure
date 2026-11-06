import java.util.Scanner;

/*
 * Project B: Warehouse Conveyor Dispatch
 * Data structure: Queue (FIFO) in a fixed-size CIRCULAR array.
 *
 * Packages are loaded at the rear (enqueue) and dispatched from the front
 * (dequeue) in the order they arrived. front and rear wrap around with
 * (index + 1) % CAPACITY, so no element is ever shifted.
 *
 * Run: javac ConveyorDispatch.java && java ConveyorDispatch
 */
public class ConveyorDispatch {

    static final int CAPACITY = 5;

    // One package on the belt
    static class Package {
        int id;
        String destination;

        Package(int id, String destination) {
            this.id = id;
            this.destination = destination;
        }
    }

    // The belt itself
    static Package[] items = new Package[CAPACITY];
    static int front = 0;   // index of the next package to leave
    static int rear = -1;   // index of the last package that joined
    static int count = 0;   // how many packages are on the belt

    static boolean isEmpty() {
        return count == 0;
    }

    static boolean isFull() {
        return count == CAPACITY;
    }

    // Add a package at the rear
    static void enqueue(Package p) {
        if (isFull()) {
            System.out.println("Belt is full! Cannot load package #" + p.id + ".");
            return;
        }
        rear = (rear + 1) % CAPACITY;   // move rear forward, wrap to 0 at the end
        items[rear] = p;
        count++;
        System.out.println("Loaded package #" + p.id + " -> " + p.destination + " at slot " + rear + ".");
    }

    // Remove and return the package at the front (null if the belt is empty)
    static Package dequeue() {
        if (isEmpty()) {
            System.out.println("Belt is empty! Nothing to dispatch.");
            return null;
        }
        Package p = items[front];
        items[front] = null;              // clear the slot (nothing is shifted)
        front = (front + 1) % CAPACITY;   // move front forward, wrap to 0 at the end
        count--;
        return p;
    }

    // Look at the front package without removing it (null if empty)
    static Package peek() {
        if (isEmpty()) {
            return null;
        }
        return items[front];
    }

    // Print every package from front to rear
    static void display() {
        if (isEmpty()) {
            System.out.println("Belt is empty.");
            return;
        }
        System.out.println("Belt (front to rear):");
        for (int i = 0; i < count; i++) {
            int index = (front + i) % CAPACITY;
            System.out.println("  Slot " + index + ": Package #" + items[index].id + " -> " + items[index].destination);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int nextId = 1;   // package IDs are given out automatically
        int choice = 0;

        System.out.println("*** Warehouse Conveyor Dispatch ***");

        while (choice != 6) {
            System.out.println("\n1. Load package (enqueue)");
            System.out.println("2. Dispatch next package (dequeue)");
            System.out.println("3. Next package (peek)");
            System.out.println("4. View the belt");
            System.out.println("5. isEmpty / isFull");
            System.out.println("6. Exit");
            System.out.print("Enter choice: ");

            if (!scanner.hasNextInt()) {   // the user typed letters, not a number
                scanner.nextLine();
                System.out.println("Please enter a number.");
                continue;
            }
            choice = scanner.nextInt();
            scanner.nextLine();            // throw away the Enter key

            if (choice == 1) {
                System.out.print("Enter destination: ");
                String destination = scanner.nextLine();
                enqueue(new Package(nextId, destination));
                nextId++;

            } else if (choice == 2) {
                Package p = dequeue();
                if (p != null) {
                    System.out.println("Dispatched package #" + p.id + " -> " + p.destination + ".");
                }

            } else if (choice == 3) {
                Package p = peek();
                if (p != null) {
                    System.out.println("Next to dispatch: package #" + p.id + " -> " + p.destination);
                } else {
                    System.out.println("Belt is empty.");
                }

            } else if (choice == 4) {
                display();

            } else if (choice == 5) {
                System.out.println("isEmpty: " + isEmpty() + "   isFull: " + isFull());

            } else if (choice == 6) {
                System.out.println("Exiting. Goodbye!");

            } else {
                System.out.println("Invalid choice. Please enter 1-6.");
            }
        }

        scanner.close();
    }
}
