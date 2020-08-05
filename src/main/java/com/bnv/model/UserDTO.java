package com.javainuse.model;

public class UserDTO {
	private String username;
	private String password;
	private String password_encoded;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword_encoded() {
		return password_encoded;
	}

	public void setPassword_encoded(String password_encoded) {
		this.password_encoded = password_encoded;
	}
	
}