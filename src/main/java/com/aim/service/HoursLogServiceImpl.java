package com.aim.service;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.aim.entity.HourLog;
import com.aim.entity.TimeSheetSubmission;
import com.aim.repository.HoursLogRepository;
import com.aim.repository.TimeSheetSubmissionRepository;
import com.aim.repository.UserDetailsRepository;
import com.aim.response.AddHoursResponse;

@Service
public class HoursLogServiceImpl implements HoursLogService {

	@Autowired
	HoursLogRepository hoursLogRepository;
	
	@Autowired
	UserDetailsRepository userDetailsRepository;
	
	@Autowired
	TimeSheetSubmissionRepository timeSheetSubmissionRepository;

	/**
	 * Add hours
	 */
	@Override
	public String saveHours(String dailyHours, String extraHours, long date, Integer userDetailId) {
		HourLog hourLog = new HourLog();
		
		hourLog.setHoursDate(new Date(date));
		
		HourLog alreadyEntry = hoursLogRepository.findByHoursDate(hourLog.getHoursDate());
		if(alreadyEntry != null) {
			hourLog = alreadyEntry;
		}
		
		if(!StringUtils.isEmpty(dailyHours)) {
			float dailyHour = convertMiniutesToHour(dailyHours);
			hourLog.setDailyHours(dailyHour);
		} else {
			hourLog.setDailyHours(0);
		}
		if(!StringUtils.isEmpty(extraHours)) {
			float extraHour = convertMiniutesToHour(extraHours);
			hourLog.setExtraHours(extraHour);
		} else {
			hourLog.setExtraHours(0);
		}
		hourLog.setUserDetail(userDetailsRepository.findByUserDetailId(userDetailId));
		hoursLogRepository.save(hourLog);
		return "success";
	}

	private float convertMiniutesToHour(String dailyHours) {
		String[] hourMin = dailyHours.split(":");
		float hour = 0.0F;
		float min = 0.0F;
		if(hourMin.length > 0) {
			hour = Float.valueOf(hourMin[0]); 
		}
		if(hourMin.length == 2) {
			min = Float.valueOf(hourMin[1]) / 60F;
			hour = hour + min;
			DecimalFormat decimalFormat = new DecimalFormat("#.##");
			hour = Float.valueOf(decimalFormat.format(hour));
		}
		return hour;
	}

	/**
	 * Delete hours
	 */
	@Override
	public Object deleteHours(Integer id) {
		hoursLogRepository.deleteById(id);
		return "success";
	}

	@Override
	public Object getAddHoursList(Integer userDetailId) {
		List<HourLog> hourLogs = (List<HourLog>) hoursLogRepository.findByUserDetailUserDetailId(userDetailId);
		if(CollectionUtils.isEmpty(hourLogs)) 
			return null;
		return AddHoursResponse.getResponseDTO(hourLogs);
	}

	@Override
	public String checkDateApprove(long date) {
		TimeSheetSubmission timeSheetSubmission = timeSheetSubmissionRepository.findByApproveByDate(new Date(date));
		
		if(timeSheetSubmission != null) 
			return "This week date already approved";
		return "success";
	}
	
}
