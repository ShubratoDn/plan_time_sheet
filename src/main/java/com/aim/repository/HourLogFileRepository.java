package com.aim.repository;

import java.util.Date;
import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aim.entity.HourLogFile;
import com.aim.entity.User;
import com.aim.entity.UserDetail;
import com.aim.response.UserDetailActiveMonth;

@Repository
@Transactional
public interface HourLogFileRepository extends CrudRepository <HourLogFile, Integer> {

	@Query("SELECT h FROM HourLogFile h where h.userDetail = :userDetail and (YEAR(h.startDate) = :year or YEAR(h.endDate) = :year)")
	List<HourLogFile> findHourLogFile(@Param("userDetail") UserDetail userDetail, @Param("year") Integer year);

	@Query("SELECT h FROM HourLogFile h where h.approve = :approve AND h.reject = :reject AND (YEAR(h.startDate) = :yearFile or YEAR(h.endDate) = :yearFile) ORDER BY h.modifiedDatetime DESC")
	List<HourLogFile> findAllHourLogFile(@Param("yearFile") Integer yearFile, @Param("approve") boolean approve,
			@Param("reject") boolean reject);
	
	@Query("SELECT h FROM HourLogFile h where (YEAR(h.startDate) = :yearFile or YEAR(h.endDate) = :yearFile) ORDER BY h.modifiedDatetime DESC")
	List<HourLogFile> findAllHourLogFileNoSort(@Param("yearFile") Integer yearFile);

	List<HourLogFile> findByStartDateAndEndDateAndUserDetail(Date startDate, Date endDate, UserDetail userDetail);

	HourLogFile findByStartDateAndEndDateAndUserDetailAndApprove(Date date, Date addDays, UserDetail userDetail,
			Boolean aPPROVED);

	Integer countByApproveAndReject(Boolean aPPROVED, Boolean nOT_REJECTED);
	
	@Query("SELECT COUNT(h) FROM HourLogFile h where h.approve = :approve AND h.reject = :reject AND YEAR(h.startDate) = :year AND MONTH(h.startDate) = :month ")
	Integer countByApproveAndRejectByMonthAndYear(@Param("month") Integer month, @Param("year") Integer year, 
			@Param("approve") boolean approve, @Param("reject") boolean reject);

	HourLogFile findByIdAndApproveNot(Integer id, boolean b);

	@Query("SELECT h FROM HourLogFile h where h.userDetail.user = :user AND h.approve = :approve AND h.reject = :reject AND (YEAR(h.startDate) = :yearFile or YEAR(h.endDate) = :yearFile) ORDER BY h.modifiedDatetime DESC")
	List<HourLogFile> findAllHourLogFileByUser(@Param("yearFile") Integer yearFile, @Param("user") User user, @Param("approve") boolean approve, @Param("reject") boolean reject);
	
	@Query("SELECT h FROM HourLogFile h where h.userDetail.user = :user AND (YEAR(h.startDate) = :yearFile or YEAR(h.endDate) = :yearFile) ORDER BY h.modifiedDatetime DESC")
	List<HourLogFile> findAllHourLogFileNoSortByUser(@Param("yearFile") Integer yearFile, @Param("user") User user);

	@Query("SELECT h FROM HourLogFile h where h.userDetail.user = :user AND h.approve = :approve AND YEAR(h.startDate) = :year AND MONTH(h.startDate) = :month ORDER BY h.modifiedDatetime DESC")
	List<HourLogFile> getHourLogFile(@Param("month") Integer month, @Param("year") Integer year, @Param("approve") boolean approve, @Param("user") User user);

	@Query("SELECT h FROM HourLogFile h where h.userDetail.user = :user AND YEAR(h.startDate) = :year AND MONTH(h.startDate) = :month ORDER BY h.modifiedDatetime DESC")
	List<HourLogFile> getHourLogFile(@Param("month") Integer month, @Param("year") Integer year, @Param("user") User user);

	@Query("SELECT COUNT(h) FROM HourLogFile h where h.approve = :approve AND h.reject = :reject AND YEAR(h.startDate) = :year AND h.userDetail = :userDetail")
	Integer countByApproveAndRejectAndUserDetailActive(@Param("approve") boolean approve, @Param("reject") boolean reject, @Param("year") Integer year, @Param("userDetail") UserDetail userDetail);
	
	@Query("SELECT h.userDetail FROM HourLogFile h where YEAR(h.startDate) = :year AND h.userDetail.user.role = 'ROLE_USER' group by h.userDetail")
	List<UserDetail> findTotalActiveUser(@Param("year") Integer year);

	@Query("SELECT h.userDetail FROM HourLogFile h where YEAR(h.startDate) = :year AND h.userDetail.ptax = 0.0 And h.userDetail.user.role = 'ROLE_USER' group by h.userDetail")
	List<UserDetail> findTotalActiveUserByC2CType(@Param("year") Integer year);

	@Query("SELECT h.userDetail FROM HourLogFile h where YEAR(h.startDate) = :year AND h.userDetail.ptax != 0.0 And h.userDetail.user.role = 'ROLE_USER' group by h.userDetail")
	List<UserDetail> findTotalActiveUserByPtaxType(@Param("year") Integer year);

	@Query("SELECT h.userDetail FROM HourLogFile h where MONTH(h.startDate) = :month AND YEAR(h.startDate) = :year AND h.userDetail.user.role = 'ROLE_USER' group by h.userDetail")
	List<UserDetail> findTotalActiveUserByMonth(@Param("month") Integer month, @Param("year") Integer year);

	@Query("SELECT h.userDetail FROM HourLogFile h where MONTH(h.startDate) = :month AND YEAR(h.startDate) = :year AND h.userDetail.ptax = 0.0 And h.userDetail.user.role = 'ROLE_USER' group by h.userDetail")
	List<UserDetail> findTotalActiveUserByC2CTypeByMonth(@Param("month") Integer month, @Param("year") Integer year);

	@Query("SELECT h.userDetail FROM HourLogFile h where MONTH(h.startDate) = :month AND YEAR(h.startDate) = :year AND h.userDetail.ptax != 0.0 And h.userDetail.user.role = 'ROLE_USER' group by h.userDetail")
	List<UserDetail> findTotalActiveUserByPtaxTypeByMonth(@Param("month") Integer month, @Param("year") Integer year);

	@Query("SELECT COUNT(h) FROM HourLogFile h where h.approve = :approve AND h.reject = :reject AND YEAR(h.startDate) = :year")
	int countByApproveAndRejectByYear(@Param("year") Integer year, 
			@Param("approve") boolean approve, @Param("reject") boolean reject);
	
	@Query("SELECT COUNT(h) FROM HourLogFile h where h.approve = :approve AND h.reject = :reject AND YEAR(h.startDate) = :year AND h.userDetail.user.id = :userId")
	int countByApproveAndRejectAndUserByYear(@Param("year") Integer year, 
			@Param("approve") boolean approve, @Param("reject") boolean reject, @Param("userId") Integer userId);

	@Query("SELECT new com.aim.response.UserDetailActiveMonth(h.userDetail,Month(h.startDate)) FROM HourLogFile h where year(h.startDate) = :year group by Month(h.startDate), h.userDetail")
	List<UserDetailActiveMonth> UserDetailActiveMonth(@Param("year") Integer year);
	
	@Query("SELECT new com.aim.response.UserDetailActiveMonth(h.userDetail,Day(h.startDate)) FROM HourLogFile h where Month(h.startDate) = :month And year(h.startDate) = :year group by h.startDate,h.userDetail")
	List<com.aim.response.UserDetailActiveMonth> UserDetailActiveDay(@Param("year") Integer year, @Param("month") Integer month);

	@Query("SELECT h FROM HourLogFile h where h.approve = :approve AND h.reject = :reject AND h.userDetail.userDetailId = :userDetailId AND (YEAR(h.startDate) = :yearFile or YEAR(h.endDate) = :yearFile) ORDER BY h.modifiedDatetime DESC")
	List<HourLogFile> findAllHourLogNotApprovedFile(@Param("userDetailId") Integer userDetailId, @Param("yearFile") Integer yearFile, @Param("approve") boolean approve,
			@Param("reject") boolean reject);

	List<HourLogFile> findByUserDetail(UserDetail userDetail);

}
