package com.aim.model;

import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aim.config.TenantContext;
import com.aim.entity.HourLogFile;
import com.aim.entity.HourLogFilePath;
import com.aim.repository.HourLogFilePathRepository;

public class HourLogFilePathFromSubDb  implements Callable<List<HourLogFilePath>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(GetCompanyByName.class);
	
	private HourLogFile hourLogFile;
	
	private HourLogFilePathRepository hourLogFilePathRepository;
	
	private String db;
	
	public HourLogFilePathFromSubDb(HourLogFilePathRepository hourLogFilePathRepository,HourLogFile hourLogFile, String db) {
		this.hourLogFilePathRepository = hourLogFilePathRepository;
		this.hourLogFile = hourLogFile;
		this.db = db;
	}
	
	public List<HourLogFilePath> call() throws Exception{
		
		TenantContext.setCurrentTenant(db);
		List<HourLogFilePath> hourLogFilePaths = hourLogFilePathRepository.findByHourLogFile(hourLogFile);
		TenantContext.clear();
		return hourLogFilePaths;
	}
}
