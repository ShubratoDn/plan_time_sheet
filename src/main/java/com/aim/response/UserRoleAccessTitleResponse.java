package com.aim.response;

import java.util.ArrayList;
import java.util.List;

import com.aim.enums.PermissionTitle;
import com.aim.request.UserRoleAccessRequest;

public class UserRoleAccessTitleResponse {

	private PermissionTitle permissionTitle;
	private List<UserRoleAccessRequest> userRoleAccessRequests = new ArrayList<UserRoleAccessRequest>();
	
	public PermissionTitle getPermissionTitle() {
		return permissionTitle;
	}
	public void setPermissionTitle(PermissionTitle permissionTitle) {
		this.permissionTitle = permissionTitle;
	}
	public List<UserRoleAccessRequest> getUserRoleAccessRequests() {
		return userRoleAccessRequests;
	}
	public void setUserRoleAccessRequests(List<UserRoleAccessRequest> userRoleAccessRequests) {
		this.userRoleAccessRequests = userRoleAccessRequests;
	}
	
}
