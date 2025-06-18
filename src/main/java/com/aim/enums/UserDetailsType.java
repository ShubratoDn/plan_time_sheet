package com.aim.enums;

public enum UserDetailsType {

	ALL("all","All"),
	C2cType("c2c-type","C2C"),
	W2Type("w2-type","W2");
	
	public String urlParam;
	
	public String displayLabel;
	
	private UserDetailsType(String urlParam, String displayLabel) {
        this.urlParam = urlParam;
        this.displayLabel = displayLabel;
    }
	
	/**
	 * Get month sort by url param
	 * @param urlParam
	 * @return
	 */
	public static UserDetailsType getUserDetailsType(String urlParam) {
		
		if(urlParam == null)
			return UserDetailsType.ALL;
		
		for(UserDetailsType userDetailsType : UserDetailsType.values()) {
			
			if(userDetailsType.urlParam.equals(urlParam))
				return userDetailsType;
		}
		return UserDetailsType.ALL;
	}
	
}
