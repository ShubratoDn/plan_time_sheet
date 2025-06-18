package com.aim.service;

import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.data.domain.Page;

import com.aim.entity.Company;
import com.aim.entity.HourLog;
import com.aim.entity.HourLogFile;
import com.aim.entity.InternalUser;
import com.aim.entity.PermissionPlan;
import com.aim.entity.User;
import com.aim.entity.UserDetail;
import com.aim.enums.AdminTimeSheetAction;
import com.aim.enums.HourLogStatus;
import com.aim.request.CalculationCountRequest;
import com.aim.request.CompanyRequest;
import com.aim.request.UserDetailRequest;
import com.aim.response.AddUserTimeSheet;
import com.aim.response.CalendarResponse;
import com.aim.response.DefaultCalendarResponse;
import com.aim.response.HomePageUserResponse;
import com.aim.response.HourLogResponse;
import com.aim.response.MonthHoursLogResponse;
import com.aim.response.PendingHourLogFile;
import com.aim.response.SchedularResponse;
import com.aim.response.UserTimeSheetDate;

public interface UserService {

	void saveUser(User user);

	User findUserByEmail(String email);

	Boolean isValidActivationKey(String key) throws Exception;

	Boolean resetPassword(String key, String password) throws Exception;

	List<User> findByRole(String role);
	
	Page<User> findByFirstNameStartsWith(String startsWith, Integer page);

	void sendActivation(Integer userId);

	void saveUserDetails(UserDetail userDetails);

	LinkedHashMap<String, Map<String, Double>> getHourLog(Integer userDetailId, Integer year,Integer m);

	void saveUserFile(String fileName, String filePath, Integer id, Date expDate, String type, String remark);

	List<Map<String, Object>> getUserHourLog(Integer year, User user, Integer userDetailId, Integer pageNo, Integer length);

//	void setTimeSheetSubmission(Integer userDetailId, String key) throws ParseException;
	
//	List<AdminTimeSheetResponse> getAdminToBeApproveTimeSheet(AdminTimeSheetAction adminTimeSheetAction);

	List<DefaultCalendarResponse> getDefaultCalendar(Integer userDetailId, Integer month, Integer year);

	void setDefaultCalendar(Float dailyHour, Float extraHour, Float vacationHour, Integer userDetailId, Date date, String newNote);

	List<MonthHoursLogResponse> getUserHourLogByMonth(Integer year, User user, Integer userDetailId, Integer i, Integer length);

	List<HourLogFile> getHourLogFile(Integer userDetailId, Integer year);

	void saveUserHourFile(List<String[]> string, Integer userDetailId, Date startDate, Date endDate,
			String description, String remark);

	List<HourLogFile> getAllHourLogFile(Integer yearFile, AdminTimeSheetAction adminTimeSheetAction);

	HourLog changeHourLogStatus(Integer userDetailId, Date date, HourLogStatus hourLogStatus);

	AddUserTimeSheet getCalendarResponse(Integer userDetailId, Date startDate, Date endDate);

	void sendEmailToUser(String description, String email, String subject, String ccEmail, File file);

	void saveInternalUser(InternalUser internalUser);

	List<UserTimeSheetDate> getDateList(Integer year, Integer userDetailId, Date startDate);

	Date getValidStartDate(List<UserTimeSheetDate> userTimeSheetDates, Integer userDetailId) throws ParseException;

	void addActivity(String string, String string2, UserDetail userDetail);

	List<CalendarResponse> getSchedularResponse(Date startDate, Date endDate);

	List<HourLogFile> getHourLogFileByUser(Integer yearFile, Integer userId, AdminTimeSheetAction adminTimeSheetAction);

	List<CalendarResponse> getDefaulteResponse(Date startDate, Date endDate);

	String viewHoursLogPdf(Integer id);

	void setUserActive(String key, String key1);

	UserDetail getUserDetail(@Valid UserDetailRequest userDetailRequest);

	UserDetailRequest setUserDetailRequest(UserDetail userDetail);

	void addActivityWithOtherDetails(String text, String string, String string2);

	String viewSchedularPdf(SchedularResponse schedularResponses);

	List<PendingHourLogFile> getPengindTimesheet(Integer yearFile, Integer userId);

	List<HourLogFile> getHourLogNotApprovedFile(Integer userDetailId, Integer yearFile);

	CalculationCountRequest getCalculationCountRequest(HourLogResponse hourLogResponse, UserDetail userDetail, int i);

	CalculationCountRequest getCalculationCountForSingleDayRequest(HourLog hourLog, UserDetail userDetail,
			int month, int dayFill);

	Company getCompany();

	Company saveCompany(CompanyRequest companyRequest, String filePath1);

	void addPrivateSignature(String privateSignature, User loginUser);

	Page<User> findAll(Integer page);

	String setPlanString(PermissionPlan permissionPlan);

	String getFinalFileFolder(String u, int d);

	User findOne(int id);
	User findById(int id);
}
