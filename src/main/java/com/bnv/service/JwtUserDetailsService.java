package com.bnv.service;

import java.util.ArrayList;

import com.bnv.model.ChangepassModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bnv.dao.UserDao;
import com.bnv.model.DAOUser;
import com.bnv.model.UserDTO;

@Service
public class JwtUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UserDao userDao;

	@Autowired
	private PasswordEncoder bcryptEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		DAOUser user = userDao.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("user không tồn tại");
		}
		return new org.springframework.security.core.userdetails.User(user.getUser_name(), user.getUser_pwd_encoded(),
				new ArrayList<>());
	}
	
	public DAOUser save(UserDTO user) {
		DAOUser newUser = new DAOUser();
		newUser.setUser_name(user.getUsername());
		newUser.setUser_pwd(user.getPassword());
		newUser.setUser_pwd_encoded(bcryptEncoder.encode(user.getPassword()));
		return userDao.save(newUser);
	}

	public void updateUser(ChangepassModel model){
		userDao.updatePasswordUser(model.getNew_password(),bcryptEncoder.encode(model.getNew_password()),model.getUsername());
	}
}