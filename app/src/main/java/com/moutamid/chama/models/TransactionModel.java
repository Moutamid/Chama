package com.moutamid.chama.models;

public class TransactionModel {
    public String id, userID;
    public double amount;
    public long timestamp;
    public String type; // Normal, Locked, Send, Withdraw

    public TransactionModel() {
    }
}
