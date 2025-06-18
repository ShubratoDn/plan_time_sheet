package com.aim.model;


import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aim.config.TenantContext;
import com.aim.entity.PermissionPlan;
import com.aim.repository.PermissionPlanRepository;

public class GetPermissionPlan implements Callable<PermissionPlan> {
	private static final Logger LOGGER = LoggerFactory.getLogger(GetCompanyByName.class);
	
	private PermissionPlanRepository permissionPlanRepository;
	
	private String urlSlug;
	
	public GetPermissionPlan(PermissionPlanRepository permissionPlanRepository, String urlSlug) {
		this.permissionPlanRepository = permissionPlanRepository;
		this.urlSlug = urlSlug;
	}
	
	public PermissionPlan call() throws Exception{
		
		TenantContext.setCurrentTenant(null);
		PermissionPlan permissionPlan = permissionPlanRepository.findByCompanyUrlSlug(this.urlSlug);
		TenantContext.clear();
		if(permissionPlan == null) {
			permissionPlan = new PermissionPlan();
			permissionPlan.setUserLimit(25);
		}
		return permissionPlan;
	}
}


