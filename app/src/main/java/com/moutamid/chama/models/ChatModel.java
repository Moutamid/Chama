package com.moutamid.chama.models;

public class ChatModel {
    public String id, name, image, lastMessage, money;
    public boolean isMoneyShared, chatType;
    public long timestamp;

    public ChatModel() {
    }

    public ChatModel(String id, String name, String image, String lastMessage, String money, boolean isMoneyShared, boolean chatType, long timestamp) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.lastMessage = lastMessage;
        this.money = money;
        this.isMoneyShared = isMoneyShared;
        this.chatType = chatType;
        this.timestamp = timestamp;
    }
}
