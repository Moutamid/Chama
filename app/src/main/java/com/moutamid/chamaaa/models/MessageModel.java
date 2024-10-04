package com.moutamid.chamaaa.models;

public class MessageModel {
    public String id, senderID, receiverID, chatID, message, image, money, caption, receiverName;
    public boolean isMoneyShared, isGroup, isImageShared, isPoll;
    public long timestamp;
    public PollModel pollModel;

    public MessageModel() {
    }
}
