package com.system.price_tracker;

public class table_item {
    private int code;
    private String item;
    private String group;
    private String unit;
    private String cate;

    public int getCode() {
        return code;
    }

    public String getItem() {
        return item;
    }

    public String getGroup() {
        return group;
    }

    public String getUnit() {
        return unit;
    }

    public String getCate() {
        return cate;
    }

    public table_item(int code, String item, String group, String unit, String cate) {
        this.code = code;
        this.item = item;
        this.group = group;
        this.unit = unit;
        this.cate = cate;
    }
}
