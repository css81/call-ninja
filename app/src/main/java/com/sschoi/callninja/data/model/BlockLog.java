package com.sschoi.callninja.data.model;

/**
 * 차단 로그 모델
 */
public class BlockLog {

    private String phoneNumber;
    private String blockTime; // "YYYY-MM-DD HH:mm:ss" 형식

    public BlockLog(String phoneNumber, String blockTime) {
        this.phoneNumber = phoneNumber;
        this.blockTime = blockTime;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getBlockTime() {
        return blockTime;
    }
}
