package com.aim.model;

import java.util.concurrent.Callable;

import com.aim.config.TenantContext;
import com.aim.entity.User;
import com.aim.repository.UserRepository;

public class GetUserThread implements Callable<User>{

	private String email;
	private String dbName;
    private UserRepository userRepository;
 
    public GetUserThread(UserRepository userCompanyRepository, String email,String dbName) {
    	this.email = email;
    	this.dbName = dbName;
        this.userRepository = userCompanyRepository;
    }
 
    public User call() throws Exception {
    	TenantContext.setCurrentTenant(this.dbName);
    	User dbCurrent = userRepository.findByEmail(email);
    	TenantContext.clear();
        return dbCurrent;
    }
    
}
