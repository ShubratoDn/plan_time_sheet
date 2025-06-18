package com.aim.response;

import java.util.Date;
import java.util.List;

import com.aim.enums.Month;
import com.aim.request.UserTimeSheetSubmitRequest;

public class MonthHoursLogResponse {
	
	private Month month;
	
	private Date startDate;
	
	private Date endDate;

	private List<DefaultCalendarResponse> defaultCalendarResponses;
	
	private UserTimeSheetSubmitRequest userTimeSheet;
	
	private Float total;

	public Month getMonth() {
		return month;
	}

	public void setMonth(Month month) {
		this.month = month;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public List<DefaultCalendarResponse> getDefaultCalendarResponses() {
		return defaultCalendarResponses;
	}

	public void setDefaultCalendarResponses(List<DefaultCalendarResponse> defaultCalendarResponses) {
		this.defaultCalendarResponses = defaultCalendarResponses;
	}

	public UserTimeSheetSubmitRequest getUserTimeSheet() {
		return userTimeSheet;
	}

	public void setUserTimeSheet(UserTimeSheetSubmitRequest userTimeSheet) {
		this.userTimeSheet = userTimeSheet;
	}

	public Float getTotal() {
		return total;
	}

	public void setTotal(Float total) {
		this.total = total;
	}
}
