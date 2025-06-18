package com.aim.service;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.aim.entity.Activity;
import com.aim.entity.Client;
import com.aim.entity.Company;
import com.aim.entity.HourLog;
import com.aim.entity.HourLogFile;
import com.aim.entity.HourLogFilePath;
import com.aim.entity.InternalUser;
import com.aim.entity.PermissionPlan;
import com.aim.entity.Schedular;
import com.aim.entity.TimeSheetSubmission;
import com.aim.entity.User;
import com.aim.entity.UserCompany;
import com.aim.entity.UserDetail;
import com.aim.entity.UserFile;
import com.aim.enums.ActivityType;
import com.aim.enums.AdminTimeSheetAction;
import com.aim.enums.HourLogStatus;
import com.aim.enums.InvoiceTo;
import com.aim.enums.Month;
import com.aim.enums.RateCountOn;
import com.aim.enums.RateType;
import com.aim.enums.TimeSheetPeriod;
import com.aim.enums.W2OrC2cType;
import com.aim.model.CompanySaveInMasterThread;
import com.aim.model.GetCompanyByName;
import com.aim.model.GetUserAllCompany;
import com.aim.model.GetUserThread;
import com.aim.model.UserEditActiveInSubDb;
import com.aim.model.UserPasswordChangeThread;
import com.aim.model.UserSaveThreadInMasterDb;
//import com.aim.model.GetUserAllCompany;
//import com.aim.model.GetUserThread;
//import com.aim.model.UserEditActiveInSubDb;
//import com.aim.model.UserPasswordChangeThread;
//import com.aim.model.UserSaveThreadInMasterDb;
import com.aim.repository.ActivityRepository;
import com.aim.repository.ClientRepository;
import com.aim.repository.CompanyRepository;
import com.aim.repository.HourLogFilePathRepository;
import com.aim.repository.HourLogFileRepository;
import com.aim.repository.HoursLogRepository;
import com.aim.repository.InternalUserRepository;
import com.aim.repository.SchedularRepository;
import com.aim.repository.TimeSheetSubmissionRepository;
import com.aim.repository.UserCompanyRepository;
import com.aim.repository.UserDetailsRepository;
import com.aim.repository.UserFileRepository;
import com.aim.repository.UserRepository;
import com.aim.request.CalculationCountRequest;
import com.aim.request.CompanyRequest;
import com.aim.request.UserDetailRequest;
import com.aim.request.UserTimeSheetSubmitRequest;
import com.aim.response.AddUserTimeSheet;
import com.aim.response.CalendarResponse;
import com.aim.response.DefaultCalendarResponse;
import com.aim.response.HourLogResponse;
import com.aim.response.MonthHoursLogResponse;
import com.aim.response.PendingHourLogFile;
import com.aim.response.SchedularResponse;
import com.aim.response.UserTimeSheetDate;
import com.aim.service.email.EmailSenderService;
import com.aim.utils.Utils;
import com.mysql.cj.Session;

@Service
public class UserServiceImpl implements UserService {
	
	final static Logger logger = Logger.getLogger(UserServiceImpl.class);
	
	@Value("${file.path}")
	private String FILE_PATH;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserDetailsRepository userDetailsRepository;
	
	@Autowired
	private HoursLogRepository hoursLogRepository;
	
	@Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private EmailSenderService emailSenderService;
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private UserFileRepository userFileRepository;
	
	@Autowired
	private TimeSheetSubmissionRepository timeSheetSubmissionRepository ;
	
	@Autowired
	private HourLogFileRepository hourLogFileRepository ;
	
	@Autowired
	private InternalUserRepository internalUserRepository ;
	
	@Autowired
	private ActivityRepository activityRepository ;
	
	@Autowired
	private SchedularRepository schedularRepository ;
	
	@Autowired
	private HourLogFilePathRepository hourLogFilePathRepository ;
	
	@Autowired
	private CompanyRepository companyRepository;
	
	@Autowired
	private UserCompanyRepository userCompanyRepository;
	
	@Autowired
	private TemplateEngine templateEngine;
	
	@Autowired
	private ClientRepository clientRepository;
	
	@Value("${timesheet.server.url}")
	private String TIMESHEET_SERVER_URL;
	
	private final Boolean APPROVED = true;
	private final Boolean NOT_APPROVED = false;
	private final Boolean REJECTED= true;
	private final Boolean NOT_REJECTED= false;
	private final Integer COMPANY_ID= 1;
	
	@Override
	public String getFinalFileFolder(String name, int count) {
		
		name = name.trim();
		
		// Convert name to url slug
		String urlSlug = Utils.toSlug(name);
		
		if (userRepository.findByFileFolder(urlSlug) == null) {
			return urlSlug;
		} else {
			return getFinalFileFolder(urlSlug + (count + 1), (count + 1));
		}
	}
	
	private String getUserDetailsFinalFileFolder(String name, int count) {

		name = name.trim();

		// Convert name to url slug
		String urlSlug = Utils.toSlug(name);

		if (userDetailsRepository.findByFileFolder(urlSlug) == null) {
			return urlSlug;
		} else {
			return getFinalFileFolder(urlSlug + (count + 1), (count + 1));
		}
	}
	
	@Override
	public void saveUser(User user) {
		String uuid;
		User loginUser = (User) request.getSession().getAttribute("user");
		Company company = getCompany();
		if(user.getId() != 0) { 
			
			User user2 = userRepository.findById(user.getId());
			uuid = user2.getUuid();
			
			user2.setEmail(user.getEmail());
			user2.setFirstName(user.getFirstName());
			user2.setLastName(user.getLastName());
			user2.setPhone(user.getPhone());
			user2.setRole(user.getRole());
			user2.setWorkEmail(user.getWorkEmail());
			user2.setCompany(company);
			userRepository.save(user2);
			
			UserCompany userCompany = new UserCompany();
			User userClone = SerializationUtils.clone(user2);
			userClone.setCompany(company);
			ThreadPoolExecutor executor1 = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
			UserSaveThreadInMasterDb userSaveThread = new UserSaveThreadInMasterDb(userRepository, companyRepository,userCompanyRepository, userClone);
			Future<UserCompany> result = executor1.submit(userSaveThread);
			
			try {
				userCompany = result.get();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} else { 
			
			uuid = UUID.randomUUID().toString();
			if(user.getPassword() == null) {
				user.setPassword(uuid);
			}
			if(user.getRole() == null)
				user.setRole("ROLE_USER");
			else
				user.setRole(user.getRole());
//			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			if(loginUser != null && loginUser.getRole().equals("ROLE_ADMIN"))
				user.setActive(1);
			user.setUuid(uuid);
			user.setCompany(company);
			
			user.setFileFolder(getFinalFileFolder(user.getFirstName(), 0));
			userRepository.save(user);
			
			UserCompany userCompany = new UserCompany();
			User userClone = SerializationUtils.clone(user);
			ThreadPoolExecutor executor1 = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
			UserSaveThreadInMasterDb userSaveThread = new UserSaveThreadInMasterDb(userRepository, companyRepository,userCompanyRepository, userClone);
			Future<UserCompany> result = executor1.submit(userSaveThread);
			
			try {
				userCompany = result.get();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			String verifyEmailLink = userCompany.getUser().getUuid();
			String companyKey = userCompany.getCompany().getUuid();
			emailSenderService.sendSuccessEmailWithPasswordLink(user.getEmail(), user.getFirstName() + " " + user.getLastName() , verifyEmailLink, companyKey);
	        List<User> users = userRepository.findByRoleOrRole("ROLE_ADMIN", "ROLE_SUPERVISOR");
			for(User user1: users) {
				emailSenderService.sendMailToAdminUserSingup(user1.getEmail(), user.getFirstName() + " " + user.getLastName() ); 
			}
		}
		logger.info("Register User : " + user.getEmail() + ", UUID :" + uuid);
	}
	
	@Override
	public void sendActivation(Integer userId) {
		User user = userRepository.findOne(userId);
		
		ThreadPoolExecutor executor1 = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
		GetUserThread g = new GetUserThread(userRepository, user.getEmail(),null);
		Future<User> result = executor1.submit(g);
		User user3 = null;
		
		try {
			user3 = result.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String verifyEmailLink = user3.getUuid();
		String companyKey = user3.getCompany().getUuid();
		
		emailSenderService.sendSuccessEmailWithPasswordLink(user.getEmail(), user.getFirstName() + " " + user.getLastName() , verifyEmailLink, companyKey);
	}

	@Override
	public User findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public Boolean isValidActivationKey(String uId) throws Exception {

		User user = userRepository.findByUuid(uId);
		if(user != null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Boolean resetPassword(String uId, String password) throws Exception {
		
		User user = userRepository.findByUuid(uId);
		if(user != null) {
			ThreadPoolExecutor executor1 = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
			GetUserAllCompany g = new GetUserAllCompany(userCompanyRepository, user.getEmail());
			Future<List<String>> result = executor1.submit(g);
			List<String> dbCurrent = result.get();
			
			ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
			for(String db : dbCurrent) {
				UserPasswordChangeThread userPasswordChangeThread = new UserPasswordChangeThread(user.getEmail(), bCryptPasswordEncoder.encode(password), db, userRepository);
//				Thread t1 = new Thread(userPasswordChangeThread);
				executor.submit(userPasswordChangeThread);
			}
			
			UserPasswordChangeThread userPasswordChangeThread = new UserPasswordChangeThread(user.getEmail(), bCryptPasswordEncoder.encode(password), null, userRepository);
			Thread t2 = new Thread(userPasswordChangeThread);
			t2.start();
			return true;
		}
		return false;
	}

	@Override
	public List<User> findByRole(String role) {
		return userRepository.findByRole(role);
	}

	/**
	 * save user details
	 */
	@Override
	public void saveUserDetails(UserDetail userDetails) {
		UserDetail userDetail2 = new UserDetail();
		
		boolean isNew = false;
		if(userDetails.getUserDetailId() == null){
			isNew = true;
		} else {
			userDetail2 = userDetailsRepository.findByUserDetailId(userDetails.getUserDetailId());
		}
		
		userDetail2.setAccountManager(userDetails.getAccountManager());
		userDetail2.setAccountManagerName(userDetails.getAccountManagerName());
		userDetail2.setaCMCommission(userDetails.getaCMCommission());
		userDetail2.setaCMRateType(userDetails.getaCMRateType());
		userDetail2.setaCMRecurssive(userDetails.isaCMRecurssive());
		userDetail2.setaCMRateCountOn(userDetails.getaCMRateCountOn());
		userDetail2.setaCMRecurssiveMonth(userDetails.getaCMRecurssiveMonth());
		userDetail2.setInvoiceTo(userDetails.getInvoiceTo());
		
		userDetail2.setAddress(userDetails.getAddress());
		userDetail2.setBusinessDevelopmentManager(userDetails.getBusinessDevelopmentManager());
		userDetail2.setbDMCommission(userDetails.getbDMCommission());
		userDetail2.setBusinessDevelopmentManagerName(userDetails.getBusinessDevelopmentManagerName());
		userDetail2.setbDMRateType(userDetails.getbDMRateType());
		userDetail2.setbDMRecurssive(userDetails.isbDMRecurssive());
		userDetail2.setbDMRateCountOn(userDetails.getbDMRateCountOn());
		userDetail2.setbDMRecurssiveMonth(userDetails.getbDMRecurssiveMonth());
		
		
		userDetail2.setC2cOrother(userDetails.getC2cOrother() == null ? 0.0f: userDetails.getC2cOrother());
		userDetail2.setC2cOrotherRateType(userDetails.getC2cOrotherRateType());
		userDetail2.setC2cOrotherRecurssive(userDetails.isC2cOrotherRecurssive());
		userDetail2.setC2cOrotherRecurssiveMonth(userDetails.getC2cOrotherRecurssiveMonth());
		
		
		userDetail2.setClientName(userDetails.getClientName());
		userDetail2.setClientRate(userDetails.getClientRate() == null ? 0.0f:userDetails.getClientRate());
		userDetail2.setConsultantRate(userDetails.getConsultantRate() == null ? 0.0f : userDetails.getConsultantRate());
		userDetail2.setEmployerEmail(userDetails.getEmployerEmail());
		userDetail2.setEmployerName(userDetails.getEmployerName());
		userDetail2.setEmployerPhone(userDetails.getEmployerPhone());
		userDetail2.setEndDate(userDetails.getEndDate());
		userDetail2.setPtax(userDetails.getPtax() == null ? 0.0f :  userDetails.getPtax());
		
		userDetail2.setRecruiter(userDetails.getRecruiter());
		userDetail2.setRecCommission(userDetails.getRecCommission());
		userDetail2.setRecruiterName(userDetails.getRecruiterName());
		userDetail2.setRecRateType(userDetails.getRecRateType());
		userDetail2.setRecRecurssive(userDetails.isRecRecurssive());
		userDetail2.setRecRateCountOn(userDetails.getRecRateCountOn());
		userDetail2.setRecRecurssiveMonth(userDetails.getRecRecurssiveMonth());
		
		
		userDetail2.setStartDate(userDetails.getStartDate());
		userDetail2.setTimeSheetPeriod(userDetails.getTimeSheetPeriod());
		userDetail2.setUser(userDetails.getUser());
		userDetail2.setUserDetailId(userDetails.getUserDetailId());
		userDetail2.setVendorName(userDetails.getVendorName());
		
		userDetail2.setClient(userDetails.getClient());
		userDetail2.setEmployer(userDetails.getEmployer());
		userDetail2.setVendor(userDetails.getVendor());
		userDetail2.setW2C2cType(userDetails.getW2C2cType());
		userDetail2.setW2(userDetails.getW2() == null ? 0.0f : userDetails.getW2());
		if(isNew) {
			
			userDetail2.setFileFolder(getUserDetailsFinalFileFolder(userDetail2.getClientName(), 0));
			List<UserDetail> userDetaillist = userDetailsRepository.findByUser(userDetails.getUser());
			userDetaillist.forEach(list -> list.setActive(false));
			userDetailsRepository.save(userDetaillist);
				
			userDetail2.setActive(true);
		} else {
			userDetail2.setActive(userDetail2.isActive());
		}
		userDetailsRepository.save(userDetail2);
		
		if(isNew) {
			User user = userRepository.findOne(userDetails.getUser().getId());
			user.setClientActiveId(userDetail2.getUserDetailId());
			userRepository.save(user);
		}
		addActivity("Add user details", ActivityType.SET_USER_DETAILS.toString() , userDetail2);
	}

	/**
	 * get hour log 
	 */
	@Override
	public LinkedHashMap<String, Map<String, Double>> getHourLog(Integer userDetailId, Integer year,Integer month) {
		
		List<HourLogResponse> hourLogResponses = new ArrayList<HourLogResponse>();
		
		if(month == null) {
			hourLogResponses = hoursLogRepository.getHourLog(userDetailId, year);
		} else {
			hourLogResponses = hoursLogRepository.getHourLogByMonth(userDetailId, year,month);
		}
		
		UserDetail userDetail = userDetailsRepository.findByUserDetailId(userDetailId);
		CalculationCountRequest calculationCountRequest = new CalculationCountRequest();
		
		if(hourLogResponses.isEmpty()) {
			hourLogResponses.add(new HourLogResponse(1, 0.0, 0.0,0.0));
		}
		
		LinkedHashMap<String, Map<String, Double>> map = new LinkedHashMap<String, Map<String, Double>>(); 
		Map<String, Double> jan = new HashMap<>();
		Map<String, Double> fab = new HashMap<>();
		Map<String, Double> mar = new HashMap<>();
		Map<String, Double> apr = new HashMap<>();
		Map<String, Double> may = new HashMap<>();
		Map<String, Double> jun = new HashMap<>();
		Map<String, Double> jul = new HashMap<>();
		Map<String, Double> aug = new HashMap<>();
		Map<String, Double> sep = new HashMap<>();
		Map<String, Double> oct = new HashMap<>();
		Map<String, Double> nov = new HashMap<>();
		Map<String, Double> dec = new HashMap<>();
		
		for(HourLogResponse hourLogResponse : hourLogResponses) {
			switch (hourLogResponse.getMonth()) {
				case 1:
					
					calculationCountRequest = getCalculationCountRequest(hourLogResponse,userDetail, 1);
							
					jan.put("Daily", calculationCountRequest.getDailyHours());
					jan.put("Extra", calculationCountRequest.getExtraHour());
					jan.put("Vacation", calculationCountRequest.getVacationHours());
					jan.put("Total", calculationCountRequest.getTotal());
					jan.put("ConsultantRate", calculationCountRequest.getConsultantRate());
					jan.put("W2Ptax",calculationCountRequest.getW2Ptax());
					jan.put("C2C",calculationCountRequest.getC2c());
					jan.put("G.Margin",calculationCountRequest.getgMargin());
					jan.put("BDMComm",calculationCountRequest.getbDMComm());
					jan.put("ACMComm",calculationCountRequest.getaCMComm());
					jan.put("RecComm",calculationCountRequest.getRecComm());
					jan.put("N.Margin",calculationCountRequest.getnMargin());
					jan.put("revenue",calculationCountRequest.getRevenue());
					
					double expense = calculationCountRequest.getConsultantRate() + calculationCountRequest.getW2Ptax() + calculationCountRequest.getC2c();
					jan.put("Expense",expense);
					
					double commission = calculationCountRequest.getbDMComm() + calculationCountRequest.getaCMComm()+ calculationCountRequest.getRecComm();
					jan.put("Commission",commission);
					break;
					
				case 2:
					
					calculationCountRequest = getCalculationCountRequest(hourLogResponse,userDetail, 2);
					
					fab.put("Daily", calculationCountRequest.getDailyHours());
					fab.put("Extra", calculationCountRequest.getExtraHour());
					fab.put("Vacation", calculationCountRequest.getVacationHours());
					fab.put("Total", calculationCountRequest.getTotal());
					fab.put("ConsultantRate", calculationCountRequest.getConsultantRate());
					fab.put("W2Ptax",calculationCountRequest.getW2Ptax());
					fab.put("C2C",calculationCountRequest.getC2c());
					fab.put("G.Margin",calculationCountRequest.getgMargin());
					fab.put("BDMComm",calculationCountRequest.getbDMComm());
					fab.put("ACMComm",calculationCountRequest.getaCMComm());
					fab.put("RecComm",calculationCountRequest.getRecComm());
					fab.put("N.Margin",calculationCountRequest.getnMargin());
					fab.put("revenue",calculationCountRequest.getRevenue());
					
					double expenseFab = calculationCountRequest.getConsultantRate() + calculationCountRequest.getW2Ptax() + calculationCountRequest.getC2c();
					fab.put("Expense",expenseFab);
					
					double commissionFab = calculationCountRequest.getbDMComm() + calculationCountRequest.getaCMComm()+ calculationCountRequest.getRecComm();
					fab.put("Commission",commissionFab);
					break;
					
				case 3:
					calculationCountRequest = getCalculationCountRequest(hourLogResponse,userDetail, 3);
					
					mar.put("Daily", calculationCountRequest.getDailyHours());
					mar.put("Extra", calculationCountRequest.getExtraHour());
					mar.put("Vacation", calculationCountRequest.getVacationHours());
					mar.put("Total", calculationCountRequest.getTotal());
					mar.put("ConsultantRate", calculationCountRequest.getConsultantRate());
					mar.put("W2Ptax",calculationCountRequest.getW2Ptax());
					mar.put("C2C",calculationCountRequest.getC2c());
					mar.put("G.Margin",calculationCountRequest.getgMargin());
					mar.put("BDMComm",calculationCountRequest.getbDMComm());
					mar.put("ACMComm",calculationCountRequest.getaCMComm());
					mar.put("RecComm",calculationCountRequest.getRecComm());
					mar.put("N.Margin",calculationCountRequest.getnMargin());
					mar.put("revenue",calculationCountRequest.getRevenue());
					
					double expenseMar= calculationCountRequest.getConsultantRate() + calculationCountRequest.getW2Ptax() + calculationCountRequest.getC2c();
					mar.put("Expense",expenseMar);
					
					double commissionMar= calculationCountRequest.getbDMComm() + calculationCountRequest.getaCMComm()+ calculationCountRequest.getRecComm();
					mar.put("Commission",commissionMar);
					break;
					
				case 4:
					
					calculationCountRequest = getCalculationCountRequest(hourLogResponse,userDetail, 4);
					
					apr.put("Daily", calculationCountRequest.getDailyHours());
					apr.put("Extra", calculationCountRequest.getExtraHour());
					apr.put("Vacation", calculationCountRequest.getVacationHours());
					apr.put("Total", calculationCountRequest.getTotal());
					apr.put("ConsultantRate", calculationCountRequest.getConsultantRate());
					apr.put("W2Ptax",calculationCountRequest.getW2Ptax());
					apr.put("C2C",calculationCountRequest.getC2c());
					apr.put("G.Margin",calculationCountRequest.getgMargin());
					apr.put("BDMComm",calculationCountRequest.getbDMComm());
					apr.put("ACMComm",calculationCountRequest.getaCMComm());
					apr.put("RecComm",calculationCountRequest.getRecComm());
					apr.put("N.Margin",calculationCountRequest.getnMargin());
					apr.put("revenue",calculationCountRequest.getRevenue());
					
					double expenseApr = calculationCountRequest.getConsultantRate() + calculationCountRequest.getW2Ptax() + calculationCountRequest.getC2c();
					apr.put("Expense",expenseApr);
					
					double commissionApr = calculationCountRequest.getbDMComm() + calculationCountRequest.getaCMComm()+ calculationCountRequest.getRecComm();
					apr.put("Commission",commissionApr);
					break;
					
				case 5:
					
					calculationCountRequest = getCalculationCountRequest(hourLogResponse,userDetail, 5);
					
					may.put("Daily", calculationCountRequest.getDailyHours());
					may.put("Extra", calculationCountRequest.getExtraHour());
					may.put("Vacation", calculationCountRequest.getVacationHours());
					may.put("Total", calculationCountRequest.getTotal());
					may.put("ConsultantRate", calculationCountRequest.getConsultantRate());
					may.put("W2Ptax",calculationCountRequest.getW2Ptax());
					may.put("C2C",calculationCountRequest.getC2c());
					may.put("G.Margin",calculationCountRequest.getgMargin());
					may.put("BDMComm",calculationCountRequest.getbDMComm());
					may.put("ACMComm",calculationCountRequest.getaCMComm());
					may.put("RecComm",calculationCountRequest.getRecComm());
					may.put("N.Margin",calculationCountRequest.getnMargin());
					may.put("revenue",calculationCountRequest.getRevenue());
					
					double expenseMay = calculationCountRequest.getConsultantRate() + calculationCountRequest.getW2Ptax() + calculationCountRequest.getC2c();
					may.put("Expense",expenseMay);
					
					double commissionMay = calculationCountRequest.getbDMComm() + calculationCountRequest.getaCMComm()+ calculationCountRequest.getRecComm();
					may.put("Commission",commissionMay);
					break;
					
				case 6:
					
					calculationCountRequest = getCalculationCountRequest(hourLogResponse,userDetail, 6);
					
					jun.put("Daily", calculationCountRequest.getDailyHours());
					jun.put("Extra", calculationCountRequest.getExtraHour());
					jun.put("Vacation", calculationCountRequest.getVacationHours());
					jun.put("Total", calculationCountRequest.getTotal());
					jun.put("ConsultantRate", calculationCountRequest.getConsultantRate());
					jun.put("W2Ptax",calculationCountRequest.getW2Ptax());
					jun.put("C2C",calculationCountRequest.getC2c());
					jun.put("G.Margin",calculationCountRequest.getgMargin());
					jun.put("BDMComm",calculationCountRequest.getbDMComm());
					jun.put("ACMComm",calculationCountRequest.getaCMComm());
					jun.put("RecComm",calculationCountRequest.getRecComm());
					jun.put("N.Margin",calculationCountRequest.getnMargin());
					jun.put("revenue",calculationCountRequest.getRevenue());
					
					double expenseJun = calculationCountRequest.getConsultantRate() + calculationCountRequest.getW2Ptax() + calculationCountRequest.getC2c();
					jun.put("Expense",expenseJun);
					
					double commissionJun = calculationCountRequest.getbDMComm() + calculationCountRequest.getaCMComm()+ calculationCountRequest.getRecComm();
					jun.put("Commission",commissionJun);
					break;
					
				case 7:
					
					calculationCountRequest = getCalculationCountRequest(hourLogResponse,userDetail, 7);
					
					jul.put("Daily", calculationCountRequest.getDailyHours());
					jul.put("Extra", calculationCountRequest.getExtraHour());
					jul.put("Vacation", calculationCountRequest.getVacationHours());
					jul.put("Total", calculationCountRequest.getTotal());
					jul.put("ConsultantRate", calculationCountRequest.getConsultantRate());
					jul.put("W2Ptax",calculationCountRequest.getW2Ptax());
					jul.put("C2C",calculationCountRequest.getC2c());
					jul.put("G.Margin",calculationCountRequest.getgMargin());
					jul.put("BDMComm",calculationCountRequest.getbDMComm());
					jul.put("ACMComm",calculationCountRequest.getaCMComm());
					jul.put("RecComm",calculationCountRequest.getRecComm());
					jul.put("N.Margin",calculationCountRequest.getnMargin());
					jul.put("revenue",calculationCountRequest.getRevenue());
					
					double expenseJul = calculationCountRequest.getConsultantRate() + calculationCountRequest.getW2Ptax() + calculationCountRequest.getC2c();
					jul.put("Expense",expenseJul);
					
					double commissionJul = calculationCountRequest.getbDMComm() + calculationCountRequest.getaCMComm()+ calculationCountRequest.getRecComm();
					jul.put("Commission",commissionJul);
					break;
					
				case 8:
					
					calculationCountRequest = getCalculationCountRequest(hourLogResponse,userDetail, 8);
					
					aug.put("Daily", calculationCountRequest.getDailyHours());
					aug.put("Extra", calculationCountRequest.getExtraHour());
					aug.put("Vacation", calculationCountRequest.getVacationHours());
					aug.put("Total", calculationCountRequest.getTotal());
					aug.put("ConsultantRate", calculationCountRequest.getConsultantRate());
					aug.put("W2Ptax",calculationCountRequest.getW2Ptax());
					aug.put("C2C",calculationCountRequest.getC2c());
					aug.put("G.Margin",calculationCountRequest.getgMargin());
					aug.put("BDMComm",calculationCountRequest.getbDMComm());
					aug.put("ACMComm",calculationCountRequest.getaCMComm());
					aug.put("RecComm",calculationCountRequest.getRecComm());
					aug.put("N.Margin",calculationCountRequest.getnMargin());
					aug.put("revenue",calculationCountRequest.getRevenue());
					
					double expenseAug = calculationCountRequest.getConsultantRate() + calculationCountRequest.getW2Ptax() + calculationCountRequest.getC2c();
					aug.put("Expense",expenseAug);
					
					double commissionAug = calculationCountRequest.getbDMComm() + calculationCountRequest.getaCMComm()+ calculationCountRequest.getRecComm();
					aug.put("Commission",commissionAug);
					break;
					
				case 9:
					
					calculationCountRequest = getCalculationCountRequest(hourLogResponse,userDetail, 9);

					sep.put("Daily", calculationCountRequest.getDailyHours());
					sep.put("Extra", calculationCountRequest.getExtraHour());
					sep.put("Vacation", calculationCountRequest.getVacationHours());
					sep.put("Total", calculationCountRequest.getTotal());
					sep.put("ConsultantRate", calculationCountRequest.getConsultantRate());
					sep.put("W2Ptax",calculationCountRequest.getW2Ptax());
					sep.put("C2C",calculationCountRequest.getC2c());
					sep.put("G.Margin",calculationCountRequest.getgMargin());
					sep.put("BDMComm",calculationCountRequest.getbDMComm());
					sep.put("ACMComm",calculationCountRequest.getaCMComm());
					sep.put("RecComm",calculationCountRequest.getRecComm());
					sep.put("N.Margin",calculationCountRequest.getnMargin());
					sep.put("revenue",calculationCountRequest.getRevenue());
					
					double expenseSep = calculationCountRequest.getConsultantRate() + calculationCountRequest.getW2Ptax() + calculationCountRequest.getC2c();
					sep.put("Expense",expenseSep);
					
					double commissionSep = calculationCountRequest.getbDMComm() + calculationCountRequest.getaCMComm()+ calculationCountRequest.getRecComm();
					sep.put("Commission",commissionSep);
					break;
					
				case 10:
					
					calculationCountRequest = getCalculationCountRequest(hourLogResponse,userDetail, 10);
					
					oct.put("Daily", calculationCountRequest.getDailyHours());
					oct.put("Extra", calculationCountRequest.getExtraHour());
					oct.put("Vacation", calculationCountRequest.getVacationHours());
					oct.put("Total", calculationCountRequest.getTotal());
					oct.put("ConsultantRate", calculationCountRequest.getConsultantRate());
					oct.put("W2Ptax",calculationCountRequest.getW2Ptax());
					oct.put("C2C",calculationCountRequest.getC2c());
					oct.put("G.Margin",calculationCountRequest.getgMargin());
					oct.put("BDMComm",calculationCountRequest.getbDMComm());
					oct.put("ACMComm",calculationCountRequest.getaCMComm());
					oct.put("RecComm",calculationCountRequest.getRecComm());
					oct.put("N.Margin",calculationCountRequest.getnMargin());
					oct.put("revenue",calculationCountRequest.getRevenue());
					
					double expenseOct = calculationCountRequest.getConsultantRate() + calculationCountRequest.getW2Ptax() + calculationCountRequest.getC2c();
					oct.put("Expense",expenseOct);
					
					double commissionOct = calculationCountRequest.getbDMComm() + calculationCountRequest.getaCMComm()+ calculationCountRequest.getRecComm();
					oct.put("Commission",commissionOct);
					break;
					
				case 11:
					
					calculationCountRequest = getCalculationCountRequest(hourLogResponse,userDetail, 11);
					
					nov.put("Daily", calculationCountRequest.getDailyHours());
					nov.put("Extra", calculationCountRequest.getExtraHour());
					nov.put("Vacation", calculationCountRequest.getVacationHours());
					nov.put("Total", calculationCountRequest.getTotal());
					nov.put("ConsultantRate", calculationCountRequest.getConsultantRate());
					nov.put("W2Ptax",calculationCountRequest.getW2Ptax());
					nov.put("C2C",calculationCountRequest.getC2c());
					nov.put("G.Margin",calculationCountRequest.getgMargin());
					nov.put("BDMComm",calculationCountRequest.getbDMComm());
					nov.put("ACMComm",calculationCountRequest.getaCMComm());
					nov.put("RecComm",calculationCountRequest.getRecComm());
					nov.put("N.Margin",calculationCountRequest.getnMargin());
					nov.put("revenue",calculationCountRequest.getRevenue());
					
					double expenseNov= calculationCountRequest.getConsultantRate() + calculationCountRequest.getW2Ptax() + calculationCountRequest.getC2c();
					nov.put("Expense",expenseNov);
					
					double commissionNov= calculationCountRequest.getbDMComm() + calculationCountRequest.getaCMComm()+ calculationCountRequest.getRecComm();
					nov.put("Commission",commissionNov);
					break;
					
				case 12:
					
					calculationCountRequest = getCalculationCountRequest(hourLogResponse,userDetail, 12);
					
					dec.put("Daily", calculationCountRequest.getDailyHours());
					dec.put("Extra", calculationCountRequest.getExtraHour());
					dec.put("Vacation", calculationCountRequest.getVacationHours());
					dec.put("Total", calculationCountRequest.getTotal());
					dec.put("ConsultantRate", calculationCountRequest.getConsultantRate());
					dec.put("W2Ptax",calculationCountRequest.getW2Ptax());
					dec.put("C2C",calculationCountRequest.getC2c());
					dec.put("G.Margin",calculationCountRequest.getgMargin());
					dec.put("BDMComm",calculationCountRequest.getbDMComm());
					dec.put("ACMComm",calculationCountRequest.getaCMComm());
					dec.put("RecComm",calculationCountRequest.getRecComm());
					dec.put("N.Margin",calculationCountRequest.getnMargin());
					dec.put("revenue",calculationCountRequest.getRevenue());
					
					double expenseDec= calculationCountRequest.getConsultantRate() + calculationCountRequest.getW2Ptax() + calculationCountRequest.getC2c();
					dec.put("Expense",expenseDec);
					
					double commissionDec= calculationCountRequest.getbDMComm() + calculationCountRequest.getaCMComm()+ calculationCountRequest.getRecComm();
					dec.put("Commission",commissionDec);
					break;

				default:
					break;
				}
			
			map.put("January", jan);
			map.put("February", fab);
			map.put("March", mar);
			map.put("April", apr);
			map.put("May", may);
			map.put("June", jun);
			map.put("July", jul);
			map.put("August", aug);
			map.put("September", sep);
			map.put("October", oct);
			map.put("November", nov);
			map.put("December", dec);
		}
		
		// for sum of all month value 
		Map<String, Double> sum = new HashMap<>();
		double extra1 = 0.0; 
		double daily1 = 0.0; 
		double vacation1 = 0.0; 
		double total1 = 0.0; 
		double consultantRate1 = 0.0;
		double w2Ptax1 = 0.0;
		double c2c1 = 0.0;
		double gMargin1 = 0.0;
		double bDMComm1 = 0.0;
		double aCMComm1 = 0.0;
		double recComm1 = 0.0;
		double nMargin1 = 0.0;
		double revenue1 = 0.0;
		double expense1 = 0.0;
		double commission1 = 0.0;
		
		for ( Map<String, Double> map2 : map.values()) {
			
			if(!map2.isEmpty()) {
				daily1 = daily1 + map2.get("Daily");
				extra1 = extra1 + map2.get("Extra");
				vacation1 = vacation1 + map2.get("Vacation");
				total1 = total1 + map2.get("Total");
				consultantRate1 = consultantRate1 + map2.get("ConsultantRate");
				w2Ptax1 = w2Ptax1 + map2.get("W2Ptax");
				c2c1 = c2c1 + map2.get("C2C");
				gMargin1 = gMargin1 + map2.get("G.Margin");
				bDMComm1 = bDMComm1 + map2.get("BDMComm");
				aCMComm1 = aCMComm1 + map2.get("ACMComm");
				recComm1 = recComm1 + map2.get("RecComm");
				nMargin1 = nMargin1 + map2.get("N.Margin");
				revenue1 = revenue1 + map2.get("revenue");
				expense1 = expense1 + map2.get("Expense");
				commission1 = commission1 + map2.get("Commission");
			}
        }
		sum.put("Daily", daily1);
		sum.put("Extra", extra1);
		sum.put("Vacation", vacation1);
		sum.put("Total", total1);
		sum.put("ConsultantRate", consultantRate1);
		sum.put("W2Ptax", w2Ptax1);
		sum.put("C2C", c2c1);
		sum.put("G.Margin", gMargin1);
		sum.put("BDMComm", bDMComm1);
		sum.put("ACMComm", aCMComm1);
		sum.put("RecComm", recComm1);
		sum.put("N.Margin", nMargin1);
		sum.put("revenue", revenue1);
		sum.put("Expense", expense1);
		sum.put("Commission", commission1);
		
		map.put("Total", sum);
		return map;
	}

	@Override
	public CalculationCountRequest getCalculationCountRequest(HourLogResponse hourLogResponse, UserDetail userDetail,
			int caseInt) {
		
		CalculationCountRequest calculationCountRequest = new CalculationCountRequest();
		
		double extra = 0.0; 
		double daily = 0.0; 
		double vacation = 0.0; 
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
		
		//total hour of user
//		Double vacation1 = hourLogResponse.getSumOfVacationHours() == null ? 0.0d : hourLogResponse.getSumOfVacationHours();
		total = hourLogResponse.getSumOfDailyHour() + hourLogResponse.getSumOfExtraHour() + hourLogResponse.getSumOfVacationHours();
		 
		//consultantRate , w2Ptax , c2c multiply by total hour
//		Revenue = (160 * 50 )
		revenue = (total * userDetail.getClientRate());
				
		consultantRate = userDetail.getConsultantRate() * total + userDetail.getW2() * total;
		
		w2Ptax = consultantRate * userDetail.getPtax()/100;
		
		if(userDetail.isC2cOrotherRecurssive()) {
			if(userDetail.getC2cOrotherRateType() == RateType.FIX)
				c2c = userDetail.getC2cOrother();
			else 
				c2c = userDetail.getC2cOrother()* total;
		} else {
			if(userDetail.getC2cOrotherRecurssiveMonth()!= null && userDetail.getC2cOrotherRecurssiveMonth()+1 == caseInt) {
				if(userDetail.getC2cOrotherRateType() == RateType.FIX)
					c2c = userDetail.getC2cOrother();
				else 
					c2c = userDetail.getC2cOrother()* total;
			}
		}
		gMargin = revenue - (consultantRate + w2Ptax + c2c);
		
		//bDMComm , aCMComm , recComm multiply by total hour
		if(userDetail.isbDMRecurssive()) {
			if(userDetail.getbDMRateType() == RateType.FIX)
				if(userDetail.getbDMRateCountOn() == RateCountOn.ON_HOURS){
					bDMComm = total * userDetail.getbDMCommission();
				} else {
					bDMComm = userDetail.getbDMCommission();
				}
			else {
				if(userDetail.getbDMRateCountOn() == RateCountOn.G_MARGIN) {
					bDMComm = (userDetail.getbDMCommission()/100) * gMargin;
				} else {
					bDMComm = (userDetail.getbDMCommission()/100) * revenue;
				}
			}
		} else {
			if(userDetail.getbDMRecurssiveMonth() != null && userDetail.getbDMRecurssiveMonth()+1 == caseInt) {
				if(userDetail.getbDMRateType() == RateType.FIX)
					bDMComm = userDetail.getbDMCommission();
				else {
					if(userDetail.getbDMRateCountOn() == RateCountOn.G_MARGIN) {
						bDMComm = (userDetail.getbDMCommission()/100) * gMargin;
					} else {
						bDMComm = (userDetail.getbDMCommission()/100) * revenue;
					}
				}
			}
		}
		
		if(userDetail.isaCMRecurssive()) {
			if(userDetail.getaCMRateType() == RateType.FIX) {
				if(userDetail.getaCMRateCountOn() == RateCountOn.ON_HOURS) {
					aCMComm = total * userDetail.getaCMCommission();
				} else {
					aCMComm = userDetail.getaCMCommission();
				}
			}else {
				if(userDetail.getaCMRateCountOn() == RateCountOn.G_MARGIN) {
					aCMComm = (userDetail.getaCMCommission()/100) * gMargin;
				} else {
					aCMComm = (userDetail.getaCMCommission()/100) * revenue;
				}
			}
		} else {
			if(userDetail.getaCMRecurssiveMonth() != null && userDetail.getaCMRecurssiveMonth()+1 == caseInt) {
				if(userDetail.getaCMRateType() == RateType.FIX)
					aCMComm = userDetail.getaCMCommission();
				else{
					if(userDetail.getaCMRateCountOn() == RateCountOn.G_MARGIN) {
						aCMComm = (userDetail.getaCMCommission()/100) * gMargin;
					} else {
						aCMComm = (userDetail.getaCMCommission()/100) * revenue;
					}
				}
			}
		}
		
		if(userDetail.isRecRecurssive()) {
			if(userDetail.getRecRateType() == RateType.FIX)
				if(userDetail.getRecRateCountOn() == RateCountOn.ON_HOURS) {
					recComm = total * userDetail.getRecCommission();
				} else {
					recComm = userDetail.getRecCommission();
				}
			else {
				if(userDetail.getRecRateCountOn() == RateCountOn.G_MARGIN) {
					recComm = (userDetail.getRecCommission()/100) * gMargin;
				} else {
					recComm = (userDetail.getRecCommission()/100) * revenue;
				}
			}
		} else {
			if(userDetail.getRecRecurssiveMonth() != null && userDetail.getRecRecurssiveMonth()+1 == caseInt) {
				if(userDetail.getRecRateType() == RateType.FIX)
					if(userDetail.getRecRateCountOn() == RateCountOn.ON_HOURS){
						recComm = total * userDetail.getRecCommission();
					} else {
						recComm = userDetail.getRecCommission();
					}
					
				else { 
					if(userDetail.getRecRateCountOn() == RateCountOn.G_MARGIN) {
						recComm = (userDetail.getRecCommission()/100) * gMargin;
					} else {
						recComm = (userDetail.getRecCommission()/100) * revenue;
					}
				}
			}
		}
		//net margin 
		nMargin = gMargin - (bDMComm + aCMComm + recComm);
		
		calculationCountRequest.setDailyHours(hourLogResponse.getSumOfDailyHour());
		calculationCountRequest.setExtraHour(hourLogResponse.getSumOfExtraHour());
		calculationCountRequest.setVacationHours(hourLogResponse.getSumOfVacationHours());
		calculationCountRequest.setTotal(total);
		calculationCountRequest.setConsultantRate(consultantRate);
		calculationCountRequest.setW2Ptax(w2Ptax );
		calculationCountRequest.setC2c(c2c);
		calculationCountRequest.setgMargin(gMargin);
		calculationCountRequest.setbDMComm(bDMComm);
		calculationCountRequest.setaCMComm(aCMComm);
		calculationCountRequest.setRecComm(recComm);
		calculationCountRequest.setnMargin(nMargin);
		calculationCountRequest.setRevenue(revenue);
		
		return calculationCountRequest;
	}

	/**
	 * calculation for single day
	 */
	@Override
	public CalculationCountRequest getCalculationCountForSingleDayRequest(HourLog hourLog, UserDetail userDetail,
			int month,int dayFill) {
		
		CalculationCountRequest calculationCountRequest = new CalculationCountRequest();
		
		Calendar calendar = Utils.getDate(2020, month-1, 1);
        Integer dayDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        
		double extra = 0.0; 
		double daily = 0.0; 
		double vacation = 0.0; 
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
		
		//total hour of user
		Double vacation1 = hourLog.getVacationHours() == null ? 0.0d : hourLog.getVacationHours();
		total = hourLog.getDailyHours() + hourLog.getExtraHours() + vacation1;
		 
		//consultantRate , w2Ptax , c2c multiply by total hour
		revenue = (total * userDetail.getClientRate());
				
		consultantRate = userDetail.getConsultantRate() * total + userDetail.getW2() * total;
		
		w2Ptax = consultantRate * userDetail.getPtax()/100;
		
		if(userDetail.isC2cOrotherRecurssive()) {
			if(userDetail.getC2cOrotherRateType() == RateType.FIX)
				c2c = Math.round((userDetail.getC2cOrother()/dayFill) * 100.0) / 100.0;
			else 
				c2c = userDetail.getC2cOrother()* total;
		} else {
			if(userDetail.getC2cOrotherRecurssiveMonth()!= null && userDetail.getC2cOrotherRecurssiveMonth()+1 == month) {
				if(userDetail.getC2cOrotherRateType() == RateType.FIX)
					c2c = Math.round((userDetail.getC2cOrother()/dayFill) * 100.0) / 100.0;
				else 
					c2c = userDetail.getC2cOrother()* total;
			}
		}
		gMargin = revenue - (consultantRate + w2Ptax + c2c);
		
		//bDMComm , aCMComm , recComm multiply by total hour
		if(userDetail.isbDMRecurssive()) {
			if(userDetail.getbDMRateType() == RateType.FIX) {
				if(userDetail.getbDMRateCountOn() == RateCountOn.ON_HOURS){
					bDMComm = total * userDetail.getbDMCommission();
				} else {
					bDMComm = Math.round((userDetail.getbDMCommission()/dayFill) * 100.0) / 100.0;
				}
				
		}else {
				if(userDetail.getbDMRateCountOn() == RateCountOn.G_MARGIN) {
					bDMComm = (userDetail.getbDMCommission()/100) * gMargin;
				} else {
					bDMComm = (userDetail.getbDMCommission()/100) * revenue;
				}
			}
		} else {
			if(userDetail.getbDMRecurssiveMonth() != null && userDetail.getbDMRecurssiveMonth()+1 == month) {
				if(userDetail.getbDMRateType() == RateType.FIX)
					bDMComm = Math.round((userDetail.getbDMCommission()/dayFill) * 100.0) / 100.0;
				else {
					if(userDetail.getbDMRateCountOn() == RateCountOn.G_MARGIN) {
						bDMComm = (userDetail.getbDMCommission()/100) * gMargin;
					} else {
						bDMComm = (userDetail.getbDMCommission()/100) * revenue;
					}
				}
			}
		}
		
		if(userDetail.isaCMRecurssive()) {
			if(userDetail.getaCMRateType() == RateType.FIX) {
				if(userDetail.getaCMRateCountOn() == RateCountOn.ON_HOURS) {
					aCMComm = total * userDetail.getaCMCommission();
				} else {
					aCMComm = Math.round((userDetail.getaCMCommission()/dayFill) * 100.0) / 100.0;
				}
				
			}else {
				if(userDetail.getaCMRateCountOn() == RateCountOn.G_MARGIN) {
					aCMComm = (userDetail.getaCMCommission()/100) * gMargin;
				} else {
					aCMComm = (userDetail.getaCMCommission()/100) * revenue;
				}
			}
		} else {
			if(userDetail.getaCMRecurssiveMonth() != null && userDetail.getaCMRecurssiveMonth()+1 == month) {
				if(userDetail.getaCMRateType() == RateType.FIX)
					aCMComm = Math.round((userDetail.getaCMCommission()/dayFill) * 100.0) / 100.0;
				else{
					if(userDetail.getaCMRateCountOn() == RateCountOn.G_MARGIN) {
						aCMComm = (userDetail.getaCMCommission()/100) * gMargin;
					} else {
						aCMComm = (userDetail.getaCMCommission()/100) * revenue;
					}
				}
			}
		}
		
		if(userDetail.isRecRecurssive()) {
			if(userDetail.getRecRateType() == RateType.FIX) {
				if(userDetail.getRecRateCountOn() == RateCountOn.ON_HOURS) {
					recComm = total * userDetail.getRecCommission();
				} else {
					recComm = Math.round((userDetail.getRecCommission()/dayFill) * 100.0) / 100.0;
				}
				
			}else {
				if(userDetail.getRecRateCountOn() == RateCountOn.G_MARGIN) {
					recComm = (userDetail.getRecCommission()/100) * gMargin;
				} else {
					recComm = (userDetail.getRecCommission()/100) * revenue;
				}
			}
		} else {
			if(userDetail.getRecRecurssiveMonth() != null && userDetail.getRecRecurssiveMonth()+1 == month) {
				if(userDetail.getRecRateType() == RateType.FIX)
					recComm = Math.round((userDetail.getRecCommission()/dayFill) * 100.0) / 100.0;
				else { 
					if(userDetail.getRecRateCountOn() == RateCountOn.G_MARGIN) {
						recComm = (userDetail.getRecCommission()/100) * gMargin;
					} else {
						recComm = (userDetail.getRecCommission()/100) * revenue;
					}
				}
			}
		}
		//net margin 
		nMargin = gMargin - (bDMComm + aCMComm + recComm);
		
		calculationCountRequest.setDailyHours(hourLog.getDailyHours());
		calculationCountRequest.setExtraHour(hourLog.getExtraHours());
		calculationCountRequest.setVacationHours(hourLog.getVacationHours());
		calculationCountRequest.setTotal(total);
		calculationCountRequest.setConsultantRate(consultantRate);
		calculationCountRequest.setW2Ptax(w2Ptax );
		calculationCountRequest.setC2c(c2c);
		calculationCountRequest.setgMargin(gMargin);
		calculationCountRequest.setbDMComm(bDMComm);
		calculationCountRequest.setaCMComm(aCMComm);
		calculationCountRequest.setRecComm(recComm);
		calculationCountRequest.setnMargin(nMargin);
		calculationCountRequest.setRevenue(revenue);
		
		return calculationCountRequest;
	}
	
	/**
	 * add user file 
	 */
	@Override
	public void saveUserFile(String fileName, String filePath, Integer id, Date expDate, String type, String remark) {

		UserFile userFile = new UserFile();
		userFile.setUser(userRepository.findById(id));
		userFile.setExpDate(expDate);
		userFile.setFilePath(filePath);
		userFile.setRemark(remark);
		userFile.setType(type);
		userFile.setFileName(fileName);

		
		userFileRepository.save(userFile);
		
	}

	/**
	 * get user hour log
	 */
	@Override
	public List<Map<String, Object>> getUserHourLog(Integer year,User user, Integer userDetailId, Integer pageNo, Integer length) {

		UserDetail userDetail = userDetailsRepository.findByUserDetailId(userDetailId);
		
		//get start date And end date 
        Map<String, Date> startToEndDate = getDatePeriod(year, pageNo, length);
		
        Date startDate = startToEndDate.get("StartDate");
        Date endDate = startToEndDate.get("EndDate");
        
		//get user hour log
		List<HourLog> hourLogs =  hoursLogRepository.findByUserDetailAndHoursDateBetween(userDetail, endDate, startDate);
		
		SimpleDateFormat simpleDateformat2 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat simpleDateformat3 = new SimpleDateFormat("MMM-dd-yyyy");

		List<Map<String, Object>> list = new ArrayList<>();
		
		// first loop for out side map 
		while(startDate.after(DateUtils.addDays(endDate, -1)))	{
			
			//inside map
			Map<String, Object> insideMap = new HashMap<>();
			insideMap.put("Start", simpleDateformat3.format(startDate));
			
			Double total = 0.0;//sum of weekly hour
			Date lastaDate = startDate; //set date for inside loop
			
			// loop for inside map
			while(startDate.after(DateUtils.addDays(lastaDate, -7)) && startDate.after(DateUtils.addDays(endDate, -1))) {	
				
				SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
				
				//get mach data for startDate from list
				final Date d2 = startDate; 
				
				Optional<HourLog> matchingObject = hourLogs.stream().
						    filter(p -> simpleDateformat2.format(p.getHoursDate()).equals(simpleDateformat2.format(d2))).
						    findFirst();
				
				if(matchingObject.isPresent()) {
					HourLog hourLog = matchingObject.get();
					
					total = total + hourLog.getDailyHours() + hourLog.getExtraHours();
					insideMap.put(simpleDateformat.format(hourLog.getHoursDate()), hourLog.getDailyHours() + hourLog.getExtraHours());
					
				} else {
					insideMap.put(simpleDateformat.format(startDate), 0.0);
				}
				
				startDate = DateUtils.addDays(startDate, -1);
				
				String weekday = simpleDateformat.format(startDate);
				
				if (weekday.equals("Sunday")) {
					break;
				}

			}
			
			insideMap.put("End", simpleDateformat3.format(DateUtils.addDays(startDate, 1)));
			insideMap.put("Total", total);
			
			UserTimeSheetSubmitRequest userTimeSheetSubmitRequest = getUserTimeSheetSubmitRequest(lastaDate, DateUtils.addDays(startDate, 1), userDetailId);
			insideMap.put("TimeSheet", userTimeSheetSubmitRequest);
			
			list.add(insideMap);
			
			if (startDate.equals(endDate) || startDate.before(endDate) ) {
				break;
			}
			
		}
		
		return list;
	}

	/**
	 * get starting & ending date accorging to page No 
	 * @param year
	 * @param pageNo
	 * @return
	 */
	private Map<String, Date> getDatePeriod(Integer year, Integer pageNo ,Integer length) {
		
		Date startDate = new Date(); // starting date
		if(startDate.getYear() + 1900 != year ) {
			
			startDate = new Date(year + "/12/31");
		}
			
		Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        Integer dayDate = calendar.get(Calendar.DAY_OF_WEEK); 
        
        if(dayDate != 1) {
        	dayDate = 8-dayDate;
        	startDate = DateUtils.addDays(startDate, dayDate);
        }
		
        startDate = DateUtils.addDays(startDate, -pageNo * 70);
//        Date endDate = new Date(year + "/01/01"); // end date 
		Date endDate = DateUtils.addDays(startDate, -69); // end date
		
		if(endDate.getYear()+1900 != year ) {
			
			endDate = new Date(year + "/01/01");
		}
		
		Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(endDate);
        Integer dayDate2 = calendar2.get(Calendar.DAY_OF_WEEK); 
        if(dayDate2 == 1) {
        	endDate = DateUtils.addDays(endDate, -6);
        } else {
        	
        	if(dayDate2 != 2) {
            	dayDate2 = 2-dayDate2;
            	endDate = DateUtils.addDays(endDate, dayDate2);
            }
        }
        
        Map<String, Date> date = new HashMap<>();
        date.put("StartDate", startDate);
        date.put("EndDate", endDate);
        
		return date;
	}
	
	/**
	 * get user time sheet for week
	 * @param lastaDate
	 * @param startDate
	 * @param userDetailId
	 * @return
	 */
	private UserTimeSheetSubmitRequest getUserTimeSheetSubmitRequest(Date lastaDate, Date startDate,
			Integer userDetailId) {
		
		UserTimeSheetSubmitRequest userTimeSheetSubmitRequest = new UserTimeSheetSubmitRequest();
		
		//prepare key 
		SimpleDateFormat simpleDateformat = new SimpleDateFormat("yyyy-MM-dd");
		String key = simpleDateformat.format(startDate) + "TO" + simpleDateformat.format(lastaDate) ;
		
		//get time sheet for week
		UserDetail userDetail = userDetailsRepository.findByUserDetailId(userDetailId);
		TimeSheetSubmission timeSheetSubmission = timeSheetSubmissionRepository.findByKeyAndUserDetail(key, userDetail);
		
		if (timeSheetSubmission == null) {
			
//			time sheet is null
			timeSheetSubmission = new TimeSheetSubmission();
		}
		
		// set userTimeSheetSubmitRequest
		userTimeSheetSubmitRequest.setKey(key);
		userTimeSheetSubmitRequest.setApprove(timeSheetSubmission.isApprove());
		userTimeSheetSubmitRequest.setApprovedBy(timeSheetSubmission.getApprovedBy());
		userTimeSheetSubmitRequest.setApprovedDate(timeSheetSubmission.getApprovedDate());
		userTimeSheetSubmitRequest.setReject(timeSheetSubmission.isReject());
		userTimeSheetSubmitRequest.setRejectedBy(timeSheetSubmission.getRejectedBy());
		userTimeSheetSubmitRequest.setRejectedDate(timeSheetSubmission.getRejectedDate());
		
		userTimeSheetSubmitRequest.setRejectReason(timeSheetSubmission.getRejectReason());
		userTimeSheetSubmitRequest.setSubmit(timeSheetSubmission.isSubmit());
		userTimeSheetSubmitRequest.setId(timeSheetSubmission.getId());
		
		return userTimeSheetSubmitRequest;
	}

	/**
	 * get default calendar
	 */
	@Override
	public List<DefaultCalendarResponse> getDefaultCalendar(Integer userDetailId, Integer month, Integer year) {
		
		if(month == null)
			month = 0;
		
		// get month first date (this month is current year month)
		Calendar calendar = Calendar.getInstance();
		int date = 1;
		calendar.set(year, month, date);
		int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);//get month day number
		
		Date date1 = calendar.getTime();
		final Date finalDate  = date1; 
		
		//get hours log 
		UserDetail userDetail = userDetailsRepository.findByUserDetailId(userDetailId);
		List<HourLog> hourLogs = hoursLogRepository.findByUserDetailAndHoursDateBetween(userDetail, DateUtils.addDays(date1, -1), DateUtils.addDays(date1, days));   
		
		List<DefaultCalendarResponse> defaultCalendarResponses = new ArrayList<>();
		
		while (date1.before(DateUtils.addDays(finalDate, days))) {
			
			final Date date2 = date1; 
			SimpleDateFormat simpleDateformat2 = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat simpleDateformat3 = new SimpleDateFormat("MMM-dd-yyyy");
			
			DefaultCalendarResponse defaultCalendarResponse = new DefaultCalendarResponse();
			defaultCalendarResponse.setDate(simpleDateformat3.format(date1));
			defaultCalendarResponse.setDateValue(date1);
			
			//get given date data from hour log list 
			Optional<HourLog> matchingObject = hourLogs.stream().
					    filter(p -> simpleDateformat2.format(p.getHoursDate()).equals(simpleDateformat2.format(date2))).
					    findFirst();
			
			if(matchingObject.isPresent()) {
				HourLog hourLog = matchingObject.get();
				defaultCalendarResponse.setDailyHour(hourLog.getDailyHours());
				defaultCalendarResponse.setExtraHour(hourLog.getExtraHours());
				defaultCalendarResponse.setVacation((hourLog.getVacationHours()) != null ? hourLog.getVacationHours() : (float)0.0 );
				defaultCalendarResponse.setHourLogStatus(hourLog.getHourLogStatus());
				defaultCalendarResponse.setRejectReason(hourLog.getRejectReason());
				
			}
			
			defaultCalendarResponses.add(defaultCalendarResponse);
			if(DateUtils.addDays(finalDate , days).equals(date1))
				break;
			
			date1 = DateUtils.addDays(date1,1);
			
		}
		return defaultCalendarResponses;
	}

	/**
	 * set default calendar value
	 */
	@Override
	public void setDefaultCalendar(Float dailyHour, Float extraHour, Float vacationHour, Integer userDetailId, Date date, String newNote) {
		
		if(dailyHour != null || extraHour != null ||  vacationHour != null) {
			UserDetail userDetail = userDetailsRepository.findByUserDetailId(userDetailId); 
			HourLog hourLog = hoursLogRepository.findByHoursDateAndUserDetail(date, userDetail);
			User loginUser = (User) request.getSession().getAttribute("user");
			String side ="float-right";
			if(loginUser.getId() == userDetail.getUser().getId()) {
				side ="float-left";
			}
			if(hourLog == null) {
				
				hourLog = new HourLog();
				hourLog.setDailyHours((dailyHour != null) ? dailyHour : (float)0.0);
				hourLog.setExtraHours((extraHour != null) ? extraHour : (float)0.0);
				hourLog.setVacationHours((vacationHour != null) ? vacationHour : (float)0.0);
				hourLog.setHoursDate(date);
				hourLog.setHourLogStatus(HourLogStatus.SUBMITTED);
				hourLog.setUserDetail(userDetail);
				
				String oldNote = (hourLog.getNotes()!=null)?hourLog.getNotes():"" ;
				
				String newT = "<div class='col-sm-12'><div class='ctext-wrap col-sm-9 "+side+"'>"+
		                "<div class='conversation-name'>"+loginUser.getFirstName() +"</div>"+
		                "<p>"+ newNote +"</p>"+
		                "</div></div>";
				
				
	            if(!StringUtils.isEmpty(newNote)) {
					hourLog.setNotes(oldNote + "<br>" + newT);
	            }
				
				hoursLogRepository.save(hourLog);
				
			} else {
				
				if(dailyHour != null)
					hourLog.setDailyHours(dailyHour);
				
				if(extraHour != null)
					hourLog.setExtraHours(extraHour);
				
				if(vacationHour != null)
					hourLog.setVacationHours(vacationHour);
				
				String oldNote = (hourLog.getNotes()!=null)?hourLog.getNotes():"" ;
				
				String newT = "<div class='col-sm-12'><div class='ctext-wrap col-sm-9 '"+side+">"+
		                "<div class='conversation-name'>"+loginUser.getFirstName() +"</div>"+
		                "<p>"+ newNote +"</p>"+
		                "</div></div>";
				
            if(!StringUtils.isEmpty(newNote)) {
				hourLog.setNotes(oldNote + "<br>" + newT);
            }
				
				hoursLogRepository.save(hourLog);
			}
		}
	}

	/**
	 *  user hour log for two Month
	 */
	@Override
	public List<MonthHoursLogResponse> getUserHourLogByMonth(Integer year, User user, Integer userDetailId, Integer PageNo,
			Integer length) {
		
		List<MonthHoursLogResponse> monthHoursLogResponses = new ArrayList<>();
		
		for(int month = 0; month<= 11; month++) {
			
			MonthHoursLogResponse monthHoursLogResponse = new MonthHoursLogResponse();
			
			monthHoursLogResponse.setMonth(Month.getMonth(month));
			
			//set UserTimeSheet Submit Request
			Calendar calendar = Calendar.getInstance();//Month star date 
			Calendar calendar2 = Calendar.getInstance();//Month end Date 
			int dateNumber = 1;
			calendar.set(year, month, dateNumber);
			int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);//get month day number
			calendar2.set(year, month, days);
			
			Date startDate = calendar.getTime();
			Date endDate = calendar2.getTime();
			
			UserTimeSheetSubmitRequest userTimeSheetSubmitReques =  getUserTimeSheetSubmitRequest(endDate,startDate,
					userDetailId);
			monthHoursLogResponse.setStartDate(startDate);
			monthHoursLogResponse.setEndDate(endDate);
			monthHoursLogResponse.setUserTimeSheet(userTimeSheetSubmitReques);
			
			//set Month hour Response
			List<DefaultCalendarResponse> defaultCalendarResponses = getDefaultCalendar(userDetailId, month, year) ;
			monthHoursLogResponse.setDefaultCalendarResponses(defaultCalendarResponses);
			
			//set total hours
			float total = (float) 0.0;
			for (DefaultCalendarResponse defaultCalendarResponse : defaultCalendarResponses) {
				
				float dailyHour =  ((defaultCalendarResponse.getDailyHour() != null)?defaultCalendarResponse.getDailyHour() : (float) 0.0);
				float extraHour =  ((defaultCalendarResponse.getExtraHour() != null)?defaultCalendarResponse.getExtraHour() : (float) 0.0);
				total = total + dailyHour + extraHour;
			}
			monthHoursLogResponse.setTotal(total);
			monthHoursLogResponses.add(monthHoursLogResponse);
			
			if(month == Calendar.getInstance().get(Calendar.MONTH) && year == Calendar.getInstance().get(Calendar.YEAR))
				break;
		}
		
		return monthHoursLogResponses;
	}

	/**
	 * get user upload hour log file
	 */
	@Override
	public List<HourLogFile> getHourLogFile(Integer userDetailId, Integer year) {

		UserDetail userDetail = userDetailsRepository.findByUserDetailId(userDetailId);
		
		List<HourLogFile> hourLogFiles = hourLogFileRepository.findHourLogFile(userDetail, year);
		
		
		return hourLogFiles;
	}

	/**
	 * Add user hour log file
	 */
	@Override
	public void saveUserHourFile(List<String[]> filePath, Integer userDetailId, Date startDate, Date endDate,
			String description, String remark) {
		
		Company company = (Company) request.getSession().getAttribute("company");
		
		UserDetail userDetail = userDetailsRepository.findByUserDetailId(userDetailId);
		List<HourLogFile> hourLogFiles = hourLogFileRepository.findByStartDateAndEndDateAndUserDetail(startDate, endDate, userDetail);
		HourLogFile hourLogFile = new HourLogFile();
		
		if(hourLogFiles.size() > 1) {
			hourLogFile = hourLogFiles.get(0);
			int i = 0;
			for(HourLogFile hourLogFile1 : hourLogFiles ) {
				if(i != 0) {
					List<HourLogFilePath> hourLogFilePaths = hourLogFilePathRepository.findByHourLogFile(hourLogFile1);
					if(!CollectionUtils.isEmpty(hourLogFilePaths)) {
						for(HourLogFilePath hourLogFilePath : hourLogFilePaths) {
							File file1 = new File(FILE_PATH + company.getFileFolder()+ hourLogFilePath.getFilePath());
							file1.delete();
						}
//						
						hourLogFilePathRepository.delete(hourLogFilePaths);
					}
					hourLogFileRepository.delete(hourLogFile1);
				}
				i++;
			}
		}else {
			
			if(CollectionUtils.isEmpty(hourLogFiles)) {
				hourLogFile = new HourLogFile();
			} else {
				hourLogFile = hourLogFiles.get(0);
			}
			
		}
		
		if(hourLogFile.getId() == null) {
			hourLogFile = new HourLogFile();
			addActivity("Time sheet submit", ActivityType.SUBMIT_TIMESHEET.toString() , userDetail);
		}else {
			List<HourLogFilePath> hourLogFilePaths = hourLogFilePathRepository.findByHourLogFile(hourLogFile);
			if(!CollectionUtils.isEmpty(hourLogFilePaths)) {
				List<HourLogFilePath> d = new ArrayList<HourLogFilePath>();
				for(HourLogFilePath hourLogFilePath : hourLogFilePaths) {
					if(hourLogFilePath.isAdminAddedFile() != true) {
						File file1 = new File(FILE_PATH + company.getFileFolder() + hourLogFilePath.getFilePath());
						file1.delete();
						d.add(hourLogFilePath);
					}
				}
				if(!CollectionUtils.isEmpty(d))
					hourLogFilePathRepository.delete(d);
			}
			addActivity("Time sheet re-submit", ActivityType.RESUBMIT_TIMESHEET.toString() , userDetail);
		}
		
		if(!CollectionUtils.isEmpty(filePath)) {
			if(filePath.size() > 1 ) {
				hourLogFile.setFileOriginalName("Multiple file");
				hourLogFile.setFilePath("Multiple file");
			} else {
				hourLogFile.setFileOriginalName(filePath.get(0)[0]);
				hourLogFile.setFilePath(filePath.get(0)[1]);
			}
		} else {
			hourLogFile.setFileOriginalName(null);
			hourLogFile.setFilePath(null);
		}
		
		hourLogFile.setUserDetail(userDetail);
		hourLogFile.setStartDate(startDate);
		hourLogFile.setEndDate(endDate);
		hourLogFile.setRemark(remark);

		hourLogFile.setReject(false);
		hourLogFile.setApprove(false);
		
		if(!description.isEmpty())
			hourLogFile.setDescription(description);
		
		hourLogFileRepository.save(hourLogFile);
		
		for(String[] path : filePath) {
			HourLogFilePath hourLogFilePath = new HourLogFilePath();
			hourLogFilePath.setFileOriginalName(path[1]);
			hourLogFilePath.setFilePath(path[0]);
			hourLogFilePath.setHourLogFile(hourLogFile);
			hourLogFilePathRepository.save(hourLogFilePath);
		}
		
	}

	@Override
	public List<HourLogFile> getAllHourLogFile(Integer yearFile, AdminTimeSheetAction adminTimeSheetAction) {
		
		List<HourLogFile> hourLogFiles = new ArrayList<>();
		
		switch (adminTimeSheetAction) {
		case APPROVED:
			
			hourLogFiles = hourLogFileRepository.findAllHourLogFile(yearFile, APPROVED, NOT_REJECTED);
			break;
			
		case REJECTED:
			
			hourLogFiles = hourLogFileRepository.findAllHourLogFile(yearFile, NOT_APPROVED, REJECTED);
			break;
			
		case TO_BE_APPROVE:
			
			hourLogFiles = hourLogFileRepository.findAllHourLogFile(yearFile, NOT_APPROVED, NOT_REJECTED);
			break;
			
		case ALL:
			
			hourLogFiles = hourLogFileRepository.findAllHourLogFileNoSort(yearFile);
			break;
		
		}
		
		return hourLogFiles;
	}

	@Override
	public List<HourLogFile> getHourLogNotApprovedFile(Integer userDetailId, Integer yearFile) {
		List<HourLogFile> hourLogFiles = new ArrayList<>();
		List<HourLogFile> hourLogFiles1 = hourLogFileRepository.findAllHourLogNotApprovedFile(userDetailId, yearFile, NOT_APPROVED, REJECTED);
		List<HourLogFile> hourLogFiles2 = hourLogFileRepository.findAllHourLogNotApprovedFile(userDetailId,yearFile, NOT_APPROVED, NOT_REJECTED);
		if(!CollectionUtils.isEmpty(hourLogFiles1))
			hourLogFiles.addAll(hourLogFiles1);
		
		if(!CollectionUtils.isEmpty(hourLogFiles2))
			hourLogFiles.addAll(hourLogFiles2);
		return hourLogFiles;
	}
	
	/**
	 * change hourlog status
	 */
	@Override
	public HourLog changeHourLogStatus(Integer userDetailId, Date date, HourLogStatus hourLogStatus) {
		UserDetail userDetail = userDetailsRepository.findByUserDetailId(userDetailId);
		HourLog hourLog = hoursLogRepository.findByHoursDateAndUserDetail(date, userDetail);
		
		if(hourLog != null && HourLogStatus.SEND_FOR_APPROVE == hourLogStatus) {
			hourLog.setHourLogStatus(hourLogStatus);
			hoursLogRepository.save(hourLog);
			return hourLog;
		}
		
		return null;
	}

	/**
	 * get selected date hours log
	 */
	@Override
	public AddUserTimeSheet getCalendarResponse(Integer userDetailId, Date startDate, Date endDate) {
		
		if(userDetailId != null ) {
			
			//if id not null then set response
			Calendar calendar = Calendar.getInstance();
			UserDetail userDetail = userDetailsRepository.findByUserDetailId(userDetailId);
			List<CalendarResponse> calendarResponses = new ArrayList<CalendarResponse>();
			SimpleDateFormat simpleDateformat3 = new SimpleDateFormat("MM/dd/yyyy");
			
			if(startDate == null) {
				startDate = new Date();
			} else {
				startDate = DateUtils.addDays(startDate, 0);
			}	
				
	        calendar.setTime(startDate);
	        
	        if(userDetail.getTimeSheetPeriod().equals(TimeSheetPeriod.ONE_WEEK.urlParam)) {
	        	Integer dayDate = calendar.get(Calendar.DAY_OF_WEEK); //one week 
	        
		        startDate = DateUtils.addDays(startDate, 2- dayDate);
		        endDate = DateUtils.addDays(startDate, 6);
	        	
	        } else if(userDetail.getTimeSheetPeriod().equals(TimeSheetPeriod.ONE_MONTH.urlParam)){
	        	
	        	int dateNumber = 1;
	        	calendar.set(startDate.getYear()+1900, startDate.getMonth(), dateNumber);
	        	int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);//one month 
	        	
	        	startDate = calendar.getTime();
	        	endDate = DateUtils.addDays(startDate, days-1);
	        	
	        } else if(userDetail.getTimeSheetPeriod().equals(TimeSheetPeriod.TWO_WEEK.urlParam)){
	        	
	        	int dayDate1 = Utils.getBiweekDay(startDate);
	        	if(dayDate1 == 0) {
	        		startDate = DateUtils.addDays(startDate, 0);
	        	} else if(dayDate1 == 1) {
					startDate = DateUtils.addDays(startDate, -6);
		        } else {
	            	dayDate1 = 2-dayDate1;
	            	startDate = DateUtils.addDays(startDate, dayDate1);
		        }
				
		        endDate = DateUtils.addDays(startDate,13);
	        }
			
			Date date = DateUtils.addDays(startDate, 0);
			while (date.before(DateUtils.addDays(endDate, 1))) {
				HourLog hourLog = hoursLogRepository.findByHoursDateAndUserDetail(date, userDetail);
				CalendarResponse calendarResponse = new CalendarResponse();
				
				if(hourLog != null) {
					
					SimpleDateFormat simpleDateformat = new SimpleDateFormat("E, MMM dd yyyy");
					
					calendarResponse.setDailyHours(hourLog.getDailyHours());
					calendarResponse.setDate(date.toString());//simpleDateformat3.format(date)
					calendarResponse.setDateFormate(simpleDateformat.format(date));
					calendarResponse.setExtraHours(hourLog.getExtraHours());
					calendarResponse.setVacationHours((hourLog.getVacationHours() == null) ? (0.0f) : hourLog.getVacationHours());
					calendarResponse.setOldNotes((hourLog.getNotes() == null) ? null : hourLog.getNotes());

				} else {
					
					SimpleDateFormat simpleDateformat = new SimpleDateFormat("E, MMM dd yyyy");
					
					calendarResponse.setDailyHours(0.0f);
					calendarResponse.setDate(date.toString());
					calendarResponse.setDateFormate(simpleDateformat.format(date));
					calendarResponse.setExtraHours(0.0f);
					calendarResponse.setVacationHours(0.0f);
					
				}
				
				if(date.after(userDetail.getEndDate()) || date.before(userDetail.getStartDate())) {
					calendarResponse.setOffDay(true);
				} else {
					calendarResponse.setOffDay(false);
				}
				
				calendarResponses.add(calendarResponse);
				date = DateUtils.addDays(date, 1);
			}
			AddUserTimeSheet addUserTimeSheet = new AddUserTimeSheet();
			addUserTimeSheet.setCalendarResponse(calendarResponses);
			addUserTimeSheet.setEndDate(simpleDateformat3.format(endDate));
			addUserTimeSheet.setStartDate(simpleDateformat3.format(startDate));
			addUserTimeSheet.setUserDetailId(userDetailId);
			
			return addUserTimeSheet;
		}else {
			AddUserTimeSheet addUserTimeSheet = new AddUserTimeSheet();
			addUserTimeSheet.setCalendarResponse(new ArrayList<CalendarResponse>());
			
			return addUserTimeSheet;
		}
	}
	/**
	 * send mail to user
	 */
	@Override
	public void sendEmailToUser(String message,String email, String subject, String ccEmail, File file) {
		final Context ctx = new Context(Locale.US);
		
		User loginUser = (User) request.getSession().getAttribute("user");
		
		ctx.setVariable("rootURL", TIMESHEET_SERVER_URL);
		ctx.setVariable("templateData", message);
		ctx.setVariable("logo", loginUser.getCompany()!= null ? loginUser.getCompany().getImagePath():null);
		final String htmlContent = templateEngine.process("mail/boxMail/box", ctx);
		System.out.println(htmlContent);
		emailSenderService.sendEmailToUserWithCC(htmlContent, email, subject, ccEmail, file, loginUser.getEmail());
	}

	/**
	 * save internal user
	 */
	@Override
	public void saveInternalUser(InternalUser internalUser) {
		internalUserRepository.save(internalUser);
	}

	/**
	 * get date list
	 */
	@Override
	public List<UserTimeSheetDate> getDateList(Integer year, Integer userDetailId, Date startDate) {
		
		if(userDetailId != null) {
		
			//when userDetailId selected
			if(startDate != null) {
				
				year = startDate.getYear() + 1900;//when start date selected
				if(year < 2017)
					year = 2017;
			} else if(year == null){
				
				year = new Date().getYear() + 1900;//when start date not select, only year is selected
			}
			
			UserDetail userDetail = userDetailsRepository.findByUserDetailId(userDetailId);
			List<UserTimeSheetDate> userTimeSheetDates = new ArrayList<UserTimeSheetDate>();
			SimpleDateFormat simpleDateformat3 = new SimpleDateFormat("MM/dd/yyyy");
			SimpleDateFormat simpleDateformat = new SimpleDateFormat("E, MMM dd yyyy");
			
			if(userDetail.getTimeSheetPeriod().equals(TimeSheetPeriod.ONE_WEEK.urlParam)) {
				
				//for one week period
				Calendar calendar = Utils.getDate(year, 0, 1);//get year first date 
				Calendar calendar2 = Utils.getDate(year, 11, 31);// get year last date
				
				Integer dayDate2 = calendar2.get(Calendar.DAY_OF_WEEK); //get week day number for last day
				Integer dayDate1 = calendar.get(Calendar.DAY_OF_WEEK); //get week day number for first day 
				
				Date firstDate = null;//according that to first day week starting date get
				if(dayDate1 == 1) {
					firstDate = DateUtils.addDays(calendar.getTime(), -6);//when it sun day(1) then -> go 6 day befor
		        } else {
		        	
	            	dayDate1 = 2-dayDate1;
	            	firstDate = DateUtils.addDays(calendar.getTime(), dayDate1);//when it n day(n) then -> go 2-n day after
		        }
				
		        Date lastDate = DateUtils.addDays(DateUtils.addDays(calendar2.getTime(), 2- dayDate2), 6);// for last day of week go 6 day after
		        
		        Date date = DateUtils.addDays(firstDate, 0);
				while (date.before(DateUtils.addDays(lastDate, 1))) {
					
					HourLogFile hourLogFile = hourLogFileRepository.findByStartDateAndEndDateAndUserDetailAndApprove(date, DateUtils.addDays(date, 6), userDetail,APPROVED);
					if(hourLogFile == null) {
						UserTimeSheetDate userTimeSheetDate = new UserTimeSheetDate();
						userTimeSheetDate.setDateString(simpleDateformat3.format(date));
						userTimeSheetDate.setStartDate(simpleDateformat.format(date));
						userTimeSheetDate.setEndDate(simpleDateformat.format(DateUtils.addDays(date, 6)));
						
						if(userDetail.getStartDate().before(DateUtils.addDays(date, 6)) && userDetail.getEndDate().after(date) ||
								simpleDateformat3.format(userDetail.getEndDate()).equals(simpleDateformat3.format(date)) ||
								simpleDateformat3.format(userDetail.getEndDate()).equals(simpleDateformat3.format(DateUtils.addDays(date, 6))) ||
								simpleDateformat3.format(userDetail.getStartDate()).equals(simpleDateformat3.format(DateUtils.addDays(date, 6))) ||
								simpleDateformat3.format(userDetail.getStartDate()).equals(simpleDateformat3.format(date)) )
							userTimeSheetDates.add(userTimeSheetDate);
					}
					date = DateUtils.addDays(date, 7);
				}
			
			} else if(userDetail.getTimeSheetPeriod().equals(TimeSheetPeriod.ONE_MONTH.urlParam)){
				
				Calendar calendar = Utils.getDate(year, 0, 1);//first day of year 
				Calendar calendar2 = Utils.getDate(year, 11, 31);//last day of year
				
				Date firstDate = calendar.getTime();
		        Date lastDate = calendar2.getTime();
				
		        Date date = DateUtils.addDays(firstDate, 0);
				while (date.before(DateUtils.addDays(lastDate, 1))) {
					
					Calendar calendar3 = Calendar.getInstance();
					calendar3.setTime(date);
					int days = calendar3.getActualMaximum(Calendar.DAY_OF_MONTH);//get month numberof day
					
					HourLogFile hourLogFile = hourLogFileRepository.findByStartDateAndEndDateAndUserDetailAndApprove(date, DateUtils.addDays(date, days-1), userDetail,APPROVED);
					if(hourLogFile == null) {
					
						UserTimeSheetDate userTimeSheetDate = new UserTimeSheetDate();
						userTimeSheetDate.setDateString(simpleDateformat3.format(date));
						userTimeSheetDate.setStartDate(simpleDateformat.format(date));
						userTimeSheetDate.setEndDate(simpleDateformat.format(DateUtils.addDays(date, days-1)));
						
						if(userDetail.getStartDate().before(DateUtils.addDays(date, days-1)) && userDetail.getEndDate().after(date) ||
								simpleDateformat3.format(userDetail.getEndDate()).equals(simpleDateformat3.format(date)) ||
								simpleDateformat3.format(userDetail.getEndDate()).equals(simpleDateformat3.format(DateUtils.addDays(date, days-1))) ||
								simpleDateformat3.format(userDetail.getStartDate()).equals(simpleDateformat3.format(DateUtils.addDays(date, days-1))) ||
								simpleDateformat3.format(userDetail.getStartDate()).equals(simpleDateformat3.format(date)) )
							userTimeSheetDates.add(userTimeSheetDate);
					
					}
					
					date = DateUtils.addDays(date,days);
				}
				
			} else if(userDetail.getTimeSheetPeriod().equals(TimeSheetPeriod.TWO_WEEK.urlParam)) {
				
//				int t = Utils.getBiweekDay(new Date());
				
				Calendar calendar = Utils.getDate(year, 0, 1);
				Calendar calendar2 = Utils.getDate(year, 11, 31);
				
				Integer dayDate2 = Utils.getBiweekDay(calendar2.getTime()); //get first day of year number ni bi-week
				Integer dayDate1 = Utils.getBiweekDay(calendar.getTime()); //same for last day 
				
				Date firstDate = null;
				if(dayDate1 == 1) {
					firstDate = DateUtils.addDays(calendar.getTime(), -13);// for sun day go 13 day before for first monday of bi week
		        } else {
	            	dayDate1 = 2-dayDate1;
	            	firstDate = DateUtils.addDays(calendar.getTime(), dayDate1);
		        }
				
		        Date lastDate = DateUtils.addDays(DateUtils.addDays(calendar2.getTime(), 2- dayDate2),13);
		        
		        Date date = DateUtils.addDays(firstDate, 0);
				while (date.before(DateUtils.addDays(lastDate, 1))) {
					
					HourLogFile hourLogFile = hourLogFileRepository.findByStartDateAndEndDateAndUserDetailAndApprove(date, DateUtils.addDays(date, 13), userDetail,APPROVED);
					if(hourLogFile == null) {
						UserTimeSheetDate userTimeSheetDate = new UserTimeSheetDate();
						userTimeSheetDate.setDateString(simpleDateformat3.format(date));
						userTimeSheetDate.setStartDate(simpleDateformat.format(date));
						userTimeSheetDate.setEndDate(simpleDateformat.format(DateUtils.addDays(date, 13)));//after 13 day
						
						if(userDetail.getStartDate().before(DateUtils.addDays(date, 13)) && userDetail.getEndDate().after(date) ||
								simpleDateformat3.format(userDetail.getEndDate()).equals(simpleDateformat3.format(date)) ||
								simpleDateformat3.format(userDetail.getEndDate()).equals(simpleDateformat3.format(DateUtils.addDays(date, 13))) ||
								simpleDateformat3.format(userDetail.getStartDate()).equals(simpleDateformat3.format(DateUtils.addDays(date, 13))) ||
								simpleDateformat3.format(userDetail.getStartDate()).equals(simpleDateformat3.format(date)) )
							userTimeSheetDates.add(userTimeSheetDate);
					}
					date = DateUtils.addDays(date, 14);//after 14 day
				}
			}
			return userTimeSheetDates;
		}else {
			return new ArrayList<UserTimeSheetDate>();
		}
	}

	/**
	 * get current date 
	 * @throws ParseException 
	 */
	@Override
	public Date getValidStartDate(List<UserTimeSheetDate> userTimeSheetDates, Integer userDetailId) throws ParseException{

		Calendar calendar = Calendar.getInstance();
		UserDetail userDetail = userDetailsRepository.findByUserDetailId(userDetailId);
		SimpleDateFormat simpleDateformat3 = new SimpleDateFormat("MM/dd/yyyy");
		
		Date startDate = new Date();
		//check end date gone
		if(startDate.after(userDetail.getEndDate())) {
			startDate = userDetail.getStartDate();
		}
	
		Date endDate = null;
			
        calendar.setTime(startDate);
        
        if(userDetail.getTimeSheetPeriod().equals(TimeSheetPeriod.ONE_WEEK.urlParam)) {
        	Integer dayDate = calendar.get(Calendar.DAY_OF_WEEK); //one week 
   	        startDate = DateUtils.addDays(startDate, 2- dayDate);
	        endDate = DateUtils.addDays(startDate, 6);
        	
        } else if(userDetail.getTimeSheetPeriod().equals(TimeSheetPeriod.ONE_MONTH.urlParam)){
        	
        	int dateNumber = 1;
        	calendar.set(startDate.getYear()+1900, startDate.getMonth(), dateNumber);
        	int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);//one month 
        	
        	startDate = calendar.getTime();
        	endDate = DateUtils.addDays(startDate, days-1);
        	
        } else if(userDetail.getTimeSheetPeriod().equals(TimeSheetPeriod.TWO_WEEK.urlParam)){
        	
        	int dayDate1 = Utils.getBiweekDay(startDate);
			if(dayDate1 == 1) {
				startDate = DateUtils.addDays(startDate, -6);
	        } else {
            	dayDate1 = 2-dayDate1;
            	startDate = DateUtils.addDays(startDate, dayDate1);
	        }
			
	        endDate = DateUtils.addDays(startDate,13);
        }
        HourLogFile hourLogFile = hourLogFileRepository.findByStartDateAndEndDateAndUserDetailAndApprove(startDate, endDate, userDetail,APPROVED);
        if(hourLogFile == null)
        	return startDate;
        else {
        	
        	Date date = null;
			for(UserTimeSheetDate userTimeSheetDate :userTimeSheetDates) {
				
				if(simpleDateformat3.parse(userTimeSheetDate.getDateString()).after(startDate)) {
					date = simpleDateformat3.parse(userTimeSheetDate.getDateString());
					break;
				}
			}
        	
        	return date;
        }
	}

	/**
	 * set activity
	 */
	@Override
	public void addActivity(String notes, String type, UserDetail userDetail) {
		User user1 = (User) request.getSession().getAttribute("user");
		User user = userRepository.findByEmail(user1.getEmail());
		
		Activity activity = new Activity();
		activity.setActivityByUser(user);
		activity.setActivityType(type);
		activity.setActivityOnUserDetails(userDetail);
		activity.setNote(notes);
		activity.setIpAddress(request.getRemoteAddr());
		
		activityRepository.save(activity);
	}
	
	@Override
	public void addActivityWithOtherDetails(String notes, String type, String other) {
		User user1 = (User) request.getSession().getAttribute("user");
		User user = userRepository.findByEmail(user1.getEmail());
		
		Activity activity = new Activity();
		activity.setActivityByUser(user);
		activity.setActivityType(type);
		activity.setActivityOnUserDetails(null);
		activity.setNote(notes);
		activity.setIpAddress(request.getRemoteAddr());
		activity.setOtherNote(other);
		
		activityRepository.save(activity);
	}

	/**
	 * get schedule timesheet
	 */
	@Override
	public List<CalendarResponse> getSchedularResponse(Date startDate, Date endDate) {
		
		List<CalendarResponse> calendarResponses = new ArrayList<CalendarResponse>();
		Date date = DateUtils.addDays(startDate, 0);
		while (date.before(DateUtils.addDays(endDate, 1))) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			
			Schedular schedular = schedularRepository.findBySchedular(day, month+1, year);
			
			CalendarResponse calendarResponse = new CalendarResponse();
			
			if(schedular != null) {
				
				SimpleDateFormat simpleDateformat = new SimpleDateFormat("E, MMM dd yyyy");
				
				calendarResponse.setDailyHours(schedular.getDailyHours());
				calendarResponse.setDate(date.toString());//simpleDateformat3.format(date)
				calendarResponse.setDateFormate(simpleDateformat.format(date));
				calendarResponse.setExtraHours(schedular.getExtraHours());
				calendarResponse.setVacationHours((schedular.getVacationHours() == null) ? (0.0f) : schedular.getVacationHours());
				
			} else {
				
				SimpleDateFormat simpleDateformat = new SimpleDateFormat("E, MMM dd yyyy");
				
				calendarResponse.setDailyHours(0.0f);
				calendarResponse.setDate(date.toString());
				calendarResponse.setDateFormate(simpleDateformat.format(date));
				calendarResponse.setExtraHours(0.0f);
				calendarResponse.setVacationHours(0.0f);
				
			}
			calendarResponses.add(calendarResponse);
			date = DateUtils.addDays(date, 1);
		}
		
		return calendarResponses;
	}

	/**
	 * get hour log file by user
	 */
	@Override
	public List<HourLogFile> getHourLogFileByUser(Integer yearFile, Integer userId,
			AdminTimeSheetAction adminTimeSheetAction) {

		List<HourLogFile> hourLogFiles = new ArrayList<>();
		User user = userRepository.findById(userId);
		
		switch (adminTimeSheetAction) {
			case APPROVED:
				
				hourLogFiles = hourLogFileRepository.findAllHourLogFileByUser(yearFile, user, APPROVED, NOT_REJECTED);
				break;
				
			case REJECTED:
				
				hourLogFiles = hourLogFileRepository.findAllHourLogFileByUser(yearFile, user,  NOT_APPROVED, REJECTED);
				break;
				
			case TO_BE_APPROVE:
				
				hourLogFiles = hourLogFileRepository.findAllHourLogFileByUser(yearFile, user, NOT_APPROVED, NOT_REJECTED);
				break;
				
			case ALL:
				
				hourLogFiles = hourLogFileRepository.findAllHourLogFileNoSortByUser(yearFile, user);
				break;
		
		}
		
		return hourLogFiles;
	}

	/**
	 * default time set
	 */
	@Override
	public List<CalendarResponse> getDefaulteResponse(Date startDate, Date endDate) {
		List<CalendarResponse> calendarResponses = new ArrayList<CalendarResponse>();
		Date date = DateUtils.addDays(startDate, 0);
		while (date.before(DateUtils.addDays(endDate, 1))) {
			CalendarResponse calendarResponse = new CalendarResponse();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			SimpleDateFormat simpleDateformat = new SimpleDateFormat("dd-MM-yyyy");
			int day = calendar.get(calendar.DAY_OF_WEEK);
			if(day!= 1 && day!= 7) {
				calendarResponse.setDailyHours(8.0f);
			} else {
				calendarResponse.setDailyHours(0.0f);
			}
			
			calendarResponse.setDate(simpleDateformat.format(date));
			calendarResponse.setDateFormate(simpleDateformat.format(date));
			calendarResponse.setExtraHours(0.0f);
			calendarResponse.setVacationHours(0.0f);
			calendarResponse.setOffDay(false);
			calendarResponses.add(calendarResponse);
			date = DateUtils.addDays(date, 1);
		}
		
		return calendarResponses;
	}

	/**
	 * details pdf html
	 */
	@Override
	public String viewHoursLogPdf(Integer id) {
		
		HourLogFile hourLogFile  = hourLogFileRepository.findById(id);
		AddUserTimeSheet addUserTimeSheet  = getCalendarResponse(hourLogFile.getUserDetail().getUserDetailId(),
				hourLogFile.getStartDate(), hourLogFile.getEndDate());
		
		final Context ctx = new Context(Locale.US);
		ctx.setVariable("hourLogFile", hourLogFile);
		ctx.setVariable("timesheetId", id);
		ctx.setVariable("addUserTimeSheet", addUserTimeSheet);
		ctx.setVariable("userDetail", userDetailsRepository.findByUserDetailId(hourLogFile.getUserDetail().getUserDetailId()));
		final String htmlContent = templateEngine.process("new/supervisor/add-time-sheet-status-pdf", ctx);
		
		return htmlContent;
	}

	@Override
	public void setUserActive(String uId, String cId) {
		
		User user = userRepository.findByUuid(uId);
		user.setActive(1);
		userRepository.save(user);
		Company company = companyRepository.findByUuid(cId);
		
		final Integer ACTIVE = 1;
		final Integer DEACTIVE = 0;
		
		User userClone = SerializationUtils.clone(user);
		UserEditActiveInSubDb userEditActiveInSubDb = new UserEditActiveInSubDb(userClone,ACTIVE,company.getDbName(),userRepository);
		Thread t = new Thread(userEditActiveInSubDb);
		t.start();
	}

	@Override
	public UserDetail getUserDetail(@Valid UserDetailRequest userDetailRequest) {
		
		UserDetail userDetail = new UserDetail();
		if(userDetailRequest.getUserDetailId() != null){
			userDetail.setUserDetailId(userDetailRequest.getUserDetailId());
		}
		
		InternalUser acmUser = internalUserRepository.findOne(userDetailRequest.getAccountManagerId());
		userDetail.setAccountManager(acmUser);
		
		InternalUser bdmUser = internalUserRepository.findOne(userDetailRequest.getBusinessDevelopmentManagerId());
		userDetail.setBusinessDevelopmentManager(bdmUser);
		
		InternalUser recUser = internalUserRepository.findOne(userDetailRequest.getRecruiterId());
		userDetail.setRecruiter(recUser);
		
		userDetail.setAccountManagerName(acmUser.getFirstname() + " " + acmUser.getLastname());
		userDetail.setaCMCommission(userDetailRequest.getaCMCommission());
		userDetail.setaCMRateType(userDetailRequest.getaCMRateType());
		userDetail.setaCMRecurssive(userDetailRequest.isaCMRecurssive());
		userDetail.setaCMRateCountOn(userDetailRequest.getaCMRateCountOn());
		userDetail.setaCMRecurssiveMonth(userDetailRequest.getaCMRecurssiveMonth());
		
		
		userDetail.setbDMCommission(userDetailRequest.getbDMCommission());
		userDetail.setBusinessDevelopmentManagerName(bdmUser.getFirstname() + " " + bdmUser.getLastname());
		userDetail.setbDMRateType(userDetailRequest.getbDMRateType());
		userDetail.setbDMRecurssive(userDetailRequest.isbDMRecurssive());
		userDetail.setbDMRateCountOn(userDetailRequest.getbDMRateCountOn());
		userDetail.setbDMRecurssiveMonth(userDetailRequest.getbDMRecurssiveMonth());
		
		
		userDetail.setClientRate(userDetailRequest.getClientRate() == null ? 0.0f:userDetailRequest.getClientRate());
		userDetail.setEndDate(userDetailRequest.getEndDate());
		
		userDetail.setRecCommission(userDetailRequest.getRecCommission());
		userDetail.setRecruiterName(recUser.getFirstname() + " " + recUser.getLastname());
		userDetail.setRecRateType(userDetailRequest.getRecRateType());
		userDetail.setRecRecurssive(userDetailRequest.isRecRecurssive());
		userDetail.setRecRateCountOn(userDetailRequest.getRecRateCountOn());
		userDetail.setRecRecurssiveMonth(userDetailRequest.getRecRecurssiveMonth());
		
		
		userDetail.setStartDate(userDetailRequest.getStartDate());
		userDetail.setTimeSheetPeriod(userDetailRequest.getTimeSheetPeriod());
		userDetail.setUser(userDetailRequest.getUser());
		userDetail.setUserDetailId(userDetailRequest.getUserDetailId());
		userDetail.setActive(userDetailRequest.isActive());
		
		InvoiceTo invoiceTo = InvoiceTo.getInvoiceTo(userDetailRequest.getInvoiceTo());
		userDetail.setInvoiceTo(invoiceTo);
		
		if(userDetailRequest.getClientId() != null) {
			Client client = clientRepository.findById(userDetailRequest.getClientId()); 
			userDetail.setClient(client);
			if(invoiceTo.urlParam.equals(InvoiceTo.CLIENT.urlParam)) {
				userDetail.setClientName(client.getClientName());
			}
		} else {
			userDetail.setClient(null);
		}
		
		if(userDetailRequest.getVendorId() != null) {
			Client vendor = clientRepository.findById(userDetailRequest.getVendorId()); 
			userDetail.setVendor(vendor);
			userDetail.setVendorName(vendor.getClientName());
			if(invoiceTo.urlParam.equals(InvoiceTo.VENDOR.urlParam)) {
				userDetail.setClientName(vendor.getClientName());
			}
		} else {
			userDetail.setVendor(null);
			userDetail.setVendorName(null);
		}
		
		userDetail.setC2cOrother(userDetailRequest.getC2cOrother() == null ? 0.0f: userDetailRequest.getC2cOrother());
		userDetail.setC2cOrotherRateType(userDetailRequest.getC2cOrotherRateType());
		userDetail.setC2cOrotherRecurssive(userDetailRequest.isC2cOrotherRecurssive());
		userDetail.setC2cOrotherRecurssiveMonth(userDetailRequest.getC2cOrotherRecurssiveMonth());
		
		userDetail.setW2C2cType(userDetailRequest.getW2OrC2cType());
		if(userDetailRequest.getW2OrC2cType() == W2OrC2cType.W2) {
			userDetail.setPtax(userDetailRequest.getPtax() == null ? 0.0f :  userDetailRequest.getPtax());
			userDetail.setW2(userDetailRequest.getW2() == null ? 0.0f : userDetailRequest.getW2());
			
			userDetail.setAddress("");
			userDetail.setConsultantRate(0.0f);
			userDetail.setEmployerEmail("");
			userDetail.setEmployerName("");
			userDetail.setEmployerPhone("");
			
		} else {
			userDetail.setPtax(0.0f);
			userDetail.setW2(0.0f);
			
			Client client1 = clientRepository.findById(userDetailRequest.getEmployerId()); 
			userDetail.setEmployer(client1);
		
			userDetail.setAddress(userDetailRequest.getAddress());
			userDetail.setConsultantRate(userDetailRequest.getConsultantRate() == null ? 0.0f : userDetailRequest.getConsultantRate());
			userDetail.setEmployerEmail(userDetailRequest.getEmployerEmail());
			userDetail.setEmployerName(client1.getClientName());
			userDetail.setEmployerPhone(userDetailRequest.getEmployerPhone());
			
		}
		
		return userDetail;
	}

	@Override
	public UserDetailRequest setUserDetailRequest(UserDetail userDetail) {
		
		UserDetailRequest userDetailRequest = new UserDetailRequest();
		
		if(userDetail.getUserDetailId() != null){
			userDetailRequest.setUserDetailId(userDetail.getUserDetailId());
		}
		
		userDetailRequest.setAccountManagerId(userDetail.getAccountManager().getId());
		userDetailRequest.setAccountManagerName(userDetail.getAccountManagerName());
		userDetailRequest.setaCMCommission(userDetail.getaCMCommission());
		userDetailRequest.setaCMRateType(userDetail.getaCMRateType());
		userDetailRequest.setaCMRecurssive(userDetail.isaCMRecurssive());
		userDetailRequest.setaCMRateCountOn(userDetail.getaCMRateCountOn());
		userDetailRequest.setaCMRecurssiveMonth(userDetail.getaCMRecurssiveMonth());
		userDetailRequest.setaCMDefault(userDetail.getAccountManager().isDefaultUser());
		
		userDetailRequest.setInvoiceTo(userDetail.getInvoiceTo()!=null?userDetail.getInvoiceTo().urlParam : "");
		
		userDetailRequest.setBusinessDevelopmentManagerId(userDetail.getBusinessDevelopmentManager().getId());
		userDetailRequest.setbDMCommission(userDetail.getbDMCommission());
		userDetailRequest.setBusinessDevelopmentManagerName(userDetail.getBusinessDevelopmentManagerName());
		userDetailRequest.setbDMRateType(userDetail.getbDMRateType());
		userDetailRequest.setbDMRecurssive(userDetail.isbDMRecurssive());
		userDetailRequest.setbDMRateCountOn(userDetail.getbDMRateCountOn());
		userDetailRequest.setbDMRecurssiveMonth(userDetail.getbDMRecurssiveMonth());
		userDetailRequest.setbDMDefault(userDetail.getBusinessDevelopmentManager().isDefaultUser());
		
		userDetailRequest.setClientRate(userDetail.getClientRate() == null ? 0.0f:userDetail.getClientRate());
		userDetailRequest.setEndDate(userDetail.getEndDate());
		
		userDetailRequest.setRecruiterId(userDetail.getRecruiter().getId());
		userDetailRequest.setRecCommission(userDetail.getRecCommission());
		userDetailRequest.setRecruiterName(userDetail.getRecruiterName());
		userDetailRequest.setRecRateType(userDetail.getRecRateType());
		userDetailRequest.setRecRecurssive(userDetail.isRecRecurssive());
		userDetailRequest.setRecRateCountOn(userDetail.getRecRateCountOn());
		userDetailRequest.setRecRecurssiveMonth(userDetail.getRecRecurssiveMonth());
		userDetailRequest.setrECDefault(userDetail.getRecruiter().isDefaultUser());
		
		userDetailRequest.setStartDate(userDetail.getStartDate());
		userDetailRequest.setTimeSheetPeriod(userDetail.getTimeSheetPeriod());
		userDetailRequest.setUser(userDetail.getUser());
		userDetailRequest.setUserDetailId(userDetail.getUserDetailId());
		userDetailRequest.setActive(userDetail.isActive());
		
		userDetailRequest.setClientName("");
		if(userDetail.getClient() != null)
			userDetailRequest.setClientId(userDetail.getClient().getId());
		if(userDetail.getVendor() != null)
			userDetailRequest.setVendorId(userDetail.getVendor().getId());
		userDetailRequest.setVendorName("");
		userDetailRequest.setC2cOrother(userDetail.getC2cOrother() == null ? 0.0f: userDetail.getC2cOrother());
		userDetailRequest.setC2cOrotherRateType(userDetail.getC2cOrotherRateType());
		userDetailRequest.setC2cOrotherRecurssive(userDetail.isC2cOrotherRecurssive());
		userDetailRequest.setC2cOrotherRecurssiveMonth(userDetail.getC2cOrotherRecurssiveMonth());
		
		
		if(userDetail.getW2C2cType() == W2OrC2cType.W2) {
			userDetailRequest.setPtax(userDetail.getPtax() == null ? 0.0f :  userDetail.getPtax());
			userDetailRequest.setW2(userDetail.getW2() == null ? 0.0f : userDetail.getW2());
			
			userDetailRequest.setAddress("");
			userDetailRequest.setConsultantRate(null);
			userDetailRequest.setEmployerEmail("");
			userDetailRequest.setEmployerName("");
			userDetailRequest.setEmployerPhone("");
			
			userDetailRequest.setW2OrC2cType(W2OrC2cType.W2);
			
		} else {
			userDetailRequest.setPtax(null);
			userDetailRequest.setW2(null);
			userDetailRequest.setW2OrC2cType(W2OrC2cType.C2C);
			userDetailRequest.setEmployerId(userDetail.getEmployer().getId());
		
			userDetailRequest.setAddress(userDetail.getAddress());
			userDetailRequest.setConsultantRate(userDetail.getConsultantRate() == null ? 0.0f : userDetail.getConsultantRate());
//			userDetailRequest.setEmployerEmail(userDetail.getEmployer().getEmail());
			userDetailRequest.setEmployerName(userDetail.getEmployer().getClientName());
			userDetailRequest.setEmployerPhone(userDetail.getEmployer().getPhone());
		}
		
		return userDetailRequest;
	}

	@Override
	public String viewSchedularPdf(SchedularResponse schedularResponses) {
		//add-schedular-pdf
		String currentDate = new SimpleDateFormat("MMM-dd-yyyy HH:mm").format(Calendar.getInstance().getTime());
		User loginUser = (User) request.getSession().getAttribute("user");
		
		final Context ctx = new Context(Locale.US);
		ctx.setVariable("schedularResponses", schedularResponses);
		ctx.setVariable("createDate", currentDate);
		ctx.setVariable("createByUser", loginUser);
		final String htmlContent = templateEngine.process("new/supervisor/add-schedular-pdf", ctx);
		
		return htmlContent;
		
	}

	/**
	 * get pending timesheet
	 */
	@Override
	public List<PendingHourLogFile> getPengindTimesheet(Integer yearFile, Integer userId) {
		
		List<User> users = new ArrayList<User>();
		if(userId != null) {
			User user1 = userRepository.findById(userId);
			users.add(user1);
		} else {
			users =	findByRole("ROLE_USER");
		}
		
		List<PendingHourLogFile> pendingHourLogFiles = new ArrayList<PendingHourLogFile>();
		for (User user : users) {
			
			List<UserDetail> userDetails = userDetailsRepository.findByUser(user);
			
			for (UserDetail userDetail : userDetails) {
				
				SimpleDateFormat simpleDateformat3 = new SimpleDateFormat("E, MMM dd yyyy");
				
				List<UserTimeSheetDate> userTimeSheetDates = getDateList(yearFile, userDetail.getUserDetailId(), null);
				
				for (UserTimeSheetDate userTimeSheetDate : userTimeSheetDates) {
					try {
						
						Date today = new Date();
						Date startDate = simpleDateformat3.parse(userTimeSheetDate.getStartDate());
						Date endDate = simpleDateformat3.parse(userTimeSheetDate.getEndDate()); 
						if((startDate.equals(today) || startDate.before(today) ) && userDetail.getStartDate().before(endDate) && userDetail.getEndDate().after(startDate) ) {
							
							List<HourLogFile> hourLogFile = hourLogFileRepository.findByStartDateAndEndDateAndUserDetail(simpleDateformat3.parse(userTimeSheetDate.getStartDate()),
								simpleDateformat3.parse(userTimeSheetDate.getEndDate()),userDetail);
							
							if(CollectionUtils.isEmpty(hourLogFile)) {
								PendingHourLogFile p = new PendingHourLogFile();
								p.setEndDate(simpleDateformat3.parse(userTimeSheetDate.getEndDate()));
								p.setStartDate(simpleDateformat3.parse(userTimeSheetDate.getStartDate()));
								p.setUserDetail(userDetail);
								
								pendingHourLogFiles.add(p);
								
							}
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return pendingHourLogFiles;
	}

	@Override
	public Company getCompany() {
		Company company = (Company) request.getSession().getAttribute("company");
		Company c = companyRepository.findByUrlSlug(company.getUrlSlug());
		return c;
	}

	@Override
	public Company saveCompany(CompanyRequest companyRequest, String filePath1) {
		Company company = companyRepository.findByUrlSlug(companyRequest.getUrlSlug());
		company.setAddress(companyRequest.getAddress());
		company.setName(companyRequest.getName());
		company.setDetails(companyRequest.getDetails());
		company.setTimesheetSubmitEmail(companyRequest.getTimesheetSubmitEmail());
		if(filePath1 != null){
			company.setImagePath(filePath1);
		}
		companyRepository.save(company);
		
		Company companyClone = SerializationUtils.clone(company);
		CompanySaveInMasterThread companySaveThread = new CompanySaveInMasterThread(companyRepository,companyClone);
		Thread t1 = new Thread(companySaveThread);
		t1.start();
		
		return company;
	}

	@Override
	public void addPrivateSignature(String privateSignature, User user) {
		
		User user2 = userRepository.findById(user.getId());
		user2.setPrivateSign(privateSignature);
		userRepository.save(user2);
	}

	@Override
	public Page<User> findAll(Integer page) {
		Pageable pageable = new PageRequest(page-1, 12);
		Page<User> d = userRepository.findAll(pageable);
		return d;
	}

	/**
	 * 
	 */
	@Override
	public String setPlanString(PermissionPlan permissionPlan) {
		String div = "<div class='col-sm-12'>";
		
		User loginUser = (User) request.getSession().getAttribute("user");
		
		Company company = getCompany();
		Company companyMaster = getCompany();
		ThreadPoolExecutor executor1 = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
		GetCompanyByName getCompanyByName = new GetCompanyByName(companyRepository,company.getName());
		Future<Company> result = executor1.submit(getCompanyByName);
		
		try {
			companyMaster = result.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		div = div + "<p> Company Name : " + company.getName()+ "<p>";
		div = div + "<p> Company Detail : <a href='"+TIMESHEET_SERVER_URL+ "/super-admin/company-detail/" +companyMaster.getId() +"'>Company details</a><p>";
		div = div + "<p> Request User Name : " + loginUser.getFirstName() +" "+loginUser.getLastName()+ "<p>";
		div = div + "<p> Request User Email : " + loginUser.getEmail()+ "<p>";
		div = div + "<ol>";
		if(permissionPlan.isCommission()) {
			div = div + "<li><p><b>Commission</b></p></li>";
		}
		if(permissionPlan.isUserCanLogin()) {
			div = div + "<li><p><b>User Can Login</b></p></li>";
		}
		if(permissionPlan.isTemplateCanSet()) {
			div = div + "<li><p><b>Template Can Set</b></p></li>";
		}
		if(permissionPlan.isSchedularCanSet()) {
			div = div + "<li><p><b>Schedular Can Set</b></p></li>";
		}
		if(permissionPlan.isQbIntegration()) {
			div = div + "<li><p><b>Qb Integration</b></p></li>";
		}
		if(permissionPlan.getUserLimit() > 0) {
			div = div + "<li><p><b>Increase User Limit to <span>"+permissionPlan.getUserLimit()+"</span></b></p></li>";
		}
		div = div + "</ol></div>";
		if(loginUser.getPrivateSign() != null) {
			div = div + "<div class='col-sm-12'>"+loginUser.getPrivateSign()+"</div>";
		}
		return div;
	}

	@Override
	public Page<User> findByFirstNameStartsWith(String startsWith, Integer page) {
		Pageable pageable = new PageRequest(page-1, 12);
		Page<User> d =userRepository.findByFirstNameStartsWithIgnoreCase(startsWith, pageable);
		return d;
	}
}