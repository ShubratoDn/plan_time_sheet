package com.aim.enums;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

public enum DefaultMailTemplate {

	SUBMIT_TIMESHEET("submit-timesheet", "(Default) Timesheet submit, {{start_date}} To {{end_date}}, {{client_name}}","Hello, <br /><br />Timesheet is submit for client :  {{client_name}} <br /><br />for time period: {{start_date}}  To {{end_date}} <br /><br />Please check and approve. <br /><br />Thank you.","Timesheet submit, {{start_date}} To {{end_date}}, {{client_name}}", "submission"),
	RE_SUBMIT_TIMESHEET("resubmit-timesheet", "(Default) Timesheet resubmit, {{start_date}} To {{end_date}}, {{client_name}}","Hello, <br /><br />Timesheet is submit for client :  {{client_name}} <br /><br />for time period: {{start_date}}  To {{end_date}} <br /><br />Please check and approve. <br /><br />Thank you.","Timesheet resubmit, {{start_date}} To {{end_date}}, {{client_name}}","submission"),
	TIMESHEET_APPROVED("timesheet-approved", "(Default) Timesheet approve, {{start_date}} To {{end_date}}, {{user_name}} for client: {{client_name}}","Hello, <br><br>Timesheet is approve of {{user_name}}  for client : {{client_name}}<br><br>for time period: {{start_date}} To {{end_date}}<br><br>Please check <br><br>Thank you.","Timesheet approve, {{start_date}} To {{end_date}}, {{user_name}} for client: {{client_name}}", "approvalTimesheet"),
	TIMESHEET_REJECTED("timesheet-rejected", "(Default) Timesheet reject, {{start_date}} To {{end_date}}, {{user_name}} for client: {{client_name}}","Hello, <br><br>Timesheet is reject of {{user_name}}  for client : {{client_name}}<br><br>for time period: {{start_date}} To {{end_date}}<br><br>Please check <br><br>Thank you.","Timesheet reject, {{start_date}} To {{end_date}}, {{user_name}} for client: {{client_name}}", "rejectedTimesheet"),
	TIMESHEET_PENDING("timesheet-pending", "(Default) Timesheet pending, {{start_date}} To {{end_date}}, {{user_name}} for client: {{client_name}}","Hello, <br><br>Your timesheet is pending for client : {{client_name}}<br><br>for time period: {{start_date}} To {{end_date}}<br><br>Please check <br><br>Thank you.","Timesheet pending, {{start_date}} To {{end_date}}, {{user_name}} for client: {{client_name}}","pendingTimesheet"),
	REQUEST_ACCESS("request-access", "(Default) Access Request","please add on following plan,<br><br> Also send us invoice to this add on plan,<br><br> Thank you","Request for access plan","accessRequest"),
	CANCELPLAN_ACCESS("cancelPlan-access", "(Default)Request To Cancel Plan","please cancel following plans,<br><br> Also send us invoice to this canceled plan,<br><br> Thank you","Request for cancel plan","accessRequest"),
	MAIL_SEND_TOADMIN("mailsend-to-admin", "(Default)","This is to inform that,Mails are sent to all admins,<br><br> with following status of details,<br><br> Thank you","","mailSentToAdmin");

	public String urlParam;
	   
	public String displayLabel;
	
	public String htmlData;
	
	public String subject;
	
	public String mailTemplateType;
	
	
	private DefaultMailTemplate(String urlParam, String displayLabel, String htmlData, String subject,String mailTemplateType) {
        this.urlParam = urlParam;
        this.displayLabel = displayLabel;
        this.htmlData = htmlData;
        this.subject = subject;
        this.mailTemplateType = mailTemplateType;
    }
	
	public static DefaultMailTemplate getDefaultMailTemplate(String urlParam) {
		if(StringUtils.isEmpty(urlParam))
			return null;
		for(DefaultMailTemplate defaultMailTemplate : DefaultMailTemplate.values()) {
			if(defaultMailTemplate.urlParam.equals(urlParam))
				return defaultMailTemplate;
		}
		return null;
	}
	
	public static List<DefaultMailTemplate> getDefaultMailTemplateByType(String mailTemplateTypeUrl) {
		
		List<DefaultMailTemplate> defaultMailTemplates = new ArrayList<DefaultMailTemplate>();
		
		if(StringUtils.isEmpty(mailTemplateTypeUrl))
			return defaultMailTemplates;
		for(DefaultMailTemplate defaultMailTemplate : DefaultMailTemplate.values()) {
			if(defaultMailTemplate.mailTemplateType.equals(mailTemplateTypeUrl))
				defaultMailTemplates.add(defaultMailTemplate);
		}
		return defaultMailTemplates;
	}
	
}
