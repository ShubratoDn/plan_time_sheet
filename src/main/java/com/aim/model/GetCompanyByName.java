package com.aim.model;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aim.config.TenantContext;
import com.aim.entity.Company;
import com.aim.repository.CompanyRepository;

public class GetCompanyByName implements Callable<Company> {

	private static final Logger LOGGER = LoggerFactory.getLogger(GetCompanyByName.class);
	
	private String name;
	
	private CompanyRepository companyRepository;
	
	public GetCompanyByName(CompanyRepository companyRepository,String name) {
		this.companyRepository = companyRepository;
		this.name = name;
	}
	
	public Company call() throws Exception{
		
		TenantContext.setCurrentTenant(null);
		Company company = companyRepository.findByName(this.name);
		TenantContext.clear();
		return company;
	}
}
