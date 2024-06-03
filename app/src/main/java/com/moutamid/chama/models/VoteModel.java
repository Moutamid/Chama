package com.moutamid.chama.models;

public class VoteModel {
    public ChatModel chatModel;
    public MessageModel model;

    public VoteModel(ChatModel chatModel, MessageModel model) {
        this.chatModel = chatModel;
        this.model = model;
    }

    public VoteModel() {
    }
}
