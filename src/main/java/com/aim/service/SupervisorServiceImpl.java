package com.aim.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aim.entity.HourLogFile;
import com.aim.entity.Schedular;
import com.aim.entity.User;
import com.aim.enums.ActivityType;
import com.aim.enums.Month;
import com.aim.repository.HourLogFileRepository;
import com.aim.repository.SchedularRepository;
import com.aim.repository.UserDetailsRepository;
import com.aim.repository.UserRepository;
import com.aim.response.CalendarResponse;
import com.aim.response.SchedularResponse;
import com.aim.response.UserDetailActiveMonth;
import com.aim.response.UserTimeSheet;
import com.aim.response.UserTypeChartResponse;
import com.aim.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SupervisorServiceImpl implements SupervisorService{
	
	final static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private HourLogFileRepository hourLogFileRepository;

	@Autowired
	private HourLogFileService hourLogFileService;
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private SchedularRepository schedularRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	public UserDetailsRepository userDetailsRepository;

	@Override
	public void aproveOrRejectFile(Integer timesheetId, boolean isApprove, String reason, String remark) {
		
		HourLogFile hourLogFile = hourLogFileService.findById(timesheetId);
		User user = (User) request.getSession().getAttribute("user");
		
		if(isApprove) {
			hourLogFile.setReject(false);
			hourLogFile.setApprove(true);
			hourLogFile.setApprovedBy(user);
			hourLogFile.setApprovedDate(new Date());
			hourLogFile.setRemark(remark);
			hourLogFileRepository.save(hourLogFile);
			userService.addActivity("Time sheet approve", ActivityType.APPROVE_TIMESHEET.toString() , hourLogFile.getUserDetail());
			
		} else {
			hourLogFile.setReject(true);
			hourLogFile.setApprove(false);
			hourLogFile.setRejectedBy(user);
			hourLogFile.setRejectedDate(new Date());
			hourLogFile.setRejectReason(reason);
			hourLogFile.setRemark(remark);
			hourLogFileRepository.save(hourLogFile);
			userService.addActivity("Time sheet reject", ActivityType.REJECT_TIMESHEET.toString() , hourLogFile.getUserDetail());
		}
	}
	
	@Override
	public SchedularResponse getSchedularByMonth(Integer month, Integer year) {
		SchedularResponse schedularResponse = new SchedularResponse();
		List<CalendarResponse> calendarResponses = new ArrayList<CalendarResponse>();
		SimpleDateFormat simpleDateformat = new SimpleDateFormat("E, MMM dd yyyy");
		
		if(month == null)
			month = new Date().getMonth();
			
		Calendar calendar = Utils.getDate(year, month, 1);
		Date startDate = calendar.getTime();
		Date endDate = DateUtils.addDays(startDate, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)-1);
		
		schedularResponse.setStartDate(simpleDateformat.format(startDate).toString());
		schedularResponse.setEndDate(simpleDateformat.format(endDate).toString());
		
		while(startDate.before(endDate)) {
			CalendarResponse calendarResponse = new CalendarResponse();
			
			Calendar calendarx = Calendar.getInstance();
			calendarx.setTime(startDate);
			
			int yearx = calendarx.get(Calendar.YEAR);
			int monthx = calendarx.get(Calendar.MONTH);
			int dayx = calendarx.get(Calendar.DAY_OF_MONTH);
			
			Schedular schedular = schedularRepository.findBySchedular(dayx, monthx+1, yearx);
			
			if(schedular != null) {
				calendarResponse.setDailyHours(schedular.getDailyHours());
				calendarResponse.setExtraHours(schedular.getExtraHours());
				calendarResponse.setVacationHours(schedular.getVacationHours());
			}else {
				calendarResponse.setVacationHours(0.0f);
			}
			calendarResponse.setDateFormate(simpleDateformat.format(startDate));
			calendarResponse.setDate(startDate.toString());
			
			calendarResponses.add(calendarResponse);
			
			startDate = DateUtils.addDays(startDate, 1);
		}
		schedularResponse.setCalendarResponse(calendarResponses);
		return schedularResponse;
	}

	/**
	 * get schedular
	 * public 
	 */
	@Override
	public List<SchedularResponse> getSchedular(Integer year) {
		
		List<SchedularResponse> schedularResponseList = new ArrayList<SchedularResponse>();
		
		for (Month month : Month.values()) {
			SchedularResponse schedularResponse = new SchedularResponse();
			List<CalendarResponse> calendarResponses = new ArrayList<CalendarResponse>();
			SimpleDateFormat simpleDateformat = new SimpleDateFormat("dd");
			
			Calendar calendar = Utils.getDate(year, month.urlParam, 1);
			Date startDate = calendar.getTime();
			
			Integer dayDate1 = calendar.get(Calendar.DAY_OF_WEEK); //one week 
	        
			Date endDate = DateUtils.addDays(startDate, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			schedularResponse.setStartDate(simpleDateformat.format(startDate).toString());
			schedularResponse.setEndDate(simpleDateformat.format(DateUtils.addDays(endDate, -1)).toString());
			
			if(dayDate1 == 1) {
	        	startDate = DateUtils.addDays(calendar.getTime(), -6);//when it sun day(1) then -> go 6 day befor
	        } else {
	        	
            	dayDate1 = 2-dayDate1;
            	startDate = DateUtils.addDays(calendar.getTime(), dayDate1);//when it n day(n) then -> go 2-n day after
	        }
			
			Calendar calendar2 = Calendar.getInstance();
	        calendar2.setTime(DateUtils.addDays(endDate, -1));
	        Integer dayDate2 = calendar2.get(Calendar.DAY_OF_WEEK); 
	        if(dayDate2 == 1) {
	        } else {
            	dayDate2 = 8-dayDate2;
            	endDate = DateUtils.addDays(endDate, dayDate2);
	        }
	        
			schedularResponse.setMonth(month);
			
			while(startDate.before(endDate)) {
				Integer integer = startDate.getMonth();
				CalendarResponse calendarResponse = new CalendarResponse();
				if(integer==month.urlParam) {
					
					Calendar calendarx = Calendar.getInstance();
					calendarx.setTime(startDate);
					
					int yearx = calendarx.get(Calendar.YEAR);
					int monthx = calendarx.get(Calendar.MONTH);
					int dayx = calendarx.get(Calendar.DAY_OF_MONTH);
					
					Schedular schedular = schedularRepository.findBySchedular(dayx, monthx+1, yearx);
					
					if(schedular != null) {
						calendarResponse.setDailyHours(schedular.getDailyHours());
						calendarResponse.setExtraHours(schedular.getExtraHours());
						calendarResponse.setVacationHours(schedular.getVacationHours());
						calendarResponse.setOffDay(schedular.isDayOff());
					}else {
						calendarResponse.setVacationHours(0.0f);
						calendarResponse.setOffDay(false);
					}
					calendarResponse.setDateFormate(simpleDateformat.format(startDate));
					calendarResponse.setDate(startDate.toString());
					calendarResponse.setDateDay(startDate);
					SimpleDateFormat simpleDateformat1 = new SimpleDateFormat("E");
					calendarResponse.setWeekday(simpleDateformat1.format(startDate).toString());
					
				}
					calendarResponses.add(calendarResponse);
					startDate = DateUtils.addDays(startDate, 1);
			}
			schedularResponse.setCalendarResponse(calendarResponses);
			schedularResponseList.add(schedularResponse);
		}
		return schedularResponseList;
	}

	
	@Override
	public List<Schedular> saveDeafulteHoursSchedular(List<CalendarResponse> calendarResponses) throws ParseException {
		
		List<Schedular> schedulars = new ArrayList<Schedular>();
		
		for(CalendarResponse calendarResponse : calendarResponses) {
			
			Date date = new SimpleDateFormat("dd-MM-yyyy").parse(calendarResponse.getDate()); 
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			
			Schedular schedular = schedularRepository.findBySchedular(day, month+1, year);
				
			if(schedular == null)
				schedular = new Schedular();
			
			schedular.setDailyHours(calendarResponse.getDailyHours());
			schedular.setExtraHours(calendarResponse.getExtraHours());
			schedular.setVacationHours(calendarResponse.getVacationHours());
			schedular.setDayOff(calendarResponse.isOffDay());
			schedular.setHoursDate(date);
			schedular.setRemark("");
			schedularRepository.save(schedular);
			
			schedulars.add(schedular);
		}
		return schedulars;
	}
	
	/**
	 * set schedular
	 * @throws ParseException 
	 */
	@Override
	public void setSchedular(Schedular schedular1) throws ParseException {
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(schedular1.getHoursDate());
		
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		
		Schedular schedular = schedularRepository.findBySchedular(day, month+1, year);
			
		if(schedular == null)
			schedular = new Schedular();
		
		schedular.setDailyHours(schedular1.getDailyHours());
		schedular.setExtraHours(schedular1.getExtraHours());
		schedular.setVacationHours(schedular1.getVacationHours());
		schedular.setDayOff(schedular1.isDayOff());
		if(schedular1.isDayOff()){
			schedular.setRemark(schedular1.getRemark());
		}else {
			schedular.setRemark("");
		}
		schedular.setHoursDate(schedular1.getHoursDate());
		
		schedularRepository.save(schedular);
		
	}

	/**
	 * get user approved time sheet by month
	 * @throws JsonProcessingException 
	 */
	@Override
	public List<UserTimeSheet> getUserTimesheet(Integer month, Integer year) throws JsonProcessingException {
		List<User> users = userRepository.findByRoleAndActive("ROLE_USER", 1);
		List<UserTimeSheet> userTimeSheets = new ArrayList<UserTimeSheet>();
		
		for(User user : users) {
			UserTimeSheet userTimeSheet = new UserTimeSheet();
			userTimeSheet.setUser(user);
			List<HourLogFile> hourLogFiles = hourLogFileRepository.getHourLogFile(month + 1, year, user);
			userTimeSheet.setTimeSheetCount(hourLogFiles.size());
			userTimeSheet.setHourLogFile(hourLogFiles);
			
			ObjectMapper mapper = new ObjectMapper();
			String newJsonData = mapper.writeValueAsString(hourLogFiles);
			userTimeSheet.setHourLogFileJSON(newJsonData);
			
			userTimeSheets.add(userTimeSheet);
		}
		
		return userTimeSheets;
	}

	/**
	 * get user type chart response
	 */
	@Override
	public UserTypeChartResponse getUserChartByYear(Integer year) {

		List<Integer> c2cUser = new ArrayList<>();
		List<Integer> ptaxUser = new ArrayList<>();
		List<Integer> totalUser = new ArrayList<>();
		
		UserTypeChartResponse userTypeChartResponse = new UserTypeChartResponse();
		List<UserDetailActiveMonth> userDetails = hourLogFileRepository.UserDetailActiveMonth(year);
		
		for(int i = 1; i<= 12; i++) {
			final int month = i;
			List<UserDetailActiveMonth> ptaxUserDetail = userDetails.stream().filter(u-> u.getMonthNum() == month && u.getUserDetail().getPtax() != 0.0).
					collect(Collectors.toList());
			
			List<UserDetailActiveMonth> c2cUserDetail = userDetails.stream().filter(u-> u.getMonthNum() == month && u.getUserDetail().getPtax() == 0.0).
					collect(Collectors.toList());
			
			c2cUser.add(c2cUserDetail.size());
			ptaxUser.add(ptaxUserDetail.size());
			totalUser.add(c2cUserDetail.size() + ptaxUserDetail.size());
			
		}
		
		userTypeChartResponse.setC2cUser(c2cUser);
		userTypeChartResponse.setPtaxUser(ptaxUser);
		userTypeChartResponse.setTotalUser(totalUser);
		return userTypeChartResponse;
	}

	@Override
	public UserTypeChartResponse getUserChartByMonth(Integer year, int month) {
		
		List<Integer> c2cUser = new ArrayList<>();
		List<Integer> ptaxUser = new ArrayList<>();
		List<Integer> totalUser = new ArrayList<>();
		
		UserTypeChartResponse userTypeChartResponse = new UserTypeChartResponse();
		List<UserDetailActiveMonth> userDetails = hourLogFileRepository.UserDetailActiveDay(year,month);
		
		Calendar calendar = Utils.getDate(year, month-1, 1);
		int numberOfDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		for(int i = 1; i<= numberOfDays; i++) {
			final int day = i;
			List<UserDetailActiveMonth> ptaxUserDetail = userDetails.stream().filter(u-> u.getMonthNum() == day && u.getUserDetail().getPtax() != 0.0).
					collect(Collectors.toList());
			
			List<UserDetailActiveMonth> c2cUserDetail = userDetails.stream().filter(u-> u.getMonthNum() == day && u.getUserDetail().getPtax() == 0.0).
					collect(Collectors.toList());
			
			c2cUser.add(c2cUserDetail.size());
			ptaxUser.add(ptaxUserDetail.size());
			totalUser.add(c2cUserDetail.size() + ptaxUserDetail.size());
			
		}
		
		userTypeChartResponse.setC2cUser(c2cUser);
		userTypeChartResponse.setPtaxUser(ptaxUser);
		userTypeChartResponse.setTotalUser(totalUser);
		
		return userTypeChartResponse;
	}

	@Override
	public Map<Integer, Map<String, String>> getoffHours(List<SchedularResponse> schedularResponses) {
		
		
		Map<Integer, Map<String, String> > map = new HashMap<>();
		
		for (SchedularResponse response : schedularResponses) {
			
			Float dailyHours = 0.0f;
			Integer off= 0;
			for(CalendarResponse calendarResponse : response.getCalendarResponse()) {
				dailyHours = dailyHours + calendarResponse.getDailyHours();
				if(calendarResponse.isOffDay()) {
					off = off+1;
				}
			}
			Map<String, String> vSet = new HashMap<>();
			vSet.put("dailyHours", dailyHours.toString());
			vSet.put("off", off.toString());
			map.put(response.getMonth().urlParam, vSet);
			
		}
		return map;
	}
	
	
}
