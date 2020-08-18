package com.bnv.model;


public class LoginResponse {

	private String access_token;

	private int err_code;

	private String expired_time;

	// Constructor, getters and setters omitted

	public String getExpired_time() {
		return expired_time;
	}

	public void setExpired_time(String expired_time) {
		this.expired_time = expired_time;
	}

	public LoginResponse(String access_token, int err_code, String expired_time) {
		this.access_token=access_token;
		this.err_code=err_code;
		this.expired_time = expired_time;
	}

	public String getToken() {
		return access_token;
	}

	public void setToken(String access_token) {
		this.access_token = access_token;
	}

	public int getErr_code() {
		return err_code;
	}

	public void setErr_code(int err_code) {
		this.err_code = err_code;
	}


}