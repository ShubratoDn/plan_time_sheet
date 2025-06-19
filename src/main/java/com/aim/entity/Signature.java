package com.aim.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name="signature")
public class Signature {
	
	private static final long serialVersionUID = -1048116796994064840L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "html_data",columnDefinition="TEXT")
	@NotEmpty(message = "Please provide an template")
	private String htmlDate;
	
	@Column(name = "title")
	@NotEmpty(message = "Please provide an title")
	private String title;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getHtmlDate() {
		return htmlDate;
	}

	public void setHtmlDate(String htmlDate) {
		this.htmlDate = htmlDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
