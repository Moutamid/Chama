package com.moutamid.chama.listener;

import com.moutamid.chama.models.UserModel;

public interface GroupMembers {
    void onRemove(UserModel userModel, int pos);
}
