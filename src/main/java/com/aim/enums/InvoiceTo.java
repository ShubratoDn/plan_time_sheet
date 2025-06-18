package com.aim.enums;

import org.springframework.util.StringUtils;

public enum InvoiceTo {
	CLIENT("client", "Client"),
	VENDOR("vendor", "Vendor");
	
	public String urlParam;
	
	public String displayLabel;
	
	private InvoiceTo(String urlParam, String displayLabel) {
        this.urlParam = urlParam;
        this.displayLabel = displayLabel;
    }
	
	public static InvoiceTo getInvoiceTo(String urlParam) {
		if(StringUtils.isEmpty(urlParam))
			return null;
		for(InvoiceTo clientTyep : InvoiceTo.values()) {
			if(clientTyep.urlParam.equals(urlParam))
				return clientTyep;
		}
		return null;
	}
}
