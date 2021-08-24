package de.mariushubatschek.is.modeling;

import java.util.*;

/**
 * Represents a domain. A doubly linked list is maintained for the current domain, and a stack is maintained for removed values
 */
public class Domain {

    private Object[] values;

    /**
     * -1 means value is in current domain, k>=0 means was removed at search level k
     */
    private int[] absent;

    private int[] next;

    private int[] prev;

    private int[] prevAbsent;

    private int head;

    private int tail;

    private int tailAbsent;

    private int size;

    public Domain(final Object[] values) {
        this.values = values;
        this.absent = new int[values.length];
        this.next = new int[values.length];
        this.prev = new int[values.length];
        this.prevAbsent = new int[values.length];
        Arrays.fill(this.absent, -1);
        for (int i = 0; i < values.length; i++) {
            next[i] = (i == values.length - 1) ? -1 : i+1;
            prev[i] = (i == 0) ? -1 : i - 1;
        }
        Arrays.fill(this.prevAbsent, -1);
        head = 0;
        tail = values.length - 1;
        tailAbsent = -1;
        size = values.length;
    }

    /**
     * Removes this single value from the domain
     *
     * Used for x != a or (remove a from don(x))
     * @param value
     * @param searchDepth
     */
    public void removeValue(final int value, final int searchDepth) {
        absent[value] = searchDepth;
        prevAbsent[value] = tailAbsent;
        tailAbsent = value;
        if (prev[value] == -1) {
            head = next[value];
        } else {
            next[prev[value]] = next[value];
        }
        if (next[value] == -1) {
            tail = prev[value];
        } else {
            prev[next[value]] = prev[value];
        }
        size--;
        //printInfo();
    }

    /**
     * Removes all domain values except for the specified value from the domain.
     *
     * Used for x <-- a or x = a
     * @param value
     * @param searchDepth
     */
    public void reduceTo(final int value, final int searchDepth) {
        int b = head;
        while (b != -1) {
            if (b != value) {
                removeValue(b, searchDepth);
            }
            b = next[b];
        }
    }

    public void addValue(final int value) {
        absent[value] = -1;
        tailAbsent = prevAbsent[value];
        if (prev[value] == -1) {
            head = value;
        } else {
            next[prev[value]] = value;
        }
        if (next[value] == -1) {
            tail = value;
        } else {
            prev[next[value]] = value;
        }
        //printInfo();
        size++;
    }

    public void restoreUpTo(final int searchDepth) {
        int b = tailAbsent;
        while (b != -1 && absent[b] >= searchDepth) {
            addValue(b);
            b = prevAbsent[b];
        }
    }

    public int size() {
        /*int size = 0;
        int b = head;
        while (b != -1) {
            size++;
            b = next[b];
        }*/
        return size;
    }

    public List<Integer> getValues() {
        List<Integer> values = new ArrayList<>();
        int b = head;
        while (b != -1) {
            values.add(b);
            b = next[b];
        }
        return values;
    }

    public int getNext(final int current) {
        return next[current];
    }

    public int getHead() {
        return head;
    }

    public int getTail() {
        return tail;
    }

    public int getAbsent(final int current) {
        return absent[current];
    }

    public Object getValue(final int valueIndex) {
        return values[valueIndex];
    }

    public boolean isValueInDom(final int valueIndex) {
        return getValues().contains(valueIndex);
    }

    @Override
    public String toString() {
        List<Object> vals = new ArrayList<>();
        for (var valueIndex : getValues()) {
            vals.add(values[valueIndex]);
        }
        return "Domain{" + vals.toString() + "}";
    }

    public void printInfo() {
        System.out.println("Absent: " + Arrays.toString(absent));
        System.out.println("prevAbsent: " + Arrays.toString(prevAbsent));
        System.out.println("next: " + Arrays.toString(next));
        System.out.println("head: " + head);
        System.out.println("tailAbsent: " + tailAbsent);
        System.out.println("tail: " + tail);
        System.out.println("prev: " + Arrays.toString(prev));
        System.out.println("Size: " + size);
    }

    public Domain copy() {
        Domain newDomain = new Domain(values);
        newDomain.absent = absent.clone();
        newDomain.next = next.clone();
        newDomain.prev = prev.clone();
        newDomain.prevAbsent = prevAbsent.clone();
        newDomain.head = head;
        newDomain.tail = tail;
        newDomain.tailAbsent = tailAbsent;
        newDomain.size = size;
        return newDomain;
    }
}
