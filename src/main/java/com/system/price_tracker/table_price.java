package com.system.price_tracker;

public class table_price {
    private int id;
    private String date;
    private int premise_code;
    private int item_code;
    private double price;
    public table_price(int id, String date, int premise_code, int item_code, double price) {
        this.id = id;
        this.date = date;
        this.premise_code = premise_code;
        this.item_code = item_code;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public int getPremise_code() {
        return premise_code;
    }

    public int getItem_code() {
        return item_code;
    }

    public double getPrice() {
        return price;
    }
}
