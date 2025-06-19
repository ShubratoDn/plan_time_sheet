package com.aim.repository;

import java.util.Date;
import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aim.entity.HourLog;
import com.aim.entity.User;
import com.aim.entity.UserDetail;
import com.aim.response.HourLogResponse;
import com.aim.response.HoursLogByMonthDay;

@Repository
@Transactional
public interface HoursLogRepository extends CrudRepository<HourLog, Integer> {

	@Transactional
	void deleteById(Integer id);

	HourLog findByHoursDate(Date hoursDate);

	@Query("SELECT new com.aim.response.HourLogResponse(MONTH(h.hoursDate), SUM(h.dailyHours), sum(h.extraHours),SUM(h.vacationHours) ) FROM HourLog h where YEAR(h.hoursDate) = :year and h.userDetail.userDetailId = :userDetailId GROUP BY MONTH(hoursDate)")
	List<HourLogResponse> getHourLog(@Param("userDetailId") Integer userDetailId, @Param("year") Integer year);
	
	@Query("SELECT new com.aim.response.HourLogResponse(MONTH(h.hoursDate), SUM(h.dailyHours), sum(h.extraHours),SUM(h.vacationHours) ) FROM HourLog h where  MONTH(h.hoursDate) = :month and YEAR(h.hoursDate) = :year and h.userDetail.userDetailId = :userDetailId GROUP BY MONTH(hoursDate)")
	List<HourLogResponse> getHourLogByMonth(@Param("userDetailId") Integer userDetailId, @Param("year") Integer year,@Param("month") Integer month);

	List<HourLog> findByUserDetailUser(User user);

	List<HourLog> findByUserDetailAndHoursDateBetween(UserDetail UserDetail, Date endDate, Date startDate);

	List<HourLog> findByUserDetailUserDetailId(Integer userDetailId);

	@Query(value="select (select daily_hours + extra_hours From timeSheet.hour_log WHERE user_detail_id= :userDetailId AND DATE(hours_date) = DATE( :date) ) AS monday,\n" + 
			"(select daily_hours + extra_hours From timeSheet.hour_log WHERE user_detail_id= :userDetailId AND DATE(hours_date) = DATE_ADD(DATE( :date),INTERVAL 1 DAY) )AS tuesday,\n" + 
			"(select daily_hours + extra_hours From timeSheet.hour_log WHERE user_detail_id= :userDetailId AND DATE(hours_date) = DATE_ADD(DATE( :date),INTERVAL 2 DAY) )AS wednesday,\n" + 
			"(select daily_hours + extra_hours From timeSheet.hour_log WHERE user_detail_id= :userDetailId AND DATE(hours_date) = DATE_ADD(DATE( :date),INTERVAL 3 DAY) )AS thursday,\n" + 
			"(select daily_hours + extra_hours From timeSheet.hour_log WHERE user_detail_id= :userDetailId AND DATE(hours_date) = DATE_ADD(DATE( :date),INTERVAL 4 DAY) )AS friday,\n" + 
			"(select daily_hours + extra_hours From timeSheet.hour_log WHERE user_detail_id= :userDetailId AND DATE(hours_date) = DATE_ADD(DATE( :date),INTERVAL 5 DAY) )AS saturday,\n" + 
			"(select daily_hours + extra_hours From timeSheet.hour_log WHERE user_detail_id= :userDetailId AND DATE(hours_date) = DATE_ADD(DATE( :date),INTERVAL 6 DAY) )AS sunday\n" + 
			"From timeSheet.hour_log where user_detail_id= :userDetailId limit 1", nativeQuery=true)
	public List<Object[]> getTimeSheetDay(@Param("date") String date, @Param("userDetailId") Integer userDetailId);

	HourLog findByHoursDateAndUserDetail(Date date, UserDetail userDetail);

	@Query("SELECT SUM(h.dailyHours + h.extraHours) FROM HourLog h where h.userDetail.user= :user")
	Double findTotalHourByUser(@Param("user")User user);

	@Query("SELECT SUM(h.dailyHours + h.extraHours) FROM HourLog h where h.userDetail = :userDetail AND h.userDetail.user= :user AND YEAR(h.hoursDate) = YEAR(NOW()) AND MONTH(h.hoursDate)= MONTH(NOW())")
	Double findCurrentMonthHourByUser(@Param("user") User user, @Param("userDetail") UserDetail userDetail);

	@Query("SELECT SUM(h.dailyHours + h.extraHours) FROM HourLog h where h.userDetail.user= :user AND YEAR(h.hoursDate) = YEAR(NOW()) AND WEEK(h.hoursDate)= WEEK(NOW())")
	Double findCurrentWeekHourByUser(@Param("user") User user);

	@Query("SELECT SUM(h.dailyHours + h.extraHours) FROM HourLog h where h.userDetail = :userDetail AND h.userDetail.user= :user AND YEAR(h.hoursDate) = :year")
	Double findCurrentYearHourByUser(@Param("user") User user, @Param("year") Integer year, @Param("userDetail") UserDetail userDetail);

	@Query("SELECT new com.aim.response.HoursLogByMonthDay(h.hoursDate, h.dailyHours, h.extraHours,h.vacationHours) FROM HourLog h where  Date(h.hoursDate) = Date(:date) and YEAR(h.hoursDate) = :year and h.userDetail.userDetailId = :userDetailId")
	List<HoursLogByMonthDay> getHourLogByMonthDay(@Param("userDetailId") Integer userDetailId, @Param("year") Integer year, @Param("date") Date date);

	@Query("SELECT SUM(h.dailyHours) FROM HourLog h where h.userDetail = :userDetail AND h.userDetail.user= :user AND YEAR(h.hoursDate) = YEAR(NOW()) AND MONTH(h.hoursDate)= MONTH(NOW())")
	Double findCurrentMonthDailyHourByUser(@Param("user")User user, @Param("userDetail") UserDetail userDetail);

	@Query("SELECT SUM(h.extraHours) FROM HourLog h where h.userDetail = :userDetail AND h.userDetail.user= :user AND YEAR(h.hoursDate) = YEAR(NOW()) AND MONTH(h.hoursDate)= MONTH(NOW())")
	Double findCurrentMonthExtraHourByUser(@Param("user")User user, @Param("userDetail") UserDetail userDetail);

	@Query("SELECT SUM(h.vacationHours) FROM HourLog h where h.userDetail = :userDetail AND h.userDetail.user= :user AND YEAR(h.hoursDate) = YEAR(NOW()) AND MONTH(h.hoursDate)= MONTH(NOW())")
	Double findCurrentMonthVactionHourByUser(@Param("user")User user, @Param("userDetail") UserDetail userDetail);

	@Query("SELECT SUM(h.dailyHours) FROM HourLog h where h.userDetail = :userDetail AND h.userDetail.user= :user AND YEAR(h.hoursDate) = :year")
	Double findCurrentYearDailyHourByUser(@Param("user") User user, @Param("year") Integer year, @Param("userDetail") UserDetail userDetail);

	@Query("SELECT SUM(h.extraHours) FROM HourLog h where h.userDetail = :userDetail AND h.userDetail.user= :user AND YEAR(h.hoursDate) = :year")
	Double findCurrentYearExtraHourByUser(@Param("user") User user, @Param("year") Integer year, @Param("userDetail") UserDetail userDetail);

	@Query("SELECT SUM(h.vacationHours) FROM HourLog h where h.userDetail = :userDetail AND h.userDetail.user= :user AND YEAR(h.hoursDate) = :year")
	Double findCurrentYearVacationHourByUser(@Param("user") User user, @Param("year") Integer year, @Param("userDetail") UserDetail userDetail);

	@Query("SELECT h FROM HourLog h where  MONTH(h.hoursDate) = :month and YEAR(h.hoursDate) = :year and h.userDetail.userDetailId = :userDetailId")
	List<HourLog> getHourLogDayByMonth(@Param("userDetailId") Integer userDetailId, @Param("year") Integer year,@Param("month") Integer month);

}
