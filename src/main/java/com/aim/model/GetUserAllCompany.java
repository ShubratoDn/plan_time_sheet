package com.aim.model;

import java.util.List;
import java.util.concurrent.Callable;

import com.aim.config.TenantContext;
import com.aim.repository.UserCompanyRepository;

public class GetUserAllCompany implements Callable<List<String>> {
 
	private String email;
    private UserCompanyRepository userCompanyRepository;
 
    public GetUserAllCompany(UserCompanyRepository userCompanyRepository, String email) {
    	this.email = email;
        this.userCompanyRepository = userCompanyRepository;
    }
 
    public List<String> call() throws Exception {
    	TenantContext.setCurrentTenant(null);
    	List<String> dbCurrent = userCompanyRepository.getDbNameByUser(email);
    	TenantContext.clear();
        return dbCurrent;
    }
}
