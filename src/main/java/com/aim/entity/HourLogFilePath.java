package com.aim.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
