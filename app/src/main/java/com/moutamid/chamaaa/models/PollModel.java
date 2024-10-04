package com.moutamid.chamaaa.models;

import java.util.List;

public class PollModel {
    public String question;
    public List<String> options;
    public String senderId;
    public Votes votes;

    public PollModel() {
    }
}
