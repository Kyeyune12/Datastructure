# RouteSummarization.java

**Extra Divide & Conquer Problem, in Java.** Summarises the routes 192.168.4.0 – 192.168.7.0 into the single entry `192.168.4.0/22` by finding the longest common prefix of their 32-bit binary strings with divide and conquer. Its output is identical to [route_summarization.c](route_summarization.c.md).

**Language:** Java · **C version:** [route_summarization.c](route_summarization.c.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
javac RouteSummarization.java
java RouteSummarization
```

## The problem in one paragraph

A router has four entries, 192.168.4.0, .5.0, .6.0 and .7.0. Replacing them with one **summary route** shrinks the routing table. Written in binary, all four share their first **22 bits**. That shared prefix with zeros after it is the network address, and its length is the `/22`. So route summarization is the **longest common prefix** problem on binary strings, the same algorithm as [LongestCommonPrefix.java](../DC_Extra_Longest_Common_Prefix/LongestCommonPrefix.java.md).

```
192.168.4.0  1100000010101000000001|0000000000
192.168.5.0  1100000010101000000001|0100000000
192.168.6.0  1100000010101000000001|1000000000
192.168.7.0  1100000010101000000001|1100000000
             <------ 22 bits ----->
```

## Walking through the code

### `toBinary` — "192.168.4.0" → 32-character bit string

```java
String[] parts = ip.split("\\.");
for (String part : parts) {
    int number = Integer.parseInt(part);          // 0..255
    String b = Integer.toBinaryString(number);    // e.g. 4 -> "100"
    while (b.length() < 8) {                      // pad to 8 bits -> "00000100"
        b = "0" + b;
    }
    bits += b;
}
```

- `split("\\.")`: the argument of `split` is a **regular expression**, and in a regex a plain `.` means "any character". `\\.` (an escaped dot) means a literal dot.
- `Integer.toBinaryString` drops leading zeros (4 → `"100"`), so the `while` loop pads each octet back to exactly 8 bits. Without the padding, the four octets wouldn't line up in 32 positions and the prefix comparison would be wrong.

The C version does the same job with `sscanf` and bit shifts (`(part >> bit) & 1`).

### `toDotted` — 32 bits → "192.168.4.0"

```java
for (int i = 0; i < 32; i += 8) {
    int number = Integer.parseInt(bits.substring(i, i + 8), 2);
    ip += number;
    if (i < 24) {
        ip += ".";                                // no dot after the last part
    }
}
```

Takes 8 bits at a time. `Integer.parseInt(text, 2)` reads the text as base 2, so `"00000100"` becomes 4. A dot goes after every octet except the last one (which starts at i = 24).

### `commonPrefix` and `lcp` — divide & conquer

```java
static String commonPrefix(String a, String b) {
    int i = 0;
    while (i < a.length() && i < b.length() && a.charAt(i) == b.charAt(i)) i++;
    return a.substring(0, i);
}

static String lcp(String[] bits, int low, int high) {
    // BASE CASE: only one address -> all 32 bits
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
```

This is the same structure as `LongestCommonPrefix.java`. `lcp` returns the actual prefix string, and its `.length()` is the prefix length. The C version returns only the length, starting from 32 in its base case.

### `main` — three steps

```java
bits[i] = toBinary(ips[i]);                       // 1. convert and print
String prefix = lcp(bits, 0, bits.length - 1);    // 2. divide & conquer
int prefixLength = prefix.length();
String networkBits = prefix;
while (networkBits.length() < 32) {               // 3. pad with zeros
    networkBits += "0";
}
System.out.println("Summary route: " + toDotted(networkBits) + "/" + prefixLength);
```

`printf("  %-15s %s%n", ...)` left-aligns each IP in 15 columns, so the binary strings line up.

## Trace

| Call | Combine | Common prefix length |
|---|---|---|
| lcp(0,1) | .4.0 with .5.0 (third octets `00000100` / `00000101`) | 23 |
| lcp(2,3) | .6.0 with .7.0 (`00000110` / `00000111`) | 23 |
| lcp(0,3) | the two 23-bit prefixes (`…0000010` / `…0000011`) | **22** |

22 bits + 10 zeros → **192.168.4.0/22**.

## Output

```
Addresses in binary:
  192.168.4.0     11000000101010000000010000000000
  192.168.5.0     11000000101010000000010100000000
  192.168.6.0     11000000101010000000011000000000
  192.168.7.0     11000000101010000000011100000000

Common prefix: 1100000010101000000001  (22 bits)
Summary route: 192.168.4.0/22
Routing entries: 4 -> 1
```

## Complexity

| | Value |
|---|---|
| Time | T(n) = 2T(n/2) + O(32) → **O(n)** for n addresses |
| Space | O(32 · n) for the bit strings; O(32 log n) for prefixes held during recursion |

## Networking caveat

`/22` covers all 1024 addresses from 192.168.4.0 to 192.168.7.255. That's exact here, because the four /24s are **contiguous and aligned**. For a set like {.4.0, .7.0} alone, the same summary would also claim .5.x and .6.x, which aren't really there. The program assumes the inputs summarise exactly. [route_summarization.c.md](route_summarization.c.md) explains this in more detail.

## C vs Java differences

| | C | Java |
|---|---|---|
| Parse octets | `sscanf("%d.%d.%d.%d")` | `split("\\.")` + `Integer.parseInt` |
| To binary | bit shift and mask: `(x >> bit) & 1` | `Integer.toBinaryString` + zero padding |
| From binary | doubling loop | `Integer.parseInt(s, 2)` |
| `lcp` returns | prefix **length** (`int`) | prefix **string** |
| Pad to 32 bits | fill a `char[33]` | `while (length < 32) += "0"` |

## What changed (simplified version)

This file never had imports. The algorithm and the output are the same; only the layout changed.

| Before | Now | Why |
|---|---|---|
| One-line `while` and `if` statements, e.g. `while (b.length() < 8) b = "0" + b;` | Braces on every loop and `if`, with the body on its own line | Easier to read |
| `lcp` written as four packed lines with comments at the end | Each step (BASE CASE, DIVIDE, CONQUER, COMBINE) has its own comment line above it | The divide & conquer steps stand out |
