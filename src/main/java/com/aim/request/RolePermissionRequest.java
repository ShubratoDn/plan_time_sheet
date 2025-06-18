package com.aim.request;

import java.util.List;

import com.aim.response.UserRoleAccessTitleResponse;

public class RolePermissionRequest {

	private List<UserRoleAccessTitleResponse> supervisorPermission;
	private List<UserRoleAccessTitleResponse> userPermission;
	public List<UserRoleAccessTitleResponse> getSupervisorPermission() {
		return supervisorPermission;
	}
	public void setSupervisorPermission(List<UserRoleAccessTitleResponse> supervisorPermission) {
		this.supervisorPermission = supervisorPermission;
	}
	public List<UserRoleAccessTitleResponse> getUserPermission() {
		return userPermission;
	}
	public void setUserPermission(List<UserRoleAccessTitleResponse> userPermission) {
		this.userPermission = userPermission;
	}
}
