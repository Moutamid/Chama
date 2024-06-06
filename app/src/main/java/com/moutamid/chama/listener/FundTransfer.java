package com.moutamid.chama.listener;

import com.moutamid.chama.models.MessageModel;

public interface FundTransfer {
    void onWithdraw(MessageModel model);
    void onReceipt(MessageModel model);
}
