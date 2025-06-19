package com.aim.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;

@Entity
@Table(name = "activity")
@Transactional
public class Activity extends BaseEntity<Serializable> {

	private static final long serialVersionUID = -5531122231251828973L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "ip_address")
	private String ipAddress;
	
	@Column(name = "note")
	private String note;
	
	@Column(name = "activity_type")
	private String activityType;
	
	@JoinColumn(name = "user_id")
	@ManyToOne
	private User activityByUser;
	
	@JoinColumn(name = "user_details_id")
	@ManyToOne
	private UserDetail activityOnUserDetails;

	@Column(name = "other_note",columnDefinition="TEXT",nullable=true)
	private String otherNote;
	
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getActivityType() {
		return activityType;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

	public User getActivityByUser() {
		return activityByUser;
	}

	public void setActivityByUser(User activityByUser) {
		this.activityByUser = activityByUser;
	}

	public UserDetail getActivityOnUserDetails() {
		return activityOnUserDetails;
	}

	public void setActivityOnUserDetails(UserDetail activityOnUserDetails) {
		this.activityOnUserDetails = activityOnUserDetails;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getOtherNote() {
		return otherNote;
	}

	public void setOtherNote(String otherNote) {
		this.otherNote = otherNote;
	}
	
}
