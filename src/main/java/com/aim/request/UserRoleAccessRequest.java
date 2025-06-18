package com.aim.request;

import com.aim.enums.Functionality;

public class UserRoleAccessRequest {

	private Integer id;
	
	private String role;
	
	private Functionality functionality;
	
	private boolean create;
	private boolean createShow;
	
	private boolean update;
	private boolean updateShow;
	
	private boolean read;
	private boolean readShow;
	
	private boolean delete;
	private boolean deleteShow;
	
	private boolean own;
	private boolean ownShow;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public Functionality getFunctionality() {
		return functionality;
	}
	public void setFunctionality(Functionality functionality) {
		this.functionality = functionality;
	}
	public boolean isCreate() {
		return create;
	}
	public void setCreate(boolean create) {
		this.create = create;
	}
	public boolean isCreateShow() {
		return createShow;
	}
	public void setCreateShow(boolean createShow) {
		this.createShow = createShow;
	}
	public boolean isUpdate() {
		return update;
	}
	public void setUpdate(boolean update) {
		this.update = update;
	}
	public boolean isUpdateShow() {
		return updateShow;
	}
	public void setUpdateShow(boolean updateShow) {
		this.updateShow = updateShow;
	}
	public boolean isRead() {
		return read;
	}
	public void setRead(boolean read) {
		this.read = read;
	}
	public boolean isReadShow() {
		return readShow;
	}
	public void setReadShow(boolean readShow) {
		this.readShow = readShow;
	}
	public boolean isDelete() {
		return delete;
	}
	public void setDelete(boolean delete) {
		this.delete = delete;
	}
	public boolean isDeleteShow() {
		return deleteShow;
	}
	public void setDeleteShow(boolean deleteShow) {
		this.deleteShow = deleteShow;
	}
	public boolean isOwn() {
		return own;
	}
	public void setOwn(boolean own) {
		this.own = own;
	}
	public boolean isOwnShow() {
		return ownShow;
	}
	public void setOwnShow(boolean ownShow) {
		this.ownShow = ownShow;
	}
	
}
