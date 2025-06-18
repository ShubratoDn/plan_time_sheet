package com.aim.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.aim.entity.Activity;
import com.aim.entity.Client;
import com.aim.entity.Company;
import com.aim.entity.Manager;
import com.aim.entity.User;
import com.aim.enums.ClientType;
import com.aim.enums.Functionality;
import com.aim.enums.UserDetailsType;
import com.aim.request.ClientAddDetail;
import com.aim.request.RolePermissionRequest;
import com.aim.response.AdminUserHoursChart;
import com.aim.response.CustomerResponse;
import com.aim.response.HomePageUserResponse;
import com.aim.response.UserTotalRevenueChart;
import com.aim.utils.Response;

public interface AdminService {

	void setRejectTimeSheet(String key, String reason, Integer userDetailId, User user);

	void setApproveTimeSheet(String key, Integer userDetailId, User user);

	List<HomePageUserResponse> getUserTotalHour(Integer year, Integer user, Integer month, UserDetailsType userDetailsType);

	List<Activity> getActivity(String type, String role, Integer id);

	AdminUserHoursChart getUserMonthHour(Integer year, Integer user, Integer userDetails, UserDetailsType userDetailsType);

	AdminUserHoursChart getUserMonthDayHour(Integer year, Integer user, Integer month, UserDetailsType userDetailsType);

	UserTotalRevenueChart getUserMonthRevenue(Integer year, Integer user, Integer userDetailId, UserDetailsType userDetailsType);

	UserTotalRevenueChart getUserMonthDayRevenue(Integer year, Integer user, int i, UserDetailsType userDetailsType);

	Client newClientAdd(ClientAddDetail client);

	List<Client> getClient();

	List<Client> getEmployer();

	Client getEmployerDetails(Integer id);

	List<Client> getVendor();

	Client getClientByEmail(String email);

	ResponseEntity<Response> newClientExist(ClientAddDetail client);

	Manager getManagerByEmail(String email);

	ClientAddDetail getClient(Client client);

	List<Manager> getClientByType(ClientType client);

	RolePermissionRequest getRolePermission();

	void setRolePermission(Integer id, Functionality functionality, boolean create, boolean delete, boolean read,
			boolean update);

	List<CustomerResponse> getCustomer(ClientType clientType);

}
