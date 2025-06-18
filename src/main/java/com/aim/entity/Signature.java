package com.aim.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

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
