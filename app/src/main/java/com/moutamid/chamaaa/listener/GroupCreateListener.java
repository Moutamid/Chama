package com.moutamid.chamaaa.listener;

import com.moutamid.chamaaa.models.UserModel;

public interface GroupCreateListener {
    void selected(UserModel userModel);
    void unselected(UserModel userModel);
    void  createChat(UserModel userModel);
}
