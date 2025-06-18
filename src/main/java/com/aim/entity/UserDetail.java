package com.aim.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.aim.enums.InvoiceTo;
import com.aim.enums.RateCountOn;
import com.aim.enums.RateType;
import com.aim.enums.W2OrC2cType;

@Entity
@Table(name = "user_detail")
public class UserDetail extends BaseEntity<Serializable>{

	private static final long serialVersionUID = 7507717334210793299L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_detail_id")
	private Integer userDetailId;
	
	@Column(name = "client_name")
	@NotEmpty(message = "Please provide a client name")
	private String clientName;
	
	@Column(name = "vendor_name")
	private String vendorName;
	
	@Column(name = "invoice_to")
	@Enumerated(EnumType.STRING)
	private InvoiceTo invoiceTo;
	
	@Column(name ="file_folder")
	private String fileFolder;
	
	@Column(name = "employer_name")
	private String employerName;
	
	@Column(name = "employer_phone")
	private String employerPhone;
	
	@Column(name = "employer_email")
	private String employerEmail;
	
	@Column(name = "address")
	private String address;
	
	@Column(name = "client_rate")
	@NotNull(message = "Please provide a client rate")
	private Float clientRate;
	
	@Column(name = "consultant_rate")
	private Float consultantRate;
	
	@Column(name = "recruiter_name")
	@NotEmpty(message = "Please provide a recruiter name")
	private String recruiterName;
	
	@ManyToOne
	@JoinColumn(name = "recruiter_id")
	private InternalUser recruiter;
	
	@Column(name = "recruiter_commission")
	@NotNull(message = "Please provide a recruiter commission")
	private Float recCommission;
	
	@Column(name = "recruiter_rate_type")
	@NotNull(message = "Please provide a recruiter rate type")
	private RateType recRateType;
	
	@Column(name = "recruiter_rate_count_on")
	@Enumerated(EnumType.STRING)
	private RateCountOn recRateCountOn;
	
	@Column(name = "recruiter_recurssive", columnDefinition = "boolean default false", nullable = false)
	private boolean recRecurssive;
	
	@Column(name = "recruiter_recurssive_month", nullable = true)
	private Integer recRecurssiveMonth;
	
	@Column(name = "account_manager_name")
	@NotEmpty(message = "Please provide an account manager name")
	private String accountManagerName;
	
	@ManyToOne
	@JoinColumn(name = "account_manager_id")
	private InternalUser accountManager;
	
	@Column(name = "account_manager_commission")
	@NotNull(message = "Please provide an account manager name")
	private Float aCMCommission;
	
	@Column(name = "account_manager_commission_rate_type")
	@NotNull(message = "Please provide a account manager rate type")
	private RateType aCMRateType;
	
	@Column(name = "account_manager_commission_rate_count_on")
	@Enumerated(EnumType.STRING)
	private RateCountOn aCMRateCountOn;
	
	@Column(name = "account_manager_recurssive", columnDefinition = "boolean default false", nullable = false)
	private boolean aCMRecurssive;
	
	@Column(name = "account_manager_recurssive_month", nullable = true)
	private Integer aCMRecurssiveMonth;
	
	@Column(name = "business_development_manager_name")
	@NotEmpty(message = "Please provide a business development manager name")
	private String businessDevelopmentManagerName;
	
	@ManyToOne
	@JoinColumn(name = "business_development_manager_id")
	private InternalUser businessDevelopmentManager;
	
	@Column(name = "business_development_manager_commission")
	@NotNull(message = "Please provide a recruiter name")
	private Float bDMCommission;
	
	@Column(name = "business_development_manager_commission_rate_type")
	@NotNull(message = "Please provide a business development manager rate type")
	private RateType bDMRateType;
	
	@Column(name = "business_development_manager_commission_rate_count_on")
	@Enumerated(EnumType.STRING)
	private RateCountOn bDMRateCountOn;
	
	@Column(name = "business_development_manager_recurssive", columnDefinition = "boolean default false", nullable = false)
	private boolean bDMRecurssive;
	
	@Column(name = "business_development_manager_recurssive_month", nullable = true)
	private Integer bDMRecurssiveMonth;
	
	@Column(name = "w2_or_c2c_type")
	private W2OrC2cType w2C2cType;
	
	@Column(name = "w2")
	private Float w2;
	
	@Column(name = "ptax")
	private Float ptax;
	
	@Column(name = "c2c_or_other")
	private Float c2cOrother;
	
	@Column(name = "c2c_or_other_rate_type")
	private RateType c2cOrotherRateType;
	
	@Column(name = "c2c_or_other_recurssive", columnDefinition = "boolean default false", nullable = false)
	private boolean c2cOrotherRecurssive;
	
	@Column(name = "c2c_or_other_recurssive_month", nullable = true)
	private Integer c2cOrotherRecurssiveMonth;
	
	
	@Column(name = "start_date")
	@DateTimeFormat(pattern = "MM/dd/yyyy")
	private Date startDate;
	
	@Column(name = "end_date")
	@DateTimeFormat(pattern = "MM/dd/yyyy")
	private Date endDate; 
	
	@Column(name = "time_sheet_period")
	private String  timeSheetPeriod; 
	
	@Column(name = "active")
	private boolean active;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "client_id")
	private Client client;
	
	@ManyToOne
	@JoinColumn(name = "employer_id")
	private Client employer;
	
	@ManyToOne
	@JoinColumn(name = "vendor_id")
	private Client vendor;

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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Client getEmployer() {
		return employer;
	}

	public void setEmployer(Client employer) {
		this.employer = employer;
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


	public Client getVendor() {
		return vendor;
	}

	public void setVendor(Client vendor) {
		this.vendor = vendor;
	}

	public InternalUser getRecruiter() {
		return recruiter;
	}

	public void setRecruiter(InternalUser recruiter) {
		this.recruiter = recruiter;
	}

	public InternalUser getAccountManager() {
		return accountManager;
	}

	public void setAccountManager(InternalUser accountManager) {
		this.accountManager = accountManager;
	}

	public InternalUser getBusinessDevelopmentManager() {
		return businessDevelopmentManager;
	}

	public void setBusinessDevelopmentManager(InternalUser businessDevelopmentManager) {
		this.businessDevelopmentManager = businessDevelopmentManager;
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

	public W2OrC2cType getW2C2cType() {
		return w2C2cType;
	}

	public void setW2C2cType(W2OrC2cType w2c2cType) {
		w2C2cType = w2c2cType;
	}
	
	public String getFileFolder() {
		return fileFolder;
	}

	public void setFileFolder(String fileFolder) {
		this.fileFolder = fileFolder;
	}

	public InvoiceTo getInvoiceTo() {
		return invoiceTo;
	}

	public void setInvoiceTo(InvoiceTo invoiceTo) {
		this.invoiceTo = invoiceTo;
	}
	
}
