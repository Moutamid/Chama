package com.moutamid.chama.models;

import java.util.ArrayList;

public class MessageModel {
    public String id, senderID, receiverID, chatID, message, image, money, caption, receiverName;
    public boolean isMoneyShared, isGroup, isImageShared, isPoll;
    public long timestamp;
    public PollModel pollModel;

    public MessageModel() {
    }
}
