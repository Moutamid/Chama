package com.moutamid.chama.models;

import java.util.ArrayList;

public class ChatModel {
    public String id, userID, name, image, lastMessage, money;
    public boolean isMoneyShared, isGroup;
    public long timestamp;
    public ArrayList<UserModel> groupMembers;
    public ChatModel() {
    }

}
