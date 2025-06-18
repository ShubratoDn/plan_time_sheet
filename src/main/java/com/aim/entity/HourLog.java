package com.aim.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.aim.enums.HourLogStatus;

@Entity
@Table(name = "hour_log")
public class HourLog extends BaseEntity<Serializable> {
	
	private static final long serialVersionUID = -1689724489061190756L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "daily_hours")
	private float dailyHours;
	
	@Column(name = "notes",columnDefinition="TEXT default null")
	private String notes;
	
	@Column(name = "extra_hours")
	private float extraHours;
	
	@Column(name = "vacation_hours")
	private Float vacationHours;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "hours_date")
	private Date hoursDate;
	
	@Column(name = "hourlog_status")
	private HourLogStatus hourLogStatus;
	
	@Column(name = "reject_reason",columnDefinition="TEXT")
	private String rejectReason;
	
	@JoinColumn(name = "user_detail_id")
	@ManyToOne
	private UserDetail userDetail;

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

	public UserDetail getUserDetail() {
		return userDetail;
	}

	public void setUserDetail(UserDetail userDetail) {
		this.userDetail = userDetail;
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

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

}
