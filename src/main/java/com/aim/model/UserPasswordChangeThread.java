package com.aim.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aim.config.TenantContext;
import com.aim.entity.User;
import com.aim.repository.UserRepository;

public class UserPasswordChangeThread extends Thread {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserPasswordChangeThread.class);
	
	private String email;
	
	private String dbName;
	
	private String password;
	
	private UserRepository userRepository;

	public UserPasswordChangeThread(String email, String password, String dbName, UserRepository userRepository){
		this.email = email;
		this.password = password;
		this.dbName = dbName;
		this.userRepository = userRepository;
		
	}
	
	public void run() {
		
		TenantContext.setCurrentTenant(dbName);
		
		//Before save when edit, need to take actual id from master db then update record
		User user = userRepository.findByEmail(email);
		user.setPassword(password);
		userRepository.save(user);
		
		TenantContext.clear();
	}
}
