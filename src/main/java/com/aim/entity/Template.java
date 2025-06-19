package com.aim.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

import com.aim.enums.MailTemplateType;

@Entity
@Table(name = "template")
public class Template extends BaseEntity<Serializable>{

	private static final long serialVersionUID = -1048116796994064840L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "html_data",columnDefinition="TEXT")
	@NotEmpty(message = "Please provide an template")
	private String htmlDate;
	
	@Column(name = "subject")
	@NotEmpty(message = "Please provide an subject")
	private String subject;
	
	@Column(name = "template_name")
	@NotEmpty(message = "Please provide an template name")
	private String templateName;
	
	@Column(name ="mail_template_type")
	@Enumerated(EnumType.STRING)
	private MailTemplateType mailTemplateType;
	
	@Column(name="role_user_permission",columnDefinition = "boolean default false", nullable = false)
	private boolean roleUserPermission = false;
	
	@Column(name="role_admin_permission", columnDefinition = "boolean default false", nullable = false)
	private boolean roleAdminPermission = false;
	
	@Column(name="role_supervisor_permission", columnDefinition = "boolean default false", nullable = false)
	private boolean roleSupervisorPermission = false;
	
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

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public MailTemplateType getMailTemplateType() {
		return mailTemplateType;
	}

	public void setMailTemplateType(MailTemplateType mailTemplateType) {
		this.mailTemplateType = mailTemplateType;
	}

	public boolean isRoleUserPermission() {
		return roleUserPermission;
	}

	public void setRoleUserPermission(boolean roleUserPermission) {
		this.roleUserPermission = roleUserPermission;
	}

	public boolean isRoleAdminPermission() {
		return roleAdminPermission;
	}

	public void setRoleAdminPermission(boolean roleAdminPermission) {
		this.roleAdminPermission = roleAdminPermission;
	}

	public boolean isRoleSupervisorPermission() {
		return roleSupervisorPermission;
	}

	public void setRoleSupervisorPermission(boolean roleSupervisorPermission) {
		this.roleSupervisorPermission = roleSupervisorPermission;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	
}
