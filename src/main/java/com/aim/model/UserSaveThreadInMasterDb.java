package com.aim.model;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aim.config.TenantContext;
import com.aim.entity.Company;
import com.aim.entity.User;
import com.aim.entity.UserCompany;
import com.aim.repository.CompanyRepository;
import com.aim.repository.UserCompanyRepository;
import com.aim.repository.UserRepository;

public class UserSaveThreadInMasterDb implements Callable<UserCompany> {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserSaveThreadInMasterDb.class);
	
	private User user;
	
	private UserRepository userRepository;
	
	private CompanyRepository companyRepository;
	
	private UserCompanyRepository userCompanyRepository;
	
	public UserSaveThreadInMasterDb(UserRepository userRepository,CompanyRepository companyRepository,UserCompanyRepository userCompanyRepository, User user) {
		this.user = user;
		this.userRepository = userRepository;
		this.companyRepository = companyRepository;
		this.userCompanyRepository = userCompanyRepository;
	}
	
	public UserCompany call() throws Exception{
		
		TenantContext.setCurrentTenant(null);
		User user = userRepository.findByEmail(this.user.getEmail());
		Company company = companyRepository.findByUrlSlug(this.user.getCompany().getUrlSlug());
		UserCompany userCompany = new UserCompany();
		//Before save when edit, need to take actual id from master db then update record
		
		if(user != null) {
			this.user.setId(user.getId());
			userCompany = userCompanyRepository.findByCompanyAndUser(company, user);
			if(userCompany == null) {
				userCompany = new UserCompany();
				userCompany.setUser(this.user);
				userCompany.setCompany(company);
			}
			userCompany.setActive(this.user.getActive()==1?true:false);
			userCompany.setRole(this.user.getRole());
			userCompanyRepository.save(userCompany);
			
		} else {
		
			this.user.setId(0);
			this.user.setCompany(null);
			userRepository.save(this.user);
			
			userCompany.setActive(this.user.getActive()==1?true:false);
			userCompany.setRole(this.user.getRole());
			userCompany.setUser(this.user);
			userCompany.setCompany(company);
			userCompanyRepository.save(userCompany);
			
		}
		
		TenantContext.clear();
		return userCompany;
	}
}
