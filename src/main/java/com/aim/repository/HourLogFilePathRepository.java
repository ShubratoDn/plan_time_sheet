package com.aim.repository;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.aim.entity.HourLogFile;
import com.aim.entity.HourLogFilePath;

@Repository
@Transactional
public interface HourLogFilePathRepository extends CrudRepository <HourLogFilePath, Integer> {

	List<HourLogFilePath> findByHourLogFile(HourLogFile hourLogFile);

	HourLogFilePath findByIdAndHourLogFile(Integer hourLogId, HourLogFile hourLogFile);


}
