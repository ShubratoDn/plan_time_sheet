package com.aim.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aim.entity.Company;
import com.aim.entity.HourLogFile;
import com.aim.entity.HourLogFilePath;
import com.aim.entity.Schedular;
import com.aim.entity.Template;
import com.aim.entity.User;
import com.aim.entity.UserDetail;
import com.aim.enums.ActivityType;
import com.aim.enums.AdminTimeSheetAction;
import com.aim.enums.Functionality;
import com.aim.enums.MailTemplateType;
import com.aim.enums.Month;
import com.aim.enums.Permission;
import com.aim.enums.UserDetailsType;
import com.aim.model.ResponseGenerator;
import com.aim.repository.HourLogFilePathRepository;
import com.aim.repository.HourLogFileRepository;
import com.aim.repository.SchedularRepository;
import com.aim.repository.UserDetailsRepository;
import com.aim.repository.UserRepository;
import com.aim.response.AddUserTimeSheet;
import com.aim.response.AdminUserHoursChart;
import com.aim.response.CalendarResponse;
import com.aim.response.DefaultCalendarResponse;
import com.aim.response.HomePageUserResponse;
import com.aim.response.PendingHourLogFile;
import com.aim.response.SchedularResponse;
import com.aim.response.UserTimeSheet;
import com.aim.response.UserTimeSheetDate;
import com.aim.response.UserTypeChartResponse;
import com.aim.service.AdminService;
import com.aim.service.HoursLogService;
import com.aim.service.PermissionService;
import com.aim.service.SupervisorService;
import com.aim.service.TemplateService;
import com.aim.service.UserService;
import com.aim.service.email.EmailSenderService;
import com.aim.utils.Response;
import com.aim.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.itextpdf.html2pdf.HtmlConverter;

@Controller
@RequestMapping("/supervisor/")
public class SupervisorController extends DataMenuController {
	
	@Value("${file.path}")
	private String FILE_PATH;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private HoursLogService hoursLogService;
	
	@Autowired
	private SupervisorService supervisorService;
	
	@Autowired
	private HourLogFileRepository hourLogFileRepository;
	
	@Autowired
	private UserDetailsRepository userDetailsRepository;
	
	@Autowired
	private SchedularRepository schedularRepository;
	
	@Autowired
	private HourLogFilePathRepository hourLogFilePathRepository;
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private TemplateService templateService;
	
	@Autowired
	private EmailSenderService emailSenderService;
	
	@Autowired
	private PermissionService permissionService;
	
	@Autowired
	private HttpServletRequest request;
	
	private final Boolean APPROVED = true;
	private final Boolean NOT_APPROVED = false;
	private final Boolean NOT_REJECTED= false;
	private final Boolean REJECTED= true;
	
	/**
	 * home page 
	 * @param model
	 * @return
	 */
	@RequestMapping(value="home", method = RequestMethod.GET)
	public String home(Model model,@RequestParam(name="year",required=false) Integer year,
			@RequestParam(name="month", required=false) Integer month) {
		
		if(year == null)
			year = new Date().getYear() + 1900;
		
		model.addAttribute("selectedYear", year);
		model.addAttribute("selectedMonth",Month.getMonth(month));
		
		UserDetailsType userDetailsType = UserDetailsType.getUserDetailsType("all");
		List<HomePageUserResponse> userTotalHour = adminService.getUserTotalHour(year,null, month, userDetailsType);
		model.addAttribute("userTotalHour", userTotalHour);
		
		model.addAttribute("totalUsers", userRepository.countByRole("ROLE_USER"));
		model.addAttribute("approvedFile", hourLogFileRepository.countByApproveAndReject(APPROVED, NOT_REJECTED));
		model.addAttribute("newFile", hourLogFileRepository.countByApproveAndReject(NOT_APPROVED, NOT_REJECTED ));
		model.addAttribute("rejectFile", hourLogFileRepository.countByApproveAndReject(NOT_APPROVED, REJECTED ));
		
		List<User> usersList = new ArrayList<User>();
		AdminUserHoursChart adminUserHoursChart = new AdminUserHoursChart();
		
		for(HomePageUserResponse homePageUserResponse: userTotalHour) {
			if(!usersList.contains(homePageUserResponse.getUserDetails().getUser()))
				usersList.add(homePageUserResponse.getUserDetails().getUser());
		}
		
		if(month != null) {
			
			if(!CollectionUtils.isEmpty(usersList)) {
				adminUserHoursChart = adminService.getUserMonthDayHour(year,null,month+1, userDetailsType);
			}
			
			Calendar calendar = new GregorianCalendar(year, month, 1);
	        int numberOfDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	        List<String> xChart = new ArrayList<>();
	        
	        for(Integer i = 1;i<=numberOfDays;i++) {
	        	xChart.add(i.toString());
			}
	        
	        model.addAttribute("xChart", xChart);
			model.addAttribute("approvedFileByFilter", hourLogFileRepository.countByApproveAndRejectByMonthAndYear(month + 1, year,APPROVED, NOT_REJECTED));
			model.addAttribute("newFileByFilter", hourLogFileRepository.countByApproveAndRejectByMonthAndYear(month + 1, year,NOT_APPROVED, NOT_REJECTED ));
			model.addAttribute("rejectFileByFilter", hourLogFileRepository.countByApproveAndRejectByMonthAndYear(month + 1, year,NOT_APPROVED, REJECTED ));
		
			List<UserDetail> totalActiveUsers = hourLogFileRepository.findTotalActiveUserByMonth(month + 1, year);
			List<UserDetail> totalActiveC2CUsers = hourLogFileRepository.findTotalActiveUserByC2CTypeByMonth(month + 1, year);
			List<UserDetail> totalActivePtaxUsers = hourLogFileRepository.findTotalActiveUserByPtaxTypeByMonth(month + 1, year);
			model.addAttribute("totalActiveUsers", totalActiveUsers.size());
			model.addAttribute("totalActiveC2CUsers", totalActiveC2CUsers.size());
			model.addAttribute("totalActivePtaxUsers", totalActivePtaxUsers.size());
			
			UserTypeChartResponse userTypeChartResponse = supervisorService.getUserChartByMonth(year, month + 1);
			model.addAttribute("userTypeChartResponse", userTypeChartResponse);
			
		} else {
			
			if(!CollectionUtils.isEmpty(usersList)) {
				adminUserHoursChart = adminService.getUserMonthHour(year,null, null, userDetailsType);
			}
			
			String xChart[] = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
			model.addAttribute("xChart", xChart);
			
			model.addAttribute("approvedFileByFilter", hourLogFileRepository.countByApproveAndRejectByYear(year,APPROVED, NOT_REJECTED));
			model.addAttribute("newFileByFilter", hourLogFileRepository.countByApproveAndRejectByYear(year,NOT_APPROVED, NOT_REJECTED ));
			model.addAttribute("rejectFileByFilter", hourLogFileRepository.countByApproveAndRejectByYear(year,NOT_APPROVED, REJECTED ));
			
			List<UserDetail> totalActiveUsers = hourLogFileRepository.findTotalActiveUser(year);
			List<UserDetail> totalActiveC2CUsers = hourLogFileRepository.findTotalActiveUserByC2CType(year);
			List<UserDetail> totalActivePtaxUsers = hourLogFileRepository.findTotalActiveUserByPtaxType(year);
			model.addAttribute("totalActiveUsers", totalActiveUsers.size());
			model.addAttribute("totalActiveC2CUsers", totalActiveC2CUsers.size());
			model.addAttribute("totalActivePtaxUsers", totalActivePtaxUsers.size());
			
			UserTypeChartResponse userTypeChartResponse = supervisorService.getUserChartByYear(year);
			model.addAttribute("userTypeChartResponse", userTypeChartResponse);
		}
		
		model.addAttribute("adminUserHoursChart", adminUserHoursChart);
		
		return "new/supervisor/home";
	}
	
	/**
	 * all user time sheet
	 * @param yearFile
	 * @param model
	 * @return
	 */
	@RequestMapping(value="timesheet-view-file-list", method = RequestMethod.GET)
	@ResponseBody
	public List<HourLogFilePath> timesheetFileList(@RequestParam(name="id",required=false) Integer id) {
		HourLogFile hourLogFile = hourLogFileRepository.findById(id);
		List<HourLogFilePath> hourLogFilePaths = new ArrayList<>();
		if(hourLogFile != null)
			hourLogFilePaths = hourLogFilePathRepository.findByHourLogFile(hourLogFile);
		
		return hourLogFilePaths;
	}
	
	/**
	 * all user time sheet
	 * @param yearFile
	 * @param model
	 * @return
	 */
	@RequestMapping(value="time-sheet", method = RequestMethod.GET)
	public String timesheet(@RequestParam(name="yearFile",required=false) Integer yearFile,
			@RequestParam(name="userId",required=false) Integer userId,
			@RequestParam(name="sort",required=false) String sort, Model model) {
		
		User loginUser = (User) request.getSession().getAttribute("user");
		boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.TIMESHEET,Permission.READ, false);
		if(!grant){
			return "redirect:/supervisor/unauthorized";
		}
		
		if(yearFile == null)
			yearFile = Calendar.getInstance().get(Calendar.YEAR);
			
		AdminTimeSheetAction adminTimeSheetAction = AdminTimeSheetAction.getAdminTimeSheetAction(sort);
		
		List<HourLogFile> hourLogFiles = new ArrayList<HourLogFile>();
		if(userId == null) {
			hourLogFiles = userService.getAllHourLogFile(yearFile, adminTimeSheetAction);
		}
		else {
			hourLogFiles = userService.getHourLogFileByUser(yearFile, userId, adminTimeSheetAction);
		}
		model.addAttribute("users",userService.findByRole("ROLE_USER"));
		
		List<PendingHourLogFile> pendingHourLogFiles = new ArrayList<PendingHourLogFile>();
		pendingHourLogFiles = userService.getPengindTimesheet(yearFile,userId);
		
		Integer approvedFileByFilter = 0;
		Integer newFileByFilter = 0;
		Integer rejectFileByFilter = 0;

		if(userId == null) {
			model.addAttribute("selectUserId","");
			approvedFileByFilter = hourLogFileRepository.countByApproveAndRejectByYear(yearFile,APPROVED, NOT_REJECTED);
			newFileByFilter = hourLogFileRepository.countByApproveAndRejectByYear(yearFile,NOT_APPROVED, NOT_REJECTED );
			rejectFileByFilter = hourLogFileRepository.countByApproveAndRejectByYear(yearFile,NOT_APPROVED, REJECTED );
		}
		else {
			model.addAttribute("selectUserId",userId);
			approvedFileByFilter = hourLogFileRepository.countByApproveAndRejectAndUserByYear(yearFile,APPROVED, NOT_REJECTED, userId);
			newFileByFilter = hourLogFileRepository.countByApproveAndRejectAndUserByYear(yearFile,NOT_APPROVED, NOT_REJECTED, userId);
			rejectFileByFilter = hourLogFileRepository.countByApproveAndRejectAndUserByYear(yearFile,NOT_APPROVED, REJECTED, userId);
			
		}
		model.addAttribute("approvedFileByFilter", approvedFileByFilter);
		model.addAttribute("newFileByFilter", newFileByFilter);
		model.addAttribute("rejectFileByFilter", rejectFileByFilter);
		model.addAttribute("pendingFileByFilter",pendingHourLogFiles.size());
		
		Integer totalFile = pendingHourLogFiles.size() + approvedFileByFilter + newFileByFilter + rejectFileByFilter;

		model.addAttribute("totalFile",totalFile);
		model.addAttribute("selectYear",yearFile);
		model.addAttribute("pendingFileByFilter",pendingHourLogFiles.size());
		model.addAttribute("selectedSort", adminTimeSheetAction);
		model.addAttribute("hourLogFiles",hourLogFiles);
		
		return "new/supervisor/time-sheet";
	}
	
	@RequestMapping(value="time-sheet/pending", method = RequestMethod.GET)
	public String timesheetPending(@RequestParam(name="yearFile",required=false) Integer yearFile,
			@RequestParam(name="userId",required=false) Integer userId, Model model) {
		
		User loginUser = (User) request.getSession().getAttribute("user");
		boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.TIMESHEET,Permission.READ, false);
		if(!grant){
			return "redirect:/supervisor/unauthorized";
		}
		
		if(yearFile == null)
			yearFile = Calendar.getInstance().get(Calendar.YEAR);
		
		List<PendingHourLogFile> pendingHourLogFiles = new ArrayList<PendingHourLogFile>();
		pendingHourLogFiles = userService.getPengindTimesheet(yearFile,userId);
		
		model.addAttribute("users",userService.findByRole("ROLE_USER"));
		Integer approvedFileByFilter = 0;
		Integer newFileByFilter = 0;
		Integer rejectFileByFilter = 0;


		if(userId == null) {
			model.addAttribute("selectUserId","");
			approvedFileByFilter = hourLogFileRepository.countByApproveAndRejectByYear(yearFile,APPROVED, NOT_REJECTED);
			newFileByFilter = hourLogFileRepository.countByApproveAndRejectByYear(yearFile,NOT_APPROVED, NOT_REJECTED );
			rejectFileByFilter = hourLogFileRepository.countByApproveAndRejectByYear(yearFile,NOT_APPROVED, REJECTED );
			
		}
		else {
			model.addAttribute("selectUserId",userId);
			approvedFileByFilter = hourLogFileRepository.countByApproveAndRejectAndUserByYear(yearFile,APPROVED, NOT_REJECTED, userId);
			newFileByFilter = hourLogFileRepository.countByApproveAndRejectAndUserByYear(yearFile,NOT_APPROVED, NOT_REJECTED, userId);
			rejectFileByFilter = hourLogFileRepository.countByApproveAndRejectAndUserByYear(yearFile,NOT_APPROVED, REJECTED, userId);
		}
		
		
		model.addAttribute("approvedFileByFilter", approvedFileByFilter);
		model.addAttribute("newFileByFilter", newFileByFilter);
		model.addAttribute("rejectFileByFilter", rejectFileByFilter);
		model.addAttribute("pendingFileByFilter",pendingHourLogFiles.size());
		
		Integer totalFile = pendingHourLogFiles.size() + approvedFileByFilter + newFileByFilter + rejectFileByFilter;

		model.addAttribute("totalFile",totalFile);
		model.addAttribute("selectYear",yearFile);
		model.addAttribute("pendingHourLogFiles",pendingHourLogFiles);
		
		return "new/supervisor/time-sheet-pending";
	}
	
	/**
	 * user Hour log download file
	 * @param id
	 * @return
	 */
	@RequestMapping(value="user-hour-log-file-print/print/{id}", method=RequestMethod.GET)
	@ResponseBody
	public void downloadUserHourFilePrint(@PathVariable(value="id") Integer id, HttpServletResponse response) {
		
		try {
			
			HourLogFile hourLogFile = hourLogFileRepository.findById(id);
			userService.addActivity("Time sheet file download", ActivityType.DOWNLOAD_FILE.toString() , hourLogFile.getUserDetail());
			response.setContentType("application/pdf");
	        response.setHeader("Content-Disposition", "inline;filename="+ hourLogFile.getUserDetail().getUser().getFirstName()+"_"+hourLogFile.getUserDetail().getUser().getLastName()+"/"+ hourLogFile.getUserDetail().getClientName() +"_"+hourLogFile.getStartDate().toString() +"_To_" +hourLogFile.getEndDate()+".pdf");
	        OutputStream outputStream = response.getOutputStream();
			String html = userService.viewHoursLogPdf(id);
        	HtmlConverter.convertToPdf(html, outputStream);
        	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
		
	/**
	 * user Hour log download file
	 * @param id
	 * @return
	 */
	@RequestMapping(value="user-hour-log-file/download/{id}", method=RequestMethod.GET,produces="application/zip")
	@ResponseBody
	public void downloadUserHourFile(@PathVariable(value="id") Integer id, HttpServletResponse response,
			HttpServletRequest request) {
		
		Company company = (Company) request.getSession().getAttribute("company");
		
		HourLogFile hourLogFile = hourLogFileRepository.findById(id);
		userService.addActivity("Time sheet file download", ActivityType.DOWNLOAD_FILE.toString() , hourLogFile.getUserDetail());
		
		response.setContentType(MediaType.ALL_VALUE);
		
		List<HourLogFilePath> hourLogFilePaths = hourLogFilePathRepository.findByHourLogFile(hourLogFile);
				
        response.setHeader("Content-Disposition", "attachment;filename="+ hourLogFile.getUserDetail().getUser().getFirstName()+"_"+hourLogFile.getUserDetail().getUser().getLastName()+"/"+ hourLogFile.getUserDetail().getClientName() +"_"+hourLogFile.getStartDate().toString() +"_To_" +hourLogFile.getEndDate()+".zip");
        response.setStatus(HttpServletResponse.SC_OK);
        
        try {
        	
        	OutputStream outStream = response.getOutputStream();
        	
        	ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(outStream));
	        
        	if(!CollectionUtils.isEmpty(hourLogFilePaths)) {
	            for (HourLogFilePath file : hourLogFilePaths) {
	            	
	            	// construct the complete absolute path of the file
	            	File f = new File(FILE_PATH + company.getFileFolder() + file.getFilePath());
	            	zos.putNextEntry(new ZipEntry(file.getFileOriginalName()));
	            	
			        FileInputStream inputStream = new FileInputStream(f);
			        
			        IOUtils.copy(inputStream, zos);

			        inputStream.close();
			        
			        zos.closeEntry();
	            }
        	}
        	
        	String html = userService.viewHoursLogPdf(id);
        	HtmlConverter.convertToPdf(html, new FileOutputStream(FILE_PATH + company.getFileFolder() + "/hourslog_table_.pdf"));
        	File file = new File(FILE_PATH + company.getFileFolder() + "/hourslog_table_.pdf");
        	zos.putNextEntry(new ZipEntry("hourslog_table_"+ new Date()+".pdf"));
	        FileInputStream inputStream = new FileInputStream(file);
	        IOUtils.copy(inputStream, zos);
	        inputStream.close();
	        zos.closeEntry();
	        file.delete();
	        
            zos.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
		
	}
	
	/**
	 * get basic details of user
	 * @param id
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="basic-detail/{id}", method = RequestMethod.GET)
	public String UserDetail(@PathVariable("id")Integer id, ModelMap modelMap) { 
		UserDetail userDetail = userDetailsRepository.findByUserDetailId(id);
		modelMap.addAttribute("userDetail", userDetail);
		return "new/admin/hour-log :: user-detail";
	}

	/**
	 * check-date-approve
	 * @param date
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping("check-date-approve")
	@ResponseBody
	public String addHours(@RequestParam long date, RedirectAttributes redirectAttributes) {
		return hoursLogService.checkDateApprove(date);
	}
	
	/**
	 * Add hours
	 * @param dailyHours
	 * @param extraHours
	 * @param date
	 * @return
	 */
	@RequestMapping(value = "add-hours", method = RequestMethod.POST)
	@ResponseBody
	public String addHours(@RequestParam String dailyHours, @RequestParam String extraHours, @RequestParam long date, @RequestParam Integer userDetailId) {
		if(userDetailId == null || userDetailId == 0)
			return "user not found";
		return hoursLogService.saveHours(dailyHours, extraHours, date, userDetailId);
	}
	
	/**
	 * Get add hours list
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "get-hours-list/{userDetailId}", method = RequestMethod.GET)
	@ResponseBody
	public Object getAddHoursList(@PathVariable("userDetailId") Integer userDetailId) {
		return hoursLogService.getAddHoursList(userDetailId);
	}
	
	/**
	 * send mail to user 
	 * @param message
	 * @param email
	 * @param subject
	 * @return
	 */
	@RequestMapping(value = "timesheet-view", method = RequestMethod.GET)
	public String timeseetView(@RequestParam(name="id") Integer id,
			ModelMap modelMap) {
			
		HourLogFile hourLogFile  = hourLogFileRepository.findById(id);
		AddUserTimeSheet addUserTimeSheet  = userService.getCalendarResponse(hourLogFile.getUserDetail().getUserDetailId(),
				hourLogFile.getStartDate(), hourLogFile.getEndDate());
		modelMap.addAttribute("hourLogFile", hourLogFile);
		modelMap.addAttribute("timesheetId", id);
		modelMap.addAttribute("addUserTimeSheet", addUserTimeSheet);
		
		UserDetail userDetail = userDetailsRepository.findByUserDetailId(hourLogFile.getUserDetail().getUserDetailId());
		modelMap.addAttribute("userDetail", userDetail);
		
		List<Template> templatesApproved = new ArrayList<Template>();
		List<Template> templatesApprovedUnSet = templateService.getMailByType(MailTemplateType.approvalTimesheet);
		templatesApproved = templateService.setApproved(templatesApprovedUnSet, userDetail.getUser(),
				addUserTimeSheet.getStartDate(),addUserTimeSheet.getEndDate(),
				userDetail.getUserDetailId());
		
		List<Template> templatesRejected = new ArrayList<Template>();
		List<Template> templatesRejectUnSet = templateService.getMailByType(MailTemplateType.rejectedTimesheet);
		templatesRejected = templateService.setRejected(templatesRejectUnSet, userDetail.getUser(),
				addUserTimeSheet.getStartDate(),addUserTimeSheet.getEndDate(),
				userDetail.getUserDetailId());
		
		modelMap.addAttribute("templatesApproved", templatesApproved);
		modelMap.addAttribute("templatesRejected", templatesRejected);
		return "new/supervisor/add-time-sheet";
	}
	
	/**
	 * get user time sheet add
	 * @param request
	 * @param userDetailId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@RequestMapping(value="add-time-sheet", method = RequestMethod.POST)
	public String getTimeSheetAdd(@ModelAttribute("addUserTimeSheet") AddUserTimeSheet addUserTimeSheet,
			@RequestParam(name= "timesheetId") Integer timesheetId,
			@RequestParam(name= "email") String email,
			@RequestParam(name= "isApprove") boolean isApprove,
			@RequestParam(name= "rejectReason",required = false) String reason,
			@RequestParam(name= "ccEmail", required = false) String ccEmail,
			@RequestParam(name= "subject") String subject,
			@RequestParam(name= "remark") String remark,
			RedirectAttributes redirectAttributes) {
		
		User loginUser = (User) request.getSession().getAttribute("user");
		boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.TIMESHEET,Permission.UPDATE, false);
		if(!grant){
			return "redirect:/supervisor/unauthorized";
		}
		
		if(StringUtils.isEmpty(addUserTimeSheet.getStartDate()) || StringUtils.isEmpty(addUserTimeSheet.getEndDate())) {
			
			redirectAttributes.addFlashAttribute("error", "Please fill start Date and End Date");
			return "redirect:/user/add-time-sheet";
		}
		
		try {
			supervisorService.aproveOrRejectFile(timesheetId, isApprove, reason, remark);
		
			for(CalendarResponse calendarResponse : addUserTimeSheet.getCalendarResponse()) {
				
				userService.setDefaultCalendar(calendarResponse.getDailyHours(),calendarResponse.getExtraHours(),
					calendarResponse.getVacationHours(), addUserTimeSheet.getUserDetailId(),
					new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy").parse(calendarResponse.getDate()),
					calendarResponse.getNewNotes());
			}
			
//			userService.sendEmailToAdmin(addUserTimeSheet.getDescription(), subject, null);
			List<User> users = userRepository.findByRoleOrRole("ROLE_ADMIN", "ROLE_SUPERVISOR");
			for(User user: users) {
				ccEmail = ccEmail + ',' + user.getEmail();
			}
			
			userService.sendEmailToUser(addUserTimeSheet.getDescription(), email, subject, ccEmail, null);
			
			if(isApprove)
				redirectAttributes.addFlashAttribute("success", "Time sheet approved successfully");
			else
				redirectAttributes.addFlashAttribute("success", "Time sheet rejected successfully");
			return "redirect:/supervisor/time-sheet";
		
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Please, try again");
			e.printStackTrace();
			return "redirect:/supervisor/time-sheet";
		}
	}
	
	/**
	 * get shedular
	 * @param message
	 * @param email
	 * @param subject
	 * @return
	 */
	@RequestMapping(value = "time-sheet-schedular", method = RequestMethod.GET)
	public String userSchedularTimeSheet(@RequestParam(name="month", required=false) Integer month,
			@RequestParam(name="year", required=false) Integer year,
			ModelMap modelMap) throws JsonProcessingException{
		
		User loginUser = (User) request.getSession().getAttribute("user");
		boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.TIME_SHEET_SCHEDULAR,Permission.READ, false);
		if(!grant){
			return "redirect:/supervisor/unauthorized";
		}
		
		if(year == null)
			year = new Date().getYear() + 1900;
		
		if(month == null)
			month = new Date().getMonth();
		List<UserTimeSheet> userTimeSheets = supervisorService.getUserTimesheet(month, year);
		
		modelMap.addAttribute("userTimeSheets", userTimeSheets);
		modelMap.addAttribute("selectedYear", year);
		modelMap.addAttribute("selectedMonth",Month.getMonth(month));
		return "new/supervisor/submittedTimesheet";
	}
	/**
	 * get shedular
	 * @param message
	 * @param email
	 * @param subject
	 * @return
	 */
	@RequestMapping(value = "unauthorized", method = RequestMethod.GET)
	public String userSchedularTimeSheet(ModelMap modelMap){
		
		return "new/unauthorized";
	}
	

	/**
	 * get user time sheet add
	 * @param request
	 * @param userDetailId
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping(value="add-user-time-sheet", method = RequestMethod.GET)
	public String setTimeSheetAdd(HttpServletRequest request,
			@RequestParam(name = "startDate", required = false) Date startDate,
			@RequestParam(name = "endDate", required = false) Date endDate,
			@RequestParam(name = "year", required = false) Integer year,
			@RequestParam(name = "user", required = false) Integer userId,
			RedirectAttributes redirectAttributes,
			ModelMap modelMap) throws ParseException {
		
		User loginUser = (User) request.getSession().getAttribute("user");
		boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.TIMESHEET,Permission.CREATE, false);
		if(!grant){
			return "redirect:/supervisor/unauthorized";
		}	
		
		List<User> users = userRepository.findByRole("ROLE_USER");
		User user = new User();
		
		if(userId != null) {
			user = userRepository.findById(userId);
		} else {
			user = users.get(0);
		}
		
		Integer userDetailId = user.getClientActiveId();
		UserDetail userDetail = null;
		int startYear = new Date().getYear() + 1900;
		if(userDetailId != null) {
			userDetail = userDetailsRepository.findByUserDetailId(userDetailId);
			startYear = Utils.getYearFromDate(userDetail.getStartDate());
		}
		SimpleDateFormat simpleDateformat3 = new SimpleDateFormat("MM/dd/yyyy");
		
		//get date list according to selected period of time
		List<UserTimeSheetDate> userTimeSheetDates = userService.getDateList(year, userDetailId, startDate);
		
		if(CollectionUtils.isEmpty(userTimeSheetDates) && userDetailId != null) {
			modelMap.addAttribute("users", users);
			modelMap.addAttribute("userSelect", user);
			modelMap.addAttribute("userDetail", userDetail);
			AddUserTimeSheet addUserTimeSheet = new AddUserTimeSheet();
			addUserTimeSheet.setCalendarResponse(new ArrayList<CalendarResponse>());
			addUserTimeSheet.setUserDetailId(userDetailId);
			modelMap.addAttribute("addUserTimeSheet", addUserTimeSheet);
			modelMap.addAttribute("templates", new ArrayList<Template>());
			modelMap.addAttribute("success", "Time sheet are not available or all are approved for this year");
			return "new/supervisor/add-user-time-sheet";
		}
		if(startDate == null) {
			
			//if date is null then select current date -> if current date approve then select next date
			if(userDetailId != null && year == null) {
				//when only userDetailId selected
				startDate = userService.getValidStartDate(userTimeSheetDates, userDetailId);
				
			} else if (year != null && year == new Date().getYear() + 1900){
				
				//when only userDetailId selected & current year selected
				startDate = userService.getValidStartDate(userTimeSheetDates, userDetailId);
				
			} if (year != null && year != new Date().getYear() + 1900) {
				
				//when only userDetailId selected & year selected
				startDate = simpleDateformat3.parse(userTimeSheetDates.get(0).getDateString());
			}
		}
		
		AddUserTimeSheet addUserTimeSheet = userService.getCalendarResponse(userDetailId, startDate, endDate);
		
		List<HourLogFile> hourLogFile = new ArrayList<>();

		if(userDetailId != null) {
			hourLogFile = hourLogFileRepository.findByStartDateAndEndDateAndUserDetail(simpleDateformat3.parse(addUserTimeSheet.getStartDate()),
				simpleDateformat3.parse(addUserTimeSheet.getEndDate()),
				userDetailsRepository.findByUserDetailId(userDetailId));
			modelMap.addAttribute("userDetail", userDetailsRepository.findByUserDetailId(userDetailId));			
			
			
		}else {
			modelMap.addAttribute("userDetail", null);
			modelMap.addAttribute("error", "Any client are not active");
		}
		
		if(CollectionUtils.isEmpty(hourLogFile)) {
			modelMap.addAttribute("resubmit", false);
			modelMap.addAttribute("remarkOld","" );


		}else {
			modelMap.addAttribute("resubmit", true);
			modelMap.addAttribute("remarkOld",hourLogFile.get(0).getRemark());
	
		}
		
		modelMap.addAttribute("userTimeSheetDates", userTimeSheetDates);
		modelMap.addAttribute("addUserTimeSheet", addUserTimeSheet);
		modelMap.addAttribute("users", users);
		modelMap.addAttribute("userSelect", user);
		
		
		if(year != null || startDate == null)
			modelMap.addAttribute("selectedYear", year);
		else
			modelMap.addAttribute("selectedYear", startDate.getYear() + 1900);
		
		List<Template> templates = new ArrayList<Template>();
		if(userDetailId != null && userDetailsRepository.findByUserDetailId(userDetailId) != null) {
			List<Template> templatesUnSet = templateService.getMailByType(MailTemplateType.submission);
			templates = templateService.setSubmission(templatesUnSet, user,startDate,endDate,userDetailId );
		}
		
		modelMap.addAttribute("templates", templates);
		modelMap.addAttribute("startYear", startYear);
		return "new/supervisor/add-user-time-sheet";
	}
	

	/**
	 * get user time sheet add
	 * @param request
	 * @param userDetailId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@RequestMapping(value="add-user-time-sheet", method = RequestMethod.POST)
	public String getTimeSheetAdd(@ModelAttribute("addUserTimeSheet") AddUserTimeSheet addUserTimeSheet,
			@RequestParam(name="file", required = false) List<MultipartFile> files,
			@RequestParam(name= "email") String email,
			@RequestParam(name= "ccEmail") String ccEmail,
			@RequestParam(name= "subject") String subject,
			@RequestParam(name = "remark", required = false) String remark,
			HttpServletRequest request,
			RedirectAttributes redirectAttributes) {
		
		User loginUser = (User) request.getSession().getAttribute("user");
		boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.TIMESHEET,Permission.CREATE, false);
		if(!grant){
			return "redirect:/supervisor/unauthorized";
		}	
		
		Company company = (Company) request.getSession().getAttribute("company");
		UserDetail userDetail = userDetailsRepository.findByUserDetailId(addUserTimeSheet.getUserDetailId());
			
		if(StringUtils.isEmpty(addUserTimeSheet.getStartDate()) || StringUtils.isEmpty(addUserTimeSheet.getEndDate())) {
			
			redirectAttributes.addFlashAttribute("error", "Please fill start Date and End Date");
			return "redirect:/supervisor/add-user-time-sheet?user="+userDetail.getUser().getId();
		}
		String year = addUserTimeSheet.getStartDate().split("/")[2];
		try {
			List<String[]> filePath = new ArrayList<>();
			File file1 = null;
			if(!CollectionUtils.isEmpty(files)) {
				int i = 0;
				for(MultipartFile file : files) {
					
					if (!new File(FILE_PATH + company.getFileFolder()).exists())
						new File(FILE_PATH + company.getFileFolder()).mkdir();
					
					if (!new File(FILE_PATH + company.getFileFolder() + "/User/").exists())
						new File(FILE_PATH + company.getFileFolder() + "/User/").mkdir();
					
					if (!new File(FILE_PATH + company.getFileFolder() + "/User/"+userDetail.getUser().getFileFolder()+"/").exists())
						new File(FILE_PATH + company.getFileFolder() + "/User/"+userDetail.getUser().getFileFolder()+"/").mkdir();
					
					if (!new File(FILE_PATH + company.getFileFolder() + "/User/" + userDetail.getUser().getFileFolder() + "/Timesheet/").exists())
						new File(FILE_PATH + company.getFileFolder() +  "/User/" + userDetail.getUser().getFileFolder() + "/Timesheet/").mkdir();
					
					if (!new File(FILE_PATH + company.getFileFolder() + "/User/" + userDetail.getUser().getFileFolder()+ "/Timesheet/"+userDetail.getFileFolder()+"/").exists())
						new File(FILE_PATH + company.getFileFolder() +  "/User/" + userDetail.getUser().getFileFolder() + "/Timesheet/"+userDetail.getFileFolder()+"/").mkdir();
					
					if(file.getOriginalFilename().length() > 0) {
						
						String filePath1 = "/User/" + userDetail.getUser().getFileFolder() + "/Timesheet/" + userDetail.getFileFolder()+"/" + new Date().getTime()+ i + "Timesheet" + 
								addUserTimeSheet.getUserDetailId() + 
								file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."));
						
						file.transferTo(new File(FILE_PATH + company.getFileFolder() + filePath1));
						file1 = new File(FILE_PATH + company.getFileFolder() + filePath1);
						String[] fileName = new String[2];
						fileName[0] = filePath1;
						fileName[1] = file.getOriginalFilename();
						filePath.add(fileName);
					}
					i++;
				}
			}	
			userService.saveUserHourFile(filePath, addUserTimeSheet.getUserDetailId(),
				new SimpleDateFormat("MM/dd/yyyy").parse(addUserTimeSheet.getStartDate()),
				new SimpleDateFormat("MM/dd/yyyy").parse(addUserTimeSheet.getEndDate()),
				addUserTimeSheet.getDescription(), remark);
		
			for(CalendarResponse calendarResponse : addUserTimeSheet.getCalendarResponse()) {
				
				userService.setDefaultCalendar(calendarResponse.getDailyHours(),calendarResponse.getExtraHours(),
					calendarResponse.getVacationHours(), addUserTimeSheet.getUserDetailId(),
					new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy").parse(calendarResponse.getDate()),calendarResponse.getNewNotes());
			}
			
			List<User> users = userRepository.findByRoleOrRole("ROLE_ADMIN", "ROLE_SUPERVISOR");
			for(User user: users) {
				ccEmail = ccEmail + ',' + user.getEmail();
			}
			userService.sendEmailToUser(addUserTimeSheet.getDescription(), email, subject, ccEmail, file1);
			
			redirectAttributes.addFlashAttribute("success", "Time sheet save successfully");
			return "redirect:/supervisor/add-user-time-sheet?user="+userDetail.getUser().getId() + "&year=" + year;
		
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Time sheet does not upload, try again");
			e.printStackTrace();
			return "redirect:/supervisor/add-user-time-sheet?user="+userDetail.getUser().getId()+ "&year=" + year;
		}
	}
	
	/**
	 * get user time sheet add
	 * @param request
	 * @param userDetailId
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping(value="add-time-sheet/changeDate", method = RequestMethod.GET)
	public String changeDateTimesheet(HttpServletRequest request,
			@RequestParam(name = "startDate", required = false) Date startDate,
			@RequestParam(name = "endDate", required = false) Date endDate,
			@RequestParam(name = "year", required = false) Integer year,
			@RequestParam(name = "user", required = false) Integer userId,
			ModelMap modelMap) throws ParseException {
		
		List<User> users = userRepository.findByRole("ROLE_USER");
		User user = new User();
		
		if(userId != null) {
			user = userRepository.findById(userId);
		} else {
			user = users.get(0);
		}
		
		Integer userDetailId = user.getClientActiveId();
		SimpleDateFormat simpleDateformat3 = new SimpleDateFormat("MM/dd/yyyy");
		
		//get date list according to selected period of time
		List<UserTimeSheetDate> userTimeSheetDates = userService.getDateList(year, userDetailId, startDate);
		
		if(CollectionUtils.isEmpty(userTimeSheetDates) && userDetailId != null) {
			modelMap.addAttribute("users", users);
			modelMap.addAttribute("userSelect", user);
			modelMap.addAttribute("userDetail", userDetailsRepository.findByUserDetailId(userDetailId));
			AddUserTimeSheet addUserTimeSheet = new AddUserTimeSheet();
			addUserTimeSheet.setCalendarResponse(new ArrayList<CalendarResponse>());
			addUserTimeSheet.setUserDetailId(userDetailId);
			modelMap.addAttribute("addUserTimeSheet", addUserTimeSheet);
			modelMap.addAttribute("success", "Time sheet are not available or all are approved for this year");
			return "new/supervisor/add-user-time-sheet";
		}
		if(startDate == null) {
			
			//if date is null then select current date -> if current date approve then select next date
			if(userDetailId != null && year == null) {
				//when only userDetailId selected
				startDate = userService.getValidStartDate(userTimeSheetDates, userDetailId);
				
			} else if (year != null && year == new Date().getYear() + 1900){
				
				//when only userDetailId selected & current year selected
				startDate = userService.getValidStartDate(userTimeSheetDates, userDetailId);
				
			} if (year != null && year != new Date().getYear() + 1900) {
				
				//when only userDetailId selected & year selected
				startDate = simpleDateformat3.parse(userTimeSheetDates.get(0).getDateString());
			}
		}
		
		AddUserTimeSheet addUserTimeSheet = userService.getCalendarResponse(userDetailId, startDate, endDate);
		
		List<HourLogFile> hourLogFile = new ArrayList<>();
		
		if(userDetailId != null) {
			hourLogFile = hourLogFileRepository.findByStartDateAndEndDateAndUserDetail(simpleDateformat3.parse(addUserTimeSheet.getStartDate()),
				simpleDateformat3.parse(addUserTimeSheet.getEndDate()),
				userDetailsRepository.findByUserDetailId(userDetailId));
			modelMap.addAttribute("userDetail", userDetailsRepository.findByUserDetailId(userDetailId));
			
		}else {
			modelMap.addAttribute("userDetail", null);
			modelMap.addAttribute("error", "Any client are not active");
		}
		if(CollectionUtils.isEmpty(hourLogFile))
			modelMap.addAttribute("resubmit", false);
		else
			modelMap.addAttribute("resubmit", true);
		modelMap.addAttribute("userTimeSheetDates", userTimeSheetDates);
		modelMap.addAttribute("addUserTimeSheet", addUserTimeSheet);
		
		if(year != null || startDate == null)
			modelMap.addAttribute("selectedYear", year);
		else
			modelMap.addAttribute("selectedYear", startDate.getYear() + 1900);
		
		modelMap.addAttribute("userSelect", user);
		
		return "new/supervisor/add-user-time-sheet ::addTimeSheet";
	}
	
	@RequestMapping(value = "hours-month/{id}/{month}/{year}", method = RequestMethod.GET)
	public String hoursMonth(@PathVariable("month") Integer month,
			@PathVariable("year") Integer year,@PathVariable("id") Integer id,
			ModelMap modelMap) {
		
		User loginUser = (User) request.getSession().getAttribute("user");
		LinkedHashMap<String, Map<String, Double>> map = userService.getHourLog(id, year,null);
		UserDetail userDetail = userDetailsRepository.findByUserDetailId(id);
		
		Month monthNum =  Month.getMonth(month);
		Map<String, Double> mHours = map.get(monthNum.displayLabel);
		List<DefaultCalendarResponse> defaultCalendarResponses = userService.getDefaultCalendar(id, month, year) ;
		modelMap.addAttribute("monthNum",monthNum);
		modelMap.addAttribute("mHours",mHours);
		modelMap.addAttribute("defaultCalendarResponses",defaultCalendarResponses);
		
		modelMap.addAttribute("selectYear",year);
		modelMap.addAttribute("hourLogs",map);
		modelMap.addAttribute("userDetail",userDetail);
		return "new/admin/hour-log ::user-month-hours-detail";
	}
	
	@RequestMapping(value = "schedular/day-hours-form", method = RequestMethod.GET)
	public String dayHoursForm(@RequestParam(name="day") Integer day,
			@RequestParam(name="month") Integer month,
			@RequestParam(name="year") Integer year,
			ModelMap modelMap) {
		
		Schedular schedular = schedularRepository.findBySchedular(day, month+1, year);
		if(schedular == null) {
			schedular = new Schedular();
			Calendar calendar = Calendar.getInstance();
			calendar = Utils.getDate(year, month, day);
			Date date = calendar.getTime();
			schedular.setHoursDate(date);
			schedular.setDailyHours((float) 0.0);
			schedular.setExtraHours((float) 0.0);
			schedular.setVacationHours((float) 0.0);
		}
		modelMap.addAttribute("schedular", schedular);
		return "new/supervisor/daySchedularFragment";
	}
	
	/**
	 * get shedular
	 * @param message
	 * @param email
	 * @param subject
	 * @return
	 */
	@RequestMapping(value = "schedular", method = RequestMethod.GET)
	public String getSchedularNew(@RequestParam(name="year", required=false) Integer year,
			ModelMap modelMap) {
		
		User loginUser = (User) request.getSession().getAttribute("user");
		boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.ADD_SCHEDULAR,Permission.CREATE, false);
		if(!grant){
			return "redirect:/supervisor/unauthorized";
		}
		
		if(year == null)
			year = new Date().getYear() + 1900;
		List<SchedularResponse> schedularResponses = supervisorService.getSchedular(year);
		List<Schedular> dayOffSchedular = schedularRepository.findByYearAndDayOff(year);
		dayOffSchedular.sort((d1,d2) -> d1.getHoursDate().compareTo(d2.getHoursDate()));
		 
		List<User> users = userRepository.findByRoleAndActive("ROLE_USER",1);
		
		Map<Integer, Map<String, String> > offHours = supervisorService.getoffHours(schedularResponses);
		
		modelMap.addAttribute("users", users);
		modelMap.addAttribute("offHours", offHours);
		modelMap.addAttribute("schedular", schedularResponses);
		modelMap.addAttribute("selectedYear", year);
		modelMap.addAttribute("dayOffSchedular", dayOffSchedular);
		return "/new/supervisor/schedular";
	}
	
	/**
	 * get shedular
	 * @param message
	 * @param email
	 * @param subject
	 * @return
	 */
	@RequestMapping(value = "schedular-save", method = RequestMethod.POST)
	public ResponseEntity<Response> setSchedularNew(Schedular schedular,String hoursDateString,
			RedirectAttributes redirectAttributes) throws ParseException {
		
		if(permissionService.getPermissionPlan().isSchedularCanSet() != true) {
			return ResponseGenerator.generateResponse(new Response("unauthorized", null), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		User loginUser = (User) request.getSession().getAttribute("user");
		boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.ADD_SCHEDULAR,Permission.CREATE, false);
		if(!grant){
			return ResponseGenerator.generateResponse(new Response("unauthorized", null), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		try {
			
			Date date = new SimpleDateFormat("dd-MM-yyyy").parse(hoursDateString); 
			schedular.setHoursDate(date);
			supervisorService.setSchedular(schedular);
			
			return ResponseGenerator.generateResponse(new Response("successfully", schedular), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseGenerator.generateResponse(new Response("Please try again", null), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * get shedular
	 * @param message
	 * @param email
	 * @param subject
	 * @return
	 */
	@RequestMapping(value = "schedular-send-email", method = RequestMethod.GET)
	public String getSchedular(@RequestParam(name="month") Integer month,
			@RequestParam(name="year") Integer year,
			ModelMap modelMap) {
		
		SchedularResponse schedularResponses = supervisorService.getSchedularByMonth(month, year);
		
		List<Template> templates = new ArrayList<Template>();
		List<Template> templatesUnSet = templateService.getMailByType(MailTemplateType.schedularTimesheet);
		templates = templateService.setSchedular(templatesUnSet,schedularResponses.getStartDate(),schedularResponses.getEndDate());
		
		List<User> users = userRepository.findByRoleAndActive("ROLE_USER",1);
		
		modelMap.addAttribute("users", users);
		modelMap.addAttribute("schedular", schedularResponses);
		modelMap.addAttribute("selectedYear", year);
		modelMap.addAttribute("selectedMonth",month);
		modelMap.addAttribute("templates",templates);
		return "/new/supervisor/schedularEmail";
	}
	/**
	 * get shedular
	 * @param message
	 * @param email
	 * @param subject
	 * @return
	 */
	@RequestMapping(value = "schedular-send-email", method = RequestMethod.POST)
	public String setSchedular(@RequestParam(name="description", required=false) String description,
			@RequestParam(name="ccEmail", required=false) String ccEmail,
			@RequestParam(name="toEmail", required=false) String toEmail,
			@RequestParam(name="subject", required=false) String subject,
			@RequestParam(name="month") Integer month,
			@RequestParam(name="year") Integer year,
			RedirectAttributes redirectAttributes) throws ParseException {
		
		User loginUser = (User) request.getSession().getAttribute("user");
		boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.ADD_SCHEDULAR,Permission.CREATE, false);
		if(!grant){
			return "redirect:/supervisor/unauthorized";
		}
		
		Company company = (Company) request.getSession().getAttribute("company");
		
		SchedularResponse schedularResponses = supervisorService.getSchedularByMonth(month, year);
		
		if(!StringUtils.isEmpty(toEmail)) {
			String html = userService.viewSchedularPdf(schedularResponses);
        	try {
				HtmlConverter.convertToPdf(html, new FileOutputStream(FILE_PATH + company.getFileFolder() + "/schedular_table_.pdf"));
				File file = new File(FILE_PATH + company.getFileFolder() + "/schedular_table_.pdf");
				userService.sendEmailToUser(description, toEmail, subject, ccEmail, file);
//				file.delete();
			} catch (FileNotFoundException e) {
				
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
        	
		}
		
		redirectAttributes.addFlashAttribute("success","Schedule send mail successfully");
		return "redirect:/supervisor/schedular?year="+year;
	}
	
	@RequestMapping(value="configuration",method=RequestMethod.GET)
	public String getConfig(ModelMap modelMap) {
		User user = (User) request.getSession().getAttribute("user");
		User userExists = userService.findUserByEmail(user.getEmail());
		modelMap.addAttribute("userLogin", userExists);
		return "new/supervisor/companyConfiguration";
	}
	
	@RequestMapping(value="hours-log-file/delete",method=RequestMethod.GET)
	public ResponseEntity<Response> deleteFile(ModelMap modelMap, Integer id, Integer hourLogId) {
		HourLogFile hourLogFile = hourLogFileRepository.findById(hourLogId);
		HourLogFilePath hourLogFilePath = hourLogFilePathRepository.findByIdAndHourLogFile(id,hourLogFile);
		if(hourLogFilePath != null) {
			hourLogFilePathRepository.delete(hourLogFilePath);
		}
		List<HourLogFilePath> filePaths = hourLogFilePathRepository.findByHourLogFile(hourLogFile);
		if(CollectionUtils.isEmpty(filePaths)) {
			hourLogFile.setFilePath(null);
			hourLogFileRepository.save(hourLogFile);
		}
		return ResponseGenerator.generateResponse(new Response("successfully", hourLogFile), HttpStatus.OK);
	}
	
	@RequestMapping(value="time-sheet-more-file",method=RequestMethod.POST)
	public ResponseEntity<Response> timesheetMoreFile(@RequestParam(name="file") List<MultipartFile> file,
			Integer hourLogFileId) {
		
		User loginUser = (User) request.getSession().getAttribute("user");
		boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.TIMESHEET,Permission.CREATE, false);
		if(!grant){
			return ResponseGenerator.generateResponse(new Response("unauthorized", null), HttpStatus.INTERNAL_SERVER_ERROR);
		}	
		
		HourLogFile hourLogFile = hourLogFileRepository.findById(hourLogFileId);
		Company company = (Company) request.getSession().getAttribute("company");
		List<String[]> filePath = new ArrayList<>();
		try {
			
			File file1 = null;
			if(!CollectionUtils.isEmpty(file)) {
				
				int i = 0;
				for(MultipartFile f : file) {
					
					if (!new File(FILE_PATH + company.getFileFolder()).exists())
						new File(FILE_PATH + company.getFileFolder()).mkdir();
					
					if (!new File(FILE_PATH + company.getFileFolder() + "/User/").exists())
						new File(FILE_PATH + company.getFileFolder() +  "/User/").mkdir();
					
					if (!new File(FILE_PATH + company.getFileFolder() + "/User/" + hourLogFile.getUserDetail().getUser().getFileFolder()).exists())
						new File(FILE_PATH + company.getFileFolder() + "/User/" + hourLogFile.getUserDetail().getUser().getFileFolder()).mkdir();
					
					if (!new File(FILE_PATH + company.getFileFolder() +  "/User/" + hourLogFile.getUserDetail().getUser().getFileFolder() + "/Timesheet/").exists())
						new File(FILE_PATH + company.getFileFolder() +  "/User/" + hourLogFile.getUserDetail().getUser().getFileFolder()+ "/Timesheet/").mkdir();
					
					if (!new File(FILE_PATH + company.getFileFolder() +  "/User/" + hourLogFile.getUserDetail().getUser().getFileFolder() + "/Timesheet/"+hourLogFile.getUserDetail().getFileFolder()+"/").exists())
						new File(FILE_PATH + company.getFileFolder() +  "/User/" + hourLogFile.getUserDetail().getUser().getFileFolder()+ "/Timesheet/"+hourLogFile.getUserDetail().getFileFolder()+"/").mkdir();
					
					if(f.getOriginalFilename().length() > 0) {
						
						String filePath1 = "/User/" + hourLogFile.getUserDetail().getUser().getFileFolder() + "/Timesheet/" + hourLogFile.getUserDetail().getFileFolder()+"/" + new Date().getTime()+i + "Timesheet" + 
								hourLogFile.getUserDetail().getFileFolder() + 
								f.getOriginalFilename().substring(f.getOriginalFilename().indexOf("."));
						
						
							f.transferTo(new File(FILE_PATH + company.getFileFolder() + filePath1));
						
						file1 = new File(FILE_PATH + company.getFileFolder() + filePath1);
						String[] fileName = new String[2];
						fileName[0] = filePath1;
						fileName[1] = f.getOriginalFilename();
						filePath.add(fileName);
					}
					i++;
				}
			}	
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
			
		for(String[] path : filePath) {
			HourLogFilePath hourLogFilePath = new HourLogFilePath();
			hourLogFilePath.setFileOriginalName(path[1]);
			hourLogFilePath.setFilePath(path[0]);
			hourLogFilePath.setHourLogFile(hourLogFile);
			hourLogFilePath.setAdminAddedFile(true);
			hourLogFilePathRepository.save(hourLogFilePath);
		}
			hourLogFile.setFileOriginalName("Multiple file");
			hourLogFile.setFilePath("Multiple file");
			hourLogFileRepository.save(hourLogFile);
		return ResponseGenerator.generateResponse(new Response("successfully", hourLogFile), HttpStatus.OK);
	}
}
