package com.bnv.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class ChangepassModel {

    @NotNull(message = "Vui lòng nhập username")
    @NotEmpty(message = "Username không được trống")
    private String username;

    @NotNull(message = "Vui lòng nhập mật khẩu hiện tại")
    @NotEmpty(message = "Mật khẩu hiện tại không được trống")
    private String current_password;

    @NotNull(message = "Vui lòng nhập mật khẩu mới")
    @NotEmpty(message = "Mật khẩu mới không được trống")
    private String new_password;

    @NotNull(message = "Vui lòng nhập lại mật khẩu mới")
    @NotEmpty(message = "Mật khẩu nhập lại không được trống")
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
