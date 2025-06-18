package com.aim.request;

import java.util.List;

import com.aim.enums.ClientType;

public class ClientAddDetail {
	
	private Integer id;
	
	private String clientName;
	
	private String address;
	
	private ClientType type;
	
	private String zipCode;
	
	private String phone;
	
	private String remark;

	private List<ManagerDetail> managerDetails;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public ClientType getType() {
		return type;
	}

	public void setType(ClientType type) {
		this.type = type;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<ManagerDetail> getManagerDetails() {
		return managerDetails;
	}

	public void setManagerDetails(List<ManagerDetail> managerDetails) {
		this.managerDetails = managerDetails;
	}
    
}
