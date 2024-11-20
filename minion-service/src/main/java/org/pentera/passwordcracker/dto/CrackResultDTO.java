package org.pentera.passwordcracker.dto;

public class CrackResultDTO {
    private String hash;
    private String crackedPassword;
    private Status status;

    public enum Status {
        CRACKED, NOT_IN_RANGE, FAILED
    }

    public CrackResultDTO() {
        this.hash = null;
        this.crackedPassword = null;
        this.status = null;
    }

    public CrackResultDTO(String hash, String crackedPassword, Status status) {
        this.hash = hash;
        this.crackedPassword = crackedPassword;
        this.status = status;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getCrackedPassword() {
        return crackedPassword;
    }

    public void setCrackedPassword(String crackedPassword) {
        this.crackedPassword = crackedPassword;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
