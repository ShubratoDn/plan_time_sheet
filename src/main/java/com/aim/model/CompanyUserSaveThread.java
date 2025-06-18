package com.aim.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aim.config.TenantContext;
import com.aim.entity.Company;
import com.aim.entity.User;
import com.aim.entity.UserCompany;
import com.aim.repository.CompanyRepository;
import com.aim.repository.UserRepository;

public class CompanyUserSaveThread extends Thread {

private static final Logger LOGGER = LoggerFactory.getLogger(CompanyUserSaveThread.class);
	
	private User user;
	
	private Company company;
	
	private String dbName;
	
	private UserCompany userCompany;
	
	private UserRepository userRepository;
	
	private CompanyRepository companyRepository;
	
	public CompanyUserSaveThread(UserRepository userRepository, CompanyRepository companyRepository,
			User user,Company company,UserCompany userCompany, String masterDbName) {
		this.user = user;
		this.company = company;
		this.userCompany = userCompany;
		this.dbName = masterDbName;
		this.userRepository = userRepository;
		this.companyRepository = companyRepository;
	}
	
	public void run() {
		
		TenantContext.setCurrentTenant(dbName);
		this.company.setId(null);
		companyRepository.save(this.company);
		this.user.setId(0);
		this.user.setRole(this.userCompany.getRole());
		this.user.setCompany(this.company);
		userRepository.save(this.user);
		TenantContext.clear();
	}
}
