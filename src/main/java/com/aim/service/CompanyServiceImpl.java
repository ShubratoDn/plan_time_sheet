package com.aim.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.aim.entity.Company;
import com.aim.entity.PermissionPlan;
import com.aim.entity.User;
import com.aim.entity.UserCompany;
import com.aim.enums.DefaultSuperAdminMailTemplet;
import com.aim.model.CompanySaveThread;
import com.aim.model.CompanyUserSaveThread;
import com.aim.model.ResponseGenerator;
import com.aim.repository.CompanyRepository;
import com.aim.repository.PermissionPlanRepository;
import com.aim.repository.UserCompanyRepository;
import com.aim.repository.UserRepository;
import com.aim.request.CompanyRequest;
import com.aim.service.email.EmailSenderService;
import com.aim.utils.Response;
import com.aim.utils.Utils;

@Service
public class CompanyServiceImpl implements CompanyService{

	final static Logger logger = LoggerFactory.getLogger(CompanyServiceImpl.class);
	
	@Autowired
	private CompanyRepository companyRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
    private UserService userService;
	
	@Autowired
	private EmailSenderService emailSenderService;
	
	@Autowired
	private UserCompanyRepository userCompanyRepository;
	
	@Autowired
	private PermissionPlanRepository permissionPlanRepository;
	
	@Value("${timesheet.server.url}")
	private String TIMESHEET_SERVER_URL;
	
	@Value("${sub.db.url}")
	private String SUB_DB_URL;
	
	@Value("${spring.datasource.username}")
	private String DB_USERNAME;
	
	@Value("${spring.datasource.password}")
	private String DB_PASSWORD;
	
	/**
	 * Save company and admin user
	 */
	@Override
	public void saveCompanyAndAdminUser(CompanyRequest companyRequest) {
		
		Company company = new Company();
		company.setAddress(companyRequest.getAddress());
		company.setDetails(companyRequest.getDetails());
		company.setName(companyRequest.getName());
		String urlSlug = getFinalSlugUrl(companyRequest.getName(),0);
		company.setUrlSlug(urlSlug);
		String uuidCompany = UUID.randomUUID().toString();
		company.setUuid(uuidCompany);
		company.setDbName(Utils.getDbName(urlSlug));
		company.setFileFolder(Utils.getDbName(urlSlug));
		company.setTimesheetSubmitEmail(companyRequest.getAdmin().getEmail());
		companyRepository.save(company);
		
		User user = userRepository.findByEmail(companyRequest.getAdmin().getEmail());
		if(user == null) {
			user = new User();
			String uuid = UUID.randomUUID().toString();
			user.setUuid(uuid);
			if(companyRequest.getAdmin().getPassword() == null) {
				user.setPassword(uuid);
			} else {
				user.setPassword(bCryptPasswordEncoder.encode(companyRequest.getAdmin().getPassword()));
			}
		}
		
		user.setRole("ROLE_ADMIN");
		user.setClientActiveId(null);
		user.setEmail(companyRequest.getAdmin().getEmail());
		user.setFileFolder(userService.getFinalFileFolder(companyRequest.getAdmin().getFirstName(),0));
		user.setFirstName(companyRequest.getAdmin().getFirstName());
		user.setLastName(companyRequest.getAdmin().getLastName());
		user.setPhone(companyRequest.getAdmin().getPhone());
		if(StringUtils.isEmpty(companyRequest.getAdmin().getWorkEmail())) {
			user.setWorkEmail(companyRequest.getAdmin().getEmail());
		} else {
			user.setWorkEmail(companyRequest.getAdmin().getWorkEmail());
		}
		
		user.setActive(1);
		user.setCompany(null);
		userRepository.save(user);
		
	    UserCompany userCompany = new UserCompany(); 
	    userCompany.setRole("ROLE_ADMIN");
	    userCompany.setCompany(company);
	    userCompany.setUser(user);
	    userCompanyRepository.save(userCompany);
	    
		String verifyEmailLink = TIMESHEET_SERVER_URL + "/company/active?cId=" + 
				company.getUuid() + "&uId=" + user.getUuid();
		
		emailSenderService.sendCompanyAddEmail(userCompany, verifyEmailLink, TIMESHEET_SERVER_URL);
		List<User> users = userRepository.findByRole("ROLE_SUPER_ADMIN");
		
		for(User user1:users) {
			emailSenderService.sendAddCompanyMailToSuperAdmin(userCompany,user1);
		}
	}

	/**
	 * Get final slug url
	 * @param name
	 * @param count
	 * @return
	 */
	private String getFinalSlugUrl(String name, int count) {
		
		name = name.trim();
		
		//Convert name to url slug
		String urlSlug = Utils.toSlug(name);
		
		if(companyRepository.findByUrlSlug(urlSlug) == null) {
			return urlSlug;
		} else {
			return getFinalSlugUrl(urlSlug + (count + 1), (count + 1));
		}
	}

	/**
	 * company key valid
	 */
	@Override
	public Boolean isValidActivationKey(String cId,String uId) {
		
		Company company = companyRepository.findByUuid(cId);
		if(company != null) {
			User user = userRepository.findByUuid(uId);
			
			if(user != null) {
				UserCompany userCompany = userCompanyRepository.findByCompanyAndUser(company,user);
				if(userCompany != null) {
					return true;
				} else {
					return false;
				}
				
			} else {
				return false;
			}
			
		} else {
			return false;
		}
	}

	@Override
	public void varify(String cId,String uId) {
		
		try {
			
			Company company = companyRepository.findByUuid(cId);
			if(!company.isVarify()) {
				User user = userRepository.findByUuid(uId);
				
				company.setActive(false);
				company.setVarify(true);
				company.setTimesheetSubmitEmail(user.getEmail());
				companyRepository.save(company);
				
				user.setActive(1);
				userRepository.save(user);
				
				UserCompany userCompany = userCompanyRepository.findByCompanyAndUser(company,user);
				userCompany.setActive(true);
				userCompanyRepository.save(userCompany);
				
				//String url = SUB_DB_URL;
				//Statement statement = null;
					
				Connection connection = DriverManager.getConnection(SUB_DB_URL+"?useSSL=false&user="+DB_USERNAME+"&password="+DB_PASSWORD); 
				
//				Class.forName("com.mysql.jdbc.Driver").newInstance();
//		
//				connection = DriverManager.getConnection(url, DB_USERNAME , DB_PASSWORD);
				Statement statement = connection.createStatement();
		
				statement.executeUpdate("CREATE DATABASE " + company.getDbName());
				statement.executeUpdate("use " + company.getDbName());
				
				ScriptRunner sr = new ScriptRunner(connection);
				
				String fileName = "static/db_migration/sub_company_db.sql";
		        ClassLoader classLoader = getClass().getClassLoader();
		        
				Reader reader = new BufferedReader(new FileReader(classLoader.getResource(fileName).getFile()));
				
				sr.runScript(reader);
				
				statement.close();
				connection.close();
				
				Company companyClone = SerializationUtils.clone(company);
				CompanyUserSaveThread companyUserSaveThread = new CompanyUserSaveThread(userRepository, companyRepository, user, companyClone, userCompany, company.getDbName());
				Thread t = new Thread(companyUserSaveThread);
				t.start();
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error in verify company : " + e);
		}
	}

	/**
	 * edit company
	 */
	@Override
	public void edit(Company company1) {
		Company company = companyRepository.findById(company1.getId()).orElse(null);
		company.setAddress(company1.getAddress());
		company.setDetails(company1.getDetails());
		company.setName(company1.getName());
		company.setFileFolder(company1.getFileFolder());
		companyRepository.save(company);
		
		Company companyClone = SerializationUtils.clone(company);
		CompanySaveThread companySaveThread = new CompanySaveThread(companyRepository,companyClone,companyClone.getDbName());
		Thread t1 = new Thread(companySaveThread);
		t1.start();
	}

	@Override
	public void active(Integer companyId, boolean active) {
		Company company = companyRepository.findById(companyId).orElse(null);
		company.setActive(active);
		companyRepository.save(company);
	}

	/**
	 * resend company email verify
	 */
	@Override
	public ResponseEntity<Response> resendCompanyEmailVarify(Integer companyId) {
		
		try {
			List<UserCompany> userCompany = userCompanyRepository.findByCompanyIdAndRole(companyId, "ROLE_ADMIN");
			User user = userCompany.get(0).getUser();
			Company company = userCompany.get(0).getCompany();
			String verifyEmailLink = TIMESHEET_SERVER_URL + "/company/active?cId=" + 
					company.getUuid() + "&uId=" + user.getUuid();
			
			System.out.println(verifyEmailLink);
			
			emailSenderService.sendCompanyAddEmail(userCompany.get(0), verifyEmailLink, TIMESHEET_SERVER_URL);
			return ResponseGenerator.generateResponse(new Response("Email send successfully", null), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("error in resend verify company email: " + e);
			return ResponseGenerator.generateResponse(new Response("Please try again", null), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	   
	}

	/**
	 * send mail to super admin for company active
	 */
	@Override
	public void sendMailToSuperAdminForCompanyActive(String cId) {
		
		Company company = companyRepository.findByUuid(cId);
		String verifyEmailLink = TIMESHEET_SERVER_URL + "/active/new-company?cId=" + cId;
		
		List<User> users = userRepository.findByRole("ROLE_SUPER_ADMIN");
		
		for(User user1:users) {
			emailSenderService.sendMailToSuperAdminForCompanyActive(company, user1, verifyEmailLink, TIMESHEET_SERVER_URL);
		}
	}

	@Override
	public void sendUserInformCompanyIsActive(Company company) {
		List<UserCompany> usercompanys = userCompanyRepository.findByCompanyIdAndRole(company.getId(), "ROLE_ADMIN");
		String verifyEmailLink = TIMESHEET_SERVER_URL + "/change-pass?uId=" + usercompanys.get(0).getUser().getUuid();
		emailSenderService.sendUserInformCompanyIsActive(company, usercompanys.get(0).getUser(), verifyEmailLink, TIMESHEET_SERVER_URL);
		
	}
	
	@Override
	public String setPlanStringBySuperAdmin(PermissionPlan permissionPlan) {
		String div = "<div class='col-sm-12'>";

		div = div + "<ol>";
		if(permissionPlan.isCommission()) {
			div = div + "<li><p><b>Commission</b></p></li>";
		}
		if(permissionPlan.isUserCanLogin()) {
			div = div + "<li><p><b>User Can Login</b></p></li>";
		}
		if(permissionPlan.isTemplateCanSet()) {
			div = div + "<li><p><b>Template Can Set</b></p></li>";
		}
		if(permissionPlan.isSchedularCanSet()) {
			div = div + "<li><p><b>Schedular Can Set</b></p></li>";
		}
		if(permissionPlan.isQbIntegration()) {
			div = div + "<li><p><b>Qb Integration</b></p></li>";
		}
		if(permissionPlan.getUserLimit() > 0) {
			div = div + "<li><p><b>Increase User Limit to <span>"+permissionPlan.getUserLimit()+"</span></b></p></li>";
		}
		div = div + "</ol></div>";
		return div;
	}

	@Override
	public void changePermissionPlan(PermissionPlan permissionPlan, Company company) {
		PermissionPlan permissionPlan1 = permissionPlanRepository.findByCompany(company);
		permissionPlan1.setCommission(permissionPlan.isCommission());
		permissionPlan1.setSchedularCanSet(permissionPlan.isSchedularCanSet());
		permissionPlan1.setQbIntegration(permissionPlan.isQbIntegration());
		permissionPlan1.setTemplateCanSet(permissionPlan.isTemplateCanSet());
		permissionPlan1.setUserCanLogin(permissionPlan.isUserCanLogin());
		permissionPlan1.setUserLimit(permissionPlan.getUserLimit());
		
		permissionPlanRepository.save(permissionPlan1);
			
		List<UserCompany> usercompanys = userCompanyRepository.findByCompanyIdAndRole(company.getId(), "ROLE_ADMIN");

		String planString = setPlanStringBySuperAdmin(permissionPlan1);
		String description = DefaultSuperAdminMailTemplet.ACTIVEPLAN_MAIL_TO_ADMIN.htmlData +company+ planString;
		
		String email = "";
		int i = 0;
		for (UserCompany u : usercompanys) {
			if(i == 0) {
				email = u.getUser().getEmail();
			} else {
				email = email +"," + u.getUser().getEmail();
			}
			i++;
		}

		userService.sendEmailToUser(description, email, DefaultSuperAdminMailTemplet.ACTIVEPLAN_MAIL_TO_ADMIN.subject, null, null);
	}

	@Override
	public List<UserCompany> getUserCompaniesByCompanyIdAndRole(Integer companyId,String role) {
		
		List<UserCompany> userCompany = userCompanyRepository.findByCompanyIdAndRole(companyId,role);
		
		return userCompany;
	}
}
