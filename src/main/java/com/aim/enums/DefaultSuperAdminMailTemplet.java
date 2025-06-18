package com.aim.enums;

import java.util.ArrayList;
import java.util.List;
import org.springframework.util.StringUtils;

public enum DefaultSuperAdminMailTemplet {

	ACTIVEPLAN_MAIL_TO_ADMIN("mailsend-to-admin", "(Default) ACTIVEPLAN_MAIL_TO_ADMIN ","This is to inform that,Mails are sent to all admins,<br><br> with following status of details,<br><br> Thank you","Your access plan is activated");

	public String urlParam;
	   
	public String displayLabel;
	
	public String htmlData;
	
	public String subject;
	
	private DefaultSuperAdminMailTemplet(String urlParam, String displayLabel, String htmlData, String subject) {
        this.urlParam = urlParam;
        this.displayLabel = displayLabel;
        this.htmlData = htmlData;
        this.subject = subject;
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
