package com.aim.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "user")
public class User implements Serializable {

	private static final long serialVersionUID = 4445508962572095520L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private int id;
	
	@Column(name = "email")
	@Email(message = "Please provide a valid Email")
	@NotEmpty(message = "Please provide an email")
	private String email;
	
	@Column(name = "work_email")
	private String workEmail;
	
	@Column(name = "password")
	private String password="$2a$10$6NiMlx20LP69VPJJwGZZL.AudxhBk19zEws70inkeVwRcoiMAFKsm";
	
	@Column(name = "first_name")
	@NotEmpty(message = "Please provide your name")
	private String firstName;
	
	@Column(name = "last_name")
	@NotEmpty(message = "Please provide your last name")
	private String lastName;
	
	@Column(name = "phone")
	@NotEmpty(message = "Please enter your phone")
	private String phone;
	
	@Column(name = "active")
	private int active;
	
	@Column(name = "client_active")
	private Integer clientActiveId;
	
	@Column(name = "role")
	private String role;
	
	@Column(name = "uuid")
	private String uuid;
	
	@Column(name ="file_folder")
	private String fileFolder;

	@ManyToOne
	@JoinColumn(name = "company_id")
	private Company company;

	@Column(name = "private_sign",columnDefinition="TEXT",nullable = true)
	private String privateSign;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWorkEmail() {
		return workEmail;
	}

	public void setWorkEmail(String workEmail) {
		this.workEmail = workEmail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Integer getClientActiveId() {
		return clientActiveId;
	}

	public void setClientActiveId(Integer clientActiveId) {
		this.clientActiveId = clientActiveId;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getPrivateSign() {
		return privateSign;
	}

	public void setPrivateSign(String privateSign) {
		this.privateSign = privateSign;
	}
	
	public String getFileFolder() {
		return fileFolder;
	}

	public void setFileFolder(String fileFolder) {
		this.fileFolder = fileFolder;
	}
	
}