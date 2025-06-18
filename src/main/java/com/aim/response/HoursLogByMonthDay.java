package com.aim.response;

import java.util.Date;

public class HoursLogByMonthDay {

	private Date date;
	private float dailyHours;
	private float extraHours;
	private float vacationHours;
	
	public HoursLogByMonthDay(Date date,Float dailyHours,Float extraHours,Float vacationHours){
		
		this.date = date;
		
		if(dailyHours != null)
			this.dailyHours = dailyHours;
				
		
		if(extraHours != null)
			this.extraHours = extraHours;
		
		if(vacationHours != null)
			this.vacationHours = vacationHours;
		
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Float getDailyHours() {
		return dailyHours;
	}

	public void setDailyHours(Float dailyHours) {
		this.dailyHours = dailyHours;
	}

	public Float getExtraHours() {
		return extraHours;
	}

	public void setExtraHours(Float extraHours) {
		this.extraHours = extraHours;
	}

	public Float getVacationHours() {
		return vacationHours;
	}

	public void setVacationHours(Float vacationHours) {
		this.vacationHours = vacationHours;
	}
	
}
