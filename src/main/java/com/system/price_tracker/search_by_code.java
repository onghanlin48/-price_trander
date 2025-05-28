package com.system.price_tracker;

public class search_by_code {
    private int code;
    private static final search_by_code instance = new search_by_code();

    public static search_by_code getInstance(){
        return instance;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
