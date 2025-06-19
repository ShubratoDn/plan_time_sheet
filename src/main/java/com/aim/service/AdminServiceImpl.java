package com.aim.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.WebContext;

import com.aim.entity.Activity;
import com.aim.entity.Client;
import com.aim.entity.Company;
import com.aim.entity.HourLog;
import com.aim.entity.InternalUser;
import com.aim.entity.Manager;
import com.aim.entity.TimeSheetSubmission;
import com.aim.entity.User;
import com.aim.entity.UserDetail;
import com.aim.entity.UserRoleAccess;
import com.aim.enums.ClientType;
import com.aim.enums.Functionality;
import com.aim.enums.PermissionTitle;
import com.aim.enums.UserDetailsType;
import com.aim.enums.W2OrC2cType;
import com.aim.model.ResponseGenerator;
import com.aim.repository.ActivityRepository;
import com.aim.repository.ClientRepository;
import com.aim.repository.HoursLogRepository;
import com.aim.repository.InternalUserRepository;
import com.aim.repository.ManagerRepository;
import com.aim.repository.TimeSheetSubmissionRepository;
import com.aim.repository.UserDetailsRepository;
import com.aim.repository.UserRepository;
import com.aim.repository.UserRoleAccessRepository;
import com.aim.request.CalculationCountRequest;
import com.aim.request.ClientAddDetail;
import com.aim.request.ManagerDetail;
import com.aim.request.RolePermissionRequest;
import com.aim.request.UserRoleAccessRequest;
import com.aim.response.AdminUserHoursChart;
import com.aim.response.CustomerResponse;
import com.aim.response.HomePageUserResponse;
import com.aim.response.HourLogResponse;
import com.aim.response.HoursLogByMonthDay;
import com.aim.response.UserRoleAccessTitleResponse;
import com.aim.response.UserTotalRevenueChart;
import com.aim.utils.Response;

@Service
public class AdminServiceImpl implements AdminService {

	@Autowired
	public TimeSheetSubmissionRepository timeSheetSubmissionRepository;
	
	@Autowired
	public UserDetailsRepository userDetailsRepository;
	
	@Autowired
	public UserService userService;
	
	@Autowired
	public UserRepository userRepository;
	
	@Autowired
	public ActivityRepository activityRepository;
	
	@Autowired
	public HoursLogRepository hoursLogRepository;
	
	@Autowired
	public ClientRepository clientRepository;
	
	@Autowired
	public InternalUserRepository internalUserRepository;
	
	@Autowired
	public ManagerRepository managerRepository;
	
	@Autowired
	public TemplateEngine templateEngine;
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private HttpServletResponse response ;
	
	@Autowired
	private UserRoleAccessRepository userRoleAccessRepository ;
	
	@Value("${timesheet.server.url}")
	private String TIMESHEET_SERVER_URL;
	
	private final boolean SHOW = true;
	private final boolean HIDE = false;
	
	/**
	 * reject time sheet
	 */
	@Override
	public void setRejectTimeSheet(String key, String reason, Integer userDetailId, User user) {
		
		UserDetail userDetail = userDetailsRepository.findByUserDetailId(userDetailId);
		TimeSheetSubmission timeSheetSubmission = timeSheetSubmissionRepository.findByKeyAndUserDetail(key, userDetail);
		
		timeSheetSubmission.setReject(true);
		timeSheetSubmission.setRejectedDate(new Date());
		timeSheetSubmission.setRejectedBy(user);
		timeSheetSubmission.setRejectReason(reason);
		timeSheetSubmission.setSubmit(false);
		
		timeSheetSubmissionRepository.save(timeSheetSubmission);
	}

	/**
	 * approve time-sheet
	 */
	@Override
	public void setApproveTimeSheet(String key, Integer userDetailId, User user) {
		
		UserDetail userDetail = userDetailsRepository.findByUserDetailId(userDetailId);
		TimeSheetSubmission timeSheetSubmission = timeSheetSubmissionRepository.findByKeyAndUserDetail(key, userDetail);
	
		timeSheetSubmission.setApprove(true);
		timeSheetSubmission.setApprovedBy(user);
		timeSheetSubmission.setApprovedDate(new Date());
		
		timeSheetSubmissionRepository.save(timeSheetSubmission);
	}

	/**
	 * home page Response
	 */
	@Override
	public List<HomePageUserResponse> getUserTotalHour(Integer year,Integer user, Integer month, UserDetailsType userDetailsType) {
		
		Iterable<UserDetail> userDetails = new ArrayList<>();
		if(month != null) {
			month = month+1;
		}
		if(user == null) {
			if(userDetailsType.equals(UserDetailsType.W2Type)) {
				userDetails = userDetailsRepository.findByW2C2cType(W2OrC2cType.W2);
			} else if(userDetailsType.equals(UserDetailsType.C2cType)){
				userDetails = userDetailsRepository.findByW2C2cType(W2OrC2cType.C2C);
			} else if(userDetailsType.equals(UserDetailsType.ALL)) {
				userDetails = userDetailsRepository.findAll();
			}
					
		} else {
			userDetails = userDetailsRepository.findByUserId(user);
		}
		List<HomePageUserResponse> homePageUserResponses = new ArrayList<>();
		
		for (UserDetail userDetail : userDetails) {
			
			HomePageUserResponse homePageUserResponse = new HomePageUserResponse();
			
			LinkedHashMap<String, Map<String, Double>> map = userService.getHourLog(userDetail.getUserDetailId(), year,month);
			
			homePageUserResponse.setTotal(map.get("Total"));
			homePageUserResponse.setMonthMap(map);
			homePageUserResponse.setUserDetails(userDetail);
			homePageUserResponses.add(homePageUserResponse);
		}
		
		return homePageUserResponses;
	}

	@Override
	public List<Activity> getActivity(String type, String role, Integer id) {
		
		List<Activity> activities = null;
		
		if(id != null) {
			User user = userService.findById(id);
			if(type == null) {
				activities = activityRepository.findAllByActivityByUserOrderByIdDesc(user);
			} else {
				activities = activityRepository.findAllByActivityByUserAndActivityTypeOrderByIdDesc(user, type);
			}
		}else {
			if(type == null) {
				activities = activityRepository.findAllByActivityByUserRoleOrderByIdDesc(role);
			} else {
				activities = activityRepository.findAllByActivityByUserRoleAndActivityTypeOrderByIdDesc(role, type);
			}
		}
		
		
		return activities;
	}

	@Override
	public AdminUserHoursChart getUserMonthHour(Integer year, Integer user, Integer userDetailId, UserDetailsType userDetailsType) {
		
		List<UserDetail> userDetails = new ArrayList<>();
		if(userDetailId == null) {
			if(user == null) {
				if(userDetailsType.equals(UserDetailsType.W2Type)) {
					userDetails = (List<UserDetail>) userDetailsRepository.findByW2C2cType(W2OrC2cType.W2);
				} else if(userDetailsType.equals(UserDetailsType.C2cType)){
					userDetails = (List<UserDetail>) userDetailsRepository.findByW2C2cType(W2OrC2cType.C2C);
				} else if(userDetailsType.equals(UserDetailsType.ALL)) {
					userDetails = (List<UserDetail>) userDetailsRepository.findAll();
				}
			}
			else
				userDetails = userDetailsRepository.findByUser(userService.findById(user));
		} else {
			userDetails = userDetailsRepository.findByUser(userService.findById(user)).stream().filter(f -> f.getUserDetailId() == userDetailId).collect(Collectors.toList());
		}
		
		List<Double> hours = new ArrayList<>();
		List<Double> extra = new ArrayList<>();
		List<Double> vacation = new ArrayList<>();
		double hours1 = 0.0, extra1= 0.0, vacation1= 0.0; 
		double hours2= 0.0, extra2= 0.0, vacation2= 0.0; 
		double hours3= 0.0, extra3= 0.0, vacation3= 0.0; 
		double hours4= 0.0, extra4= 0.0, vacation4= 0.0; 
		double hours5= 0.0, extra5= 0.0, vacation5= 0.0; 
		double hours6= 0.0, extra6= 0.0, vacation6= 0.0; 
		double hours7= 0.0, extra7= 0.0, vacation7= 0.0; 
		double hours8= 0.0, extra8= 0.0, vacation8= 0.0; 
		double hours9= 0.0, extra9= 0.0, vacation9= 0.0; 
		double hours10= 0.0, extra10= 0.0, vacation10= 0.0; 
		double hours11= 0.0, extra11= 0.0, vacation11= 0.0; 
		double hours12= 0.0, extra12= 0.0, vacation12= 0.0; 
		
		for (UserDetail userDetail : userDetails) {
			
			LinkedHashMap<String, Map<String, Double>> map = userService.getHourLog(userDetail.getUserDetailId(), year,null);
			
			hours1 = hours1 + (!map.get("January").isEmpty()?map.get("January").get("Daily"):0.0);
			hours2 = hours2 + (!map.get("February").isEmpty()? map.get("February").get("Daily"):0.0);
			hours3 = hours3 + (!map.get("March").isEmpty()?map.get("March").get("Daily"):0.0);
			hours4 = hours4 + (!map.get("April").isEmpty()?map.get("April").get("Daily"):0.0);
			hours5 = hours5 + (!map.get("May").isEmpty()?map.get("May").get("Daily"):0.0);
			hours6 = hours6 + (!map.get("June").isEmpty()?map.get("June").get("Daily"):0.0);
			hours7 = hours7 + (!map.get("July").isEmpty()?map.get("July").get("Daily"):0.0);
			hours8 = hours8 + (!map.get("August").isEmpty()?map.get("August").get("Daily"):0.0);
			hours9 = hours9 + (!map.get("September").isEmpty()?map.get("September").get("Daily"):0.0);
			hours10 = hours10 + (!map.get("October").isEmpty()?map.get("October").get("Daily"):0.0);
			hours11 = hours11 + (!map.get("November").isEmpty()?map.get("November").get("Daily"):0.0);
			hours12 = hours12 + (!map.get("December").isEmpty()?map.get("December").get("Daily"):0.0);
			
			extra1 = extra1 + (!map.get("January").isEmpty()?map.get("January").get("Extra"):0.0);
			extra2 = extra2 + (!map.get("February").isEmpty()? map.get("February").get("Extra"):0.0);
			extra3 = extra3 + (!map.get("March").isEmpty()?map.get("March").get("Extra"):0.0);
			extra4 = extra4 + (!map.get("April").isEmpty()?map.get("April").get("Extra"):0.0);
			extra5 = extra5 + (!map.get("May").isEmpty()?map.get("May").get("Extra"):0.0);
			extra6 = extra6 + (!map.get("June").isEmpty()?map.get("June").get("Extra"):0.0);
			extra7 = extra7 + (!map.get("July").isEmpty()?map.get("July").get("Extra"):0.0);
			extra8 = extra8 + (!map.get("August").isEmpty()?map.get("August").get("Extra"):0.0);
			extra9 = extra9 + (!map.get("September").isEmpty()?map.get("September").get("Extra"):0.0);
			extra10 = extra10 + (!map.get("October").isEmpty()?map.get("October").get("Extra"):0.0);
			extra11 = extra11 + (!map.get("November").isEmpty()?map.get("November").get("Extra"):0.0);
			extra12 = extra12 + (!map.get("December").isEmpty()?map.get("December").get("Extra"):0.0);
			
			vacation1 = vacation1 + (!map.get("January").isEmpty()?map.get("January").get("Vacation"):0.0);
			vacation2 = vacation2 + (!map.get("February").isEmpty()? map.get("February").get("Vacation"):0.0);
			vacation3 = vacation3 + (!map.get("March").isEmpty()?map.get("March").get("Vacation"):0.0);
			vacation4 = vacation4 + (!map.get("April").isEmpty()?map.get("April").get("Vacation"):0.0);
			vacation5 = vacation5 + (!map.get("May").isEmpty()?map.get("May").get("Vacation"):0.0);
			vacation6 = vacation6 + (!map.get("June").isEmpty()?map.get("June").get("Vacation"):0.0);
			vacation7 = vacation7 + (!map.get("July").isEmpty()?map.get("July").get("Vacation"):0.0);
			vacation8 = vacation8 + (!map.get("August").isEmpty()?map.get("August").get("Vacation"):0.0);
			vacation9 = vacation9 + (!map.get("September").isEmpty()?map.get("September").get("Vacation"):0.0);
			vacation10 = vacation10 + (!map.get("October").isEmpty()?map.get("October").get("Vacation"):0.0);
			vacation11 = vacation11 + (!map.get("November").isEmpty()?map.get("November").get("Vacation"):0.0);
			vacation12 = vacation12 + (!map.get("December").isEmpty()?map.get("December").get("Vacation"):0.0);
			
		}
		
		hours.add(Math.round(hours1 * 100.0) / 100.0); extra.add(Math.round(extra1 * 100.0) / 100.0); vacation.add(Math.round(vacation1 * 100.0) / 100.0);
		hours.add(Math.round(hours2 * 100.0) / 100.0); extra.add(Math.round(extra2 * 100.0) / 100.0); vacation.add(Math.round(vacation2 * 100.0) / 100.0);
		hours.add(Math.round(hours3 * 100.0) / 100.0); extra.add(Math.round(extra3 * 100.0) / 100.0); vacation.add(Math.round(vacation3 * 100.0) / 100.0);
		hours.add(Math.round(hours4 * 100.0) / 100.0); extra.add(Math.round(extra4 * 100.0) / 100.0); vacation.add(Math.round(vacation4 * 100.0) / 100.0);
		hours.add(Math.round(hours5 * 100.0) / 100.0); extra.add(Math.round(extra5 * 100.0) / 100.0); vacation.add(Math.round(vacation5 * 100.0) / 100.0);
		hours.add(Math.round(hours6 * 100.0) / 100.0); extra.add(Math.round(extra6 * 100.0) / 100.0); vacation.add(Math.round(vacation6 * 100.0) / 100.0);
		hours.add(Math.round(hours7 * 100.0) / 100.0); extra.add(Math.round(extra7 * 100.0) / 100.0); vacation.add(Math.round(vacation7 * 100.0) / 100.0);
		hours.add(Math.round(hours8 * 100.0) / 100.0); extra.add(Math.round(extra8 * 100.0) / 100.0); vacation.add(Math.round(vacation8 * 100.0) / 100.0);
		hours.add(Math.round(hours9 * 100.0) / 100.0); extra.add(Math.round(extra9 * 100.0) / 100.0); vacation.add(Math.round(vacation9 * 100.0) / 100.0);
		hours.add(Math.round(hours10 * 100.0) / 100.0); extra.add(Math.round(extra10 * 100.0) / 100.0); vacation.add(Math.round(vacation10 * 100.0) / 100.0);
		hours.add(Math.round(hours11 * 100.0) / 100.0); extra.add(Math.round(extra11 * 100.0) / 100.0); vacation.add(Math.round(vacation11 * 100.0) / 100.0);
		hours.add(Math.round(hours12 * 100.0) / 100.0); extra.add(Math.round(extra12 * 100.0) / 100.0); vacation.add(Math.round(vacation12 * 100.0) / 100.0);
		
		AdminUserHoursChart adminUserHoursChart = new AdminUserHoursChart();
		adminUserHoursChart.setExtra(extra);
		adminUserHoursChart.setHours(hours);
		adminUserHoursChart.setVacation(vacation);;
		return adminUserHoursChart;
	}

	@Override
	public AdminUserHoursChart getUserMonthDayHour(Integer year, Integer user, Integer month, UserDetailsType userDetailsType) {
		
		Iterable<UserDetail> userDetails = new ArrayList<>();
		List<HourLogResponse> hourLogResponses = new ArrayList<HourLogResponse>();
		AdminUserHoursChart adminUserHoursChart = new AdminUserHoursChart();
		List<Double> hours = new ArrayList<>();
		List<Double> extra = new ArrayList<>();
		List<Double> vacation = new ArrayList<>();
		
		//get day of month 
		Calendar calendar = new GregorianCalendar(year, month-1, 1);
        int numberOfDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        
		if(user == null)
			if(userDetailsType.equals(UserDetailsType.W2Type)) {
				userDetails = userDetailsRepository.findByW2C2cType(W2OrC2cType.W2);
			} else if(userDetailsType.equals(UserDetailsType.C2cType)){
				userDetails = userDetailsRepository.findByW2C2cType(W2OrC2cType.C2C);
			} else if(userDetailsType.equals(UserDetailsType.ALL)) {
				userDetails = userDetailsRepository.findAll();
			}
		else
			userDetails = userDetailsRepository.findByUser(userService.findById(user));
		
		for(int i = 1; i<=numberOfDays; i++) {
			
			double hoursDay = 0.0;
			double extraDay = 0.0;
			double vacationDay = 0.0;
			
			for(UserDetail userDetail : userDetails) {
				
				Calendar calendar1 = new GregorianCalendar(year, month-1, i);
				
//				List<HoursLogByMonthDay> hoursLogByMonthDays = hoursLogRepository.getHourLogByMonthDay(userDetail.getUserDetailId(), year,calendar1.getTime());
				
				HourLog hoursLog = hoursLogRepository.findByHoursDateAndUserDetail( calendar1.getTime(), userDetail);
				
				if(hoursLog != null) {
					
//					final Integer day = i;
//					Optional<HoursLogByMonthDay> optional = hoursLogByMonthDays.stream().filter(f-> f.getDate().getDay() == day).findFirst();
					
						
//						HoursLogByMonthDay hoursLogByMonthDay = optional.get();
						hoursDay = hoursDay + Math.round((double)hoursLog.getDailyHours() * 100.0) / 100.0;
						extraDay = extraDay + Math.round((double)hoursLog.getExtraHours() * 100.0) / 100.0;
						if(hoursLog.getVacationHours() != null)
							vacationDay = vacationDay + Math.round((double)hoursLog.getVacationHours() * 100.0) / 100.0;
						
				}
			}
			
			hours.add(hoursDay);
			extra.add(extraDay);
			vacation.add(vacationDay);
			
		}
		adminUserHoursChart.setExtra(extra);
		adminUserHoursChart.setHours(hours);
		adminUserHoursChart.setVacation(vacation);
		
		return adminUserHoursChart;
	}

	@Override
	public UserTotalRevenueChart getUserMonthRevenue(Integer year, Integer user, Integer userDetailId, UserDetailsType userDetailsType) {
		
		List<UserDetail> userDetails = new ArrayList<>();
		if(userDetailId == null) {
			if(user == null) {
				if(userDetailsType.equals(UserDetailsType.W2Type)) {
					userDetails = (List<UserDetail>) userDetailsRepository.findByW2C2cType(W2OrC2cType.W2);
				} else if(userDetailsType.equals(UserDetailsType.C2cType)){
					userDetails = (List<UserDetail>) userDetailsRepository.findByW2C2cType(W2OrC2cType.C2C);
				} else if(userDetailsType.equals(UserDetailsType.ALL)) {
					userDetails = (List<UserDetail>) userDetailsRepository.findAll();
				}
			}
			else
				userDetails = userDetailsRepository.findByUser(userService.findById(user));
		} else {
			userDetails = userDetailsRepository.findByUser(userService.findById(user)).stream().filter(f -> f.getUserDetailId() == userDetailId).collect(Collectors.toList());
		}
		
		List<Double> revenue = new ArrayList<>();
		List<Double> grossMargin = new ArrayList<>();
		List<Double> netMargin = new ArrayList<>();
		List<Double> expenseTotal = new ArrayList<>();
		List<Double> commissionTotal = new ArrayList<>();
		
		List<Double> commissionsBDM = new ArrayList<>();
		List<Double> commissionsACM = new ArrayList<>();
		List<Double> commissionsREC = new ArrayList<>();
		
		List<Double> expenseCon = new ArrayList<>();
		List<Double> expenseW2p = new ArrayList<>();
		List<Double> expenseOther = new ArrayList<>();
		
		double revenue1 = 0.0, grossMargin1= 0.0, netMargin1= 0.0,expense1= 0.0,commission1= 0.0, commissionsBDM1=0.0, commissionsACM1=0.0, commissionsREC1=0.0, expenseCon1=0.0, expenseW2p1=0.0, expenseOther1=0.0;
		double revenue2= 0.0, grossMargin2= 0.0, netMargin2= 0.0,expense2= 0.0,commission2= 0.0, commissionsBDM2=0.0, commissionsACM2=0.0, commissionsREC2=0.0, expenseCon2=0.0, expenseW2p2=0.0, expenseOther2=0.0;
		double revenue3= 0.0, grossMargin3= 0.0, netMargin3= 0.0,expense3= 0.0,commission3= 0.0, commissionsBDM3=0.0, commissionsACM3=0.0, commissionsREC3=0.0, expenseCon3=0.0, expenseW2p3=0.0, expenseOther3=0.0;
		double revenue4= 0.0, grossMargin4= 0.0, netMargin4= 0.0,expense4= 0.0,commission4= 0.0, commissionsBDM4=0.0, commissionsACM4=0.0, commissionsREC4=0.0, expenseCon4=0.0, expenseW2p4=0.0, expenseOther4=0.0; 
		double revenue5= 0.0, grossMargin5= 0.0, netMargin5= 0.0,expense5= 0.0,commission5= 0.0, commissionsBDM5=0.0, commissionsACM5=0.0, commissionsREC5=0.0, expenseCon5=0.0, expenseW2p5=0.0, expenseOther5=0.0;
		double revenue6= 0.0, grossMargin6= 0.0, netMargin6= 0.0,expense6= 0.0,commission6= 0.0, commissionsBDM6=0.0, commissionsACM6=0.0, commissionsREC6=0.0, expenseCon6=0.0, expenseW2p6=0.0, expenseOther6=0.0;
		double revenue7= 0.0, grossMargin7= 0.0, netMargin7= 0.0,expense7= 0.0,commission7= 0.0, commissionsBDM7=0.0, commissionsACM7=0.0, commissionsREC7=0.0, expenseCon7=0.0, expenseW2p7=0.0, expenseOther7=0.0;
		double revenue8= 0.0, grossMargin8= 0.0, netMargin8= 0.0,expense8= 0.0,commission8= 0.0, commissionsBDM8=0.0, commissionsACM8=0.0, commissionsREC8=0.0, expenseCon8=0.0, expenseW2p8=0.0, expenseOther8=0.0;
		double revenue9= 0.0, grossMargin9= 0.0, netMargin9= 0.0,expense9= 0.0,commission9= 0.0, commissionsBDM9=0.0, commissionsACM9=0.0, commissionsREC9=0.0, expenseCon9=0.0, expenseW2p9=0.0, expenseOther9=0.0;
		double revenue10= 0.0, grossMargin10= 0.0, netMargin10= 0.0,expense10= 0.0,commission10= 0.0, commissionsBDM10=0.0, commissionsACM10=0.0, commissionsREC10=0.0, expenseCon10=0.0, expenseW2p10=0.0, expenseOther10=0.0;
		double revenue11= 0.0, grossMargin11= 0.0, netMargin11= 0.0,expense11= 0.0,commission11= 0.0, commissionsBDM11=0.0, commissionsACM11=0.0, commissionsREC11=0.0, expenseCon11=0.0, expenseW2p11=0.0, expenseOther11=0.0;
		double revenue12= 0.0, grossMargin12= 0.0, netMargin12= 0.0,expense12= 0.0,commission12= 0.0, commissionsBDM12=0.0, commissionsACM12=0.0, commissionsREC12=0.0, expenseCon12=0.0, expenseW2p12=0.0, expenseOther12=0.0;
		
		for (UserDetail userDetail : userDetails) {
			
			LinkedHashMap<String, Map<String, Double>> map = userService.getHourLog(userDetail.getUserDetailId(), year,null);
			
			revenue1 = revenue1 + (!map.get("January").isEmpty()?map.get("January").get("revenue"):0.0);
			revenue2 = revenue2 + (!map.get("February").isEmpty()? map.get("February").get("revenue"):0.0);
			revenue3 = revenue3 + (!map.get("March").isEmpty()?map.get("March").get("revenue"):0.0);
			revenue4 = revenue4 + (!map.get("April").isEmpty()?map.get("April").get("revenue"):0.0);
			revenue5 = revenue5 + (!map.get("May").isEmpty()?map.get("May").get("revenue"):0.0);
			revenue6 = revenue6 + (!map.get("June").isEmpty()?map.get("June").get("revenue"):0.0);
			revenue7 = revenue7 + (!map.get("July").isEmpty()?map.get("July").get("revenue"):0.0);
			revenue8 = revenue8 + (!map.get("August").isEmpty()?map.get("August").get("revenue"):0.0);
			revenue9 = revenue9 + (!map.get("September").isEmpty()?map.get("September").get("revenue"):0.0);
			revenue10 = revenue10 + (!map.get("October").isEmpty()?map.get("October").get("revenue"):0.0);
			revenue11 = revenue11 + (!map.get("November").isEmpty()?map.get("November").get("revenue"):0.0);
			revenue12 = revenue12 + (!map.get("December").isEmpty()?map.get("December").get("revenue"):0.0);
			
			grossMargin1 = grossMargin1 + (!map.get("January").isEmpty()?map.get("January").get("G.Margin"):0.0);
			grossMargin2 = grossMargin2 + (!map.get("February").isEmpty()? map.get("February").get("G.Margin"):0.0);
			grossMargin3 = grossMargin3 + (!map.get("March").isEmpty()?map.get("March").get("G.Margin"):0.0);
			grossMargin4 = grossMargin4 + (!map.get("April").isEmpty()?map.get("April").get("G.Margin"):0.0);
			grossMargin5 = grossMargin5 + (!map.get("May").isEmpty()?map.get("May").get("G.Margin"):0.0);
			grossMargin6 = grossMargin6 + (!map.get("June").isEmpty()?map.get("June").get("G.Margin"):0.0);
			grossMargin7 = grossMargin7 + (!map.get("July").isEmpty()?map.get("July").get("G.Margin"):0.0);
			grossMargin8 = grossMargin8 + (!map.get("August").isEmpty()?map.get("August").get("G.Margin"):0.0);
			grossMargin9 = grossMargin9 + (!map.get("September").isEmpty()?map.get("September").get("G.Margin"):0.0);
			grossMargin10 = grossMargin10 + (!map.get("October").isEmpty()?map.get("October").get("G.Margin"):0.0);
			grossMargin11 = grossMargin11 + (!map.get("November").isEmpty()?map.get("November").get("G.Margin"):0.0);
			grossMargin12 = grossMargin12 + (!map.get("December").isEmpty()?map.get("December").get("G.Margin"):0.0);
			
			netMargin1 = netMargin1 + (!map.get("January").isEmpty()?map.get("January").get("N.Margin"):0.0);
			netMargin2 = netMargin2 + (!map.get("February").isEmpty()? map.get("February").get("N.Margin"):0.0);
			netMargin3 = netMargin3 + (!map.get("March").isEmpty()?map.get("March").get("N.Margin"):0.0);
			netMargin4 = netMargin4 + (!map.get("April").isEmpty()?map.get("April").get("N.Margin"):0.0);
			netMargin5 = netMargin5 + (!map.get("May").isEmpty()?map.get("May").get("N.Margin"):0.0);
			netMargin6 = netMargin6 + (!map.get("June").isEmpty()?map.get("June").get("N.Margin"):0.0);
			netMargin7 = netMargin7 + (!map.get("July").isEmpty()?map.get("July").get("N.Margin"):0.0);
			netMargin8 = netMargin8 + (!map.get("August").isEmpty()?map.get("August").get("N.Margin"):0.0);
			netMargin9 = netMargin9 + (!map.get("September").isEmpty()?map.get("September").get("N.Margin"):0.0);
			netMargin10 = netMargin10 + (!map.get("October").isEmpty()?map.get("October").get("N.Margin"):0.0);
			netMargin11 = netMargin11 + (!map.get("November").isEmpty()?map.get("November").get("N.Margin"):0.0);
			netMargin12 = netMargin12 + (!map.get("December").isEmpty()?map.get("December").get("N.Margin"):0.0);
			
			expense1 = expense1 + (!map.get("January").isEmpty()?map.get("January").get("Expense"):0.0);
			expense2 = expense2 + (!map.get("February").isEmpty()? map.get("February").get("Expense"):0.0);
			expense3 = expense3 + (!map.get("March").isEmpty()?map.get("March").get("Expense"):0.0);
			expense4 = expense4 + (!map.get("April").isEmpty()?map.get("April").get("Expense"):0.0);
			expense5 = expense5 + (!map.get("May").isEmpty()?map.get("May").get("Expense"):0.0);
			expense6 = expense6 + (!map.get("June").isEmpty()?map.get("June").get("Expense"):0.0);
			expense7 = expense7 + (!map.get("July").isEmpty()?map.get("July").get("Expense"):0.0);
			expense8 = expense8 + (!map.get("August").isEmpty()?map.get("August").get("Expense"):0.0);
			expense9 = expense9 + (!map.get("September").isEmpty()?map.get("September").get("Expense"):0.0);
			expense10 = expense10 + (!map.get("October").isEmpty()?map.get("October").get("Expense"):0.0);
			expense11 = expense11 + (!map.get("November").isEmpty()?map.get("November").get("Expense"):0.0);
			expense12 = expense12 + (!map.get("December").isEmpty()?map.get("December").get("Expense"):0.0);
			
			commission1 = commission1 + (!map.get("January").isEmpty()?map.get("January").get("Commission"):0.0);
			commission2 = commission2 + (!map.get("February").isEmpty()? map.get("February").get("Commission"):0.0);
			commission3 = commission3 + (!map.get("March").isEmpty()?map.get("March").get("Commission"):0.0);
			commission4 = commission4 + (!map.get("April").isEmpty()?map.get("April").get("Commission"):0.0);
			commission5 = commission5 + (!map.get("May").isEmpty()?map.get("May").get("Commission"):0.0);
			commission6 = commission6 + (!map.get("June").isEmpty()?map.get("June").get("Commission"):0.0);
			commission7 = commission7 + (!map.get("July").isEmpty()?map.get("July").get("Commission"):0.0);
			commission8 = commission8 + (!map.get("August").isEmpty()?map.get("August").get("Commission"):0.0);
			commission9 = commission9 + (!map.get("September").isEmpty()?map.get("September").get("Commission"):0.0);
			commission10 = commission10 + (!map.get("October").isEmpty()?map.get("October").get("Commission"):0.0);
			commission11 = commission11 + (!map.get("November").isEmpty()?map.get("November").get("Commission"):0.0);
			commission12 = commission12 + (!map.get("December").isEmpty()?map.get("December").get("Commission"):0.0);
			
			commissionsBDM1 = commissionsBDM1 + (!map.get("January").isEmpty()?map.get("January").get("BDMComm"):0.0);
			commissionsBDM2 = commissionsBDM2 + (!map.get("February").isEmpty()? map.get("February").get("BDMComm"):0.0);
			commissionsBDM3 = commissionsBDM3 + (!map.get("March").isEmpty()?map.get("March").get("BDMComm"):0.0);
			commissionsBDM4 = commissionsBDM4 + (!map.get("April").isEmpty()?map.get("April").get("BDMComm"):0.0);
			commissionsBDM5 = commissionsBDM5 + (!map.get("May").isEmpty()?map.get("May").get("BDMComm"):0.0);
			commissionsBDM6 = commissionsBDM6 + (!map.get("June").isEmpty()?map.get("June").get("BDMComm"):0.0);
			commissionsBDM7 = commissionsBDM7 + (!map.get("July").isEmpty()?map.get("July").get("BDMComm"):0.0);
			commissionsBDM8 = commissionsBDM8 + (!map.get("August").isEmpty()?map.get("August").get("BDMComm"):0.0);
			commissionsBDM9 = commissionsBDM9 + (!map.get("September").isEmpty()?map.get("September").get("BDMComm"):0.0);
			commissionsBDM10 = commissionsBDM10 + (!map.get("October").isEmpty()?map.get("October").get("BDMComm"):0.0);
			commissionsBDM11 = commissionsBDM11 + (!map.get("November").isEmpty()?map.get("November").get("BDMComm"):0.0);
			commissionsBDM12 = commissionsBDM12 + (!map.get("December").isEmpty()?map.get("December").get("BDMComm"):0.0);
			
			commissionsACM1 = commissionsACM1 + (!map.get("January").isEmpty()?map.get("January").get("ACMComm"):0.0);
			commissionsACM2 = commissionsACM2 + (!map.get("February").isEmpty()? map.get("February").get("ACMComm"):0.0);
			commissionsACM3 = commissionsACM3 + (!map.get("March").isEmpty()?map.get("March").get("ACMComm"):0.0);
			commissionsACM4 = commissionsACM4 + (!map.get("April").isEmpty()?map.get("April").get("ACMComm"):0.0);
			commissionsACM5 = commissionsACM5 + (!map.get("May").isEmpty()?map.get("May").get("ACMComm"):0.0);
			commissionsACM6 = commissionsACM6 + (!map.get("June").isEmpty()?map.get("June").get("ACMComm"):0.0);
			commissionsACM7 = commissionsACM7 + (!map.get("July").isEmpty()?map.get("July").get("ACMComm"):0.0);
			commissionsACM8 = commissionsACM8 + (!map.get("August").isEmpty()?map.get("August").get("ACMComm"):0.0);
			commissionsACM9 = commissionsACM9 + (!map.get("September").isEmpty()?map.get("September").get("ACMComm"):0.0);
			commissionsACM10 = commissionsACM10 + (!map.get("October").isEmpty()?map.get("October").get("ACMComm"):0.0);
			commissionsACM11 = commissionsACM11 + (!map.get("November").isEmpty()?map.get("November").get("ACMComm"):0.0);
			commissionsACM12 = commissionsACM12 + (!map.get("December").isEmpty()?map.get("December").get("ACMComm"):0.0);
			
			commissionsREC1 = commissionsREC1 + (!map.get("January").isEmpty()?map.get("January").get("RecComm"):0.0);
			commissionsREC2 = commissionsREC2 + (!map.get("February").isEmpty()? map.get("February").get("RecComm"):0.0);
			commissionsREC3 = commissionsREC3 + (!map.get("March").isEmpty()?map.get("March").get("RecComm"):0.0);
			commissionsREC4 = commissionsREC4 + (!map.get("April").isEmpty()?map.get("April").get("RecComm"):0.0);
			commissionsREC5 = commissionsREC5 + (!map.get("May").isEmpty()?map.get("May").get("RecComm"):0.0);
			commissionsREC6 = commissionsREC6 + (!map.get("June").isEmpty()?map.get("June").get("RecComm"):0.0);
			commissionsREC7 = commissionsREC7 + (!map.get("July").isEmpty()?map.get("July").get("RecComm"):0.0);
			commissionsREC8 = commissionsREC8 + (!map.get("August").isEmpty()?map.get("August").get("RecComm"):0.0);
			commissionsREC9 = commissionsREC9 + (!map.get("September").isEmpty()?map.get("September").get("RecComm"):0.0);
			commissionsREC10 = commissionsREC10 + (!map.get("October").isEmpty()?map.get("October").get("RecComm"):0.0);
			commissionsREC11 = commissionsREC11 + (!map.get("November").isEmpty()?map.get("November").get("RecComm"):0.0);
			commissionsREC12 = commissionsREC12 + (!map.get("December").isEmpty()?map.get("December").get("RecComm"):0.0);
			
			expenseCon1 = expenseCon1 + (!map.get("January").isEmpty()?map.get("January").get("ConsultantRate"):0.0);
			expenseCon2 = expenseCon2 + (!map.get("February").isEmpty()? map.get("February").get("ConsultantRate"):0.0);
			expenseCon3 = expenseCon3 + (!map.get("March").isEmpty()?map.get("March").get("ConsultantRate"):0.0);
			expenseCon4 = expenseCon4 + (!map.get("April").isEmpty()?map.get("April").get("ConsultantRate"):0.0);
			expenseCon5 = expenseCon5 + (!map.get("May").isEmpty()?map.get("May").get("ConsultantRate"):0.0);
			expenseCon6 = expenseCon6 + (!map.get("June").isEmpty()?map.get("June").get("ConsultantRate"):0.0);
			expenseCon7 = expenseCon7 + (!map.get("July").isEmpty()?map.get("July").get("ConsultantRate"):0.0);
			expenseCon8 = expenseCon8 + (!map.get("August").isEmpty()?map.get("August").get("ConsultantRate"):0.0);
			expenseCon9 = expenseCon9 + (!map.get("September").isEmpty()?map.get("September").get("ConsultantRate"):0.0);
			expenseCon10 = expenseCon10 + (!map.get("October").isEmpty()?map.get("October").get("ConsultantRate"):0.0);
			expenseCon11 = expenseCon11 + (!map.get("November").isEmpty()?map.get("November").get("ConsultantRate"):0.0);
			expenseCon12 = expenseCon12 + (!map.get("December").isEmpty()?map.get("December").get("ConsultantRate"):0.0);
			
			expenseW2p1 = expenseW2p1 + (!map.get("January").isEmpty()?map.get("January").get("W2Ptax"):0.0);
			expenseW2p2 = expenseW2p2 + (!map.get("February").isEmpty()? map.get("February").get("W2Ptax"):0.0);
			expenseW2p3 = expenseW2p3 + (!map.get("March").isEmpty()?map.get("March").get("W2Ptax"):0.0);
			expenseW2p4 = expenseW2p4 + (!map.get("April").isEmpty()?map.get("April").get("W2Ptax"):0.0);
			expenseW2p5 = expenseW2p5 + (!map.get("May").isEmpty()?map.get("May").get("W2Ptax"):0.0);
			expenseW2p6 = expenseW2p6 + (!map.get("June").isEmpty()?map.get("June").get("W2Ptax"):0.0);
			expenseW2p7 = expenseW2p7 + (!map.get("July").isEmpty()?map.get("July").get("W2Ptax"):0.0);
			expenseW2p8 = expenseW2p8 + (!map.get("August").isEmpty()?map.get("August").get("W2Ptax"):0.0);
			expenseW2p9 = expenseW2p9 + (!map.get("September").isEmpty()?map.get("September").get("W2Ptax"):0.0);
			expenseW2p10 = expenseW2p10 + (!map.get("October").isEmpty()?map.get("October").get("W2Ptax"):0.0);
			expenseW2p11 = expenseW2p11 + (!map.get("November").isEmpty()?map.get("November").get("W2Ptax"):0.0);
			expenseW2p12 = expenseW2p12 + (!map.get("December").isEmpty()?map.get("December").get("W2Ptax"):0.0);
			
			expenseOther1 = expenseOther1 + (!map.get("January").isEmpty()?map.get("January").get("C2C"):0.0);
			expenseOther2 = expenseOther2 + (!map.get("February").isEmpty()? map.get("February").get("C2C"):0.0);
			expenseOther3 = expenseOther3 + (!map.get("March").isEmpty()?map.get("March").get("C2C"):0.0);
			expenseOther4 = expenseOther4 + (!map.get("April").isEmpty()?map.get("April").get("C2C"):0.0);
			expenseOther5 = expenseOther5 + (!map.get("May").isEmpty()?map.get("May").get("C2C"):0.0);
			expenseOther6 = expenseOther6 + (!map.get("June").isEmpty()?map.get("June").get("C2C"):0.0);
			expenseOther7 = expenseOther7 + (!map.get("July").isEmpty()?map.get("July").get("C2C"):0.0);
			expenseOther8 = expenseOther8 + (!map.get("August").isEmpty()?map.get("August").get("C2C"):0.0);
			expenseOther9 = expenseOther9 + (!map.get("September").isEmpty()?map.get("September").get("C2C"):0.0);
			expenseOther10 = expenseOther10 + (!map.get("October").isEmpty()?map.get("October").get("C2C"):0.0);
			expenseOther11 = expenseOther11 + (!map.get("November").isEmpty()?map.get("November").get("C2C"):0.0);
			expenseOther12 = expenseOther12 + (!map.get("December").isEmpty()?map.get("December").get("C2C"):0.0);
			
		}
		
		revenue.add(Math.round(revenue1 * 100.0) / 100.0); grossMargin.add(Math.round(grossMargin1 * 100.0) / 100.0); netMargin.add(Math.round(netMargin1 * 100.0) / 100.0); expenseTotal.add(Math.round(expense1 * 100.0) / 100.0);commissionTotal.add(Math.round(commission1 * 100.0) / 100.0);
		revenue.add(Math.round(revenue2 * 100.0) / 100.0); grossMargin.add(Math.round(grossMargin2 * 100.0) / 100.0); netMargin.add(Math.round(netMargin2 * 100.0) / 100.0);expenseTotal.add(Math.round(expense2 * 100.0) / 100.0);commissionTotal.add(Math.round(commission2 * 100.0) / 100.0);
		revenue.add(Math.round(revenue3 * 100.0) / 100.0); grossMargin.add(Math.round(grossMargin3 * 100.0) / 100.0); netMargin.add(Math.round(netMargin3 * 100.0) / 100.0);expenseTotal.add(Math.round(expense3 * 100.0) / 100.0);commissionTotal.add(Math.round(commission3 * 100.0) / 100.0);
		revenue.add(Math.round(revenue4 * 100.0) / 100.0); grossMargin.add(Math.round(grossMargin4 * 100.0) / 100.0); netMargin.add(Math.round(netMargin4 * 100.0) / 100.0);expenseTotal.add(Math.round(expense4 * 100.0) / 100.0);commissionTotal.add(Math.round(commission4 * 100.0) / 100.0);
		revenue.add(Math.round(revenue5 * 100.0) / 100.0); grossMargin.add(Math.round(grossMargin5 * 100.0) / 100.0); netMargin.add(Math.round(netMargin5 * 100.0) / 100.0);expenseTotal.add(Math.round(expense5 * 100.0) / 100.0);commissionTotal.add(Math.round(commission5 * 100.0) / 100.0);
		revenue.add(Math.round(revenue6 * 100.0) / 100.0); grossMargin.add(Math.round(grossMargin6 * 100.0) / 100.0); netMargin.add(Math.round(netMargin6 * 100.0) / 100.0);expenseTotal.add(Math.round(expense6 * 100.0) / 100.0);commissionTotal.add(Math.round(commission6 * 100.0) / 100.0);
		revenue.add(Math.round(revenue7 * 100.0) / 100.0); grossMargin.add(Math.round(grossMargin7 * 100.0) / 100.0); netMargin.add(Math.round(netMargin7 * 100.0) / 100.0);expenseTotal.add(Math.round(expense7 * 100.0) / 100.0);commissionTotal.add(Math.round(commission7 * 100.0) / 100.0);
		revenue.add(Math.round(revenue8 * 100.0) / 100.0); grossMargin.add(Math.round(grossMargin8 * 100.0) / 100.0); netMargin.add(Math.round(netMargin8 * 100.0) / 100.0);expenseTotal.add(Math.round(expense8 * 100.0) / 100.0);commissionTotal.add(Math.round(commission8 * 100.0) / 100.0);
		revenue.add(Math.round(revenue9 * 100.0) / 100.0); grossMargin.add(Math.round(grossMargin9 * 100.0) / 100.0); netMargin.add(Math.round(netMargin9 * 100.0) / 100.0);expenseTotal.add(Math.round(expense9 * 100.0) / 100.0);commissionTotal.add(Math.round(commission9 * 100.0) / 100.0);
		revenue.add(Math.round(revenue10 * 100.0) / 100.0); grossMargin.add(Math.round(grossMargin10 * 100.0) / 100.0); netMargin.add(Math.round(netMargin10 * 100.0) / 100.0);expenseTotal.add(Math.round(expense10 * 100.0) / 100.0);commissionTotal.add(Math.round(commission10 * 100.0) / 100.0);
		revenue.add(Math.round(revenue11 * 100.0) / 100.0); grossMargin.add(Math.round(grossMargin11 * 100.0) / 100.0); netMargin.add(Math.round(netMargin11 * 100.0) / 100.0);expenseTotal.add(Math.round(expense11 * 100.0) / 100.0);commissionTotal.add(Math.round(commission11 * 100.0) / 100.0);
		revenue.add(Math.round(revenue12 * 100.0) / 100.0); grossMargin.add(Math.round(grossMargin12 * 100.0) / 100.0); netMargin.add(Math.round(netMargin12 * 100.0) / 100.0);expenseTotal.add(Math.round(expense12 * 100.0) / 100.0);commissionTotal.add(Math.round(commission12 * 100.0) / 100.0);
		
		commissionsBDM.add(Math.round(commissionsBDM1 * 100.0) / 100.0);commissionsACM.add(Math.round(commissionsACM1 * 100.0) / 100.0);commissionsREC.add(Math.round(commissionsREC1 * 100.0) / 100.0);
		commissionsBDM.add(Math.round(commissionsBDM2 * 100.0) / 100.0);commissionsACM.add(Math.round(commissionsACM2 * 100.0) / 100.0);commissionsREC.add(Math.round(commissionsREC2 * 100.0) / 100.0);
		commissionsBDM.add(Math.round(commissionsBDM3 * 100.0) / 100.0);commissionsACM.add(Math.round(commissionsACM3 * 100.0) / 100.0);commissionsREC.add(Math.round(commissionsREC3 * 100.0) / 100.0);
		commissionsBDM.add(Math.round(commissionsBDM4 * 100.0) / 100.0);commissionsACM.add(Math.round(commissionsACM4 * 100.0) / 100.0);commissionsREC.add(Math.round(commissionsREC4 * 100.0) / 100.0);
		commissionsBDM.add(Math.round(commissionsBDM5 * 100.0) / 100.0);commissionsACM.add(Math.round(commissionsACM5 * 100.0) / 100.0);commissionsREC.add(Math.round(commissionsREC5 * 100.0) / 100.0);
		commissionsBDM.add(Math.round(commissionsBDM6 * 100.0) / 100.0);commissionsACM.add(Math.round(commissionsACM6 * 100.0) / 100.0);commissionsREC.add(Math.round(commissionsREC6 * 100.0) / 100.0);
		commissionsBDM.add(Math.round(commissionsBDM7 * 100.0) / 100.0);commissionsACM.add(Math.round(commissionsACM7 * 100.0) / 100.0);commissionsREC.add(Math.round(commissionsREC7 * 100.0) / 100.0);
		commissionsBDM.add(Math.round(commissionsBDM8 * 100.0) / 100.0);commissionsACM.add(Math.round(commissionsACM8 * 100.0) / 100.0);commissionsREC.add(Math.round(commissionsREC8 * 100.0) / 100.0);
		commissionsBDM.add(Math.round(commissionsBDM9 * 100.0) / 100.0);commissionsACM.add(Math.round(commissionsACM9 * 100.0) / 100.0);commissionsREC.add(Math.round(commissionsREC9 * 100.0) / 100.0);
		commissionsBDM.add(Math.round(commissionsBDM10 * 100.0) / 100.0);commissionsACM.add(Math.round(commissionsACM10 * 100.0) / 100.0);commissionsREC.add(Math.round(commissionsREC10 * 100.0) / 100.0);
		commissionsBDM.add(Math.round(commissionsBDM11 * 100.0) / 100.0);commissionsACM.add(Math.round(commissionsACM11 * 100.0) / 100.0);commissionsREC.add(Math.round(commissionsREC11 * 100.0) / 100.0);
		commissionsBDM.add(Math.round(commissionsBDM12 * 100.0) / 100.0);commissionsACM.add(Math.round(commissionsACM12 * 100.0) / 100.0);commissionsREC.add(Math.round(commissionsREC12 * 100.0) / 100.0);
		
		expenseCon.add(Math.round(expenseCon1 * 100.0) / 100.0);expenseW2p.add(Math.round(expenseW2p1 * 100.0) / 100.0);expenseOther.add(Math.round(expenseOther1 * 100.0) / 100.0);
		expenseCon.add(Math.round(expenseCon2 * 100.0) / 100.0);expenseW2p.add(Math.round(expenseW2p2 * 100.0) / 100.0);expenseOther.add(Math.round(expenseOther2 * 100.0) / 100.0);
		expenseCon.add(Math.round(expenseCon3 * 100.0) / 100.0);expenseW2p.add(Math.round(expenseW2p3 * 100.0) / 100.0);expenseOther.add(Math.round(expenseOther3 * 100.0) / 100.0);
		expenseCon.add(Math.round(expenseCon4 * 100.0) / 100.0);expenseW2p.add(Math.round(expenseW2p4 * 100.0) / 100.0);expenseOther.add(Math.round(expenseOther4 * 100.0) / 100.0);
		expenseCon.add(Math.round(expenseCon5 * 100.0) / 100.0);expenseW2p.add(Math.round(expenseW2p5 * 100.0) / 100.0);expenseOther.add(Math.round(expenseOther5 * 100.0) / 100.0);
		expenseCon.add(Math.round(expenseCon6 * 100.0) / 100.0);expenseW2p.add(Math.round(expenseW2p6 * 100.0) / 100.0);expenseOther.add(Math.round(expenseOther6 * 100.0) / 100.0);
		expenseCon.add(Math.round(expenseCon7 * 100.0) / 100.0);expenseW2p.add(Math.round(expenseW2p7 * 100.0) / 100.0);expenseOther.add(Math.round(expenseOther7 * 100.0) / 100.0);
		expenseCon.add(Math.round(expenseCon8 * 100.0) / 100.0);expenseW2p.add(Math.round(expenseW2p8 * 100.0) / 100.0);expenseOther.add(Math.round(expenseOther8 * 100.0) / 100.0);
		expenseCon.add(Math.round(expenseCon9 * 100.0) / 100.0);expenseW2p.add(Math.round(expenseW2p9 * 100.0) / 100.0);expenseOther.add(Math.round(expenseOther9 * 100.0) / 100.0);
		expenseCon.add(Math.round(expenseCon10 * 100.0) / 100.0);expenseW2p.add(Math.round(expenseW2p10 * 100.0) / 100.0);expenseOther.add(Math.round(expenseOther10 * 100.0) / 100.0);
		expenseCon.add(Math.round(expenseCon11 * 100.0) / 100.0);expenseW2p.add(Math.round(expenseW2p11 * 100.0) / 100.0);expenseOther.add(Math.round(expenseOther11 * 100.0) / 100.0);
		expenseCon.add(Math.round(expenseCon12 * 100.0) / 100.0);expenseW2p.add(Math.round(expenseW2p12 * 100.0) / 100.0);expenseOther.add(Math.round(expenseOther12 * 100.0) / 100.0);
		
		UserTotalRevenueChart userTotalRevenueChart = new UserTotalRevenueChart();
		userTotalRevenueChart.setRevenue(revenue);
		userTotalRevenueChart.setGrossMargin(grossMargin);
		userTotalRevenueChart.setNetMargin(netMargin);
		userTotalRevenueChart.setExpenseTotal(expenseTotal);
		userTotalRevenueChart.setCommissionTotal(commissionTotal);
		
		userTotalRevenueChart.setExpenseCon(expenseCon);
		userTotalRevenueChart.setExpenseOther(expenseOther);
		userTotalRevenueChart.setExpenseW2p(expenseW2p);
		
		userTotalRevenueChart.setCommissionsACM(commissionsACM);
		userTotalRevenueChart.setCommissionsBDM(commissionsBDM);
		userTotalRevenueChart.setCommissionsREC(commissionsREC);
		return userTotalRevenueChart;
	}

	@Override
	public UserTotalRevenueChart getUserMonthDayRevenue(Integer year, Integer user, int month, UserDetailsType userDetailsType) {
		
		Iterable<UserDetail> userDetails = new ArrayList<>();
		UserTotalRevenueChart userTotalRevenueChart = new UserTotalRevenueChart();
		List<Double> revenueTotal = new ArrayList<>();
		List<Double> grossMarginTotal = new ArrayList<>();
		List<Double> netMarginTotal = new ArrayList<>();
		List<Double> expenseTotal = new ArrayList<>();
		List<Double> commissionTotal = new ArrayList<>();
		
		List<Double> commissionsBDM = new ArrayList<>();
		List<Double> commissionsACM = new ArrayList<>();
		List<Double> commissionsREC = new ArrayList<>();
		
		List<Double> expenseCon = new ArrayList<>();
		List<Double> expenseW2p = new ArrayList<>();
		List<Double> expenseOther = new ArrayList<>();
		
		//get day of month 
		Calendar calendar = new GregorianCalendar(year, month-1, 1);
        int numberOfDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		
        if(user == null)
        	if(userDetailsType.equals(UserDetailsType.W2Type)) {
				userDetails = userDetailsRepository.findByW2C2cType(W2OrC2cType.W2);
			} else if(userDetailsType.equals(UserDetailsType.C2cType)){
				userDetails = userDetailsRepository.findByW2C2cType(W2OrC2cType.C2C);
			} else if(userDetailsType.equals(UserDetailsType.ALL)) {
				userDetails = userDetailsRepository.findAll();
			}
		else
			userDetails = userDetailsRepository.findByUser(userService.findById(user));
        
        for(int i = 1; i<=numberOfDays; i++) {
			
        	double revenueUser = 0.0;
			double gMarginUser = 0.0;
			double nMarginUser = 0.0;
			double expenseUser = 0.0;
			double commissionUser = 0.0;
			
			double commissionsBDMUser = 0.0;
			double commissionsACMUser = 0.0;
			double commissionsRecUser = 0.0;
			
			double expenseConUser = 0.0;
			double expenseW2pUser = 0.0;
			double expenseOtherUser = 0.0;
			
			for(UserDetail userDetail : userDetails) {
				
				Calendar calendar1 = new GregorianCalendar(year, month-1, i);
		        
				HourLog hoursLog = hoursLogRepository.findByHoursDateAndUserDetail( calendar1.getTime(), userDetail);
				List<HourLog> hourLogsTotal = hoursLogRepository.getHourLogDayByMonth(userDetail.getUserDetailId(), year, month);
				
				int dayFill = 0;
				for (HourLog hourLog : hourLogsTotal) {
					
					float extra = hourLog.getExtraHours();
					float vacation = hourLog.getVacationHours()==null?0.0f:hourLog.getVacationHours();
					float daily = hourLog.getDailyHours();
						dayFill++;
				}
				
				double total = 0.0; 
	    		double consultantRate = 0.0;
	    		double w2Ptax = 0.0;
	    		double c2c = 0.0;
	    		double gMargin = 0.0;
	    		double bDMComm = 0.0;
	    		double aCMComm = 0.0;
	    		double recComm = 0.0;
	    		double nMargin = 0.0;
	    		double revenue = 0.0;
	    		
				if(hoursLog != null) {
					CalculationCountRequest calculationCountRequest = userService.getCalculationCountForSingleDayRequest(hoursLog,userDetail, month,dayFill);
					total =calculationCountRequest.getTotal();
					revenue = calculationCountRequest.getRevenue();
					consultantRate = calculationCountRequest.getConsultantRate();
					w2Ptax = calculationCountRequest.getW2Ptax();
					c2c = calculationCountRequest.getC2c();
					gMargin = calculationCountRequest.getgMargin();
					//bDMComm , aCMComm , recComm multiply by total hour
					bDMComm = calculationCountRequest.getbDMComm();
					aCMComm = calculationCountRequest.getaCMComm();
					recComm = calculationCountRequest.getRecComm();
					nMargin = calculationCountRequest.getnMargin();
					
					revenueUser = revenueUser + revenue;
					gMarginUser = gMarginUser + gMargin;
					nMarginUser = nMarginUser + nMargin;
					expenseUser = expenseUser + consultantRate + w2Ptax + c2c;
					commissionUser = commissionUser + bDMComm + aCMComm + recComm;
					
					commissionsBDMUser = commissionsBDMUser + bDMComm;
					commissionsACMUser = commissionsACMUser + aCMComm;
					commissionsRecUser = commissionsRecUser + recComm;
					
					expenseConUser = expenseConUser + consultantRate;
					expenseW2pUser = expenseW2pUser + w2Ptax;
					expenseOtherUser = expenseOtherUser + c2c;
					
				}
			}
			
			revenueTotal.add(Math.round((double)revenueUser * 100.0) / 100.0);
			grossMarginTotal.add(Math.round((double)gMarginUser * 100.0) / 100.0);
			netMarginTotal.add(Math.round((double)nMarginUser * 100.0) / 100.0);
			expenseTotal.add(Math.round((double)expenseUser * 100.0) / 100.0);
			commissionTotal.add(Math.round((double)commissionUser * 100.0) / 100.0);
			
			commissionsBDM.add(Math.round((double)commissionsBDMUser * 100.0) / 100.0);
			commissionsACM.add(Math.round((double)commissionsACMUser * 100.0) / 100.0);
			commissionsREC.add(Math.round((double)commissionsRecUser * 100.0) / 100.0);
			
			expenseCon.add(Math.round((double)expenseConUser * 100.0) / 100.0);
			expenseW2p.add(Math.round((double)expenseW2pUser * 100.0) / 100.0);
			expenseOther.add(Math.round((double)expenseOtherUser * 100.0) / 100.0);
			
			
		}
        userTotalRevenueChart.setGrossMargin(grossMarginTotal);
        userTotalRevenueChart.setRevenue(revenueTotal);
        userTotalRevenueChart.setNetMargin(netMarginTotal);
        userTotalRevenueChart.setExpenseTotal(expenseTotal);
        userTotalRevenueChart.setCommissionTotal(commissionTotal);
        userTotalRevenueChart.setCommissionsACM(commissionsACM);
        userTotalRevenueChart.setCommissionsBDM(commissionsBDM);
        userTotalRevenueChart.setCommissionsREC(commissionsREC);
        userTotalRevenueChart.setExpenseCon(expenseCon);
        userTotalRevenueChart.setExpenseOther(expenseOther);
        userTotalRevenueChart.setExpenseW2p(expenseW2p);
        return userTotalRevenueChart;
	}

	@Override
	public Client newClientAdd(ClientAddDetail clientAddDetail) {
		
		Client client = new Client();
		client.setAddress(clientAddDetail.getAddress());
		client.setClientName(clientAddDetail.getClientName());
		client.setPhone(clientAddDetail.getPhone());
		client.setRemark(clientAddDetail.getRemark());
		client.setType(clientAddDetail.getType());
		client.setZipCode(clientAddDetail.getZipCode());
		clientRepository.save(client);
		
		List<Manager> managers = new ArrayList<Manager>();
		if(!CollectionUtils.isEmpty(clientAddDetail.getManagerDetails())) {
			for (ManagerDetail managerDetail : clientAddDetail.getManagerDetails()) {
				Manager manager = getManagerByEmail(managerDetail.getEmail());
				if(manager == null) {
					manager = new Manager();
				}
				manager.setClient(client);
				manager.setDepartment(managerDetail.getDepartment());
				manager.setEmail(managerDetail.getEmail());
				manager.setManagerName(managerDetail.getManagerName());
				manager.setPhone(managerDetail.getPhone());
				
				managers.add(manager);
			}
		managerRepository.saveAll(managers);
		}
		return client;
	}

	@Override
	public List<Client> getClient() {
		List<Client> clientList = clientRepository.findByType(ClientType.CLIENT);
		if(CollectionUtils.isEmpty(clientList)) {
			clientList = new ArrayList<>();
			return clientList;
		}
		return clientList;
	}

	@Override
	public List<Client> getEmployer() {
		List<Client> clientList = clientRepository.findByType(ClientType.EMPLOYEE);
		if(CollectionUtils.isEmpty(clientList)) {
			clientList = new ArrayList<>();
			return clientList;
		}
		return clientList;
	}

	@Override
	public Client getEmployerDetails(Integer id) {
		Client client = clientRepository.findById(id).orElse(null);
		return client;
	}

	@Override
	public List<Client> getVendor() {
		List<Client> clientList = clientRepository.findByType(ClientType.VENDOR);
		if(CollectionUtils.isEmpty(clientList)) {
			clientList = new ArrayList<>();
			return clientList;
		}
		return clientList;
	}

	@Override
	public Client getClientByEmail(String email) {
//		Client client = clientRepository.findByEmail(email);
		Client client = new Client();
		return client;
	}
	@Override
	public Manager getManagerByEmail(String email) {
		Manager client = managerRepository.findByEmail(email);
		return client;
	}

	@Override
	public ResponseEntity<Response> newClientExist(ClientAddDetail clientAddDetail) {
	
		int i=0; 
		boolean exist = false;
		if(!CollectionUtils.isEmpty(clientAddDetail.getManagerDetails())) {
		
			for (ManagerDetail managerDetail : clientAddDetail.getManagerDetails()) {
				
				InternalUser internalUser = internalUserRepository.findByEmail(managerDetail.getEmail());
				if(internalUser != null){
					clientAddDetail.getManagerDetails().get(i).setErrorMsg("Using this email id already an internal user existed.");
					exist = true;
				}
				User user1 = userRepository.findByEmail(managerDetail.getEmail());
				if(user1 != null) {
					clientAddDetail.getManagerDetails().get(i).setErrorMsg("Using this email id already an user existed");
					exist = true;
				}
				
				Manager client1 = getManagerByEmail(managerDetail.getEmail());
	//			if(client1 != null && client1.getId() != client1.getId() && client1.getEmail().equals(managerDetail.getEmail())) {
				if(client1 != null) {
					clientAddDetail.getManagerDetails().get(i).setErrorMsg("Using this email id already an manager existed");
					exist = true;
				}
				
				List<ManagerDetail> existList = clientAddDetail.getManagerDetails().stream()
	            .filter(c -> c.getEmail().equals(managerDetail.getEmail()))
	            .collect(Collectors.toList());
	
				if(existList.size() > 1) {
					clientAddDetail.getManagerDetails().get(i).setErrorMsg("This email id already an exist in list");
					exist = true;
						
				}
				
				i++;
			}
		
		}
		if(exist) {
			return ResponseGenerator.generateResponse(new Response("Error") ,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		Client clientAdd = newClientAdd(clientAddDetail);
		return ResponseGenerator.generateResponse(new Response("add client successfully", clientAdd), HttpStatus.OK);
	}

	@Override
	public ClientAddDetail getClient(Client client) {
		if(client != null) {
			ClientAddDetail clientAddDetail = new ClientAddDetail();
			clientAddDetail.setClientName(client.getClientName());
			clientAddDetail.setPhone(client.getPhone());
			clientAddDetail.setRemark(client.getRemark());
			clientAddDetail.setType(client.getType());
			clientAddDetail.setZipCode(client.getZipCode());
			clientAddDetail.setAddress(client.getAddress());
			clientAddDetail.setId(client.getId());
			List<Manager> managers = managerRepository.findByClient(client);
			List<ManagerDetail> managerDetails = new ArrayList<ManagerDetail>();
			for (Manager manager : managers) {
				ManagerDetail managerDetail = new ManagerDetail();
				
				managerDetail.setDepartment(manager.getDepartment());
				managerDetail.setEmail(manager.getEmail());
				managerDetail.setId(manager.getId());
				managerDetail.setManagerName(manager.getManagerName());
				managerDetail.setPhone(manager.getPhone());
				
				managerDetails.add(managerDetail);
			}
			
			clientAddDetail.setManagerDetails(managerDetails);
			
			return clientAddDetail;
		}
		return null;
	}

	/**
	 * get client Manager
	 */
	@Override
	public List<Manager> getClientByType(ClientType type) {
		List<Manager> managers = managerRepository.findByClientType(type);
		return managers;
	}

	@Override
	public RolePermissionRequest getRolePermission() {
		
		List<UserRoleAccessTitleResponse> supervisorPermission = new ArrayList<UserRoleAccessTitleResponse>();
		List<UserRoleAccessTitleResponse> userPermission = new ArrayList<UserRoleAccessTitleResponse>();
		
		RolePermissionRequest rolePermissionRequest = new RolePermissionRequest();
		
		for(Functionality functionality : Functionality.values()) {
			
			//FOR ROLE SUPERVISOR
			UserRoleAccess userRoleAccess = userRoleAccessRepository.findByRoleAndFunctionality("ROLE_SUPERVISOR", functionality);
			UserRoleAccessRequest userRoleAccessRequest = new UserRoleAccessRequest();
			
			if(userRoleAccess != null) {
				
				userRoleAccessRequest.setCreate(userRoleAccess.isCreate());
				userRoleAccessRequest.setDelete(userRoleAccess.isDelete());
				userRoleAccessRequest.setFunctionality(userRoleAccess.getFunctionality());
				userRoleAccessRequest.setId(userRoleAccess.getId());
				userRoleAccessRequest.setOwn(false);
				userRoleAccessRequest.setRead(userRoleAccess.isRead());
				userRoleAccessRequest.setUpdate(userRoleAccess.isUpdate());
				
				switch (functionality) {
				case TIMESHEET:
					userRoleAccessRequest.setCreateShow(SHOW);
					userRoleAccessRequest.setDeleteShow(HIDE);
					userRoleAccessRequest.setOwnShow(HIDE);
					userRoleAccessRequest.setReadShow(SHOW);
					userRoleAccessRequest.setUpdateShow(SHOW);
					
					if(titleContain(supervisorPermission, Functionality.TIMESHEET.groupUrlParam)) {
						 supervisorPermission.stream().filter(o -> o.getPermissionTitle().urlParam.equals(Functionality.TIMESHEET.groupUrlParam))
						 	.findFirst().get().getUserRoleAccessRequests().add(userRoleAccessRequest);
					
					} else {
						UserRoleAccessTitleResponse userRoleAccessTitleResponse = new UserRoleAccessTitleResponse();
						PermissionTitle permissionTitle = PermissionTitle.getPermissionTitle(Functionality.TIMESHEET.groupUrlParam);
						
						userRoleAccessTitleResponse.setPermissionTitle(permissionTitle);
						List<UserRoleAccessRequest> userRoleAccessRequests = new ArrayList<UserRoleAccessRequest>();
						userRoleAccessRequests.add(userRoleAccessRequest);
						userRoleAccessTitleResponse.setUserRoleAccessRequests(userRoleAccessRequests);
						supervisorPermission.add(userRoleAccessTitleResponse);
						
					}
					
					break;
				case CONSULTANT_DASHBOARD:
					userRoleAccessRequest.setCreateShow(HIDE);
					userRoleAccessRequest.setDeleteShow(HIDE);
					userRoleAccessRequest.setOwnShow(HIDE);
					userRoleAccessRequest.setReadShow(SHOW);
					userRoleAccessRequest.setUpdateShow(HIDE);
					if(titleContain(supervisorPermission, Functionality.CONSULTANT_DASHBOARD.groupUrlParam)) {
						supervisorPermission.stream().filter(o -> o.getPermissionTitle().urlParam.equals(Functionality.CONSULTANT_DASHBOARD.groupUrlParam))
					 	.findFirst().get().getUserRoleAccessRequests().add(userRoleAccessRequest);
					
					} else {
						UserRoleAccessTitleResponse userRoleAccessTitleResponse = new UserRoleAccessTitleResponse();
						PermissionTitle permissionTitle = PermissionTitle.getPermissionTitle(Functionality.CONSULTANT_DASHBOARD.groupUrlParam);
						
						userRoleAccessTitleResponse.setPermissionTitle(permissionTitle);
						List<UserRoleAccessRequest> userRoleAccessRequests = new ArrayList<UserRoleAccessRequest>();
						userRoleAccessRequests.add(userRoleAccessRequest);
						userRoleAccessTitleResponse.setUserRoleAccessRequests(userRoleAccessRequests);
						supervisorPermission.add(userRoleAccessTitleResponse);
						
					}
					break;
				case HOURS_DASHBOARD:
					userRoleAccessRequest.setCreateShow(HIDE);
					userRoleAccessRequest.setDeleteShow(HIDE);
					userRoleAccessRequest.setOwnShow(HIDE);
					userRoleAccessRequest.setReadShow(SHOW);
					userRoleAccessRequest.setUpdateShow(HIDE);
					if(titleContain(supervisorPermission, Functionality.HOURS_DASHBOARD.groupUrlParam)) {
						supervisorPermission.stream().filter(o -> o.getPermissionTitle().urlParam.equals(Functionality.HOURS_DASHBOARD.groupUrlParam))
					 	.findFirst().get().getUserRoleAccessRequests().add(userRoleAccessRequest);
					
					} else {
						UserRoleAccessTitleResponse userRoleAccessTitleResponse = new UserRoleAccessTitleResponse();
						PermissionTitle permissionTitle = PermissionTitle.getPermissionTitle(Functionality.HOURS_DASHBOARD.groupUrlParam);
						
						userRoleAccessTitleResponse.setPermissionTitle(permissionTitle);
						List<UserRoleAccessRequest> userRoleAccessRequests = new ArrayList<UserRoleAccessRequest>();
						userRoleAccessRequests.add(userRoleAccessRequest);
						userRoleAccessTitleResponse.setUserRoleAccessRequests(userRoleAccessRequests);
						supervisorPermission.add(userRoleAccessTitleResponse);
						
					}
					break;
				case ADD_SCHEDULAR:
					userRoleAccessRequest.setCreateShow(SHOW);
					userRoleAccessRequest.setDeleteShow(HIDE);
					userRoleAccessRequest.setOwnShow(HIDE);
					userRoleAccessRequest.setReadShow(HIDE);
					userRoleAccessRequest.setUpdateShow(HIDE);
					if(titleContain(supervisorPermission, Functionality.ADD_SCHEDULAR.groupUrlParam)) {
						supervisorPermission.stream().filter(o -> o.getPermissionTitle().urlParam.equals(Functionality.ADD_SCHEDULAR.groupUrlParam))
					 	.findFirst().get().getUserRoleAccessRequests().add(userRoleAccessRequest);
					
					} else {
						UserRoleAccessTitleResponse userRoleAccessTitleResponse = new UserRoleAccessTitleResponse();
						PermissionTitle permissionTitle = PermissionTitle.getPermissionTitle(Functionality.ADD_SCHEDULAR.groupUrlParam);
						
						userRoleAccessTitleResponse.setPermissionTitle(permissionTitle);
						List<UserRoleAccessRequest> userRoleAccessRequests = new ArrayList<UserRoleAccessRequest>();
						userRoleAccessRequests.add(userRoleAccessRequest);
						userRoleAccessTitleResponse.setUserRoleAccessRequests(userRoleAccessRequests);
						supervisorPermission.add(userRoleAccessTitleResponse);
						
					}
					break;
				case TIME_SHEET_SCHEDULAR:
					userRoleAccessRequest.setCreateShow(HIDE);
					userRoleAccessRequest.setDeleteShow(HIDE);
					userRoleAccessRequest.setOwnShow(HIDE);
					userRoleAccessRequest.setReadShow(SHOW);
					userRoleAccessRequest.setUpdateShow(HIDE);
					if(titleContain(supervisorPermission, Functionality.TIME_SHEET_SCHEDULAR.groupUrlParam)) {
						supervisorPermission.stream().filter(o -> o.getPermissionTitle().urlParam.equals(Functionality.TIME_SHEET_SCHEDULAR.groupUrlParam))
					 	.findFirst().get().getUserRoleAccessRequests().add(userRoleAccessRequest);
					
					} else {
						UserRoleAccessTitleResponse userRoleAccessTitleResponse = new UserRoleAccessTitleResponse();
						PermissionTitle permissionTitle = PermissionTitle.getPermissionTitle(Functionality.TIME_SHEET_SCHEDULAR.groupUrlParam);
						
						userRoleAccessTitleResponse.setPermissionTitle(permissionTitle);
						List<UserRoleAccessRequest> userRoleAccessRequests = new ArrayList<UserRoleAccessRequest>();
						userRoleAccessRequests.add(userRoleAccessRequest);
						userRoleAccessTitleResponse.setUserRoleAccessRequests(userRoleAccessRequests);
						supervisorPermission.add(userRoleAccessTitleResponse);
						
					}
					break;
				case GENERAL_MAIL:
					userRoleAccessRequest.setCreateShow(SHOW);
					userRoleAccessRequest.setDeleteShow(HIDE);
					userRoleAccessRequest.setOwnShow(HIDE);
					userRoleAccessRequest.setReadShow(HIDE);
					userRoleAccessRequest.setUpdateShow(HIDE);
					if(titleContain(supervisorPermission, Functionality.GENERAL_MAIL.groupUrlParam)) {
						supervisorPermission.stream().filter(o -> o.getPermissionTitle().urlParam.equals(Functionality.GENERAL_MAIL.groupUrlParam))
					 	.findFirst().get().getUserRoleAccessRequests().add(userRoleAccessRequest);
					
					} else {
						UserRoleAccessTitleResponse userRoleAccessTitleResponse = new UserRoleAccessTitleResponse();
						PermissionTitle permissionTitle = PermissionTitle.getPermissionTitle(Functionality.GENERAL_MAIL.groupUrlParam);
						
						userRoleAccessTitleResponse.setPermissionTitle(permissionTitle);
						List<UserRoleAccessRequest> userRoleAccessRequests = new ArrayList<UserRoleAccessRequest>();
						userRoleAccessRequests.add(userRoleAccessRequest);
						userRoleAccessTitleResponse.setUserRoleAccessRequests(userRoleAccessRequests);
						supervisorPermission.add(userRoleAccessTitleResponse);
						
					}
					break;
				case PENDING_TIMESHEET_MAIL:
					userRoleAccessRequest.setCreateShow(SHOW);
					userRoleAccessRequest.setDeleteShow(HIDE);
					userRoleAccessRequest.setOwnShow(HIDE);
					userRoleAccessRequest.setReadShow(HIDE);
					userRoleAccessRequest.setUpdateShow(HIDE);
					if(titleContain(supervisorPermission, Functionality.PENDING_TIMESHEET_MAIL.groupUrlParam)) {
						supervisorPermission.stream().filter(o -> o.getPermissionTitle().urlParam.equals(Functionality.PENDING_TIMESHEET_MAIL.groupUrlParam))
						.findFirst().get().getUserRoleAccessRequests().add(userRoleAccessRequest);
						
					} else {
						UserRoleAccessTitleResponse userRoleAccessTitleResponse = new UserRoleAccessTitleResponse();
						PermissionTitle permissionTitle = PermissionTitle.getPermissionTitle(Functionality.PENDING_TIMESHEET_MAIL.groupUrlParam);
						
						userRoleAccessTitleResponse.setPermissionTitle(permissionTitle);
						List<UserRoleAccessRequest> userRoleAccessRequests = new ArrayList<UserRoleAccessRequest>();
						userRoleAccessRequests.add(userRoleAccessRequest);
						userRoleAccessTitleResponse.setUserRoleAccessRequests(userRoleAccessRequests);
						supervisorPermission.add(userRoleAccessTitleResponse);
						
					}
					break;
				case SUPERVISOR_ACTIVITY:
					userRoleAccessRequest.setCreateShow(HIDE);
					userRoleAccessRequest.setDeleteShow(HIDE);
					userRoleAccessRequest.setOwnShow(HIDE);
					userRoleAccessRequest.setReadShow(SHOW);
					userRoleAccessRequest.setUpdateShow(HIDE);
					if(titleContain(supervisorPermission, Functionality.SUPERVISOR_ACTIVITY.groupUrlParam)) {
						supervisorPermission.stream().filter(o -> o.getPermissionTitle().urlParam.equals(Functionality.SUPERVISOR_ACTIVITY.groupUrlParam))
						.findFirst().get().getUserRoleAccessRequests().add(userRoleAccessRequest);
						
					} else {
						UserRoleAccessTitleResponse userRoleAccessTitleResponse = new UserRoleAccessTitleResponse();
						PermissionTitle permissionTitle = PermissionTitle.getPermissionTitle(Functionality.SUPERVISOR_ACTIVITY.groupUrlParam);
						
						userRoleAccessTitleResponse.setPermissionTitle(permissionTitle);
						List<UserRoleAccessRequest> userRoleAccessRequests = new ArrayList<UserRoleAccessRequest>();
						userRoleAccessRequests.add(userRoleAccessRequest);
						userRoleAccessTitleResponse.setUserRoleAccessRequests(userRoleAccessRequests);
						supervisorPermission.add(userRoleAccessTitleResponse);
						
					}
					break;
				case USER_ACTIVITY:
					userRoleAccessRequest.setCreateShow(HIDE);
					userRoleAccessRequest.setDeleteShow(HIDE);
					userRoleAccessRequest.setOwnShow(HIDE);
					userRoleAccessRequest.setReadShow(SHOW);
					userRoleAccessRequest.setUpdateShow(HIDE);
					if(titleContain(supervisorPermission, Functionality.USER_ACTIVITY.groupUrlParam)) {
						supervisorPermission.stream().filter(o -> o.getPermissionTitle().urlParam.equals(Functionality.USER_ACTIVITY.groupUrlParam))
						.findFirst().get().getUserRoleAccessRequests().add(userRoleAccessRequest);
						
					} else {
						UserRoleAccessTitleResponse userRoleAccessTitleResponse = new UserRoleAccessTitleResponse();
						PermissionTitle permissionTitle = PermissionTitle.getPermissionTitle(Functionality.USER_ACTIVITY.groupUrlParam);
						
						userRoleAccessTitleResponse.setPermissionTitle(permissionTitle);
						List<UserRoleAccessRequest> userRoleAccessRequests = new ArrayList<UserRoleAccessRequest>();
						userRoleAccessRequests.add(userRoleAccessRequest);
						userRoleAccessTitleResponse.setUserRoleAccessRequests(userRoleAccessRequests);
						supervisorPermission.add(userRoleAccessTitleResponse);
						
					}
					break;
				case USER:
					userRoleAccessRequest.setCreateShow(SHOW);
					userRoleAccessRequest.setDeleteShow(HIDE);
					userRoleAccessRequest.setOwnShow(HIDE);
					userRoleAccessRequest.setReadShow(SHOW);
					userRoleAccessRequest.setUpdateShow(SHOW);
					if(titleContain(supervisorPermission, Functionality.USER.groupUrlParam)) {
						supervisorPermission.stream().filter(o -> o.getPermissionTitle().urlParam.equals(Functionality.USER.groupUrlParam))
						.findFirst().get().getUserRoleAccessRequests().add(userRoleAccessRequest);
						
					} else {
						UserRoleAccessTitleResponse userRoleAccessTitleResponse = new UserRoleAccessTitleResponse();
						PermissionTitle permissionTitle = PermissionTitle.getPermissionTitle(Functionality.USER.groupUrlParam);
						
						userRoleAccessTitleResponse.setPermissionTitle(permissionTitle);
						List<UserRoleAccessRequest> userRoleAccessRequests = new ArrayList<UserRoleAccessRequest>();
						userRoleAccessRequests.add(userRoleAccessRequest);
						userRoleAccessTitleResponse.setUserRoleAccessRequests(userRoleAccessRequests);
						supervisorPermission.add(userRoleAccessTitleResponse);
						
					}
					break;
				case INTERNAL_USER:
					userRoleAccessRequest.setCreateShow(SHOW);
					userRoleAccessRequest.setDeleteShow(HIDE);
					userRoleAccessRequest.setOwnShow(HIDE);
					userRoleAccessRequest.setReadShow(SHOW);
					userRoleAccessRequest.setUpdateShow(SHOW);
					if(titleContain(supervisorPermission, Functionality.INTERNAL_USER.groupUrlParam)) {
						supervisorPermission.stream().filter(o -> o.getPermissionTitle().urlParam.equals(Functionality.INTERNAL_USER.groupUrlParam))
						.findFirst().get().getUserRoleAccessRequests().add(userRoleAccessRequest);
						
					} else {
						UserRoleAccessTitleResponse userRoleAccessTitleResponse = new UserRoleAccessTitleResponse();
						PermissionTitle permissionTitle = PermissionTitle.getPermissionTitle(Functionality.INTERNAL_USER.groupUrlParam);
						
						userRoleAccessTitleResponse.setPermissionTitle(permissionTitle);
						List<UserRoleAccessRequest> userRoleAccessRequests = new ArrayList<UserRoleAccessRequest>();
						userRoleAccessRequests.add(userRoleAccessRequest);
						userRoleAccessTitleResponse.setUserRoleAccessRequests(userRoleAccessRequests);
						supervisorPermission.add(userRoleAccessTitleResponse);
						
					}
					break;
				case TEMPLATE:
					userRoleAccessRequest.setCreateShow(SHOW);
					userRoleAccessRequest.setDeleteShow(SHOW);
					userRoleAccessRequest.setOwnShow(HIDE);
					userRoleAccessRequest.setReadShow(SHOW);
					userRoleAccessRequest.setUpdateShow(SHOW);
					if(titleContain(supervisorPermission, Functionality.TEMPLATE.groupUrlParam)) {
						supervisorPermission.stream().filter(o -> o.getPermissionTitle().urlParam.equals(Functionality.TEMPLATE.groupUrlParam))
						.findFirst().get().getUserRoleAccessRequests().add(userRoleAccessRequest);
						
					} else {
						UserRoleAccessTitleResponse userRoleAccessTitleResponse = new UserRoleAccessTitleResponse();
						PermissionTitle permissionTitle = PermissionTitle.getPermissionTitle(Functionality.TEMPLATE.groupUrlParam);
						
						userRoleAccessTitleResponse.setPermissionTitle(permissionTitle);
						List<UserRoleAccessRequest> userRoleAccessRequests = new ArrayList<UserRoleAccessRequest>();
						userRoleAccessRequests.add(userRoleAccessRequest);
						userRoleAccessTitleResponse.setUserRoleAccessRequests(userRoleAccessRequests);
						supervisorPermission.add(userRoleAccessTitleResponse);
						
					}
					break;
				case CLIENT_ASSIGN_USER:
					userRoleAccessRequest.setCreateShow(SHOW);
					userRoleAccessRequest.setDeleteShow(HIDE);
					userRoleAccessRequest.setOwnShow(HIDE);
					userRoleAccessRequest.setReadShow(SHOW);
					userRoleAccessRequest.setUpdateShow(SHOW);
					if(titleContain(supervisorPermission, Functionality.CLIENT_ASSIGN_USER.groupUrlParam)) {
						supervisorPermission.stream().filter(o -> o.getPermissionTitle().urlParam.equals(Functionality.CLIENT_ASSIGN_USER.groupUrlParam))
						.findFirst().get().getUserRoleAccessRequests().add(userRoleAccessRequest);
						
					} else {
						UserRoleAccessTitleResponse userRoleAccessTitleResponse = new UserRoleAccessTitleResponse();
						PermissionTitle permissionTitle = PermissionTitle.getPermissionTitle(Functionality.CLIENT_ASSIGN_USER.groupUrlParam);
						
						userRoleAccessTitleResponse.setPermissionTitle(permissionTitle);
						List<UserRoleAccessRequest> userRoleAccessRequests = new ArrayList<UserRoleAccessRequest>();
						userRoleAccessRequests.add(userRoleAccessRequest);
						userRoleAccessTitleResponse.setUserRoleAccessRequests(userRoleAccessRequests);
						supervisorPermission.add(userRoleAccessTitleResponse);
						
					}
					break;
				case CLIENT_ACCESS:
					userRoleAccessRequest.setCreateShow(SHOW);
					userRoleAccessRequest.setDeleteShow(HIDE);
					userRoleAccessRequest.setOwnShow(HIDE);
					userRoleAccessRequest.setReadShow(SHOW);
					userRoleAccessRequest.setUpdateShow(SHOW);
					if(titleContain(supervisorPermission, Functionality.CLIENT_ACCESS.groupUrlParam)) {
						supervisorPermission.stream().filter(o -> o.getPermissionTitle().urlParam.equals(Functionality.CLIENT_ACCESS.groupUrlParam))
						.findFirst().get().getUserRoleAccessRequests().add(userRoleAccessRequest);
						
					} else {
						UserRoleAccessTitleResponse userRoleAccessTitleResponse = new UserRoleAccessTitleResponse();
						PermissionTitle permissionTitle = PermissionTitle.getPermissionTitle(Functionality.CLIENT_ACCESS.groupUrlParam);
						
						userRoleAccessTitleResponse.setPermissionTitle(permissionTitle);
						List<UserRoleAccessRequest> userRoleAccessRequests = new ArrayList<UserRoleAccessRequest>();
						userRoleAccessRequests.add(userRoleAccessRequest);
						userRoleAccessTitleResponse.setUserRoleAccessRequests(userRoleAccessRequests);
						supervisorPermission.add(userRoleAccessTitleResponse);
						
					}
					break;
				case VENDOR_ACCESS:
					userRoleAccessRequest.setCreateShow(SHOW);
					userRoleAccessRequest.setDeleteShow(HIDE);
					userRoleAccessRequest.setOwnShow(HIDE);
					userRoleAccessRequest.setReadShow(SHOW);
					userRoleAccessRequest.setUpdateShow(SHOW);
					if(titleContain(supervisorPermission, Functionality.VENDOR_ACCESS.groupUrlParam)) {
						supervisorPermission.stream().filter(o -> o.getPermissionTitle().urlParam.equals(Functionality.VENDOR_ACCESS.groupUrlParam))
						.findFirst().get().getUserRoleAccessRequests().add(userRoleAccessRequest);
						
					} else {
						UserRoleAccessTitleResponse userRoleAccessTitleResponse = new UserRoleAccessTitleResponse();
						PermissionTitle permissionTitle = PermissionTitle.getPermissionTitle(Functionality.VENDOR_ACCESS.groupUrlParam);
						
						userRoleAccessTitleResponse.setPermissionTitle(permissionTitle);
						List<UserRoleAccessRequest> userRoleAccessRequests = new ArrayList<UserRoleAccessRequest>();
						userRoleAccessRequests.add(userRoleAccessRequest);
						userRoleAccessTitleResponse.setUserRoleAccessRequests(userRoleAccessRequests);
						supervisorPermission.add(userRoleAccessTitleResponse);
						
					}
					break;
				case EMPLOYEE_ACCESS:
					userRoleAccessRequest.setCreateShow(SHOW);
					userRoleAccessRequest.setDeleteShow(HIDE);
					userRoleAccessRequest.setOwnShow(HIDE);
					userRoleAccessRequest.setReadShow(SHOW);
					userRoleAccessRequest.setUpdateShow(SHOW);
					if(titleContain(supervisorPermission, Functionality.EMPLOYEE_ACCESS.groupUrlParam)) {
						supervisorPermission.stream().filter(o -> o.getPermissionTitle().urlParam.equals(Functionality.EMPLOYEE_ACCESS.groupUrlParam))
					 	.findFirst().get().getUserRoleAccessRequests().add(userRoleAccessRequest);
					
					} else {
						UserRoleAccessTitleResponse userRoleAccessTitleResponse = new UserRoleAccessTitleResponse();
						PermissionTitle permissionTitle = PermissionTitle.getPermissionTitle(Functionality.EMPLOYEE_ACCESS.groupUrlParam);
						
						userRoleAccessTitleResponse.setPermissionTitle(permissionTitle);
						List<UserRoleAccessRequest> userRoleAccessRequests = new ArrayList<UserRoleAccessRequest>();
						userRoleAccessRequests.add(userRoleAccessRequest);
						userRoleAccessTitleResponse.setUserRoleAccessRequests(userRoleAccessRequests);
						supervisorPermission.add(userRoleAccessTitleResponse);
						
					}
					break;
					
				default:
					break;
				}
			}
			
			
			//For role user
			UserRoleAccess userRoleAccess1 = userRoleAccessRepository.findByRoleAndFunctionality("ROLE_USER", functionality);
			UserRoleAccessRequest userRoleAccessRequest1 = new UserRoleAccessRequest();
			if(userRoleAccess1 != null) {
				
				userRoleAccessRequest1.setCreate(userRoleAccess1.isCreate());
				userRoleAccessRequest1.setDelete(userRoleAccess1.isDelete());
				userRoleAccessRequest1.setFunctionality(userRoleAccess1.getFunctionality());
				userRoleAccessRequest1.setId(userRoleAccess1.getId());
				userRoleAccessRequest1.setOwn(false);
				userRoleAccessRequest1.setRead(userRoleAccess1.isRead());
				userRoleAccessRequest1.setUpdate(userRoleAccess1.isUpdate());
				
				switch (functionality) {
				case ADD_TIME_SHEET:
					userRoleAccessRequest1.setCreateShow(SHOW);
					userRoleAccessRequest1.setDeleteShow(HIDE);
					userRoleAccessRequest1.setOwnShow(HIDE);
					userRoleAccessRequest1.setReadShow(HIDE);
					userRoleAccessRequest1.setUpdateShow(HIDE);
					if(titleContain(userPermission, Functionality.ADD_TIME_SHEET.groupUrlParam)) {
						userPermission.stream().filter(o -> o.getPermissionTitle().urlParam.equals(Functionality.ADD_TIME_SHEET.groupUrlParam))
						 	.findFirst().get().getUserRoleAccessRequests().add(userRoleAccessRequest1);
					
					} else {
						UserRoleAccessTitleResponse userRoleAccessTitleResponse = new UserRoleAccessTitleResponse();
						PermissionTitle permissionTitle = PermissionTitle.getPermissionTitle(Functionality.ADD_TIME_SHEET.groupUrlParam);
						
						userRoleAccessTitleResponse.setPermissionTitle(permissionTitle);
						List<UserRoleAccessRequest> userRoleAccessRequests = new ArrayList<UserRoleAccessRequest>();
						userRoleAccessRequests.add(userRoleAccessRequest1);
						userRoleAccessTitleResponse.setUserRoleAccessRequests(userRoleAccessRequests);
						userPermission.add(userRoleAccessTitleResponse);
						
					}
					break;
				case HOURS_DASHBOARD:
					userRoleAccessRequest1.setCreateShow(HIDE);
					userRoleAccessRequest1.setDeleteShow(HIDE);
					userRoleAccessRequest1.setOwnShow(HIDE);
					userRoleAccessRequest1.setReadShow(SHOW);
					userRoleAccessRequest1.setUpdateShow(HIDE);
					if(titleContain(userPermission, Functionality.HOURS_DASHBOARD.groupUrlParam)) {
						userPermission.stream().filter(o -> o.getPermissionTitle().urlParam.equals(Functionality.HOURS_DASHBOARD.groupUrlParam))
						 	.findFirst().get().getUserRoleAccessRequests().add(userRoleAccessRequest1);
					
					} else {
						UserRoleAccessTitleResponse userRoleAccessTitleResponse = new UserRoleAccessTitleResponse();
						PermissionTitle permissionTitle = PermissionTitle.getPermissionTitle(Functionality.HOURS_DASHBOARD.groupUrlParam);
						
						userRoleAccessTitleResponse.setPermissionTitle(permissionTitle);
						List<UserRoleAccessRequest> userRoleAccessRequests = new ArrayList<UserRoleAccessRequest>();
						userRoleAccessRequests.add(userRoleAccessRequest1);
						userRoleAccessTitleResponse.setUserRoleAccessRequests(userRoleAccessRequests);
						userPermission.add(userRoleAccessTitleResponse);
						
					}
					
					break;
				case REPORT_TIME_SHEET:
					userRoleAccessRequest1.setCreateShow(HIDE);
					userRoleAccessRequest1.setDeleteShow(HIDE);
					userRoleAccessRequest1.setOwnShow(HIDE);
					userRoleAccessRequest1.setReadShow(SHOW);
					userRoleAccessRequest1.setUpdateShow(HIDE);
					if(titleContain(userPermission, Functionality.REPORT_TIME_SHEET.groupUrlParam)) {
						userPermission.stream().filter(o -> o.getPermissionTitle().urlParam.equals(Functionality.REPORT_TIME_SHEET.groupUrlParam))
						 	.findFirst().get().getUserRoleAccessRequests().add(userRoleAccessRequest1);
					
					} else {
						UserRoleAccessTitleResponse userRoleAccessTitleResponse = new UserRoleAccessTitleResponse();
						PermissionTitle permissionTitle = PermissionTitle.getPermissionTitle(Functionality.REPORT_TIME_SHEET.groupUrlParam);
						
						userRoleAccessTitleResponse.setPermissionTitle(permissionTitle);
						List<UserRoleAccessRequest> userRoleAccessRequests = new ArrayList<UserRoleAccessRequest>();
						userRoleAccessRequests.add(userRoleAccessRequest1);
						userRoleAccessTitleResponse.setUserRoleAccessRequests(userRoleAccessRequests);
						userPermission.add(userRoleAccessTitleResponse);
						
					}
					
					break;
				case SUBMITTED_TIMESHEET:
					userRoleAccessRequest1.setCreateShow(HIDE);
					userRoleAccessRequest1.setDeleteShow(HIDE);
					userRoleAccessRequest1.setOwnShow(HIDE);
					userRoleAccessRequest1.setReadShow(SHOW);
					userRoleAccessRequest1.setUpdateShow(HIDE);
					if(titleContain(userPermission, Functionality.SUBMITTED_TIMESHEET.groupUrlParam)) {
						userPermission.stream().filter(o -> o.getPermissionTitle().urlParam.equals(Functionality.SUBMITTED_TIMESHEET.groupUrlParam))
						 	.findFirst().get().getUserRoleAccessRequests().add(userRoleAccessRequest1);
					
					} else {
						UserRoleAccessTitleResponse userRoleAccessTitleResponse = new UserRoleAccessTitleResponse();
						PermissionTitle permissionTitle = PermissionTitle.getPermissionTitle(Functionality.SUBMITTED_TIMESHEET.groupUrlParam);
						
						userRoleAccessTitleResponse.setPermissionTitle(permissionTitle);
						List<UserRoleAccessRequest> userRoleAccessRequests = new ArrayList<UserRoleAccessRequest>();
						userRoleAccessRequests.add(userRoleAccessRequest1);
						userRoleAccessTitleResponse.setUserRoleAccessRequests(userRoleAccessRequests);
						userPermission.add(userRoleAccessTitleResponse);
						
					}
					
					break;
	
				default:
					break;
				}
			}
			
			
		}
		
		rolePermissionRequest.setSupervisorPermission(supervisorPermission);
		rolePermissionRequest.setUserPermission(userPermission);
		
		return rolePermissionRequest;
	}

	private boolean titleContain(List<UserRoleAccessTitleResponse> supervisorPermission, String urlParam) {
		PermissionTitle permissionTitle = PermissionTitle.getPermissionTitle(urlParam);
		boolean b = supervisorPermission.stream().filter(o -> o.getPermissionTitle().equals(permissionTitle)).findFirst().isPresent();
		return b;
	}

	@Override
	public void setRolePermission(Integer id, Functionality functionality, boolean create, boolean delete, boolean read,
			boolean update) {
		
		UserRoleAccess userRoleAccess = userRoleAccessRepository.findById(id).orElse(null);
		userRoleAccess.setCreate(create);
		userRoleAccess.setDelete(delete);
		userRoleAccess.setRead(read);
		userRoleAccess.setUpdate(update);
		
		userRoleAccessRepository.save(userRoleAccess);
	}

	@Override
	public List<CustomerResponse> getCustomer(ClientType clientType) {
		List<Client> clients = clientRepository.findByType(clientType);
		List<CustomerResponse> clientList = new ArrayList<>();
		for(Client client:clients) {
			CustomerResponse customerResponse = new CustomerResponse();
			customerResponse.setAddress(client.getAddress());
			customerResponse.setClientName(client.getClientName());
			customerResponse.setId(client.getId());
			customerResponse.setPhone(client.getPhone());
			customerResponse.setRemark(client.getRemark());
			customerResponse.setType(client.getType());
			customerResponse.setZipCode(client.getZipCode());
			clientList.add(customerResponse);
		}
		
		return clientList;
	}


}
