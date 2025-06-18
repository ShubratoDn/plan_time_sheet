package com.aim.enums;

import org.springframework.util.StringUtils;

public enum Permission {
	
	CREATE("create","Create"),
	UPDATE("update","Update"),
	READ("read","Read"),
	DELETE("delete","Delete");
	
	public String urlParam;
	
	public String displayLabel;
	
	private Permission(String urlParam, String displayLabel) {
        this.urlParam = urlParam;
        this.displayLabel = displayLabel;
    }
	
	public static Permission getPermission(String urlParam) {
		if(StringUtils.isEmpty(urlParam))
			return null;
		for(Permission permission : Permission.values()) {
			if(permission.urlParam.equals(urlParam))
				return permission;
		}
		return null;
	}
}
