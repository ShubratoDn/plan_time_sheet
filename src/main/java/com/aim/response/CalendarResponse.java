package com.aim.response;

import java.util.Date;

public class CalendarResponse {

	private String date;
	
	private Date dateDay;
	
	private String dateFormate;
	
	private String weekday;
	
	private float dailyHours;
	
	private float extraHours;
	
	private Float vacationHours;
	
	private boolean offDay;

	private String oldNotes;
	
	private String newNotes;

	public float getDailyHours() {
		return dailyHours;
	}

	public void setDailyHours(float dailyHours) {
		this.dailyHours = dailyHours;
	}

	public float getExtraHours() {
		return extraHours;
	}

	public void setExtraHours(float extraHours) {
		this.extraHours = extraHours;
	}

	public Float getVacationHours() {
		return vacationHours;
	}

	public void setVacationHours(Float vacationHours) {
		this.vacationHours = vacationHours;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDateFormate() {
		return dateFormate;
	}

	public void setDateFormate(String dateFormate) {
		this.dateFormate = dateFormate;
	}

	public String getWeekday() {
		return weekday;
	}

	public void setWeekday(String weekday) {
		this.weekday = weekday;
	}

	public Date getDateDay() {
		return dateDay;
	}

	public void setDateDay(Date dateDay) {
		this.dateDay = dateDay;
	}

	public boolean isOffDay() {
		return offDay;
	}

	public void setOffDay(boolean offDay) {
		this.offDay = offDay;
	}
	
	public String getOldNotes() {
		return oldNotes;
	}

	public void setOldNotes(String oldNotes) {
		this.oldNotes = oldNotes;
	}

	public String getNewNotes() {
		return newNotes;
	}

	public void setNewNotes(String newNotes) {
		this.newNotes = newNotes;
	}

}
