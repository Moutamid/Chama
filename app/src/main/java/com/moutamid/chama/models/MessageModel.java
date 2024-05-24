package com.moutamid.chama.models;

public class MessageModel {
    public String id, senderID, chatID, message, image, money;
    public boolean isMoneyShared, isGroup;
    public long timestamp;

    public MessageModel() {
    }
}
