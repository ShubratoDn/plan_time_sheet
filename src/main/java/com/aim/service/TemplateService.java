package com.aim.service;

import java.util.Date;
import java.util.List;

import com.aim.entity.Template;
import com.aim.entity.User;
import com.aim.enums.MailTemplateType;

public interface TemplateService {

	Integer saveTemplate(String subject, String htmlDate, MailTemplateType mailTemplateType, Integer id,
			boolean role_supervisor_permission,
			boolean role_admin_permission,
			boolean role_user_permission,String templateName);

	Template getTemplate(Integer id);

	Iterable<Template> getAllTempalte();

	void deleteTemplate(Integer id);

	List<Template> getMailByType(MailTemplateType admintimesheetsubmit);

	List<Template> setSubmission(List<Template> templatesUnSet, User user, Date startDate, Date endDate,
			Integer userDetailId);

	List<Template> setApproved(List<Template> templatesUnSet, User user, String startDate, String endDate,
			Integer userDetailId);

	List<Template> setRejected(List<Template> templatesRejectUnSet, User user, String startDate, String endDate,
			Integer userDetailId);

	List<Template> setSchedular(List<Template> templatesUnSet, String startDate, String endDate);

	boolean getTemplateByName(String templateName, Integer id);

	List<Template> setPendingTimesheet(List<Template> templatesUnSet, String startDate, String endDate,
			Integer userDetailsId);

	List<Template> getCloneTemplate(MailTemplateType mailTemplateType, String type);

	List<Template> setMailGenaral(List<Template> templatesUnSet);

}
