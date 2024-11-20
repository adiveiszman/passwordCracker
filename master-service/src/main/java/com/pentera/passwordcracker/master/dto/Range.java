package com.pentera.passwordcracker.master.dto;

public class Range {
    private final long start;
    private final long end;

    public Range(long start, long end) {
        this.start = start;
        this.end = end;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return "Range{" + "start=" + start + ", end=" + end + '}';
    }
}
