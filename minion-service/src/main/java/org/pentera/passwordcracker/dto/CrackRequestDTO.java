package org.pentera.passwordcracker.dto;

public class CrackRequestDTO {
    private String hash;
    private long startRange;
    private long endRange;

    public CrackRequestDTO() {
        this.hash = null;
        this.startRange = Long.MIN_VALUE;
        this.endRange = Long.MIN_VALUE;
    }

    public CrackRequestDTO(String hash, long startRange, long endRange) {
        this.hash = hash;
        this.startRange = startRange;
        this.endRange = endRange;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getStartRange() {
        return startRange;
    }

    public void setStartRange(long startRange) {
        this.startRange = startRange;
    }

    public long getEndRange() {
        return endRange;
    }

    public void setEndRange(long endRange) {
        this.endRange = endRange;
    }
}
