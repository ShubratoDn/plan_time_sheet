package com.aim.response;

import java.util.List;

import com.aim.entity.HourLogFile;
import com.aim.entity.User;

public class UserTimeSheet {

	private User user;
	
	private Integer timeSheetCount;
	
	private List<HourLogFile> hourLogFile;
	
	private String hourLogFileJSON;
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getTimeSheetCount() {
		return timeSheetCount;
	}

	public void setTimeSheetCount(Integer timeSheetCount) {
		this.timeSheetCount = timeSheetCount;
	}

	public List<HourLogFile> getHourLogFile() {
		return hourLogFile;
	}

	public void setHourLogFile(List<HourLogFile> hourLogFile) {
		this.hourLogFile = hourLogFile;
	}

	public String getHourLogFileJSON() {
		return hourLogFileJSON;
	}

	public void setHourLogFileJSON(String hourLogFileJSON) {
		this.hourLogFileJSON = hourLogFileJSON;
	}
	
}
