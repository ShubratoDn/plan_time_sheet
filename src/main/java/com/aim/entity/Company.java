package com.aim.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "company")
public class Company extends BaseEntity<Serializable>{

	private static final long serialVersionUID = -8427651149107165528L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name ="name")
	private String name;
	
	@Column(name ="image_path")
	private String imagePath;
	
	@Column(name ="address")
	private String address;
	
	@Column(name ="url_slug")
	private String urlSlug;
	
	@Column(name ="db_name")
	private String dbName;
	
	@Column(name ="file_folder")
	private String fileFolder;
	
	@Column(name ="details")
	private String details;
	
	@Column(name ="varify")
	private boolean varify;
	
	@Column(name ="active")
	private boolean active;

	@Column(name = "uuid")
	private String uuid;
	
	@Column(name = "timesheet_submit_email")
	private String timesheetSubmitEmail;
	
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getUrlSlug() {
		return urlSlug;
	}

	public void setUrlSlug(String urlSlug) {
		this.urlSlug = urlSlug;
	}

	public boolean isVarify() {
		return varify;
	}

	public void setVarify(boolean varify) {
		this.varify = varify;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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
