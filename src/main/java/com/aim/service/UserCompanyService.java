package com.aim.service;

import com.aim.entity.UserCompany;
import com.aim.repository.UserCompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserCompanyService {

    @Autowired
    private UserCompanyRepository userCompanyRepository;

    public UserCompany findOne(int id){
        return userCompanyRepository.findById(id).orElse(null);
    }
}
