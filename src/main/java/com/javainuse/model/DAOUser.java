package com.javainuse.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
//cấu hình tên bảng
@Table(name = "user_new")
public class DAOUser {

	//Khai báo trường id tự động tăng
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "ID")
	private long id;
	@Column
	private String username;
	@Column
	@JsonIgnore
	private String password;
	@Column
	@JsonIgnore
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