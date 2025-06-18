package com.aim.service;

import com.aim.entity.UserFile;
import com.aim.repository.UserFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserFileService {

    @Autowired
    private UserFileRepository userFileRepository;

    public UserFile findById(int id){
        return  userFileRepository.findById(id).orElse(null);
    }
}
