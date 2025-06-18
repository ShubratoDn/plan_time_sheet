package com.aim.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aim.config.TenantContext;
import com.aim.entity.Company;
import com.aim.repository.CompanyRepository;

public class CompanySaveThread extends Thread{

	private static final Logger LOGGER = LoggerFactory.getLogger(CompanySaveThread.class);
	
	private Company company;
	
	private String dbName;
	
	private CompanyRepository companyRepository ;;
	
	public CompanySaveThread(CompanyRepository companyRepository,Company company, String masterDbName) {
		this.company = company;
		this.dbName = masterDbName;
		this.companyRepository =companyRepository;
	}
	
	public void run() {
		
		TenantContext.setCurrentTenant(dbName);
		
		//Before save when edit, need to take actual id from master db then update record
		Company company = companyRepository.findByUrlSlug(this.company.getUrlSlug());
		if(company != null) {
			this.company.setId(company.getId());
		}
			
		companyRepository.save(this.company);
		TenantContext.clear();
	}
}
