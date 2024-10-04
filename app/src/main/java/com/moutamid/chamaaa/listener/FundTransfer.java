package com.moutamid.chamaaa.listener;

import com.moutamid.chamaaa.models.MessageModel;

public interface FundTransfer {
    void onWithdraw(MessageModel model);
    void onReceipt(MessageModel model);
}
