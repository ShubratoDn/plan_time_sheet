package com.aim.response;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aim.entity.HourLog;
import com.aim.entity.TimeSheetSubmission;
import com.aim.repository.TimeSheetSubmissionRepository;

@Component
public class AddHoursResponse {

	private String title;

	private Date start;
	
	private String className;
	
	private Integer id;
	
	private boolean approve;
	
	private static TimeSheetSubmissionRepository timeSheetSubmissionRepository;
	
	@Autowired
	public void setTimeSheetSubmissionRepository(TimeSheetSubmissionRepository timeSheetSubmissionRepository) {
		AddHoursResponse.timeSheetSubmissionRepository = timeSheetSubmissionRepository;
	}

	public static AddHoursResponse getResponseDTO(HourLog hourLog) {
		
		AddHoursResponse addHoursResponse = new AddHoursResponse();
		addHoursResponse.setId(hourLog.getId());
		addHoursResponse.setStart(hourLog.getHoursDate());
		TimeSheetSubmission timeSheetSubmission = timeSheetSubmissionRepository.findByApproveByDate(hourLog.getHoursDate());
		
		if(timeSheetSubmission != null) {
			addHoursResponse.setApprove(true);
			addHoursResponse.setClassName("bg-success");
		}
		
		if(hourLog.getDailyHours() != 0.0 && hourLog.getExtraHours() != 0.0) {
			
			String dailyHour = String.valueOf(hourLog.getDailyHours());
			String extraHour = String.valueOf(hourLog.getExtraHours());
			if(dailyHour.contains(".")) {
				dailyHour = convertHoursToMinHour(dailyHour.replace(".", ":"));
			}
			if(extraHour.contains(".")) {
				extraHour = convertHoursToMinHour(extraHour.replace(".", ":"));
			}
			
			addHoursResponse.setTitle("D - " + dailyHour + " & " + "E - " + extraHour);
		} else if(hourLog.getDailyHours() != 0.0) {
			
			String dailyHour = String.valueOf(hourLog.getDailyHours());
			if(dailyHour.contains(".")) {
				dailyHour = convertHoursToMinHour(dailyHour.replace(".", ":"));
			}
			addHoursResponse.setTitle("D - " + dailyHour );
		} else if(hourLog.getExtraHours() != 0.0) {
			
			String extraHour = String.valueOf(hourLog.getExtraHours());
			if(extraHour.contains(".")) {
				extraHour = convertHoursToMinHour(extraHour.replace(".", ":"));
			}
			addHoursResponse.setTitle("E - " + extraHour);
		}
		
		return addHoursResponse;
	}
	
	private static String convertHoursToMinHour(String dailyHour) {
		String[] hourMin = dailyHour.split(":");
		float hour = 0.0F;
		String hours = "";
		if(hourMin.length > 0) {
			hour = Float.valueOf(hourMin[0]); 
		}
		if(hourMin.length == 2) {
			String point = "0." + hourMin[1];
			float minVal = Float.valueOf(point) * 60;
			int roundedMin = (int)Math.round(minVal);
			String mi = "0." + roundedMin; 
			hour = hour + Float.valueOf(mi);
//			hours = String.valueOf(hour) + "." + roundedMin;
//			if(roundedMin < 10) {
//				hours = String.valueOf(hour) + ".0" + roundedMin;
//			}
		}
		return String.valueOf(hour);
	}

	public static List<AddHoursResponse> getResponseDTO(List<HourLog> hourLogs) {
		
		List<AddHoursResponse> addHoursResponses = new ArrayList<>();
		
		for(HourLog hourLog : hourLogs) {
			addHoursResponses.add(getResponseDTO(hourLog));
		}
		
		return addHoursResponses;
	}

	public boolean isApprove() {
		return approve;
	}

	public void setApprove(boolean approve) {
		this.approve = approve;
	}

	public String getTitle() {
		return title;
	}

	public Date getStart() {
		return start;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
}
