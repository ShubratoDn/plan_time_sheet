package com.aim.response;

import java.util.LinkedHashMap;
import java.util.Map;

import com.aim.entity.UserDetail;

public class HomePageUserResponse {
	
	private Map<String, Double> total;
	
	private LinkedHashMap<String, Map<String, Double>> monthMap;
	
	private UserDetail userDetails;

	public Map<String, Double> getTotal() {
		return total;
	}

	public void setTotal(Map<String, Double> total) {
		this.total = total;
	}

	public UserDetail getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(UserDetail userDetails) {
		this.userDetails = userDetails;
	}

	public LinkedHashMap<String, Map<String, Double>> getMonthMap() {
		return monthMap;
	}

	public void setMonthMap(LinkedHashMap<String, Map<String, Double>> monthMap) {
		this.monthMap = monthMap;
	}
	
	
}
