package com.sschoi.callninja.data.model;

/**
 * Data model representing a blocked call log entry.
 */
public class BlockLog {

    private int id;
    private String phoneNumber;
    private String blockTime;

    public BlockLog() {
    }

    public BlockLog(int id, String phoneNumber, String blockTime) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.blockTime = blockTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBlockTime() {
        return blockTime;
    }

    public void setBlockTime(String blockTime) {
        this.blockTime = blockTime;
    }
}
