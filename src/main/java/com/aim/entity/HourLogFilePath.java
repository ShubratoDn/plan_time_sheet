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

@Entity
@Table(name = "Hour_log_file_path")
public class HourLogFilePath extends BaseEntity<Serializable> {

	private static final long serialVersionUID = -1689724489061190756L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "file_original_name")
	private String fileOriginalName;
	
	@Column(name = "file_path")
	private String filePath;
	
	@JoinColumn(name = "hour_log_file_id")
	@ManyToOne
	private HourLogFile hourLogFile;
	
	@Column(name="admin_added_file")
	private boolean adminAddedFile;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFileOriginalName() {
		return fileOriginalName;
	}

	public void setFileOriginalName(String fileOriginalName) {
		this.fileOriginalName = fileOriginalName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public HourLogFile getHourLogFile() {
		return hourLogFile;
	}

	public void setHourLogFile(HourLogFile hourLogFile) {
		this.hourLogFile = hourLogFile;
	}

	public boolean isAdminAddedFile() {
		return adminAddedFile;
	}

	public void setAdminAddedFile(boolean adminAddedFile) {
		this.adminAddedFile = adminAddedFile;
	}
	
}
