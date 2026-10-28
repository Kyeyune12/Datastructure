/*
 * GREEDY PROBLEM 1: Internet Bandwidth Allocation
 *
 * Situation: An internet link has a fixed total bandwidth (e.g. 100 Mbps).
 * Several users/apps request bandwidth. Each one has a PRIORITY
 * (how important each Mbps is to them, e.g. a video call > a download).
 *
 * Goal: Share the bandwidth so the total "benefit" (priority x Mbps given)
 * is as large as possible.
 *
 * Greedy idea: Always serve the highest-priority request first.
 * Give it everything it asks for if possible; if not, give it whatever is left.
 *
 * How to run:
 *   javac BandwidthAllocation.java
 *   java BandwidthAllocation
 */
public class BandwidthAllocation {

    // A simple class to hold one user's request
    static class Request {
        String name;
        int demand;     // Mbps the user asks for
        int priority;   // benefit per Mbps (higher = more important)

        Request(String name, int demand, int priority) {
            this.name = name;
            this.demand = demand;
            this.priority = priority;
        }
    }

    // Bubble sort: keep swapping neighbours until the highest priority is first
    static void sortByPriority(Request[] requests) {
        for (int i = 0; i < requests.length - 1; i++) {
            for (int j = 0; j < requests.length - 1 - i; j++) {
                if (requests[j].priority < requests[j + 1].priority) {
                    Request temp = requests[j];
                    requests[j] = requests[j + 1];
                    requests[j + 1] = temp;
                }
            }
        }
    }

    public static void main(String[] args) {
        int totalBandwidth = 100; // Mbps available on the link

        Request[] requests = {
            new Request("File Download", 50, 1),
            new Request("Video Call",    30, 5),
            new Request("Online Exam",   20, 8),
            new Request("Video Stream",  40, 3),
            new Request("Email",          5, 2)
        };

        // STEP 1 (the greedy choice): sort by priority, highest first
        sortByPriority(requests);

        int remaining = totalBandwidth;
        int totalBenefit = 0;

        System.out.println("Total bandwidth available: " + totalBandwidth + " Mbps\n");
        System.out.printf("%-15s %-8s %-8s %-10s%n", "User", "Demand", "Given", "Priority");

        // STEP 2: go through the sorted list and give bandwidth
        for (Request r : requests) {
            int given = Math.min(r.demand, remaining); // full demand, or what's left
            remaining -= given;
            totalBenefit += given * r.priority;
            System.out.printf("%-15s %-8d %-8d %-10d%n", r.name, r.demand, given, r.priority);
        }

        System.out.println("\nBandwidth left unused: " + remaining + " Mbps");
        System.out.println("Total benefit (priority x Mbps): " + totalBenefit);
    }
}
