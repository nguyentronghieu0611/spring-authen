package com.bnv.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class UserDTO {

	@NotNull(message = "Vui lòng nhập username")
	@NotEmpty(message = "Username không được trống")
	private String username;

	@NotNull(message = "Vui lòng nhập mật khẩu")
	@NotEmpty(message = "Mật khẩu không được trống")
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