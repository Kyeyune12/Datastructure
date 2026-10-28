/*
 * EXTRA DIVIDE & CONQUER PROBLEM: Route Summarization of IP Addresses
 *
 * Situation: A router has separate entries for several networks:
 *   192.168.4.0, 192.168.5.0, 192.168.6.0, 192.168.7.0
 * Fewer entries = smaller routing table = faster packet forwarding.
 *
 * Goal: Replace them with ONE summary route, e.g. 192.168.4.0/22.
 * "/22" means: the first 22 bits are the same in every address.
 *
 * How: write every IP address as 32 binary digits (bits). The summary route
 * is just the LONGEST COMMON PREFIX of those binary strings, which we find
 * with Divide & Conquer (exactly like the list-of-words problem):
 *   DIVIDE : split the list of addresses into two halves.
 *   CONQUER: find the common bit-prefix length of each half (recursively).
 *   COMBINE: compare one address from each half, up to the shorter length.
 *   BASE CASE: one address -> all 32 of its bits.
 * Then fill the remaining bits with 0 to get the network address.
 *
 * How to run:
 *   gcc route_summarization.c -o route_summarization
 *   ./route_summarization
 */
#include <stdio.h>

#define COUNT 4

/* "192.168.4.0" -> "11000000101010000000010000000000" (32 bits + '\0') */
void toBinary(const char *ip, char bits[33]) {
    int part[4];
    sscanf(ip, "%d.%d.%d.%d", &part[0], &part[1], &part[2], &part[3]);
    int pos = 0;
    for (int p = 0; p < 4; p++) {
        for (int bit = 7; bit >= 0; bit--) {          /* 8 bits per part */
            bits[pos++] = ((part[p] >> bit) & 1) ? '1' : '0';
        }
    }
    bits[32] = '\0';
}

/* 32 bits -> prints like 192.168.4.0 */
void printDotted(const char bits[33]) {
    for (int p = 0; p < 4; p++) {
        int number = 0;
        for (int i = 0; i < 8; i++) {
            number = number * 2 + (bits[p * 8 + i] - '0');
        }
        printf("%d", number);
        if (p < 3) printf(".");
    }
}

/* COMBINE step: how many starting bits do a and b share (up to limit)? */
int commonLength(const char *a, const char *b, int limit) {
    int i = 0;
    while (i < limit && a[i] == b[i]) i++;
    return i;
}

/* Divide & Conquer: length of the common prefix of bits[low..high] */
int lcp(char bits[][33], int low, int high) {
    if (low == high) return 32;                                 /* BASE CASE */
    int mid = (low + high) / 2;                                 /* DIVIDE    */
    int left  = lcp(bits, low, mid);                            /* CONQUER   */
    int right = lcp(bits, mid + 1, high);
    int limit = (left < right) ? left : right;
    return commonLength(bits[low], bits[mid + 1], limit);       /* COMBINE   */
}

int main(void) {
    const char *ips[COUNT] = {"192.168.4.0", "192.168.5.0", "192.168.6.0", "192.168.7.0"};
    char bits[COUNT][33];

    /* Step 1: convert every address to 32 bits */
    printf("Addresses in binary:\n");
    for (int i = 0; i < COUNT; i++) {
        toBinary(ips[i], bits[i]);
        printf("  %-15s %s\n", ips[i], bits[i]);
    }

    /* Step 2: Divide & Conquer longest common prefix of the bits */
    int prefixLength = lcp(bits, 0, COUNT - 1);

    /* Step 3: keep the common bits, fill the rest with zeros */
    char network[33];
    for (int i = 0; i < 32; i++) {
        network[i] = (i < prefixLength) ? bits[0][i] : '0';
    }
    network[32] = '\0';

    printf("\nCommon prefix: %.*s  (%d bits)\n", prefixLength, bits[0], prefixLength);
    printf("Summary route: ");
    printDotted(network);
    printf("/%d\n", prefixLength);
    printf("Routing entries: %d -> 1\n", COUNT);
    return 0;
}
