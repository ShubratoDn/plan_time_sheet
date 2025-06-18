package com.aim.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.aim.entity.Template;
import com.aim.entity.User;
import com.aim.entity.UserDetail;
import com.aim.enums.DefaultMailTemplate;
import com.aim.enums.EmailNameShortCut;
import com.aim.enums.MailTemplateType;
import com.aim.enums.TimeSheetPeriod;
import com.aim.repository.TemplateRepository;
import com.aim.repository.UserDetailsRepository;

@Service
public class TemplateServiceImpl implements TemplateService{

	@Autowired
	private TemplateRepository templateRepository;
	
	@Autowired
	private UserDetailsRepository userDetailsRepository;
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private UserService userService;
	
	@Value("${timesheet.server.url}")
	private String TIMESHEET_SERVER_URL;
	
	private String PRIVATE_SIGNATURE = "{{private_signature}}";
	
	/**
	 * Add Or edit template 
	 */
	@Override
	public Integer saveTemplate(String subject, String htmlDate, MailTemplateType mailTemplateType, Integer id,
			boolean roleSupervisorPermission,
			boolean roleAdminPermission,
			boolean roleUserPermission,String templateName) {
		
		Template template = new Template();
		if(id != null) {
			template = templateRepository.findOne(id);
		}
		template.setHtmlDate(htmlDate);
		template.setMailTemplateType(mailTemplateType);
		template.setSubject(subject);
		template.setRoleAdminPermission(roleAdminPermission);
		template.setRoleSupervisorPermission(roleSupervisorPermission);
		template.setRoleUserPermission(roleUserPermission);
		template.setTemplateName(templateName);
		templateRepository.save(template);
		
		return template.getId();
	}
	
	/**
	 * get template 
	 */
	@Override
	public Template getTemplate(Integer id) {
		
		Template template = templateRepository.findOne(id);
		return template;
	}

	@Override
	public Iterable<Template> getAllTempalte() {
		Iterable<Template> templates = templateRepository.findAll();
		return templates;
	}

	@Override
	public void deleteTemplate(Integer id) {
		Template template = templateRepository.findOne(id);
		if(template != null)
			templateRepository.delete(template);
	}
	
	@Override
	public List<Template> getMailByType(MailTemplateType mailTemplateType) {
		User user1 = (User) request.getSession().getAttribute("user");
		List<Template> templates = new ArrayList<Template>();
		if(user1.getRole().equals("ROLE_ADMIN")) {
			templates = templateRepository.findByMailTemplateTypeAndRoleAdminPermission(mailTemplateType,true);
		}else if(user1.getRole().equals("ROLE_USER")) {
			templates = templateRepository.findByMailTemplateTypeAndRoleUserPermission(mailTemplateType,true);
		}else if(user1.getRole().equals("ROLE_SUPERVISOR")) {
			templates = templateRepository.findByMailTemplateTypeAndRoleSupervisorPermission(mailTemplateType,true);
		}
		
		if(templates == null)
			templates = new ArrayList<Template>(); 
			
		return templates;
	}

	@Override
	public List<Template> setSubmission(List<Template> templatesUnSet, User user, Date startDate,
			Date endDate, Integer userDetailId) {
		
		SimpleDateFormat simpleDateformat3 = new SimpleDateFormat("MM/dd/yyyy");
		User user1 = (User) request.getSession().getAttribute("user");
		List<Template> template = new ArrayList<Template>();
		if(CollectionUtils.isEmpty(templatesUnSet)) {
			return template;
		}
		
		User userExists = userService.findUserByEmail(user1.getEmail());
		final String PRIVATE_SIGN = StringUtils.isEmpty(userExists.getPrivateSign())?"":userExists.getPrivateSign();;
		
		UserDetail userDetail = userDetailsRepository.findByUserDetailId(userDetailId);
		
		for(Template unSet:templatesUnSet) {
			
			Template template2 = new Template(); 
			String replaceString = unSet.getHtmlDate().replace(EmailNameShortCut.USER_FIRST_NAME.urlParam,user.getFirstName());
			replaceString = replaceString.replace(EmailNameShortCut.USER_LAST_NAME.urlParam,user.getLastName());
			replaceString = replaceString.replace(EmailNameShortCut.CLIENT_NAME.urlParam,userDetail.getClientName());
			replaceString = replaceString.replace(EmailNameShortCut.TIMESHEET_ADDED_BY_FIRST_NAME.urlParam,user1.getFirstName());
			replaceString = replaceString.replace(EmailNameShortCut.TIMESHEET_ADDED_BY_LAST_NAME.urlParam,user1.getLastName());
			
			if(endDate != null) {
				replaceString = replaceString.replace(EmailNameShortCut.END_DATE.urlParam,simpleDateformat3.format(endDate));
			} else {
				if(userDetail.getTimeSheetPeriod().equals(TimeSheetPeriod.ONE_WEEK.urlParam)) {
					endDate = DateUtils.addDays(startDate, 6);
				} else if(userDetail.getTimeSheetPeriod().equals(TimeSheetPeriod.ONE_MONTH.urlParam)) {
					Calendar calendar3 = Calendar.getInstance();
					calendar3.setTime(startDate);
					int days = calendar3.getActualMaximum(Calendar.DAY_OF_MONTH);
					
					endDate = DateUtils.addDays(startDate, days-1);
					
				} else if(userDetail.getTimeSheetPeriod().equals(TimeSheetPeriod.TWO_WEEK.urlParam)){
					endDate = DateUtils.addDays(startDate, 13);
				} else {
					endDate = startDate;
				}
				replaceString = replaceString.replace(EmailNameShortCut.END_DATE.urlParam,simpleDateformat3.format(endDate));
			}
			replaceString = replaceString.replace(EmailNameShortCut.START_DATE.urlParam,simpleDateformat3.format(startDate));
			replaceString = replaceString.replace(PRIVATE_SIGNATURE,PRIVATE_SIGN);
		
			template2.setHtmlDate(replaceString);
			template2.setId(unSet.getId());
			template2.setMailTemplateType(unSet.getMailTemplateType());
			template2.setTemplateName(unSet.getTemplateName());
			
			String replaceString1 = unSet.getSubject().replace(EmailNameShortCut.USER_FIRST_NAME.urlParam,user.getFirstName());
			replaceString1 = replaceString1.replace(EmailNameShortCut.USER_LAST_NAME.urlParam,user.getLastName());
			replaceString1 = replaceString1.replace(EmailNameShortCut.CLIENT_NAME.urlParam,userDetail.getClientName());
			replaceString1 = replaceString1.replace(EmailNameShortCut.TIMESHEET_ADDED_BY_FIRST_NAME.urlParam,user1.getFirstName());
			replaceString1 = replaceString1.replace(EmailNameShortCut.TIMESHEET_ADDED_BY_LAST_NAME.urlParam,user1.getLastName());
			replaceString1 = replaceString1.replace(EmailNameShortCut.END_DATE.urlParam,simpleDateformat3.format(endDate));
			replaceString1 = replaceString1.replace(EmailNameShortCut.START_DATE.urlParam,simpleDateformat3.format(startDate));
			
			template2.setSubject(replaceString1);
			template.add(template2);
			
		}
		return template;
	}

	@Override
	public List<Template> setApproved(List<Template> templatesUnSet, User user, String startDate, String endDate,
			Integer userDetailId) {
		
		User user1 = (User) request.getSession().getAttribute("user");
		List<Template> template = new ArrayList<Template>();
		if(CollectionUtils.isEmpty(templatesUnSet)) {
			return template;
		}
		User userExists = userService.findUserByEmail(user1.getEmail());
		final String PRIVATE_SIGN = StringUtils.isEmpty(userExists.getPrivateSign())?"":userExists.getPrivateSign();;
		
		UserDetail userDetail = userDetailsRepository.findByUserDetailId(userDetailId);
		
		for(Template unSet:templatesUnSet) {
			
			Template template2 = new Template(); 
			String replaceString = unSet.getHtmlDate().replace(EmailNameShortCut.USER_FIRST_NAME.urlParam,user.getFirstName());
			replaceString = replaceString.replace(EmailNameShortCut.USER_LAST_NAME.urlParam,user.getLastName());
			replaceString = replaceString.replace(EmailNameShortCut.CLIENT_NAME.urlParam,userDetail.getClientName());
			replaceString = replaceString.replace(EmailNameShortCut.TIMESHEET_APPROVED_BY_FIRST_NAME.urlParam,user1.getFirstName());
			replaceString = replaceString.replace(EmailNameShortCut.TIMESHEET_APPROVED_BY_LAST_NAME.urlParam,user1.getLastName());
			replaceString = replaceString.replace(EmailNameShortCut.END_DATE.urlParam,endDate);
			replaceString = replaceString.replace(EmailNameShortCut.START_DATE.urlParam,startDate);
		
			replaceString = replaceString.replace(PRIVATE_SIGNATURE,PRIVATE_SIGN);
			
			template2.setHtmlDate(replaceString);
			template2.setId(unSet.getId());
			template2.setMailTemplateType(unSet.getMailTemplateType());
			template2.setTemplateName(unSet.getTemplateName());
			
			String replaceString1 = unSet.getSubject().replace(EmailNameShortCut.USER_FIRST_NAME.urlParam,user.getFirstName());
			replaceString1 = replaceString1.replace(EmailNameShortCut.USER_LAST_NAME.urlParam,user.getLastName());
			replaceString1 = replaceString1.replace(EmailNameShortCut.CLIENT_NAME.urlParam,userDetail.getClientName());
			replaceString1 = replaceString1.replace(EmailNameShortCut.TIMESHEET_APPROVED_BY_FIRST_NAME.urlParam,user1.getFirstName());
			replaceString1 = replaceString1.replace(EmailNameShortCut.TIMESHEET_APPROVED_BY_LAST_NAME.urlParam,user1.getLastName());
			replaceString1 = replaceString1.replace(EmailNameShortCut.END_DATE.urlParam,endDate);
			replaceString1 = replaceString1.replace(EmailNameShortCut.START_DATE.urlParam,startDate);
			
			template2.setSubject(replaceString1);
			template.add(template2);
			
		}
		return template;
	}

	@Override
	public List<Template> setRejected(List<Template> templatesRejectUnSet, User user, String startDate, String endDate,
			Integer userDetailId) {
		
		User user1 = (User) request.getSession().getAttribute("user");
		List<Template> template = new ArrayList<Template>();
		if(CollectionUtils.isEmpty(templatesRejectUnSet)) {
			return template;
		}
		
		UserDetail userDetail = userDetailsRepository.findByUserDetailId(userDetailId);
		User userExists = userService.findUserByEmail(user1.getEmail());
		final String PRIVATE_SIGN = StringUtils.isEmpty(userExists.getPrivateSign())?"":userExists.getPrivateSign();;
		
		for(Template unSet:templatesRejectUnSet) {
			
			Template template2 = new Template(); 
			String replaceString = unSet.getHtmlDate().replace(EmailNameShortCut.USER_FIRST_NAME.urlParam,user.getFirstName());
			replaceString = replaceString.replace(EmailNameShortCut.USER_LAST_NAME.urlParam,user.getLastName());
			replaceString = replaceString.replace(EmailNameShortCut.CLIENT_NAME.urlParam,userDetail.getClientName());
			replaceString = replaceString.replace(EmailNameShortCut.TIMESHEET_REJECT_BY_FIRST_NAME.urlParam,user1.getFirstName());
			replaceString = replaceString.replace(EmailNameShortCut.TIMESHEET_REJECT_BY_LAST_NAME.urlParam,user1.getLastName());
			replaceString = replaceString.replace(EmailNameShortCut.END_DATE.urlParam,endDate);
			replaceString = replaceString.replace(EmailNameShortCut.START_DATE.urlParam,startDate);
			replaceString = replaceString.replace(PRIVATE_SIGNATURE,PRIVATE_SIGN);
			
			template2.setHtmlDate(replaceString);
			template2.setId(unSet.getId());
			template2.setMailTemplateType(unSet.getMailTemplateType());
			template2.setTemplateName(unSet.getTemplateName());
			
			String replaceString1 = unSet.getSubject().replace(EmailNameShortCut.USER_FIRST_NAME.urlParam,user.getFirstName());
			replaceString1 = replaceString1.replace(EmailNameShortCut.USER_LAST_NAME.urlParam,user.getLastName());
			replaceString1 = replaceString1.replace(EmailNameShortCut.CLIENT_NAME.urlParam,userDetail.getClientName());
			replaceString1 = replaceString1.replace(EmailNameShortCut.TIMESHEET_REJECT_BY_FIRST_NAME.urlParam,user1.getFirstName());
			replaceString1 = replaceString1.replace(EmailNameShortCut.TIMESHEET_REJECT_BY_LAST_NAME.urlParam,user1.getLastName());
			replaceString1 = replaceString1.replace(EmailNameShortCut.END_DATE.urlParam,endDate);
			replaceString1 = replaceString1.replace(EmailNameShortCut.START_DATE.urlParam,startDate);
			
			template2.setSubject(replaceString1);
			template.add(template2);
			
		}
		return template;
	}

	@Override
	public List<Template> setPendingTimesheet(List<Template> templatesUnSet, String startDate, String endDate,
			Integer userDetailId) {
		
		User user1 = (User) request.getSession().getAttribute("user");
		List<Template> template = new ArrayList<Template>();
		if(CollectionUtils.isEmpty(templatesUnSet)) {
			return template;
		}
		User userExists = userService.findUserByEmail(user1.getEmail());
		final String PRIVATE_SIGN = StringUtils.isEmpty(userExists.getPrivateSign())?"":userExists.getPrivateSign();;
		
		UserDetail userDetail = userDetailsRepository.findByUserDetailId(userDetailId);
		
		for(Template unSet:templatesUnSet) {
			
			Template template2 = new Template(); 
			String replaceString = unSet.getHtmlDate().replace(EmailNameShortCut.USER_FIRST_NAME.urlParam,userDetail.getUser().getFirstName());
			replaceString = replaceString.replace(EmailNameShortCut.USER_LAST_NAME.urlParam,userDetail.getUser().getLastName());
			replaceString = replaceString.replace(EmailNameShortCut.CLIENT_NAME.urlParam,userDetail.getClientName());
			replaceString = replaceString.replace(EmailNameShortCut.END_DATE.urlParam,endDate);
			replaceString = replaceString.replace(EmailNameShortCut.START_DATE.urlParam,startDate);
			replaceString = replaceString.replace(PRIVATE_SIGNATURE,PRIVATE_SIGN);
			
			template2.setHtmlDate(replaceString);
			template2.setId(unSet.getId());
			template2.setMailTemplateType(unSet.getMailTemplateType());
			template2.setTemplateName(unSet.getTemplateName());
			
			String replaceString1 = unSet.getSubject().replace(EmailNameShortCut.USER_FIRST_NAME.urlParam,userDetail.getUser().getFirstName());
			replaceString1 = replaceString1.replace(EmailNameShortCut.USER_LAST_NAME.urlParam,userDetail.getUser().getLastName());
			replaceString1 = replaceString1.replace(EmailNameShortCut.CLIENT_NAME.urlParam,userDetail.getClientName());
			replaceString1 = replaceString1.replace(EmailNameShortCut.END_DATE.urlParam,endDate);
			replaceString1 = replaceString1.replace(EmailNameShortCut.START_DATE.urlParam,startDate);
			
			template2.setSubject(replaceString1);
			template.add(template2);
			
		}
		return template;
	}
	
	@Override
	public List<Template> setSchedular(List<Template> templatesSchedularUnSet, String startDate, String endDate) {
		
		List<Template> template = new ArrayList<Template>();
		if(CollectionUtils.isEmpty(templatesSchedularUnSet)) {
			return template;
		}
		User user1 = (User) request.getSession().getAttribute("user");
		User userExists = userService.findUserByEmail(user1.getEmail());
		final String PRIVATE_SIGN = StringUtils.isEmpty(userExists.getPrivateSign())?"":userExists.getPrivateSign();;
		
		for(Template unSet:templatesSchedularUnSet) {
			
			Template template2 = new Template(); 
			String replaceString = unSet.getHtmlDate().replace(EmailNameShortCut.SCHEDULAR_START_DATE.urlParam,startDate);
			replaceString = replaceString.replace(EmailNameShortCut.SCHEDULAR_END_DATE.urlParam,endDate);
			replaceString = replaceString.replace(PRIVATE_SIGNATURE,PRIVATE_SIGN);
			
			template2.setHtmlDate(replaceString);
			template2.setId(unSet.getId());
			template2.setMailTemplateType(unSet.getMailTemplateType());
			template2.setTemplateName(unSet.getTemplateName());
			
			String replaceString1 = unSet.getSubject().replace(EmailNameShortCut.SCHEDULAR_START_DATE.urlParam,startDate);
			replaceString1 = replaceString1.replace(EmailNameShortCut.SCHEDULAR_END_DATE.urlParam,endDate);
			
			template2.setSubject(replaceString1);
			template.add(template2);
			
		}
		return template;
	}

	@Override
	public List<Template> getCloneTemplate(MailTemplateType mailTemplateType, String type) {
		List<Template> templates = templateRepository.findByMailTemplateType(mailTemplateType);
		
		List<DefaultMailTemplate> defaultMailTemplates = DefaultMailTemplate.getDefaultMailTemplateByType(type);
		
		for(DefaultMailTemplate defaultMailTemplate : defaultMailTemplates) {
			Template template = new Template();
			template.setHtmlDate(defaultMailTemplate.htmlData);
			template.setMailTemplateType(mailTemplateType);
			template.setRoleAdminPermission(true);
			template.setRoleSupervisorPermission(true);
			template.setRoleUserPermission(true);
			template.setTemplateName(defaultMailTemplate.displayLabel);
			template.setSubject(defaultMailTemplate.subject);
			templates.add(template);
		}
		
		return templates;
	}

	@Override
	public boolean getTemplateByName(String templateName, Integer id) {
		if(id != null) {
			Template template = templateRepository.findOne(id);
			
			Template templateByName = templateRepository.findByTemplateName(templateName);
			if(templateByName != null && !template.getTemplateName().equals(templateName)) {
				return true;
			}else {
				return false;
			}
		} else {
			Template templateByName = templateRepository.findByTemplateName(templateName);
			if(templateByName != null)
				return true;
			else 
				return false;
		}
		
	}
	@Override
	public List<Template> setMailGenaral(List<Template> templatesSchedularUnSet) {
		List<Template> template = new ArrayList<Template>();
		if(CollectionUtils.isEmpty(templatesSchedularUnSet)) {
			return template;
		}
		User user1 = (User) request.getSession().getAttribute("user");
		User userExists = userService.findUserByEmail(user1.getEmail());
		final String PRIVATE_SIGN = StringUtils.isEmpty(userExists.getPrivateSign())?"":userExists.getPrivateSign();
		
		for(Template unSet:templatesSchedularUnSet) {
			
			Template template2 = new Template(); 
			String replaceString = unSet.getHtmlDate().replace(PRIVATE_SIGNATURE,PRIVATE_SIGN);
			
			template2.setHtmlDate(replaceString);
			template2.setId(unSet.getId());
			template2.setMailTemplateType(unSet.getMailTemplateType());
			template2.setTemplateName(unSet.getTemplateName());
			template2.setSubject(unSet.getSubject());
			template.add(template2);
			
		}
		return template;
	}

}
