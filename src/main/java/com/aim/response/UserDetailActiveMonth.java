package com.aim.response;

import com.aim.entity.UserDetail;

public class UserDetailActiveMonth {

	private UserDetail userDetail;
	
	private Integer monthNum;
	
	public UserDetailActiveMonth(UserDetail userDetail, Integer monthNum) {
		this.monthNum = monthNum;
		this.userDetail = userDetail;
	}

	public UserDetail getUserDetail() {
		return userDetail;
	}

	public void setUserDetail(UserDetail userDetail) {
		this.userDetail = userDetail;
	}

	public Integer getMonthNum() {
		return monthNum;
	}

	public void setMonthNum(Integer monthNum) {
		this.monthNum = monthNum;
	}
}
