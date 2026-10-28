/*
 * Warehouse Conveyor Package Dispatch System (Java)
 * ---------------------------------------------------
 * Implements a fixed-size circular array queue manually (no
 * imports, no ArrayList, no shifting of elements).
 *
 * Circular-array invariants:
 *   front = index of the next package to be dispatched (dequeued)
 *   rear  = index of the most recently added package (enqueued)
 *   count = number of packages currently in the queue
 *
 * enqueue: rear = (rear + 1) % CAPACITY, then store at packages[rear]
 * dequeue: read packages[front], then front = (front + 1) % CAPACITY
 *
 * Neither operation ever shifts array elements. The array stays fixed;
 * only front/rear/count move (and wrap around using modulo).
 */

/* ---------- 1. System model ---------- */

class Package {
    String id;
    String destination;

    Package(String id, String destination) {
        this.id = id;
        this.destination = destination;
    }

    @Override
    public String toString() {
        return "ID: " + id + " | Destination: " + destination;
    }
}

/* ---------- Data structure layer (Queue mechanics only) ---------- */

class ConveyorQueue {
    private static final int CAPACITY = 5;

    private Package[] packages;
    private int front;
    private int rear;
    private int count;

    ConveyorQueue() {
        packages = new Package[CAPACITY];
        initializeQueue();
    }

    void initializeQueue() {
        front = 0;
        rear = -1;
        count = 0;
    }

    boolean isEmpty() {
        return count == 0;
    }

    boolean isFull() {
        return count == CAPACITY;
    }

    boolean enqueue(Package pkg) {
        if (isFull()) {
            System.out.println("Conveyor buffer is full.");
            return false;
        }

        rear = (rear + 1) % CAPACITY;
        packages[rear] = pkg;
        count = count + 1;

        System.out.println("Package added successfully.");
        return true;
    }

    Package dequeue() {
        if (isEmpty()) {
            System.out.println("No packages waiting.");
            return null;
        }

        Package pkg = packages[front];
        packages[front] = null; // clear reference (not a shift)
        front = (front + 1) % CAPACITY;
        count = count - 1;

        System.out.println("Dispatching package:");
        System.out.println("  " + pkg);

        return pkg;
    }

    Package peek() {
        if (isEmpty()) {
            System.out.println("No packages waiting.");
            return null;
        }

        Package pkg = packages[front];
        System.out.println("Next package:");
        System.out.println("  " + pkg);

        // Peek must NOT modify front, rear, or count.
        return pkg;
    }

    void displayQueue() {
        if (isEmpty()) {
            System.out.println("Conveyor buffer is empty.");
            return;
        }

        System.out.println("Packages currently waiting (FIFO order):");

        int index = front;
        for (int i = 0; i < count; i++) {
            System.out.println("  [" + (i + 1) + "] " + packages[index]);
            index = (index + 1) % CAPACITY;
        }
    }

    int getCount() {
        return count;
    }

    int getFront() {
        return front;
    }

    int getRear() {
        return rear;
    }

    static int getCapacity() {
        return CAPACITY;
    }
}

/* ---------- Application layer ---------- */

public class ConveyorDispatch {

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

    static void dispatchNextPackage(ConveyorQueue queue) {
        if (queue.isEmpty()) {
            System.out.println("No package available for dispatch.");
            return;
        }

        Package pkg = queue.dequeue();

        System.out.println("Conveyor moving package...");
        System.out.println("Package ID: " + pkg.id);
        System.out.println("Destination: " + pkg.destination);
        System.out.println("Package dispatched successfully.");
    }

    public static void main(String[] args) {
        ConveyorQueue queue = new ConveyorQueue();

        int choice;

        do {
            System.out.println("\n================================");
            System.out.println(" WAREHOUSE CONVEYOR DISPATCH");
            System.out.println("================================");
            System.out.println("1. Add Package");
            System.out.println("2. Dispatch Next Package");
            System.out.println("3. View Next Package");
            System.out.println("4. View Waiting Packages");
            System.out.println("5. Show Buffer Status");
            System.out.println("6. Exit");
            System.out.println("================================");
            System.out.print("Enter choice: ");

            String line = readLine();
            try {
                choice = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                choice = -1;
            }

            switch (choice) {
                case 1:
                    System.out.print("Enter package ID: ");
                    String id = readLine();

                    System.out.print("Enter destination: ");
                    String destination = readLine();

                    queue.enqueue(new Package(id, destination));
                    break;

                case 2:
                    dispatchNextPackage(queue);
                    break;

                case 3:
                    queue.peek();
                    break;

                case 4:
                    queue.displayQueue();
                    break;

                case 5:
                    System.out.println("Capacity: " + ConveyorQueue.getCapacity());
                    System.out.println("Packages: " + queue.getCount());
                    System.out.println("Available: " + (ConveyorQueue.getCapacity() - queue.getCount()));
                    System.out.println("Front index: " + queue.getFront());
                    System.out.println("Rear index: " + queue.getRear());
                    break;

                case 6:
                    System.out.println("Exiting...");
                    break;

                default:
                    System.out.println("Invalid option.");
                    break;
            }

        } while (choice != 6);

    }
}