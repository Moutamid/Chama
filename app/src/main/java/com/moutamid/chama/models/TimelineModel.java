package com.moutamid.chama.models;

public class TimelineModel {
    public String id, name, desc;
    public long timeline;

    public TimelineModel(String id, String name, String desc, long timeline) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.timeline = timeline;
    }
}
