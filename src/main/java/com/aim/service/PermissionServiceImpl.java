package com.aim.service;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aim.entity.Company;
import com.aim.entity.PermissionPlan;
import com.aim.entity.User;
import com.aim.entity.UserRoleAccess;
import com.aim.enums.Functionality;
import com.aim.enums.Permission;
import com.aim.model.GetPermissionPlan;
import com.aim.model.GetUserThread;
import com.aim.repository.PermissionPlanRepository;
import com.aim.repository.UserRoleAccessRepository;

@Service
public class PermissionServiceImpl implements PermissionService{

	@Autowired
	private UserRoleAccessRepository userRoleAccessRepository;
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private PermissionPlanRepository permissionPlanRepository;
	
	@Override
	public boolean grantPermission(String role, Functionality functionality, Permission permission, boolean itsOwn) {
		
		if(role.equals("ROLE_ADMIN")) {
			return true;
		}
		UserRoleAccess userRoleAccess = userRoleAccessRepository.findByRoleAndFunctionality(role, functionality);
		if(userRoleAccess == null) {
			return false;
		}
		switch (permission) {
		case CREATE:
			if(userRoleAccess.isCreate()) {
//				if(itsOwn == userRoleAccess.isOwn()) {
					return true;
//				} 
			}
			break;
		case READ:
			if(userRoleAccess.isRead()) {
//				if(itsOwn == userRoleAccess.isOwn()) {
					return true;
//				} 
			}
			break;
		case UPDATE:
			if(userRoleAccess.isUpdate()) {
//				if(itsOwn == userRoleAccess.isOwn()) {
				return true;
//				} 
			}
			break;
		case DELETE:
			if(userRoleAccess.isDelete()) {
//				if(itsOwn == userRoleAccess.isOwn()) {
					return true;
//				} 
			}
			break;

		default:
			break;
		};
		
		return false;
	}
	@Override
	public boolean grantPermission() {
		
		return false;
	}
	@Override
	public PermissionPlan getPermissionPlan() {
		
		Company company = (Company) request.getSession().getAttribute("company");
		
		ThreadPoolExecutor executor1 = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
		GetPermissionPlan g = new GetPermissionPlan(permissionPlanRepository, company.getUrlSlug());
		Future<PermissionPlan> result = executor1.submit(g);
		PermissionPlan permissionPlan = null;
		
		try {
			permissionPlan = result.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return permissionPlan;
	}

}
