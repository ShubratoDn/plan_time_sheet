package com.aim.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aim.entity.Schedular;

@Repository
@Transactional
public interface SchedularRepository extends CrudRepository<Schedular,Integer>{

	@Query("SELECT s FROM Schedular s where YEAR(s.hoursDate) = :year AND MONTH(s.hoursDate) = :month AND DAY(s.hoursDate)=:day")
	Schedular findBySchedular(@Param("day") Integer day, @Param("month") Integer month,@Param("year") Integer year);

	@Query("SELECT s FROM Schedular s where YEAR(s.hoursDate) = :year AND s.dayOff = true")
	List<Schedular> findByYearAndDayOff(@Param("year") Integer year);

}
