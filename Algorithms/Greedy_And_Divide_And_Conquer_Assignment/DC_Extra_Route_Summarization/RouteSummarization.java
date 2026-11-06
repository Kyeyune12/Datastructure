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
 *   CONQUER: find the common bit-prefix of each half (recursively).
 *   COMBINE: common prefix of the two results.
 *   BASE CASE: one address -> all 32 of its bits.
 * Then fill the remaining bits with 0 to get the network address.
 *
 * How to run:
 *   javac RouteSummarization.java
 *   java RouteSummarization
 */
public class RouteSummarization {

    // "192.168.4.0" -> "11000000101010000000010000000000" (32 bits)
    static String toBinary(String ip) {
        String[] parts = ip.split("\\.");
        String bits = "";
        for (String part : parts) {
            int number = Integer.parseInt(part);           // 0..255
            String b = Integer.toBinaryString(number);     // e.g. "100"
            while (b.length() < 8) {                       // pad to 8 bits
                b = "0" + b;
            }
            bits += b;
        }
        return bits;
    }

    // 32 bits -> "192.168.4.0"
    static String toDotted(String bits) {
        String ip = "";
        for (int i = 0; i < 32; i += 8) {
            int number = Integer.parseInt(bits.substring(i, i + 8), 2);
            ip += number;
            if (i < 24) {
                ip += ".";                                 // no dot after the last part
            }
        }
        return ip;
    }

    // COMBINE step: common prefix of TWO bit strings
    static String commonPrefix(String a, String b) {
        int i = 0;
        while (i < a.length() && i < b.length() && a.charAt(i) == b.charAt(i)) {
            i++;
        }
        return a.substring(0, i);
    }

    // Divide & Conquer: common prefix of bits[low..high]
    static String lcp(String[] bits, int low, int high) {
        // BASE CASE: only one address
        if (low == high) {
            return bits[low];
        }

        // DIVIDE
        int mid = (low + high) / 2;

        // CONQUER
        String left = lcp(bits, low, mid);
        String right = lcp(bits, mid + 1, high);

        // COMBINE
        return commonPrefix(left, right);
    }

    public static void main(String[] args) {
        String[] ips = {"192.168.4.0", "192.168.5.0", "192.168.6.0", "192.168.7.0"};

        // Step 1: convert every address to 32 bits
        String[] bits = new String[ips.length];
        System.out.println("Addresses in binary:");
        for (int i = 0; i < ips.length; i++) {
            bits[i] = toBinary(ips[i]);
            System.out.printf("  %-15s %s%n", ips[i], bits[i]);
        }

        // Step 2: Divide & Conquer longest common prefix of the bits
        String prefix = lcp(bits, 0, bits.length - 1);
        int prefixLength = prefix.length();

        // Step 3: fill the rest with zeros to get the network address
        String networkBits = prefix;
        while (networkBits.length() < 32) {
            networkBits += "0";
        }

        System.out.println("\nCommon prefix: " + prefix + "  (" + prefixLength + " bits)");
        System.out.println("Summary route: " + toDotted(networkBits) + "/" + prefixLength);
        System.out.println("Routing entries: " + ips.length + " -> 1");
    }
}
