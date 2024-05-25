package com.moutamid.chama.models;

import java.util.ArrayList;

public class MessageModel {
    public String id, senderID, chatID, message, image, money, caption;
    public boolean isMoneyShared, isGroup, isImageShared, isPoll;
    public long timestamp;
    public PollModel pollModel;

    public MessageModel() {
    }
}
