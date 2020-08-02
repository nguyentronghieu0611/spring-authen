package com.javainuse.model;

import java.util.Date;

public class LoginResponse {

    private String token;

    private int err_code;
    
    private Date expired_time;

    // Constructor, getters and setters omitted
    
    public Date getExpired_time() {
		return expired_time;
	}

	public void setExpired_time(Date expired_time) {
		this.expired_time = expired_time;
	}

	public LoginResponse(String token, int err_code, Date expired_time) {
    	this.token=token;
    	this.err_code=err_code;
    	this.expired_time = expired_time;
    }

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getErr_code() {
		return err_code;
	}

	public void setErr_code(int err_code) {
		this.err_code = err_code;
	}
    
    
}