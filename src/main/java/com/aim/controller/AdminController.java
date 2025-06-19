package com.aim.controller;

import com.aim.entity.*;
import com.aim.enums.*;
import com.aim.model.GetCompanyByName;
import com.aim.model.GetSuperAdmin;
import com.aim.model.ResponseGenerator;
import com.aim.repository.*;
import com.aim.request.*;
import com.aim.response.*;
import com.aim.service.*;
import com.aim.service.email.EmailSenderService;
import com.aim.utils.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@RequestMapping("/admin/")
public class AdminController extends DataMenuController {
	
	@Value("${file.path}")
	private String FILE_PATH;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserDetailsRepository userDetailsRepository;
	
	@Autowired
	private UserFileRepository userFileRepository;
	
	@Autowired
	private HourLogFileRepository hourLogFileRepository;

	@Autowired
	private HourLogFileService hourLogFileService;
	
	@Autowired
	private InternalUserRepository internalUserRepository;
	
	@Autowired
	private ActivityRepository activityRepository;
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private PermissionService permissionService;
	
	@Autowired
	private EmailSenderService emailSenderService;
	
	@Autowired
	private SignatureRepository signatureRepository;
	
	@Value("${timesheet.server.url}")
	private String TIMESHEET_SERVER_URL;
	
	@Autowired
	private TemplateEngine templateEngine;
	
	@Autowired
	private CompanyRepository companyRepository;
	
	@Autowired
	private TemplateService templateService;
	
	@Autowired
	private HourLogFilePathRepository hourLogFilePathRepository;

	@Autowired
	private UserFileService userFileService;

	private final Boolean APPROVED = true;
	private final Boolean NOT_APPROVED = false;
	private final Boolean REJECTED= true;
	private final Boolean NOT_REJECTED= false;
	
	final static Logger logger = LoggerFactory.getLogger(AdminController.class);
	
	@RequestMapping(value="home", method = RequestMethod.GET)
	public ModelAndView home(@RequestParam(name="year",required=false) Integer year, 
			@RequestParam(name="user",required=false) Integer user,
			@RequestParam(name="userType",required=false, defaultValue="all") String userType,
			@RequestParam(name="month",required=false) Integer month) {
		
		if(year == null) {
			year = Calendar.getInstance().get(Calendar.YEAR);
		}
		
		UserDetailsType userDetailsType = UserDetailsType.getUserDetailsType(userType);
		
		List<HomePageUserResponse> userTotalHour = adminService.getUserTotalHour(year,user,month, userDetailsType);
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("userTotalHour", userTotalHour);
		float netMargin = 0.0f;
		float grossMargin = 0.0f;
		float revenue = 0.0f;
		float consultanse = 0.0f;
		float commission = 0.0f;
		
		AdminUserHoursChart adminUserHoursChart = new AdminUserHoursChart();
		UserTotalRevenueChart userTotalRevenueChart = new UserTotalRevenueChart();
		
		List<String> users = new ArrayList<String>();
		List<User> usersList = new ArrayList<User>();
		List<Float> netMargins = new ArrayList<Float>();
		List<Float> grossMargins = new ArrayList<Float>();
		List<Float> revenues = new ArrayList<Float>();
		List<Float> consultanses = new ArrayList<Float>();
		List<Float> commissions = new ArrayList<Float>();
		List<Float> commissionsBDM = new ArrayList<Float>();
		List<Float> commissionsACM = new ArrayList<Float>();
		List<Float> commissionsREC = new ArrayList<Float>();
		List<Float> expensesCon = new ArrayList<Float>();
		List<Float> expensesWs2P = new ArrayList<Float>();
		List<Float> expensesOther = new ArrayList<Float>();
		
		for(HomePageUserResponse homePageUserResponse: userTotalHour) {
			if(!usersList.contains(homePageUserResponse.getUserDetails().getUser()))
				usersList.add(homePageUserResponse.getUserDetails().getUser());
		}
		
		List<HomePageUserResponse> userResponses = new ArrayList<>();
		if(user != null) {
			
			userResponses = userTotalHour.stream().filter(f -> f.getUserDetails().getUser().getId() == user)
					.collect(Collectors.toList());
			
			modelAndView.addObject("selectedUserName", userService.findOne(user).getFirstName()+ " " + userService.findOne(user).getLastName());
		} else {
			
			userResponses = userTotalHour;
			modelAndView.addObject("selectedUserName","All User");
		}
		
		if(month == null) {
			
			if(!CollectionUtils.isEmpty(usersList)) {
				adminUserHoursChart = adminService.getUserMonthHour(year,user, null, userDetailsType);
			}
			
			if(!CollectionUtils.isEmpty(usersList)) {
				userTotalRevenueChart = adminService.getUserMonthRevenue(year,user, null, userDetailsType);
			}
			
			for(HomePageUserResponse h: userResponses) {
				
			    netMargin = netMargin + Float.valueOf(h.getTotal().get("N.Margin").toString());
			    grossMargin = grossMargin + Float.valueOf(h.getTotal().get("G.Margin").toString());
			    revenue = revenue + Float.valueOf(h.getTotal().get("revenue").toString());
			    
			    users.add(h.getUserDetails().getUser().getFirstName() + " ( " + h.getUserDetails().getClientName() +" )");
			    
			    netMargins.add(Float.valueOf(h.getTotal().get("N.Margin").toString()));
			    grossMargins.add(Float.valueOf(h.getTotal().get("G.Margin").toString()));
			    revenues.add(Float.valueOf(h.getTotal().get("revenue").toString()));
			    
			    consultanse = consultanse + Float.valueOf(h.getTotal().get("ConsultantRate").toString()) + 
			    		Float.valueOf(h.getTotal().get("W2Ptax").toString()) +
			    		Float.valueOf(h.getTotal().get("C2C").toString());
			    
			    commission = commission + Float.valueOf(h.getTotal().get("BDMComm").toString()) + 
			    		Float.valueOf(h.getTotal().get("ACMComm").toString()) +
			    		Float.valueOf(h.getTotal().get("RecComm").toString());
			    
			   float c = Float.valueOf(h.getTotal().get("ConsultantRate").toString()) + 
	    		Float.valueOf(h.getTotal().get("W2Ptax").toString()) +
	    		Float.valueOf(h.getTotal().get("C2C").toString());
			    
			   consultanses.add(c);
			    
			   float co = Float.valueOf(h.getTotal().get("BDMComm").toString()) + 
	    		Float.valueOf(h.getTotal().get("ACMComm").toString()) +
	    		Float.valueOf(h.getTotal().get("RecComm").toString());
			   
			   commissions.add(co);
			   float f1 = Float.valueOf(h.getTotal().get("BDMComm").toString());
			   float f2 = Float.valueOf(h.getTotal().get("ACMComm").toString());
			   float f3 = Float.valueOf(h.getTotal().get("RecComm").toString());
			   commissionsBDM.add(f1);
			   commissionsACM.add(f2);
			   commissionsREC.add(f3);
			   
			   float c1 = Float.valueOf(h.getTotal().get("ConsultantRate").toString());
			   float c2 = Float.valueOf(h.getTotal().get("W2Ptax").toString());
			   float c3 = Float.valueOf(h.getTotal().get("C2C").toString());
			   expensesCon.add(c1);
			   expensesWs2P.add(c2);
			   expensesOther.add(c3);
			}
			
		} else {
			
			if(!CollectionUtils.isEmpty(usersList)) {
				adminUserHoursChart = adminService.getUserMonthDayHour(year,user,month+1, userDetailsType);
			}
			
			if(!CollectionUtils.isEmpty(usersList)) {
				userTotalRevenueChart = adminService.getUserMonthDayRevenue(year,user, month+1, userDetailsType);
			}
			
			Month monthNum =  Month.getMonth(month);
			for(HomePageUserResponse h: userResponses) {
				
				users.add(h.getUserDetails().getUser().getFirstName() + " ( " + h.getUserDetails().getClientName() +" )");
				    
				if(!h.getMonthMap().get(monthNum.displayLabel).isEmpty()) {
					
				    netMargin = netMargin + Float.valueOf(h.getMonthMap().get(monthNum.displayLabel).get("N.Margin").toString());
				    grossMargin = grossMargin + Float.valueOf(h.getMonthMap().get(monthNum.displayLabel).get("G.Margin").toString());
				    revenue = revenue + Float.valueOf(h.getMonthMap().get(monthNum.displayLabel).get("revenue").toString());
				   
				    netMargins.add(Float.valueOf(h.getMonthMap().get(monthNum.displayLabel).get("N.Margin").toString()));
				    grossMargins.add(Float.valueOf(h.getMonthMap().get(monthNum.displayLabel).get("G.Margin").toString()));
				    revenues.add(Float.valueOf(h.getMonthMap().get(monthNum.displayLabel).get("revenue").toString()));
				
				    consultanse = consultanse + Float.valueOf(h.getMonthMap().get(monthNum.displayLabel).get("ConsultantRate").toString()) + 
				    		Float.valueOf(h.getMonthMap().get(monthNum.displayLabel).get("W2Ptax").toString()) +
				    		Float.valueOf(h.getMonthMap().get(monthNum.displayLabel).get("C2C").toString());
				    
				    commission = commission + Float.valueOf(h.getMonthMap().get(monthNum.displayLabel).get("BDMComm").toString()) + 
				    		Float.valueOf(h.getMonthMap().get(monthNum.displayLabel).get("ACMComm").toString()) +
				    		Float.valueOf(h.getMonthMap().get(monthNum.displayLabel).get("RecComm").toString());
				    
				    float c = Float.valueOf(h.getTotal().get("ConsultantRate").toString()) + 
				    		Float.valueOf(h.getTotal().get("W2Ptax").toString()) +
				    		Float.valueOf(h.getTotal().get("C2C").toString());
						    
				    consultanses.add(c);
						    
				    float co = Float.valueOf(h.getTotal().get("BDMComm").toString()) + 
				    		Float.valueOf(h.getTotal().get("ACMComm").toString()) +
				    		Float.valueOf(h.getTotal().get("RecComm").toString());
						   
				    commissions.add(co);
				    
				    float f1 = Float.valueOf(h.getTotal().get("BDMComm").toString());
				    float f2 = Float.valueOf(h.getTotal().get("ACMComm").toString());
				    float f3 = Float.valueOf(h.getTotal().get("RecComm").toString());
				    commissionsBDM.add(f1);
					commissionsACM.add(f2);
					commissionsREC.add(f3);
					
					float c1 = Float.valueOf(h.getTotal().get("ConsultantRate").toString());
				    float c2 = Float.valueOf(h.getTotal().get("W2Ptax").toString());
				    float c3 = Float.valueOf(h.getTotal().get("C2C").toString());
				    expensesCon.add(c1);
				    expensesWs2P.add(c2);
				    expensesOther.add(c3);
						   
				} else {
					
					netMargin = netMargin + 0.0f;
				    grossMargin = grossMargin + 0.0f;
				    revenue = revenue + 0.0f;
				   
				    netMargins.add(0.0f);
				    grossMargins.add(0.0f);
				    revenues.add(0.0f);
				    consultanses.add(0.0f);
				    commissions.add(0.0f);
				    expensesWs2P.add(0.0f);
				    expensesCon.add(0.0f);
				    expensesOther.add(0.0f);
				    commissionsBDM.add(0.0f);
					commissionsACM.add(0.0f);
					commissionsREC.add(0.0f);
							
				    consultanse = consultanse + 0.0f;
				    commission = commission + 0.0f;
				    
				}
			}
		}
		
		// get x value for chart x
		if(month != null) {
			Calendar calendar = new GregorianCalendar(year, month, 1);
	        int numberOfDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	        List<String> xChart = new ArrayList<>();
	        
	        for(Integer i = 1;i<=numberOfDays;i++) {
	        	xChart.add(i.toString());
			}
	        
	        modelAndView.addObject("xChart", xChart);
	        Month monthNum =  Month.getMonth(month);
	        modelAndView.addObject("selectedMonthString", monthNum.displayLabel);
		} else {
			modelAndView.addObject("selectedMonthString", "All Month");
			String xChart[] = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
			modelAndView.addObject("xChart", xChart);
		}
		
		modelAndView.addObject("netMargin", Math.round((double)netMargin * 100.0) / 100.0);
		modelAndView.addObject("grossMargin", Math.round((double)grossMargin * 100.0) / 100.0);
		modelAndView.addObject("revenue", Math.round((double)revenue * 100.0) / 100.0);
		if(month != null)
			modelAndView.addObject("selectMonth", month);
		
		modelAndView.addObject("users", users);
		modelAndView.addObject("usersList", usersList);
		modelAndView.addObject("netMargins", netMargins);
		modelAndView.addObject("grossMargins", grossMargins);
		modelAndView.addObject("adminUserHoursChart", adminUserHoursChart);
		modelAndView.addObject("userTotalRevenueChart", userTotalRevenueChart);
		modelAndView.addObject("revenues", revenues);
		modelAndView.addObject("commissions", commissions);
		modelAndView.addObject("expenses", consultanses);

		modelAndView.addObject("commissionsBDM", commissionsBDM);
		modelAndView.addObject("commissionsACM", commissionsACM);
		modelAndView.addObject("commissionsREC", commissionsREC);
		
		modelAndView.addObject("expensesWs2P", expensesWs2P);
		modelAndView.addObject("expensesCon", expensesCon);
		modelAndView.addObject("expensesOther", expensesOther);
		
		modelAndView.addObject("consultanse",Math.round((double)consultanse * 100.0) / 100.0);
		modelAndView.addObject("commission",Math.round((double) commission * 100.0) / 100.0);
		
		modelAndView.addObject("selectType", userType);
		modelAndView.addObject("selectYear", year);
		modelAndView.addObject("selectUser", user);
		modelAndView.addObject("totlaUsers", userRepository.countByRole("ROLE_USER"));
		modelAndView.addObject("approvedFile", hourLogFileRepository.countByApproveAndReject(APPROVED, NOT_REJECTED));
		modelAndView.addObject("newFile", hourLogFileRepository.countByApproveAndReject(NOT_APPROVED, NOT_REJECTED ));
		
		modelAndView.setViewName("new/admin/home");
		return modelAndView;
	}
	
	@RequestMapping(value="add-user", method = RequestMethod.GET)
	public String registration(HttpServletRequest request, ModelMap modelMap) {
		User loginUser = (User) request.getSession().getAttribute("user");
		if(!loginUser.getRole().equals("ROLE_ADMIN")) {
			boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.USER,Permission.CREATE, false);
			if(!grant){
				return "redirect:/supervisor/unauthorized";
			}
		}
		
		User user = new User();
		modelMap.addAttribute("user", user);
		modelMap.addAttribute("internalUser", new InternalUser());
		modelMap.addAttribute("users", userRepository.findAll());
		return "new/admin/add-user";
	}
	
	/**
	 * get Internal user list
	 * @param id
	 * @return
	 */
	@RequestMapping(value="internal-user", method = RequestMethod.GET)
	public String internalUser(@RequestParam(name ="id", required = false) Integer id,
			ModelMap modelMap, HttpServletRequest request) {
		
		if(permissionService.getPermissionPlan().isCommission() == false) {return "redirect:/supervisor/unauthorized";}
		
		User loginUser = (User) request.getSession().getAttribute("user");
		if(!loginUser.getRole().equals("ROLE_ADMIN")) {
			boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.INTERNAL_USER,Permission.CREATE, false);
			boolean grant1 = permissionService.grantPermission(loginUser.getRole(), Functionality.INTERNAL_USER,Permission.READ, false);
			if(!grant && !grant1){
				return "redirect:/supervisor/unauthorized";
			}
		}
		
		modelMap.addAttribute("internalUsers",internalUserRepository.findAllByDefaultUser(false));
		InternalUser user =  new InternalUser();
		if(id != null) {
			user = internalUserRepository.findById(id).orElse(null);
		}
		if(user != null && user.isDefaultUser()) {
			return "redirect:/admin/internal-user";
		}
		modelMap.addAttribute("internalUser", user);
		return "new/admin/internal-user";
	}
	
	@RequestMapping(value="internal-user/list", method = RequestMethod.GET)
	public String internalUser(ModelMap modelMap, HttpServletRequest request) {
		if(permissionService.getPermissionPlan().isCommission() == false) {return "redirect:/supervisor/unauthorized";}
		
		User loginUser = (User) request.getSession().getAttribute("user");
		if(!loginUser.getRole().equals("ROLE_ADMIN")) {
			boolean grant1 = permissionService.grantPermission(loginUser.getRole(), Functionality.INTERNAL_USER,Permission.READ, false);
			if(!grant1){
				return "redirect:/supervisor/unauthorized";
			}
		}
		
		modelMap.addAttribute("internalUsers",internalUserRepository.findAllByDefaultUser(false));
		
		return "new/admin/internal-user-list";
	}
	
	@RequestMapping(value="edit/{id}", method = RequestMethod.GET)
	public String edit(@PathVariable("id") int id, ModelMap modelMap, HttpServletRequest request) {
		
		User loginUser = (User) request.getSession().getAttribute("user");
		if(!loginUser.getRole().equals("ROLE_ADMIN")) {
			boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.USER,Permission.UPDATE, false);
			if(!grant){
				return "redirect:/supervisor/unauthorized";
			}
		}
		
		User user = userService.findOne(id);
		modelMap.addAttribute("user", user);
		modelMap.addAttribute("internalUser", new InternalUser());
		
		modelMap.addAttribute("users", userRepository.findAll());
		return "new/admin/add-user";
	}
	
	@RequestMapping(value="user-list", method = RequestMethod.GET)
	public String userList(ModelMap modelMap, HttpServletRequest request) {
		
		User loginUser = (User) request.getSession().getAttribute("user");
		if(!loginUser.getRole().equals("ROLE_ADMIN")) {
			boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.USER,Permission.READ, false);
			if(!grant){
				return "redirect:/supervisor/unauthorized";
			}
		}
		
		modelMap.addAttribute("users", userRepository.findAll());
		return "new/admin/user-list";
	}
	
	@RequestMapping(value="user-grid", method = RequestMethod.GET)
	public String userGrid(ModelMap modelMap,
			@RequestParam(name = "page", required = false, defaultValue="1") Integer  page,
			@RequestParam(name = "startWith", required = false, defaultValue="a") String startWith,
			HttpServletRequest request) {
				
		User loginUser = (User) request.getSession().getAttribute("user");
		if(!loginUser.getRole().equals("ROLE_ADMIN")) {
			boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.USER,Permission.READ, false);
			if(!grant){
				return "redirect:/supervisor/unauthorized";
			}
		}
		
		Page<User> pagec = userService.findByFirstNameStartsWith(startWith, page);
		modelMap.addAttribute("users",pagec.getContent());
		modelMap.addAttribute("currentPage",page);
		modelMap.addAttribute("startWith",startWith);
		modelMap.addAttribute("totalPage",pagec.getTotalPages());
		modelMap.addAttribute("totalUser",pagec.getTotalElements());
		return "new/admin/user-grid";
	}
	
	@RequestMapping(value="user-list/forgot-password/{id}", method = RequestMethod.GET)
	public String forgotPasswordEmail(@PathVariable("id") int id,
			HttpServletRequest request, RedirectAttributes redirectAttributes) {
		
		User loginUser = (User) request.getSession().getAttribute("user");
		if(!loginUser.getRole().equals("ROLE_ADMIN")) {
			boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.USER,Permission.READ, false);
			if(!grant){
				return "redirect:/supervisor/unauthorized";
			}
		}
		
		User userExists = userService.findOne(id);
		
		if(userExists == null) {
			redirectAttributes.addFlashAttribute("error", "User not found");
			return "redirect:/admin/user-list";
		}
		
		String verifyEmailLink = TIMESHEET_SERVER_URL + "/change-pass?uId=" + userExists.getUuid();// + "&d=" + EncryptDecryptUtils.encrypt(Utils.currentStringDate());
		
		emailSenderService.sendForgotPasswordEmail(userExists, verifyEmailLink, TIMESHEET_SERVER_URL);
		
		redirectAttributes.addFlashAttribute("success", "Mail send successfully");
		return "redirect:/admin/user-list";
	}
	
	@RequestMapping(value = "add-user", method = RequestMethod.POST)
	public String createNewUser(HttpServletRequest request, 
			@Valid User user, BindingResult bindingResult, RedirectAttributes redirectAttributes,
			ModelMap model) {
		
		User loginUser = (User) request.getSession().getAttribute("user");
		if(!loginUser.getRole().equals("ROLE_ADMIN")) {
			boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.USER,Permission.CREATE, false);
			if(!grant){
				return "redirect:/supervisor/unauthorized";
			}
		}
		
		User userExists = userService.findUserByEmail(user.getEmail());
		
		Manager client = adminService.getManagerByEmail(user.getEmail());
		
		if(client != null) {
			bindingResult.rejectValue("email", "error.user","Using this email id already a manager existed.");
			model.addAttribute("user", user);
			model.addAttribute("internalUser", new InternalUser());
			model.addAttribute("users", userRepository.findAll());
			return "new/admin/add-user";
		}
		
		InternalUser internalUser = internalUserRepository.findByEmail(user.getEmail());
		if(internalUser != null) {
			bindingResult.rejectValue("email", "error.user","Using this email id already internal user existed.");
			model.addAttribute("user", user);
			model.addAttribute("internalUser", new InternalUser());
			model.addAttribute("users", userRepository.findAll());
			return "new/admin/add-user";
		}
		
		if(user.getId() != 0) {
			
			if (userExists != null && userExists.getId() != user.getId()) {
				bindingResult.rejectValue("email", "error.user", "There is already a other user registered with the email provided");
			}
			
			if (bindingResult.hasErrors()) {
				model.addAttribute("user", user);
				model.addAttribute("internalUser", new InternalUser());
				model.addAttribute("users", userRepository.findAll());
				return "new/admin/add-user";
			}
			
			userService.saveUser(user);
			userService.addActivity("Edit user", ActivityType.EDIT_USER.toString() , null);
			redirectAttributes.addFlashAttribute("success", "User has been update successfully");
			return "redirect:/admin/user-list";
		} else {
			
			if (userExists != null) {
				bindingResult.rejectValue("email", "error.user", "There is already a user registered with the email provided");
			}
			if (bindingResult.hasErrors()) {
				model.addAttribute("user", user);
				model.addAttribute("internalUser", new InternalUser());
				model.addAttribute("users", userRepository.findAll());
				return "new/admin/add-user";
			} else {
				userService.saveUser(user);
				userService.addActivity("Add user", ActivityType.ADD_USER.toString() , null);
				redirectAttributes.addFlashAttribute("success", "User has been registered successfully");
				redirectAttributes.addFlashAttribute("newUserAdd",user);
				
			}
			return "redirect:/admin/add-user";
		}
		
	}
	
	@RequestMapping(value="deactive/{id}", method = RequestMethod.GET)
	public String deactive(@PathVariable("id")Integer id, HttpServletRequest request) {
		
		User loginUser = (User) request.getSession().getAttribute("user");
		if(!loginUser.getRole().equals("ROLE_ADMIN")) {
			boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.USER,Permission.UPDATE, false);
			if(!grant){
				return "redirect:/supervisor/unauthorized";
			}
		}
		
		User user = userService.findOne(id);
		if(user != null) {
			user.setActive(0);
			userRepository.save(user);
			userService.addActivity("Deactive user", ActivityType.DEACTIVE_USER.toString() , null);
		}
		return "redirect:/admin/user-list";
	}
	
	@RequestMapping(value="active/{id}", method = RequestMethod.GET)
	public String active(@PathVariable("id")Integer id, HttpServletRequest request) {
		
		User loginUser = (User) request.getSession().getAttribute("user");
		if(!loginUser.getRole().equals("ROLE_ADMIN")) {
			boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.USER,Permission.UPDATE, false);
			if(!grant){
				return "redirect:/supervisor/unauthorized";
			}
		}
		
		User user = userService.findOne(id);
		if(user != null) {
			user.setActive(1);
			userRepository.save(user);
			userService.addActivity("Active user", ActivityType.ACTIVE_USER.toString() , null);
		}
		return "redirect:/admin/user-list";
	}
	
	@RequestMapping(value="user-detail/{id}", method = RequestMethod.GET)
	public String details(@PathVariable("id") Integer id, HttpServletRequest request,
			Model model,@RequestParam(name="show",required=false) boolean show,
			@RequestParam(name="userDetailId",required=false) Integer userDetailId) {
		
		User loginUser = (User) request.getSession().getAttribute("user");
		if(!loginUser.getRole().equals("ROLE_ADMIN")) {
			boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.CLIENT_ASSIGN_USER,Permission.READ, false);
			if(!grant){
				return "redirect:/supervisor/unauthorized";
			}
		}
		
		User user = userService.findOne(id);
		List<UserDetail> userDetails = userDetailsRepository.findByUser(user);
		
		UserDetail userDetail = new UserDetail();
		UserDetailRequest userDetailRequest = new UserDetailRequest();
		
		if(userDetailId == null) {
			userDetailRequest.setUser(user);
			userDetailRequest.setW2OrC2cType(W2OrC2cType.W2);
			model.addAttribute("hoursLogFileList",false );
		} else {
			userDetail = userDetailsRepository.findByUserDetailId(userDetailId);
			userDetailRequest = userService.setUserDetailRequest(userDetail);
			List<HourLogFile> hourLogFile = hourLogFileRepository.findByUserDetail(userDetail);
			if(!CollectionUtils.isEmpty(hourLogFile))
				model.addAttribute("hoursLogFileList",true );
			else 
				model.addAttribute("hoursLogFileList",false );
		}
		
		List<UserFile> userFile = userFileRepository.findByUser(user);
		model.addAttribute("userFiles",userFile);
		model.addAttribute("show",show);
		
		model.addAttribute("BDMList",internalUserRepository.findAllByInternalUserTypeOrderByDefaultUserDesc(InternalUserType.BDM.urlParam));
		model.addAttribute("accountManagers",internalUserRepository.findAllByInternalUserTypeOrderByDefaultUserDesc(InternalUserType.ACCOUNT_MANAGER.urlParam));
		model.addAttribute("recruiters",internalUserRepository.findAllByInternalUserTypeOrderByDefaultUserDesc(InternalUserType.RECRUITER.urlParam));
		model.addAttribute("userDetail",userDetailRequest);
		model.addAttribute("userDetails",userDetails);
		
		return "new/admin/add-user-detail";
	}
	
	@RequestMapping(value="user-detail/client/{userDetailId}", method = RequestMethod.GET)
	public ModelAndView details(@PathVariable("userDetailId") Integer userDetailId) {
		
		ModelAndView modelAndView = new ModelAndView();
		UserDetail userDetail = userDetailsRepository.findByUserDetailId(userDetailId);
		modelAndView.addObject("userDetail",userDetail);
		ClientAddDetail clientAddDetail = adminService.getClient(userDetail.getClient());
		ClientAddDetail vendorAddDetails = adminService.getClient(userDetail.getVendor());
		ClientAddDetail employerAddDetails = adminService.getClient(userDetail.getEmployer());
		
		modelAndView.addObject("clientAddDetail",clientAddDetail);
		
		if(userDetail.getVendor() != null)
			modelAndView.addObject("vendorAddDetails",vendorAddDetails);
		
		if(userDetail.getEmployer() != null)
			modelAndView.addObject("employerAddDetails",employerAddDetails);
		
		modelAndView.setViewName("new/admin/clientDetails");
		return modelAndView;
	}
	
	/**
	 * save user details 
	 * @param userDetailRequest
	 * @return
	 */
	@RequestMapping(value="add-user-detail", method = RequestMethod.POST)
	public String addDetails(@Valid UserDetailRequest userDetailRequest, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, Model model, HttpServletRequest request) { 
		
		User loginUser = (User) request.getSession().getAttribute("user");
		if(!loginUser.getRole().equals("ROLE_ADMIN")) {
			boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.USER,Permission.UPDATE, false);
			if(!grant){
				return "redirect:/supervisor/unauthorized";
			}
		}
		
		if (bindingResult.hasErrors()) {
			
			model.addAttribute("error", bindingResult.getFieldErrors().get(0).getDefaultMessage());
			model.addAttribute("userDetail",userDetailRequest);
			return "new/admin/add-user-detail";
		} else {
			//check validation
			InternalUser acmUser = internalUserRepository.findById(userDetailRequest.getAccountManagerId()).orElse(null);
			if(acmUser == null) {
				model.addAttribute("error", "Selected account manager not available");
				model.addAttribute("userDetail",userDetailRequest);
				return "new/admin/add-user-detail";
			}
			if(userDetailRequest.getInvoiceTo().equals(InvoiceTo.CLIENT.urlParam)){
				if(userDetailRequest.getClientId() == null) {
					model.addAttribute("error", "Please selected client");
					model.addAttribute("userDetail",userDetailRequest);
					return "new/admin/add-user-detail";
				}
			} else {
				if(userDetailRequest.getVendorId() == null) {
					model.addAttribute("error", "Please selected vendor");
					model.addAttribute("userDetail",userDetailRequest);
					return "new/admin/add-user-detail";
				}
			}
			InternalUser bdmUser = internalUserRepository.findById(userDetailRequest.getBusinessDevelopmentManagerId()).orElse(null);
			if(bdmUser == null) {
				model.addAttribute("error", "Selected business development manager not available");
				model.addAttribute("userDetail",userDetailRequest);
				return "new/admin/add-user-detail";
			}
			InternalUser recUser = internalUserRepository.findById(userDetailRequest.getRecruiterId()).orElse(null);
			if(recUser == null) {
				model.addAttribute("error", "Selected recruiter not available");
				model.addAttribute("userDetail",userDetailRequest);
				return "new/admin/add-user-detail";
			}
			
			if(userDetailRequest.getW2OrC2cType() == W2OrC2cType.W2) {
				
				if(userDetailRequest.getW2() == null) {
					model.addAttribute("error", "Please add W2 rate");
					model.addAttribute("userDetail",userDetailRequest);
					return "new/admin/add-user-detail";
				}
				if(userDetailRequest.getPtax() == null) {
					model.addAttribute("error", "Please add Ptax rate");
					model.addAttribute("userDetail",userDetailRequest);
					return "new/admin/add-user-detail";
				}
				if(userDetailRequest.getC2cOrotherRateType() == null) {
					model.addAttribute("error", "Other rate type");
					model.addAttribute("userDetail",userDetailRequest);
					return "new/admin/add-user-detail";
				}
				
			} else if(userDetailRequest.getW2OrC2cType() == W2OrC2cType.C2C) {
				if(userDetailRequest.getEmployerId() == null) {
					model.addAttribute("error", "Please select Employer");
					model.addAttribute("userDetail",userDetailRequest);
					return "new/admin/add-user-detail";
				}
				if(userDetailRequest.getConsultantRate() == null) {
					model.addAttribute("error", "Please add consultant rate");
					model.addAttribute("userDetail",userDetailRequest);
					return "new/admin/add-user-detail";
				}
			} else {
				model.addAttribute("error", "Select W2 Or C2C");
				model.addAttribute("userDetail",userDetailRequest);
				return "new/admin/add-user-detail";
			}
			 
			
			UserDetail userdetails = userService.getUserDetail(userDetailRequest);
			userService.saveUserDetails(userdetails);
			
			redirectAttributes.addFlashAttribute("success", "User details has been save successfully");
			return "redirect:/admin/user-detail/"+userDetailRequest.getUser().getId();
		}
	}
	
	/**
	 * get hour log of user
	 * @param userDetailId
	 * @return
	 */
	@RequestMapping(value="user-hour-log/{userDetailId}", method = RequestMethod.GET)
	public ModelAndView getHourLog(@PathVariable("userDetailId") Integer userDetailId,
			@RequestParam(name="year",required=false) Integer year,
			@RequestParam(name="yearFile", required=false) Integer yearFile) { 
		
		if(year == null) {
			
			year = Calendar.getInstance().get(Calendar.YEAR);
		}
		
		LinkedHashMap<String, Map<String, Double>> map = userService.getHourLog(userDetailId, year,null);
		UserDetail userDetail = userDetailsRepository.findByUserDetailId(userDetailId);
		
		List<Float> netMargins = new ArrayList<Float>();
		
		for (Map.Entry<String, Map<String, Double>> entry : map.entrySet())  {
			if(!entry.getKey().equals("Total")) {
				if(entry.getValue() != null && entry.getValue().get("N.Margin") != null) {
					netMargins.add(Float.valueOf(entry.getValue().get("N.Margin").toString()));
				} else {
					netMargins.add(0f);
				}
			}   
		}
		
		if(yearFile == null)
			yearFile = Calendar.getInstance().get(Calendar.YEAR);//get current year
		
		List<HourLogFile> hourLogFiles = userService.getHourLogFile(userDetailId, yearFile);
		
		List<HourLogFile> notApprovedFile = userService.getHourLogNotApprovedFile(userDetailId, year);
		
		Month monthNum =  Month.getMonth(0);
		Map<String, Double> mHours = map.get(monthNum.displayLabel);
		List<DefaultCalendarResponse> defaultCalendarResponses = userService.getDefaultCalendar(userDetailId, 0, year) ;
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("monthNum",monthNum);
		modelAndView.addObject("mHours",mHours);
		modelAndView.addObject("defaultCalendarResponses",defaultCalendarResponses);
		
		modelAndView.addObject("chartData",netMargins);
		modelAndView.addObject("selectYear",year);
		modelAndView.addObject("hourLogFiles",hourLogFiles);
		modelAndView.addObject("notApprovedFile",notApprovedFile);
		modelAndView.addObject("hourLogs",map);
		modelAndView.addObject("userDetail",userDetail);
		modelAndView.addObject("selectYearFile", yearFile);
		modelAndView.setViewName("new/admin/hour-log");
		
		return modelAndView;
	}
	@RequestMapping(value="user-hours-detail/{userDetailId}", method = RequestMethod.GET)
	public String getHourLogs(@PathVariable("userDetailId") Integer userDetailId,
			@RequestParam(name="year",required=false) Integer year,Model modelAndView,
			@RequestParam(name="yearFile", required=false) Integer yearFile) { 
		
		if(year == null) {
			
			year = Calendar.getInstance().get(Calendar.YEAR);
		}
		
		LinkedHashMap<String, Map<String, Double>> map = userService.getHourLog(userDetailId, year,null);
		UserDetail userDetail = userDetailsRepository.findByUserDetailId(userDetailId);
		
		List<Float> netMargins = new ArrayList<Float>();
		
		for (Map.Entry<String, Map<String, Double>> entry : map.entrySet())  {
		    if(!entry.getKey().equals("Total")) {
		        if(entry.getValue() != null && entry.getValue().get("N.Margin") != null) {
                    netMargins.add(Float.valueOf(entry.getValue().get("N.Margin").toString()));
                } else {
                    netMargins.add(0f);
                }
		    }   
		}
		
		if(yearFile == null)
			yearFile = Calendar.getInstance().get(Calendar.YEAR);//get current year
		
		List<HourLogFile> hourLogFiles = userService.getHourLogFile(userDetailId, yearFile);
		
		List<HourLogFile> notApprovedFile = userService.getHourLogNotApprovedFile(userDetailId, year);
		
		Month monthNum =  Month.getMonth(0);
		Map<String, Double> mHours = map.get(monthNum.displayLabel);
		List<DefaultCalendarResponse> defaultCalendarResponses = userService.getDefaultCalendar(userDetailId, 0, year) ;
		modelAndView.addAttribute("monthNum",monthNum);
		modelAndView.addAttribute("mHours",mHours);
		modelAndView.addAttribute("defaultCalendarResponses",defaultCalendarResponses);
		
		modelAndView.addAttribute("chartData",netMargins);
		modelAndView.addAttribute("selectYear",year);
		modelAndView.addAttribute("hourLogFiles",hourLogFiles);
		modelAndView.addAttribute("notApprovedFile",notApprovedFile);
		modelAndView.addAttribute("hourLogs",map);
		modelAndView.addAttribute("userDetail",userDetail);
		modelAndView.addAttribute("selectYearFile", yearFile);
		
		return "new/admin/hour-log::user-hours-detail";
	}
	
	/**
	 * Add Files
	 * @return
	 * @throws IOException 
	 * @throws IllegalStateException 
	 */
	@RequestMapping(value="user-file", method = RequestMethod.POST)
	public String getUserFile(@RequestParam(name = "file") MultipartFile multipartFile,
			@RequestParam(name = "id") Integer id, @RequestParam(name="expDate") Date expDate,
			@RequestParam(name="type") String type, @RequestParam(name="remark") String remark,
			RedirectAttributes redirectAttributes, HttpServletRequest request) throws IllegalStateException, IOException { 
		
		Company company = (Company) request.getSession().getAttribute("company");
		User user = userService.findOne(id);
		String filePath =null;
		String fileName =null;

		if(multipartFile != null) {
			
//			if(multipartFile.getSize() > profileMaxImageSize)
//				return ResponseGenerator.generateResponse(Utils.ERROR, "file.size.max");
	
			if (!new File(FILE_PATH + company.getFileFolder()).exists())
				new File(FILE_PATH + company.getFileFolder()).mkdir();
			
			if (!new File(FILE_PATH + company.getFileFolder() + "/User/").exists())
				new File(FILE_PATH + company.getFileFolder() + "/User/").mkdir();
			
			if (!new File(FILE_PATH + company.getFileFolder() + "/User/"+ user.getFileFolder()+"/").exists())
				new File(FILE_PATH + company.getFileFolder() + "/User/"+ user.getFileFolder()+"/").mkdir();
			
			if (!new File(FILE_PATH + company.getFileFolder() + "/User/"+ user.getFileFolder()+"/OtherFiles/").exists())
				new File(FILE_PATH + company.getFileFolder() + "/User/"+ user.getFileFolder()+"/OtherFiles/").mkdir();
			
			if(multipartFile.getOriginalFilename().length() > 0) {
				
				filePath ="/User/"+ user.getFileFolder()+"/OtherFiles/" + new Date().getTime() + "UserFile" + id + multipartFile.getOriginalFilename().
							substring(multipartFile.getOriginalFilename().indexOf("."));
				multipartFile.transferTo(new File(FILE_PATH + company.getFileFolder() + filePath));
				
				fileName = multipartFile.getOriginalFilename();
			}
		}
		userService.saveUserFile(fileName, filePath, id, expDate, type, remark);
		
		redirectAttributes.addFlashAttribute("success", "file has been save successfully");
		return "redirect:/admin/user-detail/"+id;
	}
	
	/**
	 * delete user file
	 * @param id
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value="user-file/delete", method = RequestMethod.GET)
	public String deleteDetails(@RequestParam("id")Integer id, RedirectAttributes redirectAttributes, 
			HttpServletRequest request) { 
		
		Company company = (Company) request.getSession().getAttribute("company");
		
		UserFile userFile = userFileService.findById(id);
		Integer userId = userFile.getUser().getId();
		
		File file = new File(FILE_PATH + company.getFileFolder() + userFile.getFilePath());
		file.delete();
		userFileRepository.delete(userFile);
		
		redirectAttributes.addFlashAttribute("success", "File has been deleted successfully");
		return "redirect:/admin/user-detail/"+userId;
	}
	
	/**
	 * get basic details of user
	 * @param id
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="basic-detail/{id}", method = RequestMethod.GET)
	public String UserDetail(@PathVariable("id")Integer id, ModelMap modelMap) { 
		UserDetail userDetail = userDetailsRepository.findByUserDetailId(id);
		modelMap.addAttribute("userDetail", userDetail);
		return "new/admin/hour-log :: user-detail";
	}
	
	/**
	 * download file
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/user-file/download/{id}", method=RequestMethod.GET,produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	public FileSystemResource downloadFile(@PathVariable(value="id") Integer id, HttpServletRequest request) {
		
		Company company = (Company) request.getSession().getAttribute("company");
		UserFile userFile = userFileService.findById(id);
		return new FileSystemResource(new File(FILE_PATH + company.getFileFolder() + userFile.getFilePath()));
	}
	
	/**
	 * user Hour log download file
	 * @param id
	 * @return
	 */
	@RequestMapping(value="user-hour-log-file/download/{id}", method=RequestMethod.GET,produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	public void downloadUserHourFile(@PathVariable(value="id") Integer id, HttpServletResponse response, 
			HttpServletRequest request) {
		
		Company company = (Company) request.getSession().getAttribute("company");
		
		HourLogFile hourLogFile = hourLogFileService.findById(id);
		userService.addActivity("Time sheet file download", ActivityType.DOWNLOAD_FILE.toString() , hourLogFile.getUserDetail());
		
		response.setContentType(MediaType.ALL_VALUE);
		
		List<HourLogFilePath> hourLogFilePaths = hourLogFilePathRepository.findByHourLogFile(hourLogFile);
		
		if(!CollectionUtils.isEmpty(hourLogFilePaths)) {
			if (hourLogFilePaths.size() == 1) {
				String filePath = hourLogFilePaths.get(0).getFilePath();
				File f = new File(FILE_PATH + company.getFileFolder() + filePath);
		
				try {
		
			        // construct the complete absolute path of the file
			        FileInputStream inputStream = new FileInputStream(FILE_PATH + company.getFileFolder() + filePath);
			         
			        Path source = Paths.get(FILE_PATH + company.getFileFolder() + filePath);
			        String mimeType = Files.probeContentType(source);
			        // get MIME type of the file
			        if (mimeType == null) {
			            // set to binary type if MIME mapping not found
			            mimeType = "application/octet-stream";
			        }
			        // set content attributes for the response
			        response.setContentType(mimeType);
			        response.setContentLength((int) f.length());
			 
			        // get output stream of the response
			        OutputStream outStream = response.getOutputStream();
			 
			        byte[] buffer = new byte[100000];
			        int bytesRead = -1;
			 
			        // write bytes read from the input stream into the output stream
			        while ((bytesRead = inputStream.read(buffer)) != -1) {
			            outStream.write(buffer, 0, bytesRead);
			        }
			 
			        inputStream.close();
			        outStream.close();
			 
				} catch(Exception e) {
					e.printStackTrace();
				}
				
			} else {
				
				response.setContentType("application/octet-stream");
		        response.setHeader("Content-Disposition", "attachment;filename="+ hourLogFile.getStartDate().toString() +"_To_" +hourLogFile.getEndDate()+".zip");
		        response.setStatus(HttpServletResponse.SC_OK);
		        
		        try {
		        	
		        	OutputStream outStream = response.getOutputStream();
		        	
		        	ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(outStream));
		        	
		            for (HourLogFilePath file : hourLogFilePaths) {
		            	// construct the complete absolute path of the file
		            	File f = new File(FILE_PATH + company.getFileFolder() + file.getFilePath());
		            	zos.putNextEntry(new ZipEntry(file.getFileOriginalName()));
		            	
				        FileInputStream inputStream = new FileInputStream(FILE_PATH + company.getFileFolder() + file.getFilePath());
				        
				        Path source = Paths.get(FILE_PATH + company.getFileFolder() + file.getFilePath());
				        String mimeType = Files.probeContentType(source);
				        // get MIME type of the file
				        if (mimeType == null) {
				            // set to binary type if MIME mapping not found
				            mimeType = "application/octet-stream";
				        }
				        // set content attributes for the response
				        response.setContentType(mimeType);
				        response.setContentLength((int) f.length());
				 
				        // get output stream of the response
				        BufferedInputStream fif = new BufferedInputStream(inputStream);
				        
				        byte[] buffer = new byte[100000];
				        int bytesRead = -1;
				 
				        // write bytes read from the input stream into the output stream
				        while ((bytesRead = fif.read(buffer)) != -1) {
				        	zos.write(buffer, 0, bytesRead);
				        }
				 
				        fif.close();
				        inputStream.close();
				        zos.closeEntry();
		                
		            }
		            zos.close();
		            
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
			}
		}
		
	}
	
	/**
	 * get activity list
	 */
	@RequestMapping(value="activity", method = RequestMethod.GET) 
	public String setInernalUser(@RequestParam (name="type",required = false) String type,
			@RequestParam(name="role", required = false) String role,
			@RequestParam(name="id", required = false) Integer id,
			HttpServletRequest request,
			ModelMap modelMap){
		
		User loginUser = (User) request.getSession().getAttribute("user");
		if(!loginUser.getRole().equals("ROLE_ADMIN") && role.equals("ROLE_SUPERVISOR")) {
			boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.SUPERVISOR_ACTIVITY,Permission.READ, false);
			if(!grant){
				return "redirect:/supervisor/unauthorized";
			}
		}
		if(!loginUser.getRole().equals("ROLE_ADMIN") && role.equals("ROLE_USER")) {
			boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.USER_ACTIVITY,Permission.READ, false);
			if(!grant){
				return "redirect:/supervisor/unauthorized";
			}
		}
		if(!loginUser.getRole().equals("ROLE_ADMIN") && role.equals("ROLE_ADMIN")) {
			return "redirect:/supervisor/unauthorized";
		}
		if(role == null)
			role = "ROLE_USER";
		
		List<Activity> activities = adminService.getActivity(type, role, id);
		
		List<User> users = userService.findByRole(role);
//		activities.stream().filter(list -> !users.contains(list.getActivityByUser()))
//				.allMatch(list -> users.add(list.getActivityByUser()));
		
		modelMap.addAttribute("users",users);
		if(id != null)
			modelMap.addAttribute("id",id);
		else
			modelMap.addAttribute("id","");
		modelMap.addAttribute("activities",activities);
		modelMap.addAttribute("type",type);
		modelMap.addAttribute("role",role);
		return "/new/admin/activity";
	}
	
	/**
	 * save internal user
	 * @param internalUser
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value="internal-user", method = RequestMethod.POST) 
	public String setInternalUser(@Valid @ModelAttribute("internalUser") InternalUser internalUser, 
			BindingResult bindingResult, HttpServletRequest request,
			RedirectAttributes redirectAttributes, ModelMap modelMap){
		
		if(permissionService.getPermissionPlan().isCommission() == false) {return "redirect:/supervisor/unauthorized";}
		
		User loginUser = (User) request.getSession().getAttribute("user");
		if(!loginUser.getRole().equals("ROLE_ADMIN")) {
			boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.INTERNAL_USER,Permission.UPDATE, false);
			boolean grant1 = permissionService.grantPermission(loginUser.getRole(), Functionality.INTERNAL_USER,Permission.CREATE, false);
			if(!grant && internalUser.getId() != null){
				return "redirect:/supervisor/unauthorized";
			}
			if(!grant1 && internalUser.getId() == null){
				return "redirect:/supervisor/unauthorized";
			}
		}
		
		InternalUser user = internalUserRepository.findByEmail(internalUser.getEmail());
		
		if(bindingResult.hasErrors()) {
			modelMap.addAttribute("error", bindingResult.getFieldErrors().get(0).getDefaultMessage());
			modelMap.addAttribute("internalUsers",internalUserRepository.findAll());
			modelMap.addAttribute("internalUser", internalUser);
			return "new/admin/internal-user";
		}
		
		if((user != null && user.getId() != internalUser.getId() && user.getEmail().equals(internalUser.getEmail())) ) {
			
			modelMap.addAttribute("internalUsersEmailError","Using this email id already an internal user existed.");
			modelMap.addAttribute("internalUsers",internalUserRepository.findAll());
			modelMap.addAttribute("internalUser", internalUser);
			return "new/admin/internal-user";
		}
		
		User user1 = userRepository.findByEmail(internalUser.getEmail());
		if(user1 != null) {
			
			modelMap.addAttribute("internalUsersEmailError","Using this email id already an user existed.");
			modelMap.addAttribute("internalUsers",internalUserRepository.findAll());
			modelMap.addAttribute("internalUser", internalUser);
			return "new/admin/internal-user";
		}
		
		Manager client = adminService.getManagerByEmail(internalUser.getEmail());
		if(client != null) {
			modelMap.addAttribute("internalUsersEmailError","Using this email id already a manager existed.");
			
			modelMap.addAttribute("internalUsers",internalUserRepository.findAll());
			modelMap.addAttribute("internalUser", internalUser);
			return "new/admin/internal-user";
		}
		
		userService.saveInternalUser(internalUser);
		redirectAttributes.addFlashAttribute("success","Internal user has been save successfully");
		return "redirect:/admin/internal-user";
	}
	
	/**
	 * active user details
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value="user-detail/active/{id}", method = RequestMethod.GET) 
	public String setUserDetailsActive(@PathVariable("id") Integer id,
			RedirectAttributes redirectAttributes){
		
		UserDetail userDetail = userDetailsRepository.findById(id).orElse(null);
		List<UserDetail> userDetails = userDetailsRepository.findByUser(userDetail.getUser());
		userDetails.forEach(list -> list.setActive(false));
		userDetailsRepository.saveAll(userDetails);
		
		userDetail.setActive(true);
		userDetailsRepository.save(userDetail);
		
		User user = userService.findOne(userDetail.getUser().getId());
		user.setClientActiveId(userDetail.getUserDetailId());
		userRepository.save(user);
		
		redirectAttributes.addFlashAttribute("success","Active has been done successfully");
		return "redirect:/admin/user-detail/" + userDetail.getUser().getId();
	}
	
	/**
	 * de-active user details
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value="user-detail/de-active/{id}", method = RequestMethod.GET) 
	public String setUserDetailsDeactive(@PathVariable("id") Integer id,
			RedirectAttributes redirectAttributes){
		
		UserDetail userDetail = userDetailsRepository.findById(id).orElse(null);
		userDetail.setActive(false);
		userDetailsRepository.save(userDetail);
		
		User user = userService.findOne(userDetail.getUser().getId());
		user.setClientActiveId(null);
		userRepository.save(user);
		redirectAttributes.addFlashAttribute("success","De-active has been done successfully");
		return "redirect:/admin/user-detail/" + userDetail.getUser().getId();
	}
	
	/**
	 * add client, employee, vendor
	 * @param clientAddDetail
	 * @return
	 */
	@RequestMapping(value="new-client-add", method = RequestMethod.POST) 
	public ResponseEntity<Response> newClientAdd(@ModelAttribute(value = "clientAddDetail") ClientAddDetail clientAddDetail, 
			HttpServletRequest request){
		
		User loginUser = (User) request.getSession().getAttribute("user");
		if(!loginUser.getRole().equals("ROLE_ADMIN")) {
			boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.CLIENT_ASSIGN_USER,Permission.CREATE, false);
			if(!grant){
				return ResponseGenerator.generateResponse(new Response("Please try again", null), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		try {
			return adminService.newClientExist(clientAddDetail);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error in resend verify company email: " + e);
			return ResponseGenerator.generateResponse(new Response("Please try again", null), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * get add
	 * @param type
	 * @param map
	 * @return
	 */
	@RequestMapping(value="new-client-add", method = RequestMethod.GET) 
	public String clientAdd(String type, ModelMap map, HttpServletRequest request){
		
		User loginUser = (User) request.getSession().getAttribute("user");
		if(!loginUser.getRole().equals("ROLE_ADMIN")) {
			boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.CLIENT_ASSIGN_USER,Permission.CREATE, false);
			if(!grant){
				return "redirect:/supervisor/unauthorized";
			}
		}
		
		ClientType typeClient = ClientType.getClientType(type);
		ClientAddDetail clientAddDetail = new ClientAddDetail();
		clientAddDetail.setType(typeClient);
		List<ManagerDetail> managerDetails = new ArrayList<>();
        clientAddDetail.setManagerDetails(managerDetails);
		
		map.addAttribute("clientAddDetail",clientAddDetail);
		return "new/admin/clientDetailsAddPopupForm";
	}
	/**
     * Remove edit credential education
     * @param modelMap
     * @param request
     * @return
     */
    @RequestMapping(value = "new-client-add", params = {"removeRowManager"})
    public String removeEducation(ModelMap modelMap, HttpServletRequest request, 
    		@ModelAttribute(value = "clientAddDetail") ClientAddDetail clientAddDetail) {
    
        final Integer rowId = Integer.valueOf(request.getParameter("removeRowManager"));
        
        clientAddDetail.getManagerDetails().remove(rowId.intValue());
        
        modelMap.addAttribute("clientAddDetail", clientAddDetail);
        return "new/admin/clientAddInnerFragment";
    }
    /**
     * add row
     * @param modelMap
     * @param clientAddDetail
     * @return
     */
    @RequestMapping(value = "new-client-add", params = {"addRowManager"}, method = RequestMethod.POST)
    public String addEducation(ModelMap modelMap, @ModelAttribute(value = "clientAddDetail") ClientAddDetail clientAddDetail) {
    	
    	if(CollectionUtils.isEmpty(clientAddDetail.getManagerDetails())) {
    		
    		List<ManagerDetail> managerDetails = new ArrayList<>();
    		managerDetails.add(new ManagerDetail());
    		clientAddDetail.setManagerDetails(managerDetails);
    	} else {
    		clientAddDetail.getManagerDetails().add(new ManagerDetail());
    	}
    	
    	modelMap.addAttribute("clientAddDetail", clientAddDetail);
    	return "new/admin/clientAddInnerFragment";
    }
    /**
     * set error
     * @param modelMap
     * @param clientAddDetail
     * @return
     */
	@RequestMapping(value = "new-client-add", params = {"sectionFormError"}, method = RequestMethod.POST)
    public String sectionFormError(ModelMap modelMap, @ModelAttribute(value = "clientAddDetail") ClientAddDetail clientAddDetail) {
        
		int i =0; 
		for (ManagerDetail managerDetail : clientAddDetail.getManagerDetails()) {
			
			InternalUser internalUser = internalUserRepository.findByEmail(managerDetail.getEmail());
			if(internalUser != null){
				clientAddDetail.getManagerDetails().get(i).setErrorMsg("Using this email id already an internal user existed.");
			}
			User user1 = userRepository.findByEmail(managerDetail.getEmail());
			if(user1 != null) {
				clientAddDetail.getManagerDetails().get(i).setErrorMsg("Using this email id already an user existed");
			}
			
			Manager client1 = adminService.getManagerByEmail(managerDetail.getEmail());
			if(client1 != null && client1.getId() != client1.getId() && client1.getEmail().equals(managerDetail.getEmail())) {
				clientAddDetail.getManagerDetails().get(i).setErrorMsg("Using this email id already an manager existed");
				
			}
			
			List<ManagerDetail> existList = clientAddDetail.getManagerDetails().stream()
            .filter(c -> c.getEmail().equals(managerDetail.getEmail()))
            .collect(Collectors.toList());

			if(existList.size() > 1) {
				clientAddDetail.getManagerDetails().get(i).setErrorMsg("This email id already an exist in list");
					
			}
			i++;
		}
        
        modelMap.addAttribute("clientAddDetail", clientAddDetail);
        return "new/admin/clientAddInnerFragment";
    }
	@RequestMapping(value="get-client", method = RequestMethod.GET) 
	public ResponseEntity<Response> getClient(){
		
		try {
			List<Client> client = adminService.getClient();
			return ResponseGenerator.generateResponse(new Response("get client successfully", client), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("error in resend verify company email: " + e);
			return ResponseGenerator.generateResponse(new Response("Please try again", null), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value="get-employer", method = RequestMethod.GET) 
	public ResponseEntity<Response> getEmployer(){
		
		try {
			List<Client> client = adminService.getEmployer();
			return ResponseGenerator.generateResponse(new Response("get employer successfully", client), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("error in resend verify company email: " + e);
			return ResponseGenerator.generateResponse(new Response("Please try again", null), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value="get-vendor", method = RequestMethod.GET) 
	public ResponseEntity<Response> getVendor(){
		
		try {
			List<Client> client = adminService.getVendor();
			return ResponseGenerator.generateResponse(new Response("get vendor successfully", client), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("error in resend verify company email: " + e);
			return ResponseGenerator.generateResponse(new Response("Please try again", null), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value="set-internal-user/{id}", method = RequestMethod.GET) 
	public ResponseEntity<Response> setInternalUser(@PathVariable("id") Integer id){
		
		try {
			InternalUser user = internalUserRepository.findById(id).orElse(null);
			if(user == null) {
				return ResponseGenerator.generateResponse(new Response("Please try again", null), HttpStatus.INTERNAL_SERVER_ERROR);
			} 
			return ResponseGenerator.generateResponse(new Response("get internal User successfully", user), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("error in resend verify company email: " + e);
			return ResponseGenerator.generateResponse(new Response("Please try again", null), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value="get-employer-details/{id}", method = RequestMethod.GET) 
	public ResponseEntity<Response> getEmployerDetails(@PathVariable("id") Integer id){
		
		try {
			Client client = adminService.getEmployerDetails(id);
			if(client != null)
				return ResponseGenerator.generateResponse(new Response("get employer successfully", client), HttpStatus.OK);
			
			return ResponseGenerator.generateResponse(new Response("Please try again", null), HttpStatus.INTERNAL_SERVER_ERROR);
			
		} catch (Exception e) {
			logger.error("error in resend verify company email: " + e);
			return ResponseGenerator.generateResponse(new Response("Please try again", null), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Template List
	 * @param modal
	 * @return
	 */
	@RequestMapping(value="template", method = RequestMethod.GET) 
	public String template(ModelMap modal, HttpServletRequest request){
		
		User loginUser = (User) request.getSession().getAttribute("user");
		if(!loginUser.getRole().equals("ROLE_ADMIN")) {
			boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.TEMPLATE,Permission.READ, false);
			if(!grant){
				return "redirect:/supervisor/unauthorized";
			}
		}
		
		Iterable<Template> templateList = templateService.getAllTempalte();
		modal.addAttribute("templateList", templateList);
		
		return "new/admin/templateList";
	}
	
	/**
	 * add template step
	 * @return
	 */
	@RequestMapping(value="template/add", method = RequestMethod.GET) 
	public String templateAdd(HttpServletRequest request){
		User loginUser = (User) request.getSession().getAttribute("user");
		if(!loginUser.getRole().equals("ROLE_ADMIN")) {
			boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.TEMPLATE,Permission.CREATE, false);
			if(!grant){
				return "redirect:/supervisor/unauthorized";
			}
		}
		
		return "redirect:/admin/template/add-step-1";
	}
	@RequestMapping(value="template/add-step-1", method = RequestMethod.GET) 
	public String templateAddStep1(ModelMap modal, HttpServletRequest request){
		
		if(permissionService.getPermissionPlan().isTemplateCanSet() == false) {return "redirect:/supervisor/unauthorized";}
		
		User loginUser = (User) request.getSession().getAttribute("user");
		if(!loginUser.getRole().equals("ROLE_ADMIN")) {
			boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.TEMPLATE,Permission.CREATE, false);
			if(!grant){
				return "redirect:/supervisor/unauthorized";
			}
		}
		return "new/admin/templateAddStep1";
	}
	@RequestMapping(value="template/add-step-2", method = RequestMethod.GET) 
	public String templateAddStep2(String type, ModelMap modal,HttpServletRequest request){
		
		if(permissionService.getPermissionPlan().isTemplateCanSet() == false) {return "redirect:/supervisor/unauthorized";}
		
		User loginUser = (User) request.getSession().getAttribute("user");
		if(!loginUser.getRole().equals("ROLE_ADMIN")) {
			boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.TEMPLATE,Permission.CREATE, false);
			if(!grant){
				return "redirect:/supervisor/unauthorized";
			}
		}
		MailTemplateType mailTemplateType = MailTemplateType.getMailTemplateType(type);
		Template template = new Template();
		template.setMailTemplateType(mailTemplateType);
		modal.addAttribute("template", template);
		
		Iterable<Signature> signatures = signatureRepository.findAll();
		modal.addAttribute("signatures", signatures);
		
		List<EmailNameShortCut> emailNameShortCuts = EmailNameShortCut.getEmailNameShortCut(mailTemplateType);
		modal.addAttribute("emailNameShortCuts", emailNameShortCuts);
		
		List<Template> templateClone = templateService.getCloneTemplate(mailTemplateType, type);
		
		modal.addAttribute("templateClone", templateClone);
		return "new/admin/templateAddStep2";
	}
	@RequestMapping(value="template/add-step-3", method = RequestMethod.POST) 
	public String templateAddStep3(String subject,
			String htmlDate,
			MailTemplateType mailTemplateType,
			ModelMap modal,
			@RequestParam(name="templateName",required=true)  String templateName,
			@RequestParam(name="id",required=false) Integer id,
			boolean roleSupervisorPermission,
			boolean roleAdminPermission,
			boolean roleUserPermission,
			String mailTemplateFor,
			HttpServletRequest request,
			RedirectAttributes redirectAttributes){
		
		User loginUser = (User) request.getSession().getAttribute("user");
		if(!loginUser.getRole().equals("ROLE_ADMIN")) {
			boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.TEMPLATE,Permission.CREATE, false);
			if(!grant){
				return "redirect:/supervisor/unauthorized";
			}
		}
		
		boolean templateNameExist = templateService.getTemplateByName(templateName,id);
		if(templateNameExist == true) {
			redirectAttributes.addFlashAttribute("error", "This name template already exist");
			return "redirect:/admin/template/add-step-2?type=" + mailTemplateType.urlParam;
		}
			
		if(!loginUser.getRole().equals("ROLE_ADMIN")) {
			boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.TEMPLATE,Permission.READ, false);
			if(!grant){
				return "redirect:/admin/template/add-step-1";
			}
		}
		
		Integer saveId = templateService.saveTemplate(subject,htmlDate,mailTemplateType,id,roleSupervisorPermission,roleAdminPermission,roleUserPermission,templateName);
		redirectAttributes.addFlashAttribute("isFromStep2", true);
		return "redirect:/admin/template/view/"+saveId;
	}
	
	/**
	 * view template
	 * @param id
	 * @param modal
	 * @return
	 */
	@RequestMapping(value="template/view/{id}", method = RequestMethod.GET) 
	public String templateView(@PathVariable("id") Integer id,ModelMap modal, HttpServletRequest request){
		
		User loginUser = (User) request.getSession().getAttribute("user");
		if(!loginUser.getRole().equals("ROLE_ADMIN")) {
			boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.TEMPLATE,Permission.READ, false);
			if(!grant){
				return "redirect:/supervisor/unauthorized";
			}
		}
		
		Template template = templateService.getTemplate(id);
		
		if(template != null) {
			final Context ctx = new Context(Locale.US);
			ctx.setVariable("rootURL", TIMESHEET_SERVER_URL);
			ctx.setVariable("templateData", template.getHtmlDate());
			final String htmlContent = templateEngine.process("mail/boxMail/box", ctx);
			
			modal.addAttribute("htmlContent", htmlContent);
		}
		modal.addAttribute("template", template);
		return "new/admin/templateView";
	}
	@RequestMapping(value="template/view/default/{urlParam}", method = RequestMethod.GET) 
	public String templateView(@PathVariable("urlParam") String urlParam, ModelMap modal, HttpServletRequest request){
		
		User loginUser = (User) request.getSession().getAttribute("user");
		if(!loginUser.getRole().equals("ROLE_ADMIN")) {
			boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.TEMPLATE,Permission.READ, false);
			if(!grant){
				return "redirect:/supervisor/unauthorized";
			}
		}
		
		DefaultMailTemplate defaultMailTemplate = DefaultMailTemplate.getDefaultMailTemplate(urlParam);
		
		final Context ctx = new Context(Locale.US);
		ctx.setVariable("rootURL", TIMESHEET_SERVER_URL);
		ctx.setVariable("templateData", defaultMailTemplate.htmlData);
		final String htmlContent = templateEngine.process("mail/boxMail/box", ctx);
		
		modal.addAttribute("htmlContent", htmlContent);
		modal.addAttribute("template", defaultMailTemplate);
		return "new/admin/templateViewDefault";
	}
	@RequestMapping(value="template/delete/{id}", method = RequestMethod.GET) 
	public ResponseEntity<Response> templateDelete(@PathVariable("id") Integer id,HttpServletRequest request){
		User loginUser = (User) request.getSession().getAttribute("user");
		if(!loginUser.getRole().equals("ROLE_ADMIN")) {
			boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.TEMPLATE,Permission.DELETE, false);
			if(!grant){
				return ResponseGenerator.generateResponse(new Response("Delete template is unauthorized", null), HttpStatus.UNAUTHORIZED);
			}
		}
		templateService.deleteTemplate(id);
		return ResponseGenerator.generateResponse(new Response("Delete template successfully", null), HttpStatus.OK);
	}
	
	/**
	 * edit template
	 * @param id
	 * @param modal
	 * @return
	 */
	@RequestMapping(value="template/edit", method = RequestMethod.GET) 
	public String templateAddStep2(@RequestParam(name="id",required=false) Integer id, ModelMap modal, HttpServletRequest request){
		
		User loginUser = (User) request.getSession().getAttribute("user");
		if(!loginUser.getRole().equals("ROLE_ADMIN")) {
			boolean grant = permissionService.grantPermission(loginUser.getRole(), Functionality.TEMPLATE,Permission.UPDATE, false);
			if(!grant){
				return "redirect:/supervisor/unauthorized";
			}
		}
		
		Template template = templateService.getTemplate(id);
		if(template == null) {
			return "redirect:/admin/template/view/"+id;
		}
		template.setMailTemplateType(template.getMailTemplateType());
		modal.addAttribute("template", template);
		
		Iterable<Signature> signatures = signatureRepository.findAll();
		modal.addAttribute("signatures", signatures);
		
		List<EmailNameShortCut> emailNameShortCuts = EmailNameShortCut.getEmailNameShortCut(template.getMailTemplateType());
		modal.addAttribute("emailNameShortCuts", emailNameShortCuts);
		
		List<Template> templateClone = new ArrayList<Template>();
		modal.addAttribute("templateClone", templateClone);
		return "new/admin/templateAddStep2";
	}
	
	/**
	 * get signature
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="signature", method = RequestMethod.GET) 
	public String signature(ModelMap modelMap){
		
		Iterable<Signature> signatures = signatureRepository.findAll();
		Signature signature = new Signature();
		modelMap.addAttribute("signatures", signatures);
		modelMap.addAttribute("signature", signature);
		return "new/admin/signature";
	}
	
	/**
	 * add signature
	 * @param signature
	 * @param bindingResult
	 * @return
	 */
	@RequestMapping(value="signature/add", method = RequestMethod.POST) 
	@ResponseBody
	public ResponseEntity<Response> signatureAdd(Signature signature, BindingResult bindingResult){
		if (bindingResult.hasErrors()) {
			return ResponseGenerator.generateResponse(new Response("error", null), HttpStatus.BAD_REQUEST);
		}
		
		signatureRepository.save(signature);
		return ResponseGenerator.generateResponse(new Response("success", null), HttpStatus.OK);
	}
	
	/**
	 * delete signature
	 * @param id
	 * @return
	 */
	@RequestMapping(value="signature/delete/{id}", method = RequestMethod.GET) 
	@ResponseBody
	public ResponseEntity<Response> signatureDelete(@PathVariable("id") Integer id){
		
		Signature signature = signatureRepository.findById(id).orElse(null);
		if(signature == null) {
			return ResponseGenerator.generateResponse(new Response("error", null), HttpStatus.BAD_REQUEST);
		}
		signatureRepository.delete(signature);
		return ResponseGenerator.generateResponse(new Response("success", null), HttpStatus.OK);
	}
	
	/**
	 * send mail
	 * @param type
	 * @return
	 */
	@RequestMapping(value="mail/send-mail", method = RequestMethod.GET) 
	public String sendMail(String type,ModelMap modelMap){
		
		List<Template> templatesUnSet = templateService.getMailByType(MailTemplateType.general);
		
		List<User> users = userRepository.findByRoleAndActive("ROLE_USER",1);
		List<User> usersNotActive = userRepository.findByRoleAndActive("ROLE_USER",0);
		List<Manager> client = adminService.getClientByType(ClientType.CLIENT);
		List<Manager> employer = adminService.getClientByType(ClientType.EMPLOYEE);
		List<Manager> vendor = adminService.getClientByType(ClientType.VENDOR);
		
		modelMap.addAttribute("bDMListToMail",internalUserRepository.findAllByInternalUserType(InternalUserType.BDM.urlParam));
		modelMap.addAttribute("accountManagersToMail",internalUserRepository.findAllByInternalUserType(InternalUserType.ACCOUNT_MANAGER.urlParam));
		modelMap.addAttribute("recruitersToMail",internalUserRepository.findAllByInternalUserType(InternalUserType.RECRUITER.urlParam));
		modelMap.addAttribute("usersToMail", users);
		modelMap.addAttribute("usersNotActive", usersNotActive);
		modelMap.addAttribute("clientToMail", client);
		modelMap.addAttribute("employerToMail", employer);
		modelMap.addAttribute("vendorToMail", vendor);
		
		List<Template> templates = new ArrayList<Template>();
		templates = templateService.setMailGenaral(templatesUnSet);
		
		modelMap.addAttribute("templates",templates);
		return "new/genaralMailSend/genaralMailDiv";
	}
	/**
	 * 
	 */
	@RequestMapping(value="mail/send-mail/reminder", method = RequestMethod.GET) 
	public String sendMailType(String urlParams,Integer userDetailsId,
			String startDate, String endDate,
			ModelMap modelMap){
		
		UserDetail userDetail = userDetailsRepository.findByUserDetailId(userDetailsId);
		MailTemplateType mailTemplateType = MailTemplateType.getMailTemplateType(urlParams);
		List<Template> templatesUnSet = templateService.getMailByType(mailTemplateType);
		List<Template> templates = templateService.setPendingTimesheet(templatesUnSet,startDate, endDate, userDetailsId);
		modelMap.addAttribute("templates",templates);
		modelMap.addAttribute("user",userDetail.getUser());
		modelMap.addAttribute("userDetail",userDetail);
		modelMap.addAttribute("startDate",startDate);
		modelMap.addAttribute("endDate",endDate);
		return "new/genaralMailSend/typeMailDiv";
	}
	
	/**
	 * send mail
	 * @return
	 */
	@RequestMapping(value="mail/send-mail", method = RequestMethod.POST) 
	@ResponseBody
	public ResponseEntity<Response> sendMailPost(@RequestParam(name="ccEmail", required=false) String ccEmail,
			@RequestParam(name="toEmail", required=false) String toEmail,
			@RequestParam(name="description", required=false) String description,
			@RequestParam(name="subject", required=false) String subject,ModelMap modelMap){
		
		try {
			userService.sendEmailToUser(description, toEmail, subject, ccEmail, null);
			return ResponseGenerator.generateResponse(new Response("success", null), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseGenerator.generateResponse(new Response("error", null), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * send mail
	 * @return
	 */
	@RequestMapping(value="mail/access-send-mail", method = RequestMethod.POST) 
	@ResponseBody
	public ResponseEntity<Response> sendAccessMailPost(
			PermissionPlan permissionPlan,
			@RequestParam(name="description", required=false) String description,
			@RequestParam(name="subject", required=false) String subject, ModelMap modelMap){
		
		try {
			
			String planString = userService.setPlanString(permissionPlan);
			description = description + planString;
			
			ThreadPoolExecutor executor1 = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
			GetSuperAdmin g = new GetSuperAdmin(userRepository);
			Future<List<User>> result = executor1.submit(g);
			List<User> user3 = new ArrayList<User>();
			
			try {
				user3 = result.get();
			} catch (Exception e) {
				e.printStackTrace();
			}
			String email = "";
			int i = 0;
			for (User u : user3) {
				if(i == 0) {
					email = u.getEmail();
				} else {
					email = email +", " +u.getEmail();
				}
				i++;
			}
			userService.sendEmailToUser(description, email, subject, null, null);
			return ResponseGenerator.generateResponse(new Response("success", null), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseGenerator.generateResponse(new Response("error", null), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * send mail
	 * @return
	 */
	@RequestMapping(value="template/exist", method = RequestMethod.GET) 
	@ResponseBody
	public ResponseEntity<Response> templateNameExist(@RequestParam(name="templateName",required=true) String templateName,
			@RequestParam(name="id",required=false) Integer id){
		
		try {
			boolean templateNameExist = templateService.getTemplateByName(templateName,id);
			if(templateNameExist == true) {
				return ResponseGenerator.generateResponse(new Response("success", true), HttpStatus.OK);
			}
			return ResponseGenerator.generateResponse(new Response("success", false), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseGenerator.generateResponse(new Response("error", null), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * send mail
	 * @return
	 */
	@RequestMapping(value="company/configuration", method = RequestMethod.GET) 
	public String companyConfiguration(ModelMap modelMap, HttpServletRequest request){
		User user = (User) request.getSession().getAttribute("user");
		User userExists = userService.findUserByEmail(user.getEmail());
		modelMap.addAttribute("userLogin", userExists);
		return "new/admin/companyConfiguration";
	}
	
	@RequestMapping(value="company/set", method = RequestMethod.GET) 
	public String companySet(ModelMap modelMap){
		Company company = userService.getCompany();
		CompanyRequest companyRequest = new CompanyRequest();
		companyRequest.setAddress(company.getAddress());
		companyRequest.setDetails(company.getDetails());
		companyRequest.setId(company.getId());
		companyRequest.setName(company.getName());
		companyRequest.setUrlSlug(company.getUrlSlug());
		companyRequest.setTimesheetSubmitEmail(company.getTimesheetSubmitEmail());
		companyRequest.setFileFolder(company.getFileFolder());
		companyRequest.setImagePath(company.getImagePath());
		modelMap.addAttribute("company", companyRequest);
		boolean folderExists = false;
		if (new File(FILE_PATH + company.getFileFolder()).exists())
			folderExists = true;
		modelMap.addAttribute("folderExists", folderExists);
		
		return "new/admin/companySet";
	}
	
	@RequestMapping(value="company/set", method = RequestMethod.POST) 
	public String companySetSave(CompanyRequest companyRequest, ModelMap modelMap,
			@RequestParam(name="file", required = false) MultipartFile file,
			RedirectAttributes redirectAttributes, HttpServletRequest request){
		
		Company company = new Company();
		ThreadPoolExecutor executor1 = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
		GetCompanyByName getCompanyByName = new GetCompanyByName(companyRepository,companyRequest.getName());
		Future<Company> result = executor1.submit(getCompanyByName);
		
		try {
			company = result.get();
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("error", "Please try again");
			return "redirect:/admin/company/set";
		}
		
		if(company != null && !companyRequest.getUrlSlug().equals(company.getUrlSlug())) {
			redirectAttributes.addFlashAttribute("error", "Other company available this name");
			return "redirect:/admin/company/set";
		}
		
		if (!new File(FILE_PATH + company.getFileFolder()).exists())
			new File(FILE_PATH + company.getFileFolder()).mkdir();
		
		new File(FILE_PATH + company.getFileFolder()).mkdir();
		
		String filePath1 = null;
		String p = null;
		if(file.getOriginalFilename().length() > 0) {
			
			filePath1 = company.getDbName() + new Date().getTime() +
					file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."));
			
			try {
				file.transferTo(new File(FILE_PATH +company.getFileFolder()+"/" + filePath1));
			} catch (IllegalStateException | IOException e) {
				e.printStackTrace();
			}
			new File(FILE_PATH + company.getFileFolder() + filePath1);
		}
		
		if(filePath1 != null)
		p = company.getFileFolder()+"/" + filePath1;
		Company company1 = userService.saveCompany(companyRequest, p);
		ThreadPoolExecutor executor2 = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
		GetCompanyByName getCompanyByName2 = new GetCompanyByName(companyRepository,companyRequest.getName());
		Future<Company> result2 = executor2.submit(getCompanyByName2);
		try {
			company = result2.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.getSession().setAttribute("company", company);
		redirectAttributes.addFlashAttribute("success", "Change save");
		return "redirect:/admin/company/set";
	}

	@RequestMapping(value="role/permission", method = RequestMethod.GET) 
	public String rolePermission(ModelMap modelMap){
		RolePermissionRequest rolePermissionRequest = adminService.getRolePermission();
		modelMap.addAttribute("rolePermissionRequest", rolePermissionRequest);
		return "new/admin/rolePermissionForm";
	}
	@RequestMapping(value="admin-first-page", method = RequestMethod.GET) 
	public String adminFirstPage(ModelMap modelMap){
		
		return "new/admin/admin-first-page";
	}
	
	@RequestMapping(value="plan/permission", method = RequestMethod.GET) 
	public String planPermission(@RequestParam(name = "accessReq", required = false, defaultValue = "false") boolean accessReq ,
			ModelMap modelMap){
		PermissionPlan permissionPlan = permissionService.getPermissionPlan();
		modelMap.addAttribute("permissionPlan", permissionPlan);
		modelMap.addAttribute("accessReq", accessReq);
		modelMap.addAttribute("requestAccessHtml", DefaultMailTemplate.REQUEST_ACCESS.htmlData);
		modelMap.addAttribute("cancelPlanHtml", DefaultMailTemplate.CANCELPLAN_ACCESS.htmlData);


		return "new/admin/planPermission";
	}
	/**
	 * send mail
	 */
	@RequestMapping(value="set-role-permission", method = RequestMethod.POST) 
	@ResponseBody
	public ResponseEntity<Response> setRolePermission(@RequestParam("id") Integer id,
			@RequestParam("functionality") Functionality functionality,
			@RequestParam(name="create", required = false, defaultValue = "false") boolean create,
			@RequestParam(name="delete", required = false, defaultValue = "false") boolean delete,
			@RequestParam(name="read", required = false, defaultValue = "false") boolean read,
			@RequestParam(name="update", required = false, defaultValue = "false") boolean update){
		try {
			adminService.setRolePermission(id, functionality, create, delete, read, update);
			return ResponseGenerator.generateResponse(new Response("success", false), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseGenerator.generateResponse(new Response("error", null), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@RequestMapping(value="customer/{urlParam}", method = RequestMethod.GET) 
	@ResponseBody
	public ResponseEntity<Response> adminCustomer(@PathVariable("urlParam") String urlParam){
		try {
			ClientType clientType = ClientType.getClientType(urlParam);
			List<CustomerResponse> clients = adminService.getCustomer(clientType);
			return ResponseGenerator.generateResponse(new Response("get customer successfully", clients), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseGenerator.generateResponse(new Response("error", null), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value="client-list", method = RequestMethod.GET) 
	public String getClient(ModelMap modelMap){
	
		
		List<CustomerResponse> clients = adminService.getCustomer(ClientType.CLIENT);
		modelMap.addAttribute("clients",clients);

		return "new/admin/client-list";
	
	}
	
	@RequestMapping(value="vendors-list", method = RequestMethod.GET) 
	public String getVendor(ModelMap modelMap){
	

		List<CustomerResponse> clients = adminService.getCustomer(ClientType.VENDOR);
		modelMap.addAttribute("clients",clients);

		return "new/admin/vendors-list";
	
	}
	
	@RequestMapping(value="employee-list", method = RequestMethod.GET) 
	public String getEmployee(ModelMap modelMap){
	
	
		List<CustomerResponse> clients = adminService.getCustomer(ClientType.EMPLOYEE);
		modelMap.addAttribute("clients",clients);

		return "new/admin/employee-list";
	
	}
	//TODO:add time-sheet-manage time-shhet-reven dashboard client kadhine customer

	
}
