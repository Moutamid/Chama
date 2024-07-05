package com.moutamid.chama.models;

import java.util.ArrayList;

public class ChatModel {
    public String id, userID, name, image, lastMessage, money, whoShared;
    public boolean isMoneyShared, isGroup;
    public long timestamp;
    public String adminID;
    public boolean isSocoGroup;
    public ArrayList<UserModel> groupMembers;

    public ChatModel() {
    }

}
