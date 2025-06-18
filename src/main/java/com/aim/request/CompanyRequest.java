package com.aim.request;

import com.aim.entity.User;

public class CompanyRequest {
	
	private Integer id;
	
	private String name;
	
	private String address="";
	
	private String details="";
	
	private String urlSlug;
	
	private String fileFolder;
	
	private String timesheetSubmitEmail;
	
	private User admin;
	
	private String imagePath;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrlSlug() {
		return urlSlug;
	}

	public void setUrlSlug(String urlSlug) {
		this.urlSlug = urlSlug;
	}

	public User getAdmin() {
		return admin;
	}

	public void setAdmin(User admin) {
		this.admin = admin;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getTimesheetSubmitEmail() {
		return timesheetSubmitEmail;
	}

	public void setTimesheetSubmitEmail(String timesheetSubmitEmail) {
		this.timesheetSubmitEmail = timesheetSubmitEmail;
	}

	public String getFileFolder() {
		return fileFolder;
	}

	public void setFileFolder(String fileFolder) {
		this.fileFolder = fileFolder;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	
}
