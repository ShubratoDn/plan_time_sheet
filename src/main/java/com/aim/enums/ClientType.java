package com.aim.enums;

import org.springframework.util.StringUtils;

public enum ClientType {

	CLIENT("client", "Client"),
	IMPLEMENTER("implementer", "Implementer"),
	EMPLOYEE("employee", "Employer"),
	VENDOR("vendor", "Vendor");
	
	public String urlParam;
	
	public String displayLabel;
	
	private ClientType(String urlParam, String displayLabel) {
        this.urlParam = urlParam;
        this.displayLabel = displayLabel;
    }
	
	public static ClientType getClientType(String urlParam) {
		if(StringUtils.isEmpty(urlParam))
			return null;
		for(ClientType clientTyep : ClientType.values()) {
			if(clientTyep.urlParam.equals(urlParam))
				return clientTyep;
		}
		return null;
	}
}
