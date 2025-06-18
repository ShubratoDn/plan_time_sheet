package com.aim.service;

import com.aim.entity.HourLogFile;
import com.aim.repository.HourLogFilePathRepository;
import com.aim.repository.HourLogFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HourLogFileService {

    @Autowired
    private HourLogFileRepository hourLogPathRepository;
    public HourLogFile findById(int id){
        return hourLogPathRepository.findById(id).orElse(null);
    }
}
