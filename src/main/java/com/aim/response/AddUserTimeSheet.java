package com.aim.response;

import java.util.List;

public class AddUserTimeSheet {

	private Integer userDetailId;
	
	private String startDate; 
	
	private String endDate;
	
	private String description;
		
	private List<CalendarResponse> calendarResponse;

	public Integer getUserDetailId() {
		return userDetailId;
	}

	public void setUserDetailId(Integer userDetailId) {
		this.userDetailId = userDetailId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

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
	

}
