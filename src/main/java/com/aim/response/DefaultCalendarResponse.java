package com.aim.response;

import java.util.Date;

import com.aim.enums.HourLogStatus;

public class DefaultCalendarResponse {

	private String date;
	
	private Date dateValue;
	
	private Float dailyHour;
	
	private Float extraHour;
	
	private Float vacation;
	
	private HourLogStatus hourLogStatus;
	
	private String rejectReason;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Float getDailyHour() {
		return dailyHour;
	}

	public void setDailyHour(Float dailyHour) {
		this.dailyHour = dailyHour;
	}

	public Float getExtraHour() {
		return extraHour;
	}

	public void setExtraHour(Float extraHour) {
		this.extraHour = extraHour;
	}

	public Float getVacation() {
		return vacation;
	}

	public void setVacation(Float vacation) {
		this.vacation = vacation;
	}

	public Date getDateValue() {
		return dateValue;
	}

	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	public HourLogStatus getHourLogStatus() {
		return hourLogStatus;
	}

	public void setHourLogStatus(HourLogStatus hourLogStatus) {
		this.hourLogStatus = hourLogStatus;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}
}
