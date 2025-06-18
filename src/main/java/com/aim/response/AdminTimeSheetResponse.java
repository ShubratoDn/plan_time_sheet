package com.aim.response;

import com.aim.entity.UserDetail;

public class AdminTimeSheetResponse {

	private String key;
	
	private String mondayHour;
	
	private String tuesdayHour;
	
	private String wednesdayHour;
	
	private String thursdayHour;
	
	private String fridayHour;
	
	private String saturdayHour;
	
	private String sundayHour;
	
	private String totalHour;
	
	private UserDetail userDetail;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getMondayHour() {
		return mondayHour;
	}

	public void setMondayHour(String mondayHour) {
		this.mondayHour = mondayHour;
	}

	public String getTuesdayHour() {
		return tuesdayHour;
	}

	public void setTuesdayHour(String tuesdayHour) {
		this.tuesdayHour = tuesdayHour;
	}

	public String getWednesdayHour() {
		return wednesdayHour;
	}

	public void setWednesdayHour(String wednesdayHour) {
		this.wednesdayHour = wednesdayHour;
	}

	public String getThursdayHour() {
		return thursdayHour;
	}

	public void setThursdayHour(String thursdayHour) {
		this.thursdayHour = thursdayHour;
	}

	public String getFridayHour() {
		return fridayHour;
	}

	public void setFridayHour(String fridayHour) {
		this.fridayHour = fridayHour;
	}

	public String getSaturdayHour() {
		return saturdayHour;
	}

	public void setSaturdayHour(String saturdayHour) {
		this.saturdayHour = saturdayHour;
	}

	public String getSundayHour() {
		return sundayHour;
	}

	public void setSundayHour(String sundayHour) {
		this.sundayHour = sundayHour;
	}

	public String getTotalHour() {
		return totalHour;
	}

	public void setTotalHour(String totalHour) {
		this.totalHour = totalHour;
	}

	public UserDetail getUserDetail() {
		return userDetail;
	}

	public void setUserDetail(UserDetail userDetail) {
		this.userDetail = userDetail;
	}

}
