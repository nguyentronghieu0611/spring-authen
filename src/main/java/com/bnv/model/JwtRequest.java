package com.bnv.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class JwtRequest implements Serializable {

	private static final long serialVersionUID = 5926468583005150707L;

	@NotNull(message = "Vui lòng nhập username")
	@NotEmpty(message = "Username không được trống")
	private String username;
	@NotNull(message = "Vui lòng nhập mật khẩu")
	@NotEmpty(message = "Mật khẩu không được trống")
	private String password;
	
	public JwtRequest()
	{

	}
	public JwtRequest(String username, String password) {
		this.setUsername(username);
		this.setPassword(password);
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}