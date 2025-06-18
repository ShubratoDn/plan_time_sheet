package com.aim.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aim.entity.TimeSheetSubmission;
import com.aim.entity.UserDetail;

@Repository
@Transactional
public interface TimeSheetSubmissionRepository extends CrudRepository<TimeSheetSubmission, Integer> {

	TimeSheetSubmission findByKeyAndUserDetail(String key, UserDetail userDetail);

	@Query(value="SELECT * FROM timeSheet.time_sheet_submission WHERE approve = true AND \n" + 
			"(DATE(:hoursDate) BETWEEN DATE(week_start_date) AND DATE(week_end_date))",nativeQuery=true)
	TimeSheetSubmission findByApproveByDate(@Param("hoursDate") Date hoursDate);

	List<TimeSheetSubmission> findAllByApprove(boolean b);

	List<TimeSheetSubmission> findAllByApproveAndReject(boolean b, boolean c);

	List<TimeSheetSubmission> findAllByApproveAndRejectAndSubmit(boolean b, boolean c, boolean d);

}
