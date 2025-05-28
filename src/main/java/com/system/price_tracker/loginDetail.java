package com.system.price_tracker;

public class loginDetail {
    private static final loginDetail instance = new loginDetail();
    private String username;
    private String email;

    private String password;
    private boolean check;


    private loginDetail(){}

    public static loginDetail getInstance(){
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

public void setLogin(boolean check){
        this.check = check;
}
public boolean getLogin(){
        return check;
}
    public void clearAllValues() {
        username = null;
        email = null;
        password = null;
        check = false;
    }
}
