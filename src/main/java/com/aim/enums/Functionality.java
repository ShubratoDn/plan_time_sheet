package com.aim.enums;

import org.springframework.util.StringUtils;

public enum Functionality {

	TIMESHEET("Timesheet","Timesheet","timesheet"),
	CONSULTANT_DASHBOARD("consultant-dashboard","Consultant dashboard","dashboard"),
	HOURS_DASHBOARD("hours-dashboard","Hours dashboard","dashboard"),
	ADD_SCHEDULAR("add-schedular","Add schedular","schedular"),
	TIME_SHEET_SCHEDULAR("time-sheet-schedular","Timesheet schedular","schedular"),
	GENERAL_MAIL("general-mail","General mail","mail"),
	PENDING_TIMESHEET_MAIL("pending-timesheet-mail","Pending timesheet mail","mail"),
	ADD_TIME_SHEET("add-time-sheet","Add timesheet","timesheet"),
	SUBMITTED_TIMESHEET("submitted-timesheet","Submitted timesheet","timesheet"),
	REPORT_TIME_SHEET("report-time-sheet","Report timesheet","timesheet"),
	SUPERVISOR_ACTIVITY("supervisor-activity","Supervisor activity","activity"),
	USER_ACTIVITY("user-activity","User activity","activity"),
	USER("user","User","user"),
	INTERNAL_USER("internal-user","Internal user","user"),
	TEMPLATE("template","Template","template"),
	CLIENT_ASSIGN_USER("client-assign-user","Customer assign to user","user"),
	CLIENT_ACCESS("client-access","Client access","user"),
	VENDOR_ACCESS("vendor-access","Vendor access","user"),
	EMPLOYEE_ACCESS("employee-access","Employee access","user");
	
	public String urlParam;
	
	public String displayLabel;
	
	public String groupUrlParam;
	
	private Functionality(String urlParam, String displayLabel, String groupUrlParam) {
        this.urlParam = urlParam;
        this.displayLabel = displayLabel;
        this.groupUrlParam = groupUrlParam;
    }
	
	public static Functionality getClientType(String urlParam) {
		if(StringUtils.isEmpty(urlParam))
			return null;
		for(Functionality functionality : Functionality.values()) {
			if(functionality.urlParam.equals(urlParam))
				return functionality;
		}
		return null;
	}
	
}
