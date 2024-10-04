package com.moutamid.chamaaa.models;

import java.util.ArrayList;

public class Sell {
    public String name;
    public ArrayList<SaleModel> list;
    public Sell(String name, ArrayList<SaleModel> list) {
        this.name = name;
        this.list = list;
    }
}

