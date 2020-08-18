package com.bnv.model;

public class ChangepassModel {
    private String username;
    private String current_password;
    private String new_password;
    private String retype_password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ChangepassModel(String username, String current_password, String new_password, String retype_password) {
        this.username = username;
        this.current_password = current_password;
        this.new_password = new_password;
        this.retype_password = retype_password;
    }

    public String getCurrent_password() {
        return current_password;
    }

    public void setCurrent_password(String current_password) {
        this.current_password = current_password;
    }

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }

    public String getRetype_password() {
        return retype_password;
    }

    public void setRetype_password(String retype_password) {
        this.retype_password = retype_password;
    }
}
