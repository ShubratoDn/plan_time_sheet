package com.aim.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.aim.entity.Template;
import com.aim.entity.User;
import com.aim.entity.UserDetail;
import com.aim.enums.ActivityType;
import com.aim.enums.Functionality;
import com.aim.enums.MailTemplateType;
import com.aim.enums.Permission;
import com.aim.enums.TimeSheetPeriod;
import com.aim.enums.UserDetailsType;
import com.aim.model.ResponseGenerator;
import com.aim.repository.HourLogFilePathRepository;
import com.aim.repository.HourLogFileRepository;
import com.aim.repository.HoursLogRepository;
import com.aim.repository.UserDetailsRepository;
import com.aim.repository.UserRepository;
import com.aim.response.AddUserTimeSheet;
import com.aim.response.AdminUserHoursChart;
import com.aim.response.CalendarResponse;
import com.aim.response.MonthHoursLogResponse;
import com.aim.response.UserTimeSheetDate;
import com.aim.service.AdminService;
import com.aim.service.PermissionService;
import com.aim.service.SupervisorService;
import com.aim.service.TemplateService;
import com.aim.service.UserService;
import com.aim.utils.Response;
import com.aim.utils.Utils;

@Controller
@RequestMapping("/user/")
public class UserController extends DataMenuController {

	@Autowired
	private UserService userService;

	@Autowired
	private UserDetailsRepository userDetailsRepository;

	@Autowired
	private HourLogFileRepository hourLogFileRepository;

	@Autowired
	private HoursLogRepository hoursLogRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AdminService adminService;

	@Autowired
	private HourLogFilePathRepository hourLogFilePathRepository;

	@Autowired
	private TemplateService templateService;

	@Autowired
	private PermissionService permissionService;

	@Autowired
	private SupervisorService supervisorService;

	@Value("${file.path}")
	private String FILE_PATH;

	private final Boolean APPROVED = true;
	private final Boolean NOT_APPROVED = false;
	private final Boolean NOT_REJECTED = false;
	private final Boolean REJECTED = true;

	/**
	 * home page
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("home")
	public String userHome(Model model, HttpServletRequest request,
			@RequestParam(name = "year", required = false) Integer year,
			@RequestParam(name = "client", required = false) Integer userDetailId,
			@RequestParam(name = "user", required = false) Integer userId,
			@RequestParam(name = "page", required = false, defaultValue = "1") Integer pageNo) {

		User user1 = (User) request.getSession().getAttribute("user");

		boolean grant = permissionService.grantPermission(user1.getRole(), Functionality.HOURS_DASHBOARD,
				Permission.READ, false);
		if (!grant && user1.getRole().equals("ROLE_SUPERVISOR")) {
			return "redirect:/supervisor/unauthorized";
		}

		User user = new User();
		List<User> userList = new ArrayList<>();

		if (user1.getRole().equals("ROLE_USER")) {
			user = (User) request.getSession().getAttribute("user");
		} else {
			userList = userRepository.findByRole("ROLE_USER");
			if (userId != null) {
				user = userRepository.findById(userId).orElse(null);
				model.addAttribute("userList", userList);
				if (user == null) {
					if (!CollectionUtils.isEmpty(userList)) {
						user = userList.get(0);
					} else {
						user = (User) request.getSession().getAttribute("user");
					}
				}
			} else {
				if (!CollectionUtils.isEmpty(userList)) {
					user = userList.get(0);
					model.addAttribute("userList", userList);
				} else {
					user = (User) request.getSession().getAttribute("user");
				}
			}
		}

		int length = 10;

		if (year == null) {
			year = Calendar.getInstance().get(Calendar.YEAR);
		}

		Double currentWekkHour = hoursLogRepository.findCurrentWeekHourByUser(user);
		List<UserDetail> userDetails = userDetailsRepository.findByUser(user);

		Optional<UserDetail> optional = userDetails.stream().filter(f -> f.isActive() == true).findFirst();
		UserDetail userDetail = new UserDetail();

		if (!CollectionUtils.isEmpty(userDetails)) {
			if (userDetailId != null) {
				Optional<UserDetail> optional1 = userDetails.stream().filter(f -> f.getUserDetailId() == userDetailId)
						.findFirst();
				userDetail = optional1.get();
			} else {
				if (optional.isPresent()) {
					userDetail = optional.get();
				} else {
					userDetail = userDetails.get(0);
				}
			}

			if (optional.isPresent()) {
				model.addAttribute("userDetailActive", optional.get());
			} else {
				model.addAttribute("userDetailActive", new UserDetail());
			}

			model.addAttribute("userDetail", userDetail);
			model.addAttribute("approvedFile", hourLogFileRepository
					.countByApproveAndRejectAndUserDetailActive(APPROVED, NOT_REJECTED, year, userDetail));
			model.addAttribute("newFile", hourLogFileRepository.countByApproveAndRejectAndUserDetailActive(NOT_APPROVED,
					NOT_REJECTED, year, userDetail));
			model.addAttribute("rejectFile", hourLogFileRepository
					.countByApproveAndRejectAndUserDetailActive(NOT_APPROVED, REJECTED, year, userDetail));

			UserDetailsType userDetailsType = UserDetailsType.getUserDetailsType("all");
			AdminUserHoursChart adminUserHoursChart = new AdminUserHoursChart();
			;
			adminUserHoursChart = adminService.getUserMonthHour(year, user.getId(), userDetail.getUserDetailId(),
					userDetailsType);

			String xChart[] = { "January", "February", "March", "April", "May", "June", "July", "August", "September",
					"October", "November", "December" };
			model.addAttribute("xChart", xChart);

			model.addAttribute("adminUserHoursChart", adminUserHoursChart);

			Double currentYearHour = hoursLogRepository.findCurrentYearHourByUser(user, year, userDetail);
			Double currentYearDailyHour = hoursLogRepository.findCurrentYearDailyHourByUser(user, year, userDetail);
			Double currentYearExtraHour = hoursLogRepository.findCurrentYearExtraHourByUser(user, year, userDetail);
			Double currentYearVactionHour = hoursLogRepository.findCurrentYearVacationHourByUser(user, year,
					userDetail);

			Double currentMonthHour = hoursLogRepository.findCurrentMonthHourByUser(user, userDetail);
			Double currentMonthDailyHour = hoursLogRepository.findCurrentMonthDailyHourByUser(user, userDetail);
			Double currentMonthExtraHour = hoursLogRepository.findCurrentMonthExtraHourByUser(user, userDetail);
			Double currentMonthVacationHour = hoursLogRepository.findCurrentMonthVactionHourByUser(user, userDetail);

			model.addAttribute("userDetails", userDetails);

			model.addAttribute("currentMonthHour", currentMonthHour != null ? currentMonthHour : 0.0);
			model.addAttribute("currentMonthDailyHour", currentMonthDailyHour != null ? currentMonthDailyHour : 0.0);
			model.addAttribute("currentMonthExtraHour", currentMonthExtraHour != null ? currentMonthExtraHour : 0.0);
			model.addAttribute("currentMonthVacationHour",
					currentMonthVacationHour != null ? currentMonthVacationHour : 0.0);
			model.addAttribute("currentWekkHour", currentWekkHour != null ? currentWekkHour : 0.0);

			model.addAttribute("currentYearHour", currentYearHour != null ? currentYearHour : 0.0);
			model.addAttribute("currentYearDailyHour", currentYearDailyHour != null ? currentYearDailyHour : 0.0);
			model.addAttribute("currentYearExtraHour", currentYearExtraHour != null ? currentYearExtraHour : 0.0);
			model.addAttribute("currentYearVactionHour", currentYearVactionHour != null ? currentYearVactionHour : 0.0);

			model.addAttribute("selectYear", year);
			model.addAttribute("selectUser", user);

		} else {

			model.addAttribute("userDetail", new UserDetail());
			model.addAttribute("userDetails", new ArrayList<>());
			model.addAttribute("userDetailActive", new UserDetail());
			model.addAttribute("approvedFile", 0);
			model.addAttribute("newFile", 0);
			model.addAttribute("rejectFile", 0);

			AdminUserHoursChart adminUserHoursChart = new AdminUserHoursChart();
			;
			String xChart[] = { "January", "February", "March", "April", "May", "June", "July", "August", "September",
					"October", "November", "December" };
			model.addAttribute("xChart", xChart);
			model.addAttribute("adminUserHoursChart", adminUserHoursChart);

			model.addAttribute("totalHour", 0.0);

			model.addAttribute("currentMonthHour", 0.0);
			model.addAttribute("currentMonthDailyHour", 0.0);
			model.addAttribute("currentMonthExtraHour", 0.0);
			model.addAttribute("currentMonthVacationHour", 0.0);
			model.addAttribute("currentWekkHour", 0.0);

			model.addAttribute("currentYearHour", 0.0);
			model.addAttribute("currentYearDailyHour", 0.0);
			model.addAttribute("currentYearExtraHour", 0.0);
			model.addAttribute("currentYearVactionHour", 0.0);

			model.addAttribute("avgPerDay", 0.0);
			model.addAttribute("selectYear", year);
			model.addAttribute("selectUser", user);

		}

		// hours Table

		if (!CollectionUtils.isEmpty(userDetails)) {

			List<Map<String, Object>> userHourLogs = new ArrayList<>();

			if (userDetail.getTimeSheetPeriod().equals(TimeSheetPeriod.ONE_WEEK.urlParam)
					|| userDetail.getTimeSheetPeriod().equals(TimeSheetPeriod.TWO_WEEK.urlParam)) {
				userHourLogs = userService.getUserHourLog(year, user, userDetail.getUserDetailId(), pageNo - 1, length);

				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, year);

				if (year == Calendar.getInstance().get(Calendar.YEAR)) {

					int month = Calendar.getInstance().get(Calendar.MONTH);
					int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
					cal.set(Calendar.MONTH, month);
					cal.set(Calendar.DAY_OF_MONTH, day);
				} else {

					cal.set(Calendar.MONTH, Calendar.DECEMBER);
					cal.set(Calendar.DAY_OF_MONTH, 31);
				}

				int ordinalDay = cal.get(Calendar.DAY_OF_YEAR);
				int weekDay = cal.get(Calendar.DAY_OF_WEEK) - 1; // Sunday = 0
				int numberOfWeeks = (ordinalDay - weekDay + 10) / 7;

				int totalPage = numberOfWeeks / 10;
				model.addAttribute("currentPage", pageNo);
				model.addAttribute("userHourLogs", userHourLogs);
				model.addAttribute("totalPage", totalPage);

			} else if (userDetail.getTimeSheetPeriod().equals(TimeSheetPeriod.ONE_MONTH.urlParam)) {
				List<MonthHoursLogResponse> userHourLogsByMonth = userService.getUserHourLogByMonth(year, user,
						userDetail.getUserDetailId(), pageNo - 1, length);
				model.addAttribute("userHourLogsByMonth", userHourLogsByMonth);
			}

			model.addAttribute("userDetailId", userDetail.getUserDetailId());
		}
		return "new/user/home";
	}

	/**
	 * Get add hours list
	 *
	 * @return
	 */
	@RequestMapping(value = "user-hour-log/{userDetailId}", method = RequestMethod.GET)
	public String getUserHoursLog(@PathVariable("userDetailId") Integer userDetailId, HttpServletRequest request,
			Model model, @RequestParam(name = "year", required = false) Integer year,
			@RequestParam(name = "yearFile", required = false) Integer yearFile,
			@RequestParam(name = "page", required = false, defaultValue = "1") Integer pageNo,
			@RequestParam(name = "timeSheet", required = false) boolean hideReport,
			@RequestParam(name = "report", required = false) boolean hideTimeSheet) {

		User user1 = (User) request.getSession().getAttribute("user");

		boolean grant = permissionService.grantPermission(user1.getRole(), Functionality.REPORT_TIME_SHEET,
				Permission.READ, false);
		boolean grant1 = permissionService.grantPermission(user1.getRole(), Functionality.SUBMITTED_TIMESHEET,
				Permission.READ, false);
		if ((!grant && hideTimeSheet) || (!grant1 && hideReport) || (!grant1 && !grant)) {
			return "redirect:/user/unauthorized";
		}

		if (year == null)
			year = Calendar.getInstance().get(Calendar.YEAR);// get current year

		int length = 10;
		User user = (User) request.getSession().getAttribute("user");
		UserDetail userDetail = userDetailsRepository.findByUserDetailId(userDetailId);
		if (user.getId() != userDetail.getUser().getId()) {
			return "redirect:/user/home";
		}

		List<Map<String, Object>> userHourLogs = new ArrayList<>();

		if (userDetail.getTimeSheetPeriod().equals(TimeSheetPeriod.ONE_WEEK.urlParam)
				|| userDetail.getTimeSheetPeriod().equals(TimeSheetPeriod.TWO_WEEK.urlParam)) {
			userHourLogs = userService.getUserHourLog(year, user, userDetailId, pageNo - 1, length);

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);

			if (year == Calendar.getInstance().get(Calendar.YEAR)) {

				int month = Calendar.getInstance().get(Calendar.MONTH);
				int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
				cal.set(Calendar.MONTH, month);
				cal.set(Calendar.DAY_OF_MONTH, day);
			} else {

				cal.set(Calendar.MONTH, Calendar.DECEMBER);
				cal.set(Calendar.DAY_OF_MONTH, 31);
			}

			int ordinalDay = cal.get(Calendar.DAY_OF_YEAR);
			int weekDay = cal.get(Calendar.DAY_OF_WEEK) - 1; // Sunday = 0
			int numberOfWeeks = (ordinalDay - weekDay + 10) / 7;

			int totalPage = numberOfWeeks / 10;
			model.addAttribute("currentPage", pageNo);
			model.addAttribute("userHourLogs", userHourLogs);
			model.addAttribute("totalPage", totalPage);

		} else if (userDetail.getTimeSheetPeriod().equals(TimeSheetPeriod.ONE_MONTH.urlParam)) {
			List<MonthHoursLogResponse> userHourLogsByMonth = userService.getUserHourLogByMonth(year, user,
					userDetailId, pageNo - 1, length);
			model.addAttribute("userHourLogsByMonth", userHourLogsByMonth);
		}

		if (yearFile == null)
			yearFile = Calendar.getInstance().get(Calendar.YEAR);// get current year

		List<HourLogFile> hourLogFiles = userService.getHourLogFile(userDetailId, yearFile);
		List<UserDetail> userDetails = userDetailsRepository.findByUser(user);

		model.addAttribute("userDetail", userDetailsRepository.findByUserDetailId(userDetailId));
		model.addAttribute("hourLogFiles", hourLogFiles);
		model.addAttribute("userDetailId", userDetailId);
		model.addAttribute("selectYear", year);
		model.addAttribute("selectYearFile", yearFile);
		model.addAttribute("hideReport", hideReport);
		model.addAttribute("hideTimeSheet", hideTimeSheet);
		model.addAttribute("userDetails", userDetails);
		return "/new/user/user-hour-log";
	}

	/**
	 * add user hour log file
	 * 
	 * @param userDetailId
	 * @param startDate
	 * @param request
	 * @param endDate
	 * @param redirectAttributes
	 * @return
	 * @throws MalformedURLException
	 */
	@RequestMapping(value = "add-user-hour-log-file", method = RequestMethod.POST)
	public String AddUserHourLogFile(@RequestParam(name = "userDetailId") Integer userDetailId,
			@RequestParam(name = "startDate") Date startDate, HttpServletRequest request,
			@RequestParam(name = "endDate") Date endDate, RedirectAttributes redirectAttributes,
			@RequestParam(name = "file") List<MultipartFile> files,
			@RequestParam(name = "description", required = false) String description,@RequestParam(name = "remark", required = false) String remark) throws MalformedURLException {

		try {
			Company company = (Company) request.getSession().getAttribute("company");
			User user = (User) request.getSession().getAttribute("user");
			UserDetail userDetail = userDetailsRepository.findByUserDetailId(userDetailId);

			List<String[]> filePath = new ArrayList<>();
			File file1 = null;
			if (!CollectionUtils.isEmpty(files)) {
				
				int i = 0;
				for (MultipartFile file : files) {

					if (!new File(FILE_PATH + company.getFileFolder()).exists())
						new File(FILE_PATH + company.getFileFolder()).mkdir();

					if (!new File(FILE_PATH + company.getFileFolder() + "/User/").exists())
						new File(FILE_PATH + company.getFileFolder() + "/User/").mkdir();

					if (!new File(FILE_PATH + company.getFileFolder() + "/User/" + user.getFileFolder() + "/").exists())
						new File(FILE_PATH + company.getFileFolder() + "/User/" + user.getFileFolder() + "/").mkdir();

					if (!new File(FILE_PATH + company.getFileFolder() + "/User/" + user.getFileFolder() + "/Timesheet/").exists())
						new File(FILE_PATH + company.getFileFolder() + "/User/" + user.getFileFolder() + "/Timesheet/").mkdir();

					if (!new File(FILE_PATH + company.getFileFolder() + "/User/" + user.getFileFolder() + "/Timesheet/"+ userDetail.getFileFolder() + "/").exists())
						new File(FILE_PATH + company.getFileFolder() + "/User/" + user.getFileFolder() + "/Timesheet/"+ userDetail.getFileFolder() + "/").mkdir();

					if (file.getOriginalFilename().length() > 0) {

						String filePath1 = "/User/" + userDetail.getUser().getFileFolder() + "/Timesheet/"
								+ userDetail.getFileFolder() + "/" + new Date().getTime()+i + "Timesheet" + userDetailId
								+ file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."));

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

			userService.saveUserHourFile(filePath, userDetailId, startDate, endDate, description,remark);
			redirectAttributes.addFlashAttribute("success", "Hour Log file upload successfully");

		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Hour Log file does not upload, try again");
			e.printStackTrace();
		}
		return "redirect:/user/user-hour-log/" + userDetailId;
	}

	/**
	 * download file //user-add-hours
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/user-hour-file/download/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	public void downloadFile(@PathVariable(value = "id") Integer id, HttpServletResponse response,
			HttpServletRequest request) {
		HourLogFile hourLogFile = hourLogFileRepository.findById(id).orElse(null);
		userService.addActivity("Time sheet file download", ActivityType.DOWNLOAD_FILE.toString(),
				hourLogFile.getUserDetail());

		response.setContentType(MediaType.ALL_VALUE);
		Company company = (Company) request.getSession().getAttribute("company");

		List<HourLogFilePath> hourLogFilePaths = hourLogFilePathRepository.findByHourLogFile(hourLogFile);

		if (!CollectionUtils.isEmpty(hourLogFilePaths)) {
			if (hourLogFilePaths.size() == 1) {
				String filePath = hourLogFilePaths.get(0).getFilePath();
				File f = new File(FILE_PATH + company.getFileFolder() + filePath);

				try {

					// construct the complete absolute path of the file
					FileInputStream inputStream = new FileInputStream(FILE_PATH + company.getFileFolder() + filePath);

					Path source = Paths.get(FILE_PATH + company.getFileFolder() + filePath);
					String mimeType = Files.probeContentType(source);
					// get MIME type of the file
					if (mimeType == null) {
						// set to binary type if MIME mapping not found
						mimeType = "application/octet-stream";
					}
					// set content attributes for the response
					response.setContentType(mimeType);
					response.setContentLength((int) f.length());

					// get output stream of the response
					OutputStream outStream = response.getOutputStream();

					byte[] buffer = new byte[100000];
					int bytesRead = -1;

					// write bytes read from the input stream into the output stream
					while ((bytesRead = inputStream.read(buffer)) != -1) {
						outStream.write(buffer, 0, bytesRead);
					}

					inputStream.close();
					outStream.close();

				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {

				response.setContentType("application/octet-stream");
				response.setHeader("Content-Disposition", "attachment;filename=" + hourLogFile.getStartDate().toString()
						+ "_To_" + hourLogFile.getEndDate() + ".zip");
				response.setStatus(HttpServletResponse.SC_OK);

				try {

					OutputStream outStream = response.getOutputStream();

					ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(outStream));

					for (HourLogFilePath file : hourLogFilePaths) {
						// construct the complete absolute path of the file
						File f = new File(FILE_PATH + company.getFileFolder() + file.getFilePath());
						zos.putNextEntry(new ZipEntry(file.getFileOriginalName()));

						FileInputStream inputStream = new FileInputStream(
								FILE_PATH + company.getFileFolder() + file.getFilePath());

						Path source = Paths.get(FILE_PATH + company.getFileFolder() + file.getFilePath());
						String mimeType = Files.probeContentType(source);
						// get MIME type of the file
						if (mimeType == null) {
							// set to binary type if MIME mapping not found
							mimeType = "application/octet-stream";
						}
						// set content attributes for the response
						response.setContentType(mimeType);
						response.setContentLength((int) f.length());

						// get output stream of the response
						BufferedInputStream fif = new BufferedInputStream(inputStream);

						byte[] buffer = new byte[100000];
						int bytesRead = -1;

						// write bytes read from the input stream into the output stream
						while ((bytesRead = fif.read(buffer)) != -1) {
							zos.write(buffer, 0, bytesRead);
						}

						fif.close();
						inputStream.close();
						zos.closeEntry();

					}
					zos.close();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * get user time sheet add
	 * 
	 * @param request
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value = "add-time-sheet", method = RequestMethod.GET)
	public String setTimeSheetAdd(HttpServletRequest request,
			@RequestParam(name = "startDate", required = false) Date startDate,
			@RequestParam(name = "endDate", required = false) Date endDate,
			@RequestParam(name = "year", required = false) Integer year, ModelMap modelMap) throws ParseException {

		User loginUser = (User) request.getSession().getAttribute("user");
		boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.ADD_TIME_SHEET,
				Permission.CREATE, false);
		if (!grant) {
			return "redirect:/user/unauthorized";
		}

		User user = (User) request.getSession().getAttribute("user");
		user = userService.findById(user.getId());
		Integer userDetailId = user.getClientActiveId();
		SimpleDateFormat simpleDateformat3 = new SimpleDateFormat("MM/dd/yyyy");

		// get date list according to selected period of time
		List<UserTimeSheetDate> userTimeSheetDates = userService.getDateList(year, userDetailId, startDate);

		if (CollectionUtils.isEmpty(userTimeSheetDates) && userDetailId != null) {
			modelMap.addAttribute("userDetails", userDetailsRepository.findByUser(user));
			modelMap.addAttribute("userDetail", userDetailsRepository.findByUserDetailId(userDetailId));
			AddUserTimeSheet addUserTimeSheet = new AddUserTimeSheet();
			addUserTimeSheet.setCalendarResponse(new ArrayList<CalendarResponse>());
			addUserTimeSheet.setUserDetailId(userDetailId);
			modelMap.addAttribute("addUserTimeSheet", addUserTimeSheet);
			modelMap.addAttribute("templates", new ArrayList<Template>());
			modelMap.addAttribute("success", "Time sheet are not available or all are approved for this year");
			return "/new/user/add-time-sheet";
		}
		if (startDate == null) {

			// if date is null then select current date -> if current date approve then
			// select next date
			if (userDetailId != null && year == null) {
				// when only userDetailId selected
				startDate = userService.getValidStartDate(userTimeSheetDates, userDetailId);

			} else if (year != null && year == new Date().getYear() + 1900) {

				// when only userDetailId selected & current year selected
				startDate = userService.getValidStartDate(userTimeSheetDates, userDetailId);

			}
			if (year != null && year != new Date().getYear() + 1900) {

				// when only userDetailId selected & year selected
				startDate = simpleDateformat3.parse(userTimeSheetDates.get(0).getDateString());
			}
		}

		AddUserTimeSheet addUserTimeSheet = userService.getCalendarResponse(userDetailId, startDate, endDate);

		List<HourLogFile> hourLogFile = new ArrayList<>();

		if (userDetailId != null) {
			hourLogFile = hourLogFileRepository.findByStartDateAndEndDateAndUserDetail(
					simpleDateformat3.parse(addUserTimeSheet.getStartDate()),
					simpleDateformat3.parse(addUserTimeSheet.getEndDate()),
					userDetailsRepository.findByUserDetailId(userDetailId));
			modelMap.addAttribute("userDetail", userDetailsRepository.findByUserDetailId(userDetailId));

		} else {
			modelMap.addAttribute("userDetail", null);
			modelMap.addAttribute("error", "Any client are not active");
		}
		if (CollectionUtils.isEmpty(hourLogFile)) {
			modelMap.addAttribute("resubmit", false);
			modelMap.addAttribute("remarkOld","");
		}else {
			modelMap.addAttribute("resubmit", true);
			modelMap.addAttribute("remarkOld",hourLogFile.get(0).getRemark());
		}
		modelMap.addAttribute("userTimeSheetDates", userTimeSheetDates);
		modelMap.addAttribute("addUserTimeSheet", addUserTimeSheet);

		if (year != null || startDate == null)
			modelMap.addAttribute("selectedYear", year);
		else
			modelMap.addAttribute("selectedYear", startDate.getYear() + 1900);

		List<Template> templates = new ArrayList<Template>();
		if (userDetailId != null && userDetailsRepository.findByUserDetailId(userDetailId) != null) {
			List<Template> templatesUnSet = templateService.getMailByType(MailTemplateType.submission);
			templates = templateService.setSubmission(templatesUnSet, user, startDate, endDate, userDetailId);
		}

		modelMap.addAttribute("templates", templates);

		return "/new/user/add-time-sheet";
	}


	@RequestMapping(value = "add-time-sheet", method = RequestMethod.POST)
	public String getTimeSheetAdd(@ModelAttribute("addUserTimeSheet") AddUserTimeSheet addUserTimeSheet,
			@RequestParam(name = "file", required = false) List<MultipartFile> files,
			@RequestParam(name = "email") String email, @RequestParam(name = "ccEmail") String ccEmail,
			@RequestParam(name = "subject") String subject, HttpServletRequest request,
			@RequestParam(name = "remark") String remark,RedirectAttributes redirectAttributes) {

		User loginUser = (User) request.getSession().getAttribute("user");
		boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.ADD_TIME_SHEET,
				Permission.CREATE, false);
		if (!grant) {
			return "redirect:/user/unauthorized";
		}

		Company company = (Company) request.getSession().getAttribute("company");

		if (StringUtils.isEmpty(addUserTimeSheet.getStartDate())
				|| StringUtils.isEmpty(addUserTimeSheet.getEndDate())) {

			redirectAttributes.addFlashAttribute("error", "Please fill start Date and End Date");
			return "redirect:/user/add-time-sheet";
		}
		UserDetail userDetail = userDetailsRepository.findByUserDetailId(addUserTimeSheet.getUserDetailId());
		try {
			List<String[]> filePath = new ArrayList<>();
			File file1 = null;
			if (!CollectionUtils.isEmpty(files)) {
				
				int i = 0;
				for (MultipartFile file : files) {

					if (!new File(FILE_PATH + company.getFileFolder()).exists())
						new File(FILE_PATH + company.getFileFolder()).mkdir();
					
					if (!new File(FILE_PATH + company.getFileFolder() + "/User/").exists())
						new File(FILE_PATH + company.getFileFolder() + "/User/").mkdir();
					
					if (!new File(FILE_PATH + company.getFileFolder() + "/User/"+ loginUser.getFileFolder()+"/").exists())
						new File(FILE_PATH + company.getFileFolder() + "/User/"+ loginUser.getFileFolder()+"/").mkdir();
					
					if (!new File(FILE_PATH + company.getFileFolder() + "/User/" + loginUser.getFileFolder() + "/HourLog/").exists())
						new File(FILE_PATH + company.getFileFolder() +  "/User/" + loginUser.getFileFolder() + "/HourLog/").mkdir();
					
					if (!new File(FILE_PATH + company.getFileFolder() + "/User/" + loginUser.getFileFolder()+ "/HourLog/"+userDetail.getFileFolder()+"/").exists())
						new File(FILE_PATH + company.getFileFolder() +  "/User/" + loginUser.getFileFolder() + "/HourLog/"+userDetail.getFileFolder()+"/").mkdir();
					

					if (file.getOriginalFilename().length() > 0) {

						String filePath1 = "/User/" + loginUser.getFileFolder() + "/HourLog/"+userDetail.getFileFolder()+"/" + new Date().getTime() +i+ "HourLog"
								+ addUserTimeSheet.getUserDetailId()
								+ file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."));

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
					addUserTimeSheet.getDescription(),remark);

			for (CalendarResponse calendarResponse : addUserTimeSheet.getCalendarResponse()) {

				userService.setDefaultCalendar(calendarResponse.getDailyHours(), calendarResponse.getExtraHours(),
						calendarResponse.getVacationHours(), addUserTimeSheet.getUserDetailId(),
						new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy").parse(calendarResponse.getDate()),calendarResponse.getNewNotes());
			}

			List<User> users = userRepository.findByRoleOrRole("ROLE_ADMIN", "ROLE_SUPERVISOR");
			for (User user : users) {
				ccEmail = ccEmail + ',' + user.getEmail();
			}
			userService.sendEmailToUser(addUserTimeSheet.getDescription(), email, subject, ccEmail, file1);

			redirectAttributes.addFlashAttribute("success", "Time sheet save successfully");
			redirectAttributes.addAttribute("timeSheet", true);
			return "redirect:/user/user-hour-log/" + addUserTimeSheet.getUserDetailId();

		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Time sheet does not upload, try again");
			e.printStackTrace();
			return "redirect:/user/add-time-sheet";
		}
	}

	/**
	 * GET submited time sheet
	 * 
	 * @param request
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "submitted-timesheet", method = RequestMethod.GET)
	public String getSubmittedTimeSheet(HttpServletRequest request, RedirectAttributes redirectAttributes) {

		User user = (User) request.getSession().getAttribute("user");
		List<UserDetail> userDetails = userDetailsRepository.findByUser(user);
		if (!CollectionUtils.isEmpty(userDetails)) {
			redirectAttributes.addAttribute("timeSheet", true);
			return "redirect:/user/user-hour-log/" + userDetails.get(0).getUserDetailId();
		} else
			return "redirect:/user/home";
	}

	/**
	 * get report
	 * 
	 * @param request
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "report", method = RequestMethod.GET)
	public String getReport(HttpServletRequest request, RedirectAttributes redirectAttributes) {

		User user = (User) request.getSession().getAttribute("user");
		List<UserDetail> userDetails = userDetailsRepository.findByUser(user);
		if (!CollectionUtils.isEmpty(userDetails)) {
			redirectAttributes.addAttribute("report", true);
			return "redirect:/user/user-hour-log/" + userDetails.get(0).getUserDetailId();
		} else
			return "redirect:/user/home";
	}

	/**
	 * delete file
	 * 
	 * @param id
	 * @param userDetailId
	 * @param request
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "hour-log-file/delete/{id}/{userDetailId}", method = RequestMethod.GET)
	public String deleteFile(@PathVariable("id") Integer id, @PathVariable("userDetailId") Integer userDetailId,
			HttpServletRequest request, RedirectAttributes redirectAttributes) {

		Company company = (Company) request.getSession().getAttribute("company");

		HourLogFile hourLogFile = hourLogFileRepository.findById(id).orElse(null);
		List<HourLogFilePath> hourLogFilePaths = hourLogFilePathRepository.findByHourLogFile(hourLogFile);
		if (!CollectionUtils.isEmpty(hourLogFilePaths)) {
			for (HourLogFilePath hourLogFilePath : hourLogFilePaths) {
				File file = new File(FILE_PATH + company.getFileFolder() + hourLogFilePath.getFilePath());
				file.delete();
			}
			hourLogFilePathRepository.deleteAll(hourLogFilePaths);
		}
		hourLogFileRepository.delete(hourLogFile);
		userService.addActivity("Time sheet delete", ActivityType.DELETE_TIMESHEET.toString(), null);
		redirectAttributes.addFlashAttribute("success", "File has been deleted successfully");
		return "redirect:/user/user-hour-log/" + userDetailId + "?timeSheet=true";
	}

	/**
	 * hour log file edit
	 * 
	 * @param id
	 * @param modelMap
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "hour-log-file/edit/{id}", method = RequestMethod.GET)
	public String editFile(@PathVariable("id") Integer id, ModelMap modelMap, HttpServletRequest request) {

		User user = (User) request.getSession().getAttribute("user");

		HourLogFile hourLogFile = hourLogFileRepository.findByIdAndApproveNot(id, true);
		if (hourLogFile == null)
			return "redirect:/user/home";
		List<UserTimeSheetDate> userTimeSheetDates = userService.getDateList(null,
				hourLogFile.getUserDetail().getUserDetailId(), hourLogFile.getStartDate());

		AddUserTimeSheet addUserTimeSheet = userService.getCalendarResponse(
				hourLogFile.getUserDetail().getUserDetailId(), hourLogFile.getStartDate(), hourLogFile.getEndDate());

		modelMap.addAttribute("userTimeSheetDates", userTimeSheetDates);
		modelMap.addAttribute("isEdit", true);
		modelMap.addAttribute("timesheetId", id);
		modelMap.addAttribute("addUserTimeSheet", addUserTimeSheet);
		modelMap.addAttribute("userDetail",
				userDetailsRepository.findByUserDetailId(hourLogFile.getUserDetail().getUserDetailId()));
		modelMap.addAttribute("userDetails", userDetailsRepository.findByUser(user));
		modelMap.addAttribute("resubmit", true);
		return "/new/user/add-time-sheet";
	}

	/**
	 * get scheduled time sheet
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@RequestMapping(value = "schedule-time-sheet", method = RequestMethod.GET)
	@ResponseBody
	public List<CalendarResponse> getSchedulaTimesheet(
			@RequestParam(name = "startDate", required = false) Date startDate,
			@RequestParam(name = "endDate", required = false) Date endDate) {

		List<CalendarResponse> calendarResponses = userService.getSchedularResponse(startDate, endDate);

		return calendarResponses;
	}

	/**
	 * get user time sheet add
	 * 
	 * @param request
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value = "add-time-sheet/changeDate", method = RequestMethod.GET)
	public String changeDateTimesheet(HttpServletRequest request,
			@RequestParam(name = "startDate", required = false) Date startDate,
			@RequestParam(name = "endDate", required = false) Date endDate,
			@RequestParam(name = "year", required = false) Integer year, ModelMap modelMap) throws ParseException {

		User user = (User) request.getSession().getAttribute("user");
		Integer userDetailId = user.getClientActiveId();
		SimpleDateFormat simpleDateformat3 = new SimpleDateFormat("MM/dd/yyyy");

		// get date list according to selected period of time
		List<UserTimeSheetDate> userTimeSheetDates = userService.getDateList(year, userDetailId, startDate);

		if (CollectionUtils.isEmpty(userTimeSheetDates) && userDetailId != null) {
			modelMap.addAttribute("userDetails", userDetailsRepository.findByUser(user));
			modelMap.addAttribute("userDetail", userDetailsRepository.findByUserDetailId(userDetailId));
			AddUserTimeSheet addUserTimeSheet = new AddUserTimeSheet();
			addUserTimeSheet.setCalendarResponse(new ArrayList<CalendarResponse>());
			addUserTimeSheet.setUserDetailId(userDetailId);
			modelMap.addAttribute("addUserTimeSheet", addUserTimeSheet);
			modelMap.addAttribute("success", "Time sheet are not available or all are approved for this year");
			return "/new/user/add-time-sheet";
		}
		if (startDate == null) {

			// if date is null then select current date -> if current date approve then
			// select next date
			if (userDetailId != null && year == null) {
				// when only userDetailId selected
				startDate = userService.getValidStartDate(userTimeSheetDates, userDetailId);

			} else if (year != null && year == new Date().getYear() + 1900) {

				// when only userDetailId selected & current year selected
				startDate = userService.getValidStartDate(userTimeSheetDates, userDetailId);

			}
			if (year != null && year != new Date().getYear() + 1900) {

				// when only userDetailId selected & year selected
				startDate = simpleDateformat3.parse(userTimeSheetDates.get(0).getDateString());
			}
		}

		AddUserTimeSheet addUserTimeSheet = userService.getCalendarResponse(userDetailId, startDate, endDate);

		List<HourLogFile> hourLogFile = new ArrayList<>();

		if (userDetailId != null) {
			hourLogFile = hourLogFileRepository.findByStartDateAndEndDateAndUserDetail(
					simpleDateformat3.parse(addUserTimeSheet.getStartDate()),
					simpleDateformat3.parse(addUserTimeSheet.getEndDate()),
					userDetailsRepository.findByUserDetailId(userDetailId));
			modelMap.addAttribute("userDetail", userDetailsRepository.findByUserDetailId(userDetailId));

		} else {
			modelMap.addAttribute("userDetail", null);
			modelMap.addAttribute("error", "Any client are not active");
		}
		if (CollectionUtils.isEmpty(hourLogFile))
			modelMap.addAttribute("resubmit", false);
		else
			modelMap.addAttribute("resubmit", true);
		modelMap.addAttribute("userTimeSheetDates", userTimeSheetDates);
		modelMap.addAttribute("addUserTimeSheet", addUserTimeSheet);

		if (year != null || startDate == null)
			modelMap.addAttribute("selectedYear", year);
		else
			modelMap.addAttribute("selectedYear", startDate.getYear() + 1900);

		return "/new/user/add-time-sheet::addTimeSheet";
	}

	@RequestMapping(value = "schedular/default-time-sheet", method = RequestMethod.GET)
	public String getDefaultTimesheet(@RequestParam(name = "year", required = false) Integer year,
			RedirectAttributes redirectAttributes) throws ParseException {

		if (permissionService.getPermissionPlan().isSchedularCanSet() == true) {
			Calendar c = Utils.getDate(year, 0, 1);
			Calendar c2 = Utils.getDate(year, 11, 31);
			List<CalendarResponse> calendarResponses = userService.getDefaulteResponse(c.getTime(), c2.getTime());
			supervisorService.saveDeafulteHoursSchedular(calendarResponses);
			redirectAttributes.addFlashAttribute("success", "Set default schedular 8 hours successfully");
			return "redirect:/supervisor/schedular?year=" + year;

		} else {
			return "new/unauthorized";
		}
	}

	/**
	 * get default time sheet
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@RequestMapping(value = "default-time-sheet", method = RequestMethod.GET)
	@ResponseBody
	public List<CalendarResponse> getDefaultTimesheet(
			@RequestParam(name = "startDate", required = false) Date startDate,
			@RequestParam(name = "endDate", required = false) Date endDate) {

		List<CalendarResponse> calendarResponses = userService.getDefaulteResponse(startDate, endDate);

		return calendarResponses;
	}


	@RequestMapping(value = "unauthorized", method = RequestMethod.GET)
	public String userSchedularTimeSheet(ModelMap modelMap) {

		return "new/unauthorized";
	}
	
	@RequestMapping(value = "userProfile", method = RequestMethod.GET)
	public String userprofile(ModelMap modelMap, HttpServletRequest request) {

		User user = (User) request.getSession().getAttribute("user");
		User userExists = userService.findUserByEmail(user.getEmail());
		modelMap.addAttribute("userLogin", userExists);
		return "new/user/userProfile";
	}

	@RequestMapping(value = "private-signature", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Response> privateSignature(String privateSignature, HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		User userExists = userService.findUserByEmail(user.getEmail());
		userService.addPrivateSignature(privateSignature, userExists);
		return ResponseGenerator.generateResponse(new Response("success", false), HttpStatus.OK);
	}

	@RequestMapping(value = "configuration", method = RequestMethod.GET)
	public String getConfig(ModelMap modelMap, HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		User userExists = userService.findUserByEmail(user.getEmail());
		modelMap.addAttribute("userLogin", userExists);
		return "new/user/companyConfiguration";
	}
}
