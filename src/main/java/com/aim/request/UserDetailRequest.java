package com.aim.request;

import java.util.Date;

import jakarta.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.aim.entity.User;
import com.aim.enums.RateCountOn;
import com.aim.enums.RateType;
import com.aim.enums.W2OrC2cType;

public class UserDetailRequest {

	private Integer userDetailId;
	
	private String clientName;
	
	private String invoiceTo;
	
	private String vendorName;
	
	private String employerName;
	
	private String employerPhone;
	
	private String employerEmail;
	
	private String address;
	
	@NotNull(message = "Please provide a client rate")
	private Float clientRate;
	
	private Float consultantRate;
	
	
	@NotNull(message = "Please provide a recruiter")
	private Integer recruiterId;
	
	private String recruiterName;
	
	@NotNull(message = "Please provide a recruiter commission")
	private Float recCommission;
	
	@NotNull(message = "Please provide a recruiter rate type")
	private RateType recRateType;
	
	private boolean recRecurssive;
	
	private boolean rECDefault;
	
	private RateCountOn recRateCountOn;
	
	private Integer recRecurssiveMonth;
	
	@NotNull(message = "Please provide an account manager")
	private Integer accountManagerId;
	
	private String accountManagerName;
	
	@NotNull(message = "Please provide an account manager Commission")
	private Float aCMCommission;
	
	@NotNull(message = "Please provide a account manager rate type")
	private RateType aCMRateType;
	
	private boolean aCMRecurssive;
	
	private boolean aCMDefault;
	
	private RateCountOn aCMRateCountOn;
	
	private Integer aCMRecurssiveMonth;
	
	
	@NotNull(message = "Please provide a business development manager")
	private Integer businessDevelopmentManagerId;
	
	private String businessDevelopmentManagerName;
	
	@NotNull(message = "Please provide a business development manager Commission")
	private Float bDMCommission;
	
	@NotNull(message = "Please provide a business development manager rate type")
	private RateType bDMRateType;
	
	private boolean bDMRecurssive;
	
	private boolean bDMDefault;
	
	private RateCountOn bDMRateCountOn;
	
	private Integer bDMRecurssiveMonth;
	
	
	private Float w2;
	
	private Float ptax;
	
	private Float c2cOrother;
	
	private RateType c2cOrotherRateType;
	
	private boolean c2cOrotherRecurssive;
	
	private Integer c2cOrotherRecurssiveMonth;
	
	
	@DateTimeFormat(pattern = "MM/dd/yyyy")
	private Date startDate;
	
	@DateTimeFormat(pattern = "MM/dd/yyyy")
	private Date endDate; 
	
	private String  timeSheetPeriod; 
	
	private boolean active;
	
	private User user;
	
	private Integer clientId;
	
	private Integer employerId;
	
	private Integer vendorId;

	private W2OrC2cType w2OrC2cType;
	
	public Integer getUserDetailId() {
		return userDetailId;
	}

	public void setUserDetailId(Integer userDetailId) {
		this.userDetailId = userDetailId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getEmployerName() {
		return employerName;
	}

	public void setEmployerName(String employerName) {
		this.employerName = employerName;
	}

	public String getEmployerPhone() {
		return employerPhone;
	}

	public void setEmployerPhone(String employerPhone) {
		this.employerPhone = employerPhone;
	}

	public String getEmployerEmail() {
		return employerEmail;
	}

	public void setEmployerEmail(String employerEmail) {
		this.employerEmail = employerEmail;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Float getClientRate() {
		return clientRate;
	}

	public void setClientRate(Float clientRate) {
		this.clientRate = clientRate;
	}

	public Float getConsultantRate() {
		return consultantRate;
	}

	public void setConsultantRate(Float consultantRate) {
		this.consultantRate = consultantRate;
	}

	public String getRecruiterName() {
		return recruiterName;
	}

	public void setRecruiterName(String recruiterName) {
		this.recruiterName = recruiterName;
	}

	public Float getRecCommission() {
		return recCommission;
	}

	public void setRecCommission(Float recCommission) {
		this.recCommission = recCommission;
	}

	public String getAccountManagerName() {
		return accountManagerName;
	}

	public void setAccountManagerName(String accountManagerName) {
		this.accountManagerName = accountManagerName;
	}

	public Float getaCMCommission() {
		return aCMCommission;
	}

	public void setaCMCommission(Float aCMCommission) {
		this.aCMCommission = aCMCommission;
	}

	public String getBusinessDevelopmentManagerName() {
		return businessDevelopmentManagerName;
	}

	public void setBusinessDevelopmentManagerName(String businessDevelopmentManagerName) {
		this.businessDevelopmentManagerName = businessDevelopmentManagerName;
	}

	public Float getbDMCommission() {
		return bDMCommission;
	}

	public void setbDMCommission(Float bDMCommission) {
		this.bDMCommission = bDMCommission;
	}

	public Float getW2() {
		return w2;
	}

	public void setW2(Float w2) {
		this.w2 = w2;
	}

	public Float getPtax() {
		return ptax;
	}

	public void setPtax(Float ptax) {
		this.ptax = ptax;
	}

	public Float getC2cOrother() {
		return c2cOrother;
	}

	public void setC2cOrother(Float c2cOrother) {
		this.c2cOrother = c2cOrother;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getTimeSheetPeriod() {
		return timeSheetPeriod;
	}

	public void setTimeSheetPeriod(String timeSheetPeriod) {
		this.timeSheetPeriod = timeSheetPeriod;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public W2OrC2cType getW2OrC2cType() {
		return w2OrC2cType;
	}

	public void setW2OrC2cType(W2OrC2cType w2OrC2cType) {
		this.w2OrC2cType = w2OrC2cType;
	}

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public Integer getEmployerId() {
		return employerId;
	}

	public void setEmployerId(Integer employerId) {
		this.employerId = employerId;
	}

	public RateType getRecRateType() {
		return recRateType;
	}

	public void setRecRateType(RateType recRateType) {
		this.recRateType = recRateType;
	}

	public RateType getaCMRateType() {
		return aCMRateType;
	}

	public void setaCMRateType(RateType aCMRateType) {
		this.aCMRateType = aCMRateType;
	}

	public RateType getbDMRateType() {
		return bDMRateType;
	}

	public void setbDMRateType(RateType bDMRateType) {
		this.bDMRateType = bDMRateType;
	}

	public RateType getC2cOrotherRateType() {
		return c2cOrotherRateType;
	}

	public void setC2cOrotherRateType(RateType c2cOrotherRateType) {
		this.c2cOrotherRateType = c2cOrotherRateType;
	}

	public Integer getVendorId() {
		return vendorId;
	}

	public void setVendorId(Integer vendorId) {
		this.vendorId = vendorId;
	}

	public Integer getRecruiterId() {
		return recruiterId;
	}

	public void setRecruiterId(Integer recruiterId) {
		this.recruiterId = recruiterId;
	}

	public Integer getAccountManagerId() {
		return accountManagerId;
	}

	public void setAccountManagerId(Integer accountManagerId) {
		this.accountManagerId = accountManagerId;
	}

	public Integer getBusinessDevelopmentManagerId() {
		return businessDevelopmentManagerId;
	}

	public void setBusinessDevelopmentManagerId(Integer businessDevelopmentManagerId) {
		this.businessDevelopmentManagerId = businessDevelopmentManagerId;
	}

	public boolean isRecRecurssive() {
		return recRecurssive;
	}

	public void setRecRecurssive(boolean recRecurssive) {
		this.recRecurssive = recRecurssive;
	}

	public boolean isaCMRecurssive() {
		return aCMRecurssive;
	}

	public void setaCMRecurssive(boolean aCMRecurssive) {
		this.aCMRecurssive = aCMRecurssive;
	}

	public boolean isbDMRecurssive() {
		return bDMRecurssive;
	}

	public void setbDMRecurssive(boolean bDMRecurssive) {
		this.bDMRecurssive = bDMRecurssive;
	}

	public boolean isC2cOrotherRecurssive() {
		return c2cOrotherRecurssive;
	}

	public void setC2cOrotherRecurssive(boolean c2cOrotherRecurssive) {
		this.c2cOrotherRecurssive = c2cOrotherRecurssive;
	}

	public RateCountOn getRecRateCountOn() {
		return recRateCountOn;
	}

	public void setRecRateCountOn(RateCountOn recRateCountOn) {
		this.recRateCountOn = recRateCountOn;
	}

	public RateCountOn getaCMRateCountOn() {
		return aCMRateCountOn;
	}

	public void setaCMRateCountOn(RateCountOn aCMRateCountOn) {
		this.aCMRateCountOn = aCMRateCountOn;
	}

	public RateCountOn getbDMRateCountOn() {
		return bDMRateCountOn;
	}

	public void setbDMRateCountOn(RateCountOn bDMRateCountOn) {
		this.bDMRateCountOn = bDMRateCountOn;
	}

	public Integer getRecRecurssiveMonth() {
		return recRecurssiveMonth;
	}

	public void setRecRecurssiveMonth(Integer recRecurssiveMonth) {
		this.recRecurssiveMonth = recRecurssiveMonth;
	}

	public Integer getaCMRecurssiveMonth() {
		return aCMRecurssiveMonth;
	}

	public void setaCMRecurssiveMonth(Integer aCMRecurssiveMonth) {
		this.aCMRecurssiveMonth = aCMRecurssiveMonth;
	}

	public Integer getbDMRecurssiveMonth() {
		return bDMRecurssiveMonth;
	}

	public void setbDMRecurssiveMonth(Integer bDMRecurssiveMonth) {
		this.bDMRecurssiveMonth = bDMRecurssiveMonth;
	}

	public Integer getC2cOrotherRecurssiveMonth() {
		return c2cOrotherRecurssiveMonth;
	}

	public void setC2cOrotherRecurssiveMonth(Integer c2cOrotherRecurssiveMonth) {
		this.c2cOrotherRecurssiveMonth = c2cOrotherRecurssiveMonth;
	}

	public boolean isrECDefault() {
		return rECDefault;
	}

	public void setrECDefault(boolean rECDefault) {
		this.rECDefault = rECDefault;
	}

	public boolean isaCMDefault() {
		return aCMDefault;
	}

	public void setaCMDefault(boolean aCMDefault) {
		this.aCMDefault = aCMDefault;
	}

	public boolean isbDMDefault() {
		return bDMDefault;
	}

	public void setbDMDefault(boolean bDMDefault) {
		this.bDMDefault = bDMDefault;
	}

	public String getInvoiceTo() {
		return invoiceTo;
	}

	public void setInvoiceTo(String invoiceTo) {
		this.invoiceTo = invoiceTo;
	}
	
}
