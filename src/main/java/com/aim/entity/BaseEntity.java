package com.aim.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

@MappedSuperclass
public abstract class BaseEntity<ID extends Serializable> implements Serializable{

	@JsonIgnore
	@Column(name="created_datetime")
	public Date createdDatetime;
	
	@JsonIgnore
	@Column(name="modified_datetime")
	public Date modifiedDatetime;
	
	@PrePersist
	public void prePersist(){
		this.createdDatetime	= new Date();
		this.modifiedDatetime	= this.createdDatetime;
	}
	
	@PreUpdate
	public void preUpdate(){
		this.modifiedDatetime = new Date();
	}

	public Date getCreateDate() {
		return this.createdDatetime;
	}

	public Date getModifyDate() {
		return this.modifiedDatetime;
	}

}
