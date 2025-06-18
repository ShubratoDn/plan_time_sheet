package com.aim.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.aim.entity.Company;
import com.aim.entity.PermissionPlan;
import com.aim.entity.UserCompany;
import com.aim.request.CompanyRequest;
import com.aim.utils.Response;

public interface CompanyService {

	void saveCompanyAndAdminUser(CompanyRequest companyRequest);

	Boolean isValidActivationKey(String key,String key1);

	void varify(String key,String key1);

	void edit(Company company);

	void active(Integer companyId, boolean b);

	ResponseEntity<Response> resendCompanyEmailVarify(Integer companyId);

	void sendMailToSuperAdminForCompanyActive(String cId);

	void sendUserInformCompanyIsActive(Company company);
	
	public String setPlanStringBySuperAdmin(PermissionPlan permissionPlan);

	void changePermissionPlan(PermissionPlan permissionPlan, Company company);

	List<UserCompany> getUserCompaniesByCompanyIdAndRole(Integer companyId,String role);

}
