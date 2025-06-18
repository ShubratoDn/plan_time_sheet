package com.aim.enums;

import java.util.ArrayList;
import java.util.List;

public enum EmailNameShortCut {

	USER_FIRST_NAME("{{user_first_name}}","User first name"),
	USER_LAST_NAME("{{user_last_name}}","User last name"),
	CLIENT_NAME("{{client_name}}","Client name"),
	END_DATE("{{end_date}}","End date"),
	START_DATE("{{start_date}}","Start date"),
	SCHEDULAR_START_DATE("{{schedular_start_date}}","Schedular start date"),
	SCHEDULAR_END_DATE("{{schedular_end_date}}","Schedular end date"),
	TIMESHEET_APPROVED_BY_FIRST_NAME("{{approver_first_name}}","Timesheet approved by first name"),
	TIMESHEET_APPROVED_BY_LAST_NAME("{{approver_last_name}}","Timesheet approved by last name"),
	TIMESHEET_REJECT_BY_FIRST_NAME("{{rejecter_first_name}}","Timesheet rejected by first name"),
	TIMESHEET_REJECT_BY_LAST_NAME("{{rejecter_last_name}}","Timesheet rejected by last name"),
	TIMESHEET_ADDED_BY_FIRST_NAME("{{added_by_first_name}}","Timesheet added by first name"),
	TIMESHEET_ADDED_BY_LAST_NAME("{{added_by_last_name}}","Timesheet added by last name");
	
	public String urlParam;
	
	public String displayLabel;
	
	private EmailNameShortCut(String urlParam, String displayLabel) {
        this.urlParam = urlParam;
        this.displayLabel = displayLabel;
    }
	
	public static List<EmailNameShortCut> getEmailNameShortCut(MailTemplateType mailTemplateType){
		
		List<EmailNameShortCut> emailNameShortCuts = new ArrayList<EmailNameShortCut>();
		
		switch (mailTemplateType) {
		case submission:
			emailNameShortCuts = getSubmission();
			break;
		case approvalTimesheet:
			emailNameShortCuts = getApprovalTimesheet();
			break;
		case rejectedTimesheet:
			emailNameShortCuts = getRejectedTimesheet();
			break;
		case followupTimesheet:
			emailNameShortCuts = getFollowupTimesheet();
			break;
		case pendingTimesheet:
			emailNameShortCuts = getPendingTimesheet();
			break;
		case schedularTimesheet:
			emailNameShortCuts = getSchedularTimesheet();
			break;
		case general:
			emailNameShortCuts = getGeneral();
			break;
		default:
			break;
		}
		
		return emailNameShortCuts;
	}
	
	private static List<EmailNameShortCut> getSubmission() {
		List<EmailNameShortCut> emailNameShortCuts = new ArrayList<EmailNameShortCut>();
		emailNameShortCuts.add(USER_FIRST_NAME);
		emailNameShortCuts.add(USER_LAST_NAME);
		emailNameShortCuts.add(CLIENT_NAME);
		emailNameShortCuts.add(END_DATE);
		emailNameShortCuts.add(START_DATE);
		emailNameShortCuts.add(TIMESHEET_ADDED_BY_FIRST_NAME);
		emailNameShortCuts.add(TIMESHEET_ADDED_BY_LAST_NAME);
		
		return emailNameShortCuts;
	}
	private static List<EmailNameShortCut> getApprovalTimesheet() {
		List<EmailNameShortCut> emailNameShortCuts = new ArrayList<EmailNameShortCut>();
		emailNameShortCuts.add(USER_FIRST_NAME);
		emailNameShortCuts.add(USER_LAST_NAME);
		emailNameShortCuts.add(CLIENT_NAME);
		emailNameShortCuts.add(END_DATE);
		emailNameShortCuts.add(START_DATE);
		emailNameShortCuts.add(TIMESHEET_APPROVED_BY_FIRST_NAME);
		emailNameShortCuts.add(TIMESHEET_APPROVED_BY_LAST_NAME);
		
		return emailNameShortCuts;
	}
	
	private static List<EmailNameShortCut> getRejectedTimesheet() {
		List<EmailNameShortCut> emailNameShortCuts = new ArrayList<EmailNameShortCut>();
		emailNameShortCuts.add(USER_FIRST_NAME);
		emailNameShortCuts.add(USER_LAST_NAME);
		emailNameShortCuts.add(CLIENT_NAME);
		emailNameShortCuts.add(END_DATE);
		emailNameShortCuts.add(START_DATE);
		emailNameShortCuts.add(TIMESHEET_REJECT_BY_FIRST_NAME);
		emailNameShortCuts.add(TIMESHEET_REJECT_BY_LAST_NAME);
		
		return emailNameShortCuts;
	}
	
	private static List<EmailNameShortCut> getFollowupTimesheet() {
		List<EmailNameShortCut> emailNameShortCuts = new ArrayList<EmailNameShortCut>();
		emailNameShortCuts.add(USER_FIRST_NAME);
		emailNameShortCuts.add(USER_LAST_NAME);
		emailNameShortCuts.add(CLIENT_NAME);
		emailNameShortCuts.add(END_DATE);
		emailNameShortCuts.add(START_DATE);
		
		return emailNameShortCuts;
	}
	
	private static List<EmailNameShortCut> getPendingTimesheet() {
		List<EmailNameShortCut> emailNameShortCuts = new ArrayList<EmailNameShortCut>();
		emailNameShortCuts.add(USER_FIRST_NAME);
		emailNameShortCuts.add(USER_LAST_NAME);
		emailNameShortCuts.add(CLIENT_NAME);
		emailNameShortCuts.add(END_DATE);
		emailNameShortCuts.add(START_DATE);
		
		return emailNameShortCuts;
	}
	private static List<EmailNameShortCut> getSchedularTimesheet() {
		List<EmailNameShortCut> emailNameShortCuts = new ArrayList<EmailNameShortCut>();
		emailNameShortCuts.add(SCHEDULAR_START_DATE);
		emailNameShortCuts.add(SCHEDULAR_END_DATE);
		return emailNameShortCuts;
	}
	private static List<EmailNameShortCut> getGeneral() {
		List<EmailNameShortCut> emailNameShortCuts = new ArrayList<EmailNameShortCut>();
		return emailNameShortCuts;
	}
	
}
