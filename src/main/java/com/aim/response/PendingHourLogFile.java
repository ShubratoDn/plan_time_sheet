package com.aim.response;

import java.util.Date;

import com.aim.entity.UserDetail;

public class PendingHourLogFile {

	private Date startDate;
	
	private Date endDate;
	
	private UserDetail userDetail;

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

	public UserDetail getUserDetail() {
		return userDetail;
	}

	public void setUserDetail(UserDetail userDetail) {
		this.userDetail = userDetail;
	}
	
}
