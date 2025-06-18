package com.aim.service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.aim.entity.Schedular;
import com.aim.response.CalendarResponse;
import com.aim.response.SchedularResponse;
import com.aim.response.UserTimeSheet;
import com.aim.response.UserTypeChartResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface SupervisorService {

	void aproveOrRejectFile(Integer timesheetId, boolean isApprove, String reason, String remark);

	List<SchedularResponse> getSchedular(Integer year);

	void setSchedular(Schedular schedularResponses) throws ParseException;

	List<UserTimeSheet> getUserTimesheet(Integer month, Integer year)throws JsonProcessingException;

	UserTypeChartResponse getUserChartByYear(Integer year);

	UserTypeChartResponse getUserChartByMonth(Integer year, int i);

	List<Schedular> saveDeafulteHoursSchedular(List<CalendarResponse> calendarResponses) throws ParseException;

	SchedularResponse getSchedularByMonth(Integer month, Integer year);

	Map<Integer, Map<String, String>> getoffHours(List<SchedularResponse> schedularResponses);

}
