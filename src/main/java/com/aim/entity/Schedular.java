package com.aim.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "schedular")
public class Schedular extends BaseEntity<Serializable> {
	
	private static final long serialVersionUID = -1689724489061190756L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "daily_hours")
	private float dailyHours;
	
	@Column(name = "extra_hours")
	private float extraHours;
	
	@Column(name = "vacation_hours")
	private Float vacationHours;
	
	@Column(name = "day_off")
	private boolean dayOff;
	
	@Column(name = "remark")
	private String remark;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "hours_date")
	private Date hoursDate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

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

	public Date getHoursDate() {
		return hoursDate;
	}

	public void setHoursDate(Date hoursDate) {
		this.hoursDate = hoursDate;
	}

	public boolean isDayOff() {
		return dayOff;
	}

	public void setDayOff(boolean dayOff) {
		this.dayOff = dayOff;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
