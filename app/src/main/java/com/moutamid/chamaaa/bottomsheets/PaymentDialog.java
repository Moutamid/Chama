package com.moutamid.chamaaa.bottomsheets;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fxn.stash.Stash;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.moutamid.chamaaa.databinding.PaymentDialogBinding;
import com.moutamid.chamaaa.listener.BottomSheetDismissListener;
import com.moutamid.chamaaa.models.ChatModel;
import com.moutamid.chamaaa.models.ExpenseModel;
import com.moutamid.chamaaa.models.MessageModel;
import com.moutamid.chamaaa.models.ProductModel;
import com.moutamid.chamaaa.models.SaleModel;
import com.moutamid.chamaaa.models.StockModel;
import com.moutamid.chamaaa.models.TimelineModel;
import com.moutamid.chamaaa.models.UserModel;
import com.moutamid.chamaaa.notifications.FCMNotificationHelper;
import com.moutamid.chamaaa.utilis.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PaymentDialog extends BottomSheetDialogFragment {
    PaymentDialogBinding binding;
    ProductModel productModel;
    int quantity;
    ChatModel chatModel;
    private BottomSheetDismissListener listener;

    public PaymentDialog(ProductModel productModel, int quantity, ChatModel chatModel) {
        this.productModel = productModel;
        this.chatModel = chatModel;
        this.quantity = quantity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PaymentDialogBinding.inflate(getLayoutInflater(), container, false);

        binding.productName.setText(productModel.name);
        binding.price.setText(String.format("$%s", productModel.unit_price));
        binding.quanity.setText(String.format("x%s", quantity));
        double total = quantity * productModel.unit_price;
        binding.total.setText(String.format("$%.2f", total));

        binding.credit.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int vis = isChecked ? View.VISIBLE : View.GONE;
            binding.personName.setVisibility(vis);
        });

        binding.sell.setOnClickListener(v -> {
            if (valid()) {
                Constants.showDialog();
                updateExpense(total);
            }
        });

        return binding.getRoot();
    }

    private void updateExpense(double total) {
        ExpenseModel model = new ExpenseModel();
        model.timestamp = new Date().getTime();
        model.isExpense = false;
        String type = binding.credit.isChecked() ? "By Credit" : "By Cash";
        model.name = productModel.name + " is sell " + type + " in " + chatModel.name;
        model.price = total;
        Constants.databaseReference().child(Constants.EXPENSES).child(chatModel.adminID)
                .push().setValue(model).addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }).addOnSuccessListener(unused -> {
                    updateMessageAndTimeline(total);
                });
    }

    private void update(Map<String, Object> map, double total) {
        UserModel stashUser = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);
        Constants.databaseReference().child(Constants.CHATS).child(stashUser.id).child(chatModel.id).updateChildren(map)
                .addOnSuccessListener(unused1 -> {
                    if (chatModel.isGroup) {
                        for (UserModel userModel : chatModel.groupMembers) {
                            Constants.databaseReference().child(Constants.CHATS).child(userModel.id).child(chatModel.id).updateChildren(map)
                                    .addOnSuccessListener(unused2 -> {

                                    });
                        }
                        updateTimeLine(total);
                        ArrayList<String> ids = new ArrayList<>();
                        for (UserModel userModel : chatModel.groupMembers) {
                            if (!userModel.id.equals(Constants.auth().getCurrentUser().getUid()))
                                ids.add(userModel.id);
                        }
                        String[] id = ids.toArray(new String[0]);
                        new FCMNotificationHelper(requireContext(), chatModel.id, false).sendNotification(id, chatModel.name, map.get("lastMessage").toString());
                    } else {
                        Constants.databaseReference().child(Constants.CHATS).child(chatModel.userID).child(chatModel.id).updateChildren(map)
                                .addOnSuccessListener(unused2 -> {
                                    Constants.databaseReference().child(Constants.STATUS).child(Constants.auth().getCurrentUser().getUid()).setValue("online");
                                    String[] id = new String[]{chatModel.userID};
                                    new FCMNotificationHelper(requireContext(), chatModel.id, false).sendNotification(id, chatModel.name, map.get("lastMessage").toString());
                                    updateTimeLine(total);
                                });
                    }
                });
    }

    private void updateMessageAndTimeline(double total) {
        UserModel stashUser = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);
        MessageModel model = new MessageModel();
        model.id = UUID.randomUUID().toString();
        model.senderID = Constants.auth().getCurrentUser().getUid();
        model.chatID = chatModel.id;
        model.isGroup = chatModel.isGroup;
        model.isMoneyShared = false;
        model.isImageShared = false;
        model.money = "";
        model.timestamp = new Date().getTime();
        model.image = stashUser.image;
        String type = binding.credit.isChecked() ? "By Credit" : "By Cash";
        model.message = "A total of $" + String.format("%.2f", total) + " " + productModel.name + " is sell " + type;

        String m = chatModel.isGroup ? stashUser.name + ": " + model.message : model.message;

        Constants.databaseReference().child(Constants.MESSAGES).child(chatModel.id).child(model.id)
                .setValue(model).addOnSuccessListener(unused -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("timestamp", model.timestamp);
                    map.put("lastMessage", m);
                    map.put("isMoneyShared", false);
                    update(map, total);
                });
    }

    private void updateTimeLine(double total) {
        TimelineModel timelineModel = new TimelineModel();
        timelineModel.isChat = true;
        timelineModel.id = UUID.randomUUID().toString();
        timelineModel.chatID = chatModel.id;
        timelineModel.timeline = new Date().getTime();
        timelineModel.name = chatModel.name;
        String type = binding.credit.isChecked() ? "By Credit" : "By Cash";
        timelineModel.desc = "A total of $" + String.format("%.2f", total) + " " + productModel.name + " is sell " + type + " in " + chatModel.name;
        ArrayList<UserModel> groupMembers = chatModel.groupMembers;
        for (int i = 0; i < groupMembers.size(); i++) {
            UserModel userModel = groupMembers.get(i);
            int finalI = i;
            Constants.databaseReference().child(Constants.TIMELINE).child(userModel.id)
                    .child(timelineModel.id).setValue(timelineModel).addOnSuccessListener(unused -> {
                        if (finalI == groupMembers.size() - 1) {
                            productModel.available_stock = productModel.available_stock - quantity;
                            Constants.databaseReference().child(Constants.PRODUCTS).child(chatModel.id).child(productModel.id).setValue(productModel)
                                    .addOnSuccessListener(unused1 -> {
                                        updateStock();
                                    }).addOnFailureListener(e -> {
                                        Constants.dismissDialog();
                                        Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }).addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateStock() {
        StockModel model = new StockModel();
        model.id = UUID.randomUUID().toString();
        model.product_id = productModel.id;
        model.name = productModel.name.trim();
        model.measuring_unit = productModel.measuring_unit.trim();
        model.buying_price = productModel.buying_price;
        model.unit_price = productModel.unit_price;
        model.date = new Date().getTime();
        model.unit = productModel.unit;
        model.quantity = quantity;

        Constants.databaseReference().child(Constants.STOCK_OUT).child(model.name).child(model.id)
                .setValue(model).addOnSuccessListener(unused -> {
                    updateSale();
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateSale() {
        SaleModel saleModel = new SaleModel();
        saleModel.id = UUID.randomUUID().toString();
        saleModel.product_id = productModel.id;
        saleModel.unit_price = productModel.unit_price;
        saleModel.date = new Date().getTime();
        saleModel.quantity = quantity;
        saleModel.name = productModel.name.trim();
        saleModel.personName = binding.personName.getEditText().getText().toString();

        String type = binding.credit.isChecked() ? Constants.CREDIT_SALE : Constants.CASH_SALE;
        Constants.databaseReference().child(type).child(chatModel.id).child(saleModel.name).child(saleModel.id)
                .setValue(saleModel).addOnSuccessListener(unused -> {
                    Constants.dismissDialog();
                    dismiss();
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private boolean valid() {
        if (binding.credit.isChecked()) {
            if (binding.personName.getEditText().getText().toString().isEmpty()) {
                Toast.makeText(requireContext(), "Person Name is required", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (listener != null) {
            listener.onBottomSheetDismissed();
        }
    }

    public void setListener(BottomSheetDismissListener listener) {
        this.listener = listener;
    }

}
