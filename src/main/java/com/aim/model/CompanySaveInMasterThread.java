package com.aim.model;

import com.aim.config.TenantContext;
import com.aim.entity.Company;
import com.aim.repository.CompanyRepository;

public class CompanySaveInMasterThread  extends Thread{
	
	private Company company;
	
	private CompanyRepository companyRepository ;
	
	public CompanySaveInMasterThread(CompanyRepository companyRepository,Company company) {
		this.company = company;
		this.companyRepository =companyRepository;
	}
	
	public void run() {
		
		TenantContext.setCurrentTenant(null);
		
		//Before save when edit, need to take actual id from master db then update record
		Company company = companyRepository.findByUrlSlug(this.company.getUrlSlug());
		company.setAddress(this.company.getAddress());
		company.setDetails(this.company.getDetails());
		company.setName(this.company.getName());
		company.setTimesheetSubmitEmail(this.company.getTimesheetSubmitEmail());
		company.setFileFolder(this.company.getFileFolder());
		company.setImagePath(this.company.getImagePath());
		if(company != null) {
			this.company.setId(company.getId());
		}
			
		companyRepository.save(company);
		TenantContext.clear();
	}
}
