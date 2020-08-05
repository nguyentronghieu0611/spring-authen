package com.javainuse.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
//cấu hình tên bảng
@Table(name = "TEMP_ADM_USER")
public class DAOUser {

	//Khai báo trường id tự động tăng
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "ID")
	private long id;
	
	@Column(name = "USER_NAME")
	private String username;
	
	@Column(name = "USER_PWD")
	@JsonIgnore
	private String user_pwd;
	
	@Column(name = "USER_PWD_ENCODED")
	@JsonIgnore
	private String user_pwd_encoded;

	public String getUser_name() {
		return username;
	}

	public void setUser_name(String username) {
		this.username = username;
	}

	public String getUser_pwd() {
		return user_pwd;
	}

	public void setUser_pwd(String user_pwd) {
		this.user_pwd = user_pwd;
	}

	public String getUser_pwd_encoded() {
		return user_pwd_encoded;
	}

	public void setUser_pwd_encoded(String user_pwd_encoded) {
		this.user_pwd_encoded = user_pwd_encoded;
	}

}