package com.aim.response;

import java.util.List;

import com.aim.enums.Month;

public class SchedularResponse {

    private String startDate; 
	
    private String endDate;
    
	private String description;
	
	private Month month;
	
	private List<CalendarResponse> calendarResponse;

	public List<CalendarResponse> getCalendarResponse() {
		return calendarResponse;
	}

	public void setCalendarResponse(List<CalendarResponse> calendarResponse) {
		this.calendarResponse = calendarResponse;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Month getMonth() {
		return month;
	}

	public void setMonth(Month month) {
		this.month = month;
	}
	
}
