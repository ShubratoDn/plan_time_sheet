package com.aim.model;

import com.aim.config.TenantContext;
import com.aim.entity.User;
import com.aim.repository.UserRepository;

public class UserEditActiveInSubDb extends Thread {

	private User user;
	
	private UserRepository userRepository;
	
	private String dbName;
	
	private Integer activeAction;
	
	public UserEditActiveInSubDb(User user,Integer activeAction, String dbName, UserRepository userRepository){
		this.user = user;
		this.userRepository = userRepository;
		this.dbName = dbName;
		this.activeAction = activeAction;
	}
	
	public void run() {
		
		TenantContext.setCurrentTenant(dbName);
		
		//Before save when edit, need to take actual id from master db then update record
		User user = userRepository.findByEmail(this.user.getEmail());
		user.setActive(activeAction);
		userRepository.save(user);
		
		TenantContext.clear();
	}
}
