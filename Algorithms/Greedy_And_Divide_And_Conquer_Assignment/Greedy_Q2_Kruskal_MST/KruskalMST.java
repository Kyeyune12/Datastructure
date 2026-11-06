/*
 * GREEDY PROBLEM 2: Kruskal's Minimum Spanning Tree (MST)
 *
 * Situation: We want to connect 6 campus buildings with network cable.
 * Each possible connection (edge) has a cost (metres of cable).
 *
 * Goal: Connect ALL buildings using the LEAST total cable, with no loops.
 *
 * Greedy idea: Sort all cables from cheapest to most expensive.
 * Pick the cheapest one each time, UNLESS it would create a loop (cycle).
 * Stop when we have (number of buildings - 1) cables.
 *
 * To detect loops we use "Union-Find": every building belongs to a group.
 * If two buildings are already in the same group, joining them makes a loop.
 *
 * How to run:
 *   javac KruskalMST.java
 *   java KruskalMST
 */
public class KruskalMST {

    // An edge = a possible cable between two buildings
    static class Edge {
        int from;   // building at one end
        int to;     // building at the other end
        int cost;   // metres of cable
        Edge(int from, int to, int cost) {
            this.from = from;
            this.to = to;
            this.cost = cost;
        }
    }

    static int[] parent; // parent[i] = the "leader" of building i's group

    // Find the leader of the group that building x belongs to
    static int find(int x) {
        while (parent[x] != x) {
            x = parent[x];
        }
        return x;
    }

    // Join the groups of a and b into one group
    static void union(int a, int b) {
        parent[find(a)] = find(b);
    }

    // Bubble sort: keep swapping neighbours until the cheapest edge is first
    static void sortByCost(Edge[] edges) {
        for (int i = 0; i < edges.length - 1; i++) {
            for (int j = 0; j < edges.length - 1 - i; j++) {
                if (edges[j].cost > edges[j + 1].cost) {
                    Edge temp = edges[j];
                    edges[j] = edges[j + 1];
                    edges[j + 1] = temp;
                }
            }
        }
    }

    public static void main(String[] args) {
        String[] buildings = {"Library", "Admin", "Lab", "Hostel", "Cafeteria", "Hall"};
        int n = buildings.length;

        Edge[] edges = {
            new Edge(0, 1, 4),  // Library - Admin
            new Edge(0, 2, 3),  // Library - Lab
            new Edge(1, 2, 1),  // Admin - Lab
            new Edge(1, 3, 2),  // Admin - Hostel
            new Edge(2, 3, 4),  // Lab - Hostel
            new Edge(3, 4, 2),  // Hostel - Cafeteria
            new Edge(4, 5, 6),  // Cafeteria - Hall
            new Edge(3, 5, 7)   // Hostel - Hall
        };

        // STEP 1 (the greedy choice): sort edges from cheapest to most expensive
        sortByCost(edges);

        // At the start, every building is its own group
        parent = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }

        int totalCost = 0;
        int edgesUsed = 0;

        System.out.println("Cables chosen for the Minimum Spanning Tree:");

        // STEP 2: take the cheapest edges that don't make a loop
        for (Edge e : edges) {
            if (edgesUsed == n - 1) {
                break;   // tree is complete: n buildings need only n - 1 cables
            }

            if (find(e.from) != find(e.to)) {      // different groups -> no loop
                union(e.from, e.to);
                totalCost += e.cost;
                edgesUsed++;
                System.out.println("  " + buildings[e.from] + " - " + buildings[e.to]
                                   + "  (cost " + e.cost + ")");
            } else {
                System.out.println("  [skip] " + buildings[e.from] + " - " + buildings[e.to]
                                   + " would make a loop");
            }
        }

        System.out.println("\nTotal cable cost: " + totalCost);
    }
}
