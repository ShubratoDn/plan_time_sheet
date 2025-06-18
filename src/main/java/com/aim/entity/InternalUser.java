package com.aim.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import com.aim.enums.RateCountOn;
import com.aim.enums.RateType;

@Entity
@Table(name = "internal_user")
public class InternalUser implements Serializable {
	
	private static final long serialVersionUID = -1048116796994064840L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "first_name")
	private String firstname;
	
	@Column(name = "last_name")
	private String lastname;
	
	@Column(name = "role")
	private String internalUserType;
	
	@Column(name = "email")
	@Email(message = "Please provide a valid Email")
	@NotEmpty(message = "Please provide an email")
	private String email;
	
	@Column(name = "work_email")
	private String workEmail;
	
	@Column(name = "phone")
	private String phone;
	
	@Column(name = "rate_type")
	@Enumerated(EnumType.STRING)
	private RateType rateType;
	
	@Column(name = "rate_count_on")
	@Enumerated(EnumType.STRING)
	private RateCountOn rateCountOn;
	
	@Column(name = "recurssive", columnDefinition = "boolean default false", nullable = false)
	private boolean recurssive;
	
	@Column(name = "rate")
	private Float rate;
	
	@Column(name = "default_user")
	private boolean defaultUser;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getInternalUserType() {
		return internalUserType;
	}

	public void setInternalUserType(String internalUserType) {
		this.internalUserType = internalUserType;
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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public RateType getRateType() {
		return rateType;
	}

	public void setRateType(RateType rateType) {
		this.rateType = rateType;
	}

	public Float getRate() {
		return rate;
	}

	public void setRate(Float rate) {
		this.rate = rate;
	}

	public boolean isRecurssive() {
		return recurssive;
	}

	public void setRecurssive(boolean recurssive) {
		this.recurssive = recurssive;
	}

	public RateCountOn getRateCountOn() {
		return rateCountOn;
	}

	public void setRateCountOn(RateCountOn rateCountOn) {
		this.rateCountOn = rateCountOn;
	}

	public boolean isDefaultUser() {
		return defaultUser;
	}

	public void setDefaultUser(boolean defaultUser) {
		this.defaultUser = defaultUser;
	}
	
}
