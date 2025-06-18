package com.aim.enums;

import org.springframework.util.StringUtils;

public enum MailTemplateType {

	submission("submission", "Submission",true),
	approvalTimesheet("approvalTimesheet", "approvaltimesheet",true),
	rejectedTimesheet("rejectedTimesheet", "Rejected timesheet",true),
	followupTimesheet("followupTimesheet", "Followup timesheet",true),
	pendingTimesheet("pendingTimesheet", "Pending timesheet",true),
	schedularTimesheet("schedularTimesheet", "Schedular timesheet",true),
	general("general", "general",true);
	
	public String urlParam;
	
	public String displayLabel;
	
	public boolean editAble;
	
	private MailTemplateType(String urlParam, String displayLabel,boolean editAble) {
        this.urlParam = urlParam;
        this.displayLabel = displayLabel;
        this.editAble = editAble;
    }
	
	public static MailTemplateType getMailTemplateType(String urlParam) {
		if(StringUtils.isEmpty(urlParam))
			return null;
		for(MailTemplateType mailTemplateType : MailTemplateType.values()) {
			if(mailTemplateType.urlParam.equals(urlParam))
				return mailTemplateType;
		}
		return null;
	}
	
}
