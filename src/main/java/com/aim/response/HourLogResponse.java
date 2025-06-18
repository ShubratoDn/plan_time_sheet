package com.aim.response;

public class HourLogResponse {

	private int month;
	
	private double sumOfDailyHour;
	
	private double sumOfExtraHour;
	
	private double sumOfVacationHours;
	
	public HourLogResponse(int month, double sumOfDailyHour, double sumOfExtraHour, Double sumOfVacationHours) {
		
		super();
		this.month = month;
		this.sumOfDailyHour = sumOfDailyHour;
		this.sumOfExtraHour = sumOfExtraHour;
		if(sumOfVacationHours == null)
			sumOfVacationHours = 0.0;
		this.sumOfVacationHours = sumOfVacationHours;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public double getSumOfDailyHour() {
		return sumOfDailyHour;
	}

	public void setSumOfDailyHour(double sumOfDailyHour) {
		this.sumOfDailyHour = sumOfDailyHour;
	}

	public double getSumOfExtraHour() {
		return sumOfExtraHour;
	}

	public void setSumOfExtraHour(double sumOfExtraHour) {
		this.sumOfExtraHour = sumOfExtraHour;
	}

	public double getSumOfVacationHours() {
		return sumOfVacationHours;
	}

	public void setSumOfVacationHours(double sumOfVacationHours) {
		this.sumOfVacationHours = sumOfVacationHours;
	}
	
}
