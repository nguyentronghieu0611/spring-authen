package com.bnv.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bnv.model.DAOUser;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserDao extends CrudRepository<DAOUser, Integer> {
	
	DAOUser findByUsername(String username);

	@Transactional
	@Modifying
	@Query(value="update temp_adm_user t set t.user_pwd=:password,t.USER_PWD_ENCODED=:pass_encoded where t.user_name=:username",nativeQuery = true)
	void updatePasswordUser(@Param("password") String password,@Param("pass_encoded") String pass_encoded,@Param("username") String username);
}