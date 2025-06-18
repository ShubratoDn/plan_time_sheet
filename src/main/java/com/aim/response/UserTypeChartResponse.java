package com.aim.response;

import java.util.List;

public class UserTypeChartResponse {

	private List<Integer> c2cUser;
	private List<Integer> ptaxUser;
	private List<Integer> totalUser;
	
	public List<Integer> getC2cUser() {
		return c2cUser;
	}
	public void setC2cUser(List<Integer> c2cUser) {
		this.c2cUser = c2cUser;
	}
	public List<Integer> getPtaxUser() {
		return ptaxUser;
	}
	public void setPtaxUser(List<Integer> ptaxUser) {
		this.ptaxUser = ptaxUser;
	}
	public List<Integer> getTotalUser() {
		return totalUser;
	}
	public void setTotalUser(List<Integer> totalUser) {
		this.totalUser = totalUser;
	}
	
}
