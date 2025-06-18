package com.aim.enums;

import org.springframework.util.StringUtils;

public enum InternalUserType {

	BDM("BDM", "Business Development Manager"),
	RECRUITER("Recruiter","Recruiter"),
	ACCOUNT_MANAGER("AccountManager", "Account Manager"),
	OTHER("Other", "Other");
	
	public String displayLabel;
	public String urlParam;
	
	private InternalUserType(String urlParam, String displayLabel) {
		this.displayLabel = displayLabel;
		this.urlParam = urlParam;
	}
	
	public static InternalUserType getInternalUser(String urlParam) {
		if(StringUtils.isEmpty(urlParam))
			return null;
		for(InternalUserType internalUser : InternalUserType.values()) {
			if(internalUser.urlParam.equals(urlParam))
				return internalUser;
		}
		return null;
	}
}
