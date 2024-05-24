package com.moutamid.chama.listener;

import com.moutamid.chama.models.UserModel;

public interface GroupCreateListener {
    void selected(UserModel userModel);
    void unselected(UserModel userModel);
    void  createChat(UserModel userModel);
}
