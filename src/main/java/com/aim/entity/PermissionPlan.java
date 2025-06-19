package com.aim.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "permission_plan")
public class PermissionPlan {

	private static final long serialVersionUID = -1689724489061190756L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "company_id")
	private Company company;
	
	@Column(name="user_limit")
	private int userLimit; 
	
	@Column(name="user_can_login")
	private boolean userCanLogin;
	
	@Column(name="commission")
	private boolean commission;
	
	@Column(name="schedular_can_set")
	private boolean schedularCanSet;
	
	@Column(name="template_can_set")
	private boolean templateCanSet;
	
	@Column(name="qb_integration")
	private boolean qbIntegration;

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public int getUserLimit() {
		return userLimit;
	}

	public void setUserLimit(int userLimit) {
		this.userLimit = userLimit;
	}

	public boolean isUserCanLogin() {
		return userCanLogin;
	}

	public void setUserCanLogin(boolean userCanLogin) {
		this.userCanLogin = userCanLogin;
	}

	public boolean isCommission() {
		return commission;
	}

	public void setCommission(boolean commission) {
		this.commission = commission;
	}

	public boolean isSchedularCanSet() {
		return schedularCanSet;
	}

	public void setSchedularCanSet(boolean schedularCanSet) {
		this.schedularCanSet = schedularCanSet;
	}

	public boolean isTemplateCanSet() {
		return templateCanSet;
	}

	public void setTemplateCanSet(boolean templateCanSet) {
		this.templateCanSet = templateCanSet;
	}

	public boolean isQbIntegration() {
		return qbIntegration;
	}

	public void setQbIntegration(boolean qbIntegration) {
		this.qbIntegration = qbIntegration;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
}
