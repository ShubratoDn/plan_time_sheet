package com.aim.model;

import java.util.List;
import java.util.concurrent.Callable;

import com.aim.config.TenantContext;
import com.aim.entity.User;
import com.aim.repository.UserRepository;

public class GetSuperAdmin implements Callable<List<User>>{

    private UserRepository userRepository;
 
    public GetSuperAdmin(UserRepository userCompanyRepository) {
        this.userRepository = userCompanyRepository;
    }
 
    public List<User> call() throws Exception {
    	TenantContext.setCurrentTenant(null);
    	List<User> dbCurrent = userRepository.findByRole("ROLE_SUPER_ADMIN");
    	TenantContext.clear();
        return dbCurrent;
    }
}
