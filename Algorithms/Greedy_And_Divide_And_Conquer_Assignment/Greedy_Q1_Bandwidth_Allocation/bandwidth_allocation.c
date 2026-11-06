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
 *   gcc bandwidth_allocation.c -o bandwidth_allocation
 *   ./bandwidth_allocation
 */
#include <stdio.h>

/* One user's request */
struct Request {
    char name[20];
    int demand;    /* Mbps the user asks for */
    int priority;  /* benefit per Mbps (higher = more important) */
};

/* Simple bubble sort: highest priority first */
void sortByPriority(struct Request r[], int n) {
    for (int i = 0; i < n - 1; i++) {
        for (int j = 0; j < n - 1 - i; j++) {
            if (r[j].priority < r[j + 1].priority) {
                struct Request temp = r[j];
                r[j] = r[j + 1];
                r[j + 1] = temp;
            }
        }
    }
}

int main(void) {
    int totalBandwidth = 100; /* Mbps available on the link */

    struct Request requests[] = {
        {"File Download", 50, 1},
        {"Video Call",    30, 5},
        {"Online Exam",   20, 8},
        {"Video Stream",  40, 3},
        {"Email",          5, 2}
    };
    int n = sizeof(requests) / sizeof(requests[0]);

    /* STEP 1 (the greedy choice): sort by priority, highest first */
    sortByPriority(requests, n);

    int remaining = totalBandwidth;
    int totalBenefit = 0;

    printf("Total bandwidth available: %d Mbps\n\n", totalBandwidth);
    printf("%-15s %-8s %-8s %-10s\n", "User", "Demand", "Given", "Priority");

    /* STEP 2: go through the sorted list and give bandwidth */
    for (int i = 0; i < n; i++) {
        int given = requests[i].demand;
        if (given > remaining) {
            given = remaining;          /* not enough left: give what remains */
        }
        remaining -= given;
        totalBenefit += given * requests[i].priority;
        printf("%-15s %-8d %-8d %-10d\n", requests[i].name,
               requests[i].demand, given, requests[i].priority);
    }

    printf("\nBandwidth left unused: %d Mbps\n", remaining);
    printf("Total benefit (priority x Mbps): %d\n", totalBenefit);
    return 0;
}
