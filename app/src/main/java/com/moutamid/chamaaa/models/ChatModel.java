package com.moutamid.chamaaa.models;

import java.util.ArrayList;

public class ChatModel {
    public String id, userID, name, image, lastMessage, money, whoShared;
    public boolean isMoneyShared, isGroup;
    public long timestamp;
    public String adminID;
    public boolean isSoccoGroup, isBusinessGroup;
    public ArrayList<UserModel> groupMembers;

    public ChatModel() {
    }

}
