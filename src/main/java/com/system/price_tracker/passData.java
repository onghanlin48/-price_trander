package com.system.price_tracker;

public class passData {
    private static final passData instance = new passData();
    private String username;
    private String email;

    private String password;

    private String code;

    private int resend;

    private passData(){}

    public static passData getInstance(){
        return instance;
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getCode(){
        return code;
    }

    public void setCode(String code){
        this.code = code;
    }

    public int getResend(){
        return resend;
    }

    public void setResend(int resend){
        this.resend = resend;
    }

    public void clearAllValues() {
        username = null;
        email = null;
        password = null;
        code = null;
        resend = 0;
    }
}
