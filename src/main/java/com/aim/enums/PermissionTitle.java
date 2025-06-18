package com.aim.enums;

import org.springframework.util.StringUtils;

public enum PermissionTitle {

	TIMESHEET("timesheet","Timesheet"),
	DASHBOARD("dashboard","Dashboard"),
	SCHEDULAR("schedular","Schedular"),
	ACTIVITY("activity","Activity"),
	USER("user","User"),
	TEMPLATE("template","Template"),
	MAIL("mail","Mail");
	
	public String urlParam;
	
	public String displayLabel;
	
	private PermissionTitle(String urlParam, String displayLabel) {
        this.urlParam = urlParam;
        this.displayLabel = displayLabel;
    }
	
	public static PermissionTitle getPermissionTitle(String urlParam) {
		if(StringUtils.isEmpty(urlParam))
			return null;
		for(PermissionTitle permissionTitle : PermissionTitle.values()) {
			if(permissionTitle.urlParam.equals(urlParam))
				return permissionTitle;
		}
		return null;
	}
}