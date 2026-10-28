# route_summarization.c

**Extra Divide & Conquer Problem, in C.** Combines four router entries (192.168.4.0 – 192.168.7.0) into one **summary route**, `192.168.4.0/22`, by finding the longest common prefix of their 32-bit binary forms with divide and conquer. Its output is identical to [RouteSummarization.java](RouteSummarization.java.md).

**Language:** C · **Java version:** [RouteSummarization.java](RouteSummarization.java.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
gcc route_summarization.c -o route_summarization
./route_summarization
```

## The networking problem

A router has four separate entries:

```
192.168.4.0
192.168.5.0
192.168.6.0
192.168.7.0
```

Every entry is a row the router searches when forwarding a packet. Fewer rows means a smaller table, faster lookups, and fewer routes to advertise to other routers. **Route summarization** (also called *supernetting* or *aggregation*) replaces several routes with a single one, written `network/length`. Here that's `192.168.4.0/22`, which means "every address whose first 22 bits match 192.168.4.0".

## Turning it into a string problem

Written in binary, the four addresses are:

```
192.168.4.0  11000000 10101000 000001|00 00000000
192.168.5.0  11000000 10101000 000001|01 00000000
192.168.6.0  11000000 10101000 000001|10 00000000
192.168.7.0  11000000 10101000 000001|11 00000000
             <------- 22 bits shared ------>
```

The summary route is simply the **longest common prefix** of these four 32-character binary strings:

- its **length** (22) is the `/22`,
- its **bits**, with zeros filling the remaining 10, give the network address `192.168.4.0`.

That is exactly the problem solved in [longest_common_prefix.c](../DC_Extra_Longest_Common_Prefix/longest_common_prefix.c.md), with bits in place of letters. The same divide and conquer function is reused.

## Walking through the code

### `toBinary` — dotted decimal → 32 bits

```c
void toBinary(const char *ip, char bits[33]) {
    int part[4];
    sscanf(ip, "%d.%d.%d.%d", &part[0], &part[1], &part[2], &part[3]);
    int pos = 0;
    for (int p = 0; p < 4; p++)
        for (int bit = 7; bit >= 0; bit--)
            bits[pos++] = ((part[p] >> bit) & 1) ? '1' : '0';
    bits[32] = '\0';
}
```

- `sscanf` with the pattern `"%d.%d.%d.%d"` reads the four numbers (octets) between the dots.
- For each octet, `bit` counts from 7 down to 0, so the **most significant bit comes first**, as in normal binary notation.
- `(part[p] >> bit) & 1` shifts the wanted bit to the far right and masks off everything else, which gives 0 or 1. For 168 = `10101000`: shifting by 7 gives 1, by 6 gives 0, by 5 gives 1, and so on.
- `bits` has 33 slots: 32 characters plus the `'\0'` string terminator.

### `printDotted` — 32 bits → dotted decimal

```c
for (int p = 0; p < 4; p++) {
    int number = 0;
    for (int i = 0; i < 8; i++)
        number = number * 2 + (bits[p * 8 + i] - '0');
    printf("%d", number);
    if (p < 3) printf(".");
}
```

This is the reverse of `toBinary`. It reads 8 characters at a time and builds the number with the doubling rule (`number = number × 2 + next bit`). `bits[...] - '0'` turns the character `'0'` or `'1'` into the number 0 or 1.

### `commonLength` and `lcp` — divide & conquer on the bit strings

```c
int commonLength(const char *a, const char *b, int limit) {
    int i = 0;
    while (i < limit && a[i] == b[i]) i++;
    return i;
}

int lcp(char bits[][33], int low, int high) {
    if (low == high) return 32;                              /* BASE CASE */
    int mid = (low + high) / 2;                              /* DIVIDE */
    int left  = lcp(bits, low, mid);                         /* CONQUER */
    int right = lcp(bits, mid + 1, high);
    int limit = (left < right) ? left : right;
    return commonLength(bits[low], bits[mid + 1], limit);    /* COMBINE */
}
```

This is the same length-only approach as `longest_common_prefix.c`:

- **Base case:** one address, so all **32** bits are "common".
- **Combine:** the left half all shares its first `left` bits with `bits[low]`, and the right half shares its first `right` bits with `bits[mid + 1]`. So the whole range shares however many leading bits `bits[low]` and `bits[mid + 1]` have in common, up to `min(left, right)`.
- `commonLength` doesn't check for `'\0'`, unlike the word version. Every string is exactly 32 bits, and `limit` is never more than 32, so it can't run past the end.
- `char bits[][33]` is a 2-D array parameter. C needs the column size (33) to work out where each row starts.

### `main` — three steps

1. Convert each IP with `toBinary` and print it next to its binary form.
2. `prefixLength = lcp(bits, 0, COUNT − 1)`.
3. Build `network`: copy the first `prefixLength` bits from `bits[0]` and fill the rest with `'0'`. Then print it with `printDotted` plus `"/" prefixLength`.

`printf("%.*s", prefixLength, bits[0])` prints only the first `prefixLength` characters, which shows the shared prefix without building a new string.

## Trace

Only bits 16–23 (the third octet) differ, so only those are shown:

| Address | Third octet | Bits 16–23 |
|---|---|---|
| 192.168.4.0 | 4 | `00000100` |
| 192.168.5.0 | 5 | `00000101` |
| 192.168.6.0 | 6 | `00000110` |
| 192.168.7.0 | 7 | `00000111` |

| Call | left | right | limit | Compare | First difference | Result |
|---|---|---|---|---|---|---|
| lcp(0,1) | 32 | 32 | 32 | .4.0 vs .5.0 | bit 23 (`0` vs `1`) | **23** |
| lcp(2,3) | 32 | 32 | 32 | .6.0 vs .7.0 | bit 23 | **23** |
| lcp(0,3) | 23 | 23 | 23 | .4.0 vs .6.0 | bit 22 (`0` vs `1`) | **22** |

Prefix = first 22 bits, then 10 zeros → `11000000.10101000.00000100.00000000` = **192.168.4.0/22**.

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

n addresses, each 32 bits.

| | Value |
|---|---|
| Recurrence | T(n) = 2T(n/2) + O(32) |
| Time | O(32 · n) = **O(n)** |
| Space | O(32 · n) for the bit strings, plus O(log n) recursion stack |

## An important networking caveat

A summary covers **every** address that matches its prefix, not just the ones you started with. `/22` leaves 10 bits free, so `192.168.4.0/22` covers 2¹⁰ = 1024 addresses: 192.168.4.0 to 192.168.7.255. That's exactly the four /24 networks here, so the summary is **exact**.

It doesn't always work out that way. If the router only had `192.168.4.0` and `192.168.7.0`, the prefix would still be 22 bits and the summary would still be `192.168.4.0/22`. That summary also claims `.5.x` and `.6.x`, which the router doesn't actually have, and could attract traffic it can't deliver. A summary is exact only when the networks are **contiguous** and **aligned** to a power of 2. This program assumes that and doesn't check it.

## Notes

- `sscanf` doesn't validate the input. An octet above 255 or a malformed string would give wrong bits.
- Bitwise math on a `uint32_t` would be faster than strings: XOR two addresses and count the leading zeros. The string version is used because it reuses the longest-common-prefix code directly.
