package org.pentera.passwordcracker.dto;

public class CrackRequestDTO {

    private String hash;
    private String startRange;
    private String endRange;

    public void CrackRequestDTO() {}

    public void CrackRequestDTO(String hash, String startRange, String endRange) {
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

    public String getStartRange() {
        return startRange;
    }

    public void setStartRange(String startRange) {
        this.startRange = startRange;
    }

    public String getEndRange() {
        return endRange;
    }

    public void setEndRange(String endRange) {
        this.endRange = endRange;
    }
}
