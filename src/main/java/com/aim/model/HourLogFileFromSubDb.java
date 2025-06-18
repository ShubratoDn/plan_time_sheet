package com.aim.model;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aim.config.TenantContext;
import com.aim.entity.HourLogFile;
import com.aim.repository.HourLogFileRepository;

public class HourLogFileFromSubDb implements Callable<HourLogFile> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GetCompanyByName.class);
	
	private Integer id;
	
	private HourLogFileRepository hourLogFileRepository;
	
	private String db;
	
	public HourLogFileFromSubDb(HourLogFileRepository hourLogFileRepository,Integer id, String db) {
		this.hourLogFileRepository = hourLogFileRepository;
		this.id = id;
		this.db = db;
	}
	
	public HourLogFile call() throws Exception{
		
		TenantContext.setCurrentTenant(db);
		HourLogFile company = hourLogFileRepository.findById(id);
		TenantContext.clear();
		return company;
	}
}
