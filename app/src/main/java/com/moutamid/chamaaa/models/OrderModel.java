package com.moutamid.chamaaa.models;

public class OrderModel {
    public String id;
    public String fullName, address, country, state, city, zip, phoneNumber;
    public int quantity;
    public ProductModel productModel;

    public OrderModel() {
    }
}
