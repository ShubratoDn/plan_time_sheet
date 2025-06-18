package com.aim.service;

public interface HoursLogService {

	String saveHours(String dailyHours, String extraHours, long date, Integer userDetailId);

	Object deleteHours(Integer id);

	Object getAddHoursList(Integer userDetailId);

	String checkDateApprove(long date);

}
