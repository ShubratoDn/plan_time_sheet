package com.aim.response;

import java.util.List;

public class AdminUserHoursChart {

	List<Double> hours;
	List<Double> extra;
	List<Double> vacation;
	public List<Double> getHours() {
		return hours;
	}
	public void setHours(List<Double> hours) {
		this.hours = hours;
	}
	public List<Double> getExtra() {
		return extra;
	}
	public void setExtra(List<Double> extra) {
		this.extra = extra;
	}
	public List<Double> getVacation() {
		return vacation;
	}
	public void setVacation(List<Double> vacation) {
		this.vacation = vacation;
	}
	
}
