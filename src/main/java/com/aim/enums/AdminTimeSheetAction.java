package com.aim.enums;

import org.springframework.util.StringUtils;

public enum AdminTimeSheetAction {
	
	REJECTED("rejected", "Rejected"),
	APPROVED("approved", "Approved"),
	TO_BE_APPROVE("toBeapprove", "To Be Approve"),
	ALL("all", "All");
		
	public String urlParam;
	
	public String displayLabel;
	
	private AdminTimeSheetAction(String urlParam, String displayLabel) {
        this.urlParam = urlParam;
        this.displayLabel = displayLabel;
    }
	
	/**
	 * Get file sort by url param
	 * @param urlParam
	 * @return
	 */
	public static AdminTimeSheetAction getAdminTimeSheetAction(String urlParam) {
		
		if(StringUtils.isEmpty(urlParam))
			return AdminTimeSheetAction.TO_BE_APPROVE;
		
		for(AdminTimeSheetAction adminTimeSheetAction : AdminTimeSheetAction.values()) {
			
			if(adminTimeSheetAction.urlParam.equals(urlParam))
				return adminTimeSheetAction;
		}
		
		return AdminTimeSheetAction.TO_BE_APPROVE;
	}
}
