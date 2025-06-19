package com.aim.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "time_sheet_submission")
public class TimeSheetSubmission extends BaseEntity<Serializable> {

	private static final long serialVersionUID = 5977383081308232473L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	/**
	 * This column will have below kind of value
	 * yyyy-mm-dd To yyyy-mm-dd
	 */
	@Column(name = "key_value")
	@NotEmpty(message = "Please provide key")
	private String key;
	
	@Column(name = "submit")
	@NotNull
	private boolean submit;
	
	@Column(name = "approve")
	private boolean approve;
	
	@Column(name = "reject")
	private boolean reject;
	
	@Column(name = "reject_reason",columnDefinition="TEXT")
	private String rejectReason;
	
	@JoinColumn(name = "rejected_by")
	@ManyToOne
	private User rejectedBy;
	
	@JoinColumn(name = "approved_by")
	@ManyToOne
	private User approvedBy;
	
	@Column(name = "rejected_date")
	private Date rejectedDate;

	@Column(name = "approved_date")
	private Date approvedDate;

	@JoinColumn(name = "user_detail_id")
	@ManyToOne
	private UserDetail userDetail;
	
	@Column(name = "week_start_date")
	private Date weekStartDate;

	@Column(name = "week_end_date")
	private Date weekEndDate;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public boolean isSubmit() {
		return submit;
	}

	public void setSubmit(boolean submit) {
		this.submit = submit;
	}

	public boolean isApprove() {
		return approve;
	}

	public void setApprove(boolean approve) {
		this.approve = approve;
	}

	public boolean isReject() {
		return reject;
	}

	public void setReject(boolean reject) {
		this.reject = reject;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public User getRejectedBy() {
		return rejectedBy;
	}

	public void setRejectedBy(User rejectedBy) {
		this.rejectedBy = rejectedBy;
	}

	public User getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(User approvedBy) {
		this.approvedBy = approvedBy;
	}

	public Date getRejectedDate() {
		return rejectedDate;
	}

	public void setRejectedDate(Date rejectedDate) {
		this.rejectedDate = rejectedDate;
	}

	public Date getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	public UserDetail getUserDetail() {
		return userDetail;
	}

	public void setUserDetail(UserDetail userDetail) {
		this.userDetail = userDetail;
	}

	public Date getWeekStartDate() {
		return weekStartDate;
	}

	public Date getWeekEndDate() {
		return weekEndDate;
	}

	public void setWeekStartDate(Date weekStartDate) {
		this.weekStartDate = weekStartDate;
	}

	public void setWeekEndDate(Date weekEndDate) {
		this.weekEndDate = weekEndDate;
	}

}