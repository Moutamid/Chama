package com.moutamid.chama.models;

import java.util.ArrayList;

public class StockModel {
    public String name;
    public ArrayList<String> data;

    public StockModel() {
    }

    public StockModel(String name, ArrayList<String> data) {
        this.name = name;
        this.data = data;
    }
}
