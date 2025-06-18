package com.aim.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.aim.entity.User;
import com.aim.repository.UserRepository;
import com.aim.service.PermissionService;

@Controller
public class DataMenuController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PermissionService permissionService;
	
	@ModelAttribute("userTotalCount")
	public int getReviewCount() {
		
		List<User> userList = userRepository.findByRole("ROLE_USER");
		return userList.size();
	}
	@ModelAttribute("permissionService")
	public Object permissionService() {
		
		return permissionService;
	}
}
