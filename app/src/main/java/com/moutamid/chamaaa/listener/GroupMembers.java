package com.moutamid.chamaaa.listener;

import com.moutamid.chamaaa.models.UserModel;

public interface GroupMembers {
    void onRemove(UserModel userModel, int pos);
    void assignRule(UserModel userModel, int pos);
}
