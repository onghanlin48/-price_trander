package com.system.price_tracker;

public class table_premise {
    private int code;
    private String name;
    private String address;
    private String type;
    private String state;
    private String district;

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getType() {
        return type;
    }

    public String getState() {
        return state;
    }

    public String getDistrict() {
        return district;
    }

    public table_premise(int code, String name, String address, String type, String state, String district) {
        this.code = code;
        this.name = name;
        this.address = address;
        this.type = type;
        this.state = state;
        this.district = district;
    }
}
