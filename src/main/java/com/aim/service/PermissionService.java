package com.aim.service;

import com.aim.entity.PermissionPlan;
import com.aim.enums.Functionality;
import com.aim.enums.Permission;

public interface PermissionService {

	boolean grantPermission();

	boolean grantPermission(String role, Functionality timesheet, Permission create, boolean itsOwn);

	PermissionPlan getPermissionPlan();

}

