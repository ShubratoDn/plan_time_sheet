package com.aim.service.email;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.aim.config.MailPropertyConfig;
import com.aim.entity.Company;
import com.aim.entity.HourLogFile;
import com.aim.entity.User;
import com.aim.entity.UserCompany;
import com.aim.entity.UserDetail;

/**
 * Send event specific emails
 */
@Service
public class EmailSenderServiceImpl implements EmailSenderService ,Runnable {

	@Autowired
	private EmailService emailService;
	
	@Autowired
	private TemplateEngine templateEngine;
	
	@Value("${timesheet.server.url}")
	private String TIMESHEET_SERVER_URL;
	
	private String email;
	private String fromEmail;
	private String htmlContent;
	private String subject;
	private File file;
	private String ccEmail; 
	
	public EmailSenderServiceImpl(){ }
	
	public EmailSenderServiceImpl(String email, String htmlContent, String subject, String ccEmail, String fromEmail) {
		
		this.email = email;
		this.htmlContent = htmlContent;
		this.subject = subject;
		this.ccEmail = ccEmail;
		this.fromEmail = fromEmail;
	}
	
	@Override
	public void run() {
		System.out.println(htmlContent);
		if(file != null) {
			emailService.sendHTMLMailWithFile(fromEmail, email, subject, htmlContent,file, ccEmail);
			
		} else {
			emailService.sendHTMLMail(fromEmail, email, subject, htmlContent, ccEmail);
		}
	}
	
	/**
	 * start executor 
	 */
	private void startExecutorService() {
		
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(this);
		executor.shutdown();
	}
	
	/**
	 * Send Password email
	 */
	@Override
	public void sendSuccessEmailWithPasswordLink(String email,String name,String key, String companyKey) {
		
		final Context ctx = new Context(Locale.US);
		ctx.setVariable("rootURL", TIMESHEET_SERVER_URL);
		ctx.setVariable("activationKey", key);
		ctx.setVariable("companyKey", companyKey);
		ctx.setVariable("name", name);
		final String htmlContent = templateEngine.process("mail/registration_success", ctx);
		this.email = email;
		this.htmlContent = htmlContent;
		this.fromEmail = MailPropertyConfig.FROMEMAIL;
		this.ccEmail = null;
		this.subject = "Welcome to Timesheet by PDDN";
		startExecutorService();
	}
	
	/**
	 * Send Password email
	 * mail new design set
	 */
	@Override
	public void sendMailToAdminUserSingup(String email,String name) {
		
		final Context ctx = new Context(Locale.US);
		ctx.setVariable("rootURL", TIMESHEET_SERVER_URL);
		ctx.setVariable("name", name);
		final String htmlContent = templateEngine.process("mail/registration_user", ctx);
		this.email = email;
		this.htmlContent = htmlContent;
		this.ccEmail = null;
		this.fromEmail = MailPropertyConfig.FROMEMAIL;
		this.subject = "Welcome to Timesheet management system by ManageTP";
		startExecutorService();
	}
	
	/**
	 * forgot password mail send to user 
	 */
	@Override
	public void sendForgotPasswordEmail(User userExists, String verifyEmailLink, String rootUrl) {
		
		final Context ctx = new Context(Locale.US);
		ctx.setVariable("rootURL", TIMESHEET_SERVER_URL);
	    ctx.setVariable("fullname", userExists.getFirstName() +' ' + userExists.getLastName());
	    ctx.setVariable("serverUrl", rootUrl);
	    ctx.setVariable("forgotPasswordLink", verifyEmailLink);
	    final String htmlContent = templateEngine.process("mail/forgot-password-email", ctx);

	    this.email = userExists.getEmail();
	    this.htmlContent = htmlContent;
	    this.subject = "Password Reset Link";
	    this.ccEmail = null;
	    this.fromEmail = MailPropertyConfig.FROMEMAIL;
	    startExecutorService();
		
	}

	@Override
	public void sendEmailToUserWithCC(String message, String email, String subject, String ccEmail, File file, String fromMail) {
		this.email = email;
	    this.htmlContent = message;
	    this.subject = subject;
	    this.file = file;
	    this.ccEmail = ccEmail;
	    this.fromEmail = fromMail;
	    startExecutorService();
		
	}

	/**
	 * 
	 */
	@Override
	public void sendCompanyAddEmail(UserCompany userCompany, String verifyEmailLink, String rootUrl) {
		final Context ctx = new Context(Locale.US);
		ctx.setVariable("rootURL", rootUrl);
		ctx.setVariable("activationLink", verifyEmailLink);
		ctx.setVariable("companyName", userCompany.getCompany().getName());
		ctx.setVariable("name", userCompany.getUser().getFirstName() + ' ' + userCompany.getUser().getLastName());
		final String htmlContent = templateEngine.process("mail/company_varify_email", ctx);
		this.email = userCompany.getUser().getEmail();
		this.htmlContent = htmlContent;
		this.ccEmail = null;
		this.fromEmail = MailPropertyConfig.FROMEMAIL;
		this.subject = "ManageTP: Welcome aboard ";
		startExecutorService();
	}

	@Override
	public void sendAddCompanyMailToSuperAdmin(UserCompany userCompany,User user) {
		
		final Context ctx = new Context(Locale.US);
		ctx.setVariable("rootURL", TIMESHEET_SERVER_URL);
		ctx.setVariable("companyName", userCompany.getCompany().getName());
		ctx.setVariable("name", userCompany.getUser().getFirstName() + ' ' + userCompany.getUser().getLastName());
		final String htmlContent = templateEngine.process("mail/addNewCompanyMaliToSuperAdmin", ctx);
		this.email = user.getEmail();
		this.htmlContent = htmlContent;
		this.ccEmail = null;
		this.fromEmail = MailPropertyConfig.FROMEMAIL;
		this.subject = "New company add in Timesheet ";
		startExecutorService();
		
	}

	@Override
	public void sendMailToSuperAdminForCompanyActive(Company company,User user, String verifyEmailLink, String tIMESHEET_SERVER_URL) {
		
		final Context ctx = new Context(Locale.US);
		ctx.setVariable("rootURL", TIMESHEET_SERVER_URL);
		ctx.setVariable("verifyEmailLink", verifyEmailLink);
		ctx.setVariable("companyName", company.getName());
		final String htmlContent = templateEngine.process("mail/mailToSuperAdminForCompanyActive", ctx);
		this.email = user.getEmail();
		this.htmlContent = htmlContent;
		this.ccEmail = null;
		this.fromEmail = MailPropertyConfig.FROMEMAIL;
		this.subject = "New company add in Timesheet ";
		startExecutorService();
		
	}

	@Override
	public void sendUserInformCompanyIsActive(Company company, User user, String verifyEmailLink,
			String tIMESHEET_SERVER_URL) {

		final Context ctx = new Context(Locale.US);
		ctx.setVariable("rootURL", TIMESHEET_SERVER_URL);
		ctx.setVariable("verifyEmailLink", verifyEmailLink);
		ctx.setVariable("companyName", company.getName());
		final String htmlContent = templateEngine.process("mail/sendUserInformCompanyIsActive", ctx);
		this.email = user.getEmail();
		this.htmlContent = htmlContent;
		this.ccEmail = null;
		this.fromEmail = MailPropertyConfig.FROMEMAIL;
		this.subject = "New company add in Timesheet ";
		startExecutorService();
		
	}
	
}
