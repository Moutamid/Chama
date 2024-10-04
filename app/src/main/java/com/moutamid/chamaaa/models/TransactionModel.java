package com.moutamid.chamaaa.models;

public class TransactionModel {
    public String id;
    public double amount;
    public long timestamp;
    public String type; // Normal, Locked, Send, Withdraw

    public TransactionModel() {
    }
}
