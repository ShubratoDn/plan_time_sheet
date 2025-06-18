package com.aim.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aim.entity.Company;
import com.aim.entity.User;
import com.aim.entity.UserCompany;
import com.aim.model.InvalidReCaptchaException;
import com.aim.model.ReCaptchaInvalidException;
import com.aim.repository.CompanyRepository;
import com.aim.repository.UserCompanyRepository;
import com.aim.repository.UserRepository;
import com.aim.request.CompanyRequest;
import com.aim.service.CaptchaService;
import com.aim.service.CompanyService;
import com.aim.service.PermissionService;
import com.aim.service.UserService;
import com.aim.service.email.EmailSenderService;
import com.aim.utils.EncryptDecryptUtils;

@Controller
public class RegistrationController {
	
	final static Logger logger = Logger.getLogger(RegistrationController.class);
	
	@Autowired
	UserService userService;
	
	@Autowired
	private EmailSenderService emailSenderService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CompanyService companyService;
	
	@Autowired
	private UserCompanyRepository userCompanyRepository;
	
	@Autowired
	private BCryptPasswordEncoder bcryptEncoder;
	
	@Autowired
	private CompanyRepository companyRepository;
	
	@Autowired
	private CaptchaService captchaService;
	
	@Autowired
	private PermissionService permissionService;
	
	@Value("${timesheet.server.url}")
	private String TIMESHEET_SERVER_URL;
	
	@RequestMapping(value="registration", method = RequestMethod.GET)
	public ModelAndView registration() {
		ModelAndView modelAndView = new ModelAndView();
		CompanyRequest companyRequest = new CompanyRequest();
		User admin = new User();
		companyRequest.setAdmin(admin);
		modelAndView.addObject("company", companyRequest);
		modelAndView.setViewName("new/registration");
		return modelAndView;
	}
	
	@RequestMapping(value = "registration", method = RequestMethod.POST)
	public String createNewUser(@Valid CompanyRequest companyRequest, BindingResult bindingResult,
			ModelMap modelMap,RedirectAttributes redirectAttributes ,HttpSession session,HttpServletRequest request) {
	
		String response = request.getParameter("g-recaptcha-response");
//        try {
//			captchaService.processResponse(response);
//        } catch (InvalidReCaptchaException e) {
//        	modelMap.addAttribute("captchaErrorMsg", e.getMessage());
//        	modelMap.addAttribute("company", companyRequest);
//			return "new/registration";
//
//		} catch (ReCaptchaInvalidException e) {
//			modelMap.addAttribute("captchaErrorMsg", e.getMessage());
//			modelMap.addAttribute("company", companyRequest);
//			modelMap.addAttribute("new/registration");
//			return "new/registration";
//		}
		
		Company company = companyRepository.findByName(companyRequest.getName());
		if (company != null) {
			modelMap.addAttribute("nameError", "There is already a company registered with the name provided");
			modelMap.addAttribute("company", companyRequest);
			modelMap.addAttribute("new/registration");
			return "new/registration";
		}
		
		companyService.saveCompanyAndAdminUser(companyRequest);
		redirectAttributes.addFlashAttribute("success", "Company has been registered successfully, Please check your email for verification");
		
		return "redirect:/registration";
	}
	
	/**
	 * Get reset password form.
	 * @param key
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/change-pass", method=RequestMethod.GET)
	public String getResetPassword(@RequestParam(value="uId")String uId, ModelMap model) {
		
		if(!StringUtils.isEmpty(uId)) {
			Boolean isValid;
			try {
				isValid = userService.isValidActivationKey(uId);
				if(isValid) {
					model.addAttribute("key",uId);
					return "new/reset_password";
				} else {
					return "new/login";
				}
			} catch (Exception e) {
				e.printStackTrace();
				return "new/login";
			}
		} else {
			return "new/login";
		}
	}
	
	/**
	 * Post reset password form.
	 * @param key
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/change-pass", method=RequestMethod.POST)
	public String getResetPassword(@RequestParam(value="key")String key,@RequestParam(value="password")String password,
			@RequestParam(value="confPass")String confPass, ModelMap model,RedirectAttributes redirectAttributes) {
		if(!password.equals(confPass)) {
			model.addAttribute("error","Password and confirm password is not match");
			model.addAttribute("key",key);
			return "new/reset_password";
		} 
		if(password.length() <= 5) {
			model.addAttribute("error","Password must be more then 5 characters!");
			model.addAttribute("key",key);
			return "new/reset_password";
		} 
		try {
			Boolean isValid = userService.resetPassword(key,password);
			if(isValid) {
				redirectAttributes.addFlashAttribute("success", "Your Password reset successfully!");
				return "redirect:/";
			} else {
				redirectAttributes.addFlashAttribute("error", "Invalid link , please contact us for help !");
				return "redirect:/";
			}
		} catch (Exception e) {
			logger.error("error in change password " + e.getMessage());
			return "new/login";
		}
	}
	
	/**
	 * forgot password
	 * @return
	 */
	@RequestMapping(value="/forgot-password", method = RequestMethod.GET)
	public ModelAndView forgotPass() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("new/page-recoverpw");
		return modelAndView;
	}
	
	/**
	 * forgot password
	 * @return
	 */
	@RequestMapping(value="/profile/change-pass", method = RequestMethod.POST)
	public String changePass(Model model,HttpServletRequest request,
			@RequestParam(name="currentPassword") String oldPassword, 
			@RequestParam(name="password") String password, 
			@RequestParam(name="confPass") String confPass,
			RedirectAttributes redirectAttributes) {
		
		User user = (User) request.getSession().getAttribute("user");
		if(user == null || user.getId() == 0) {
			return "redirect:/login";
		}
		
		if(password.length() <= 5) {
			redirectAttributes.addFlashAttribute("error","Password must be more then 5 characters!");
			return "redirect:/profile/change-pass";
		} 
		
		if(!password.equals(confPass)) {
			redirectAttributes.addFlashAttribute("error","Password and confirm password is not match");
			return "redirect:/profile/change-pass";
		} 
		
		if(bcryptEncoder.matches(oldPassword, user.getPassword())) {
				
			try {
				boolean b = userService.resetPassword(user.getUuid(),password);
			} catch (Exception e) {
				logger.error("error in profile/change-pass " + e);
//				redirectAttributes.addFlashAttribute("error", "Please check your current password");
				return "redirect:/logout";
			}
			redirectAttributes.addFlashAttribute("success", "Your Password reset successfully!");
			return "redirect:/";
		} else {
			return "redirect:/logout";
		}
		
	}
	
	/**
	 * change password
	 * @return
	 */
	@RequestMapping(value="/profile/change-pass", method = RequestMethod.GET)
	public String changePassUser(Model model,HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		if(user == null || user.getId() == 0) {
			return "redirect:/login";
		}
		List<User> userList = userRepository.findByRole("ROLE_USER");
		model.addAttribute("userTotalCount", userList.size());
		model.addAttribute("permissionService", permissionService);
		return "new/change-password";
	}
	
	/**
	 * active user
	 * @return
	 */
	@RequestMapping(value="/company/active", method = RequestMethod.GET)
	public String companyActive(@RequestParam (name="cId") String cId, 
			@RequestParam (name="uId") String uId,ModelMap model) {
			
		if(!StringUtils.isEmpty(cId)) {
			Boolean isValid;
			try {
				isValid = companyService.isValidActivationKey(cId,uId);
				
				if(isValid) {
					companyService.varify(cId,uId);
					companyService.sendMailToSuperAdminForCompanyActive(cId);
					model.addAttribute("success", "We will active your company soon");
					return "new/login";
				} else {
					model.addAttribute("error", "Please try again later");
					return "new/login";
				}
			} catch (Exception e) {
				
				logger.error("error in verify company controller  : " + e);
				model.addAttribute("error", "Please try again later");
				return "new/login";
			}
		} else {
			model.addAttribute("error", "Please try again later");
			return "new/login";
		}
	}
	
	/**
	 * active user
	 * @return
	 */
	@RequestMapping(value="/active", method = RequestMethod.GET)
	public String userActive(@RequestParam (name="uId") String uId, @RequestParam (name="cId") String cId, 
			ModelMap model, RedirectAttributes redirectAttributes) {
		if(!StringUtils.isEmpty(uId)) {
			Boolean isValid;
			try {
				isValid = userService.isValidActivationKey(uId);
				Company company = companyRepository.findByUuid(cId);
				if(isValid && company != null) {
					userService.setUserActive(uId, cId);
					redirectAttributes.addFlashAttribute("success", "Active successfully, Please change password");
					return "redirect:/change-pass?uId="+uId;
				} else {
					model.addAttribute("error", "Please try again later");
					return "new/login";
				}
			} catch (Exception e) {
				logger.error("error in active user get" + e);
				model.addAttribute("error", "Please try again later");
				return "new/login";
			}
		} else {
			model.addAttribute("error", "Please try again later");
			return "new/login";
		}
	}
	
	/**
	 * active user
	 * @return
	 */
	@RequestMapping(value="/active/new-company", method = RequestMethod.GET)
	public String companyActiveBySuperAdmin(@RequestParam (name="cId") String cId, ModelMap model) {

		try {
			Company company = companyRepository.findByUuid(cId);
			if(company != null) {
				companyService.active(company.getId(),true);
				companyService.sendUserInformCompanyIsActive(company);
				model.addAttribute("success", "Active company successfully");
				return "new/login";
			} else {
				model.addAttribute("error", "Please try again later");
				return "new/login";
			}
		} catch (Exception e) {
			logger.error("error in active company by super admin get" + e);
			model.addAttribute("error", "Please try again later");
			return "new/login";
		}
	}
	
	/**
	 * forgot password
	 * @return
	 */
	@RequestMapping(value="/forgot-password/send-mail", method = RequestMethod.GET)
	public String sendMailPass(@RequestParam(name="email") String email, HttpServletRequest request,
			RedirectAttributes redirectAttributes) {
		
		User userExists = userService.findUserByEmail(email);
		List<UserCompany> userCompanys = userCompanyRepository.findByUserAndCompanyVarify(userExists, true);
		
		if (userExists != null && userCompanys.size() > 0) {
			
			String rootUrl =  request.getScheme() + "://" +   // "http" + "://
		             request.getServerName() +       // "myhost"
		             ":" + request.getServerPort();
			
			String verifyEmailLink = TIMESHEET_SERVER_URL + "/change-pass?uId=" + userExists.getUuid();// + "&d=" + EncryptDecryptUtils.encrypt(Utils.currentStringDate());
			
			emailSenderService.sendForgotPasswordEmail(userExists, verifyEmailLink, TIMESHEET_SERVER_URL);
			
			redirectAttributes.addFlashAttribute("success", "Mail send successfully, Check your mail");
			
		} else {
			redirectAttributes.addFlashAttribute("error", "Invalid Email, Check your Email id");
		}
		return "redirect:/login";
	}
	
	@RequestMapping(value="/forgot-password/data-set/user", method = RequestMethod.GET)
	public String setLoginUser(RedirectAttributes redirectAttributes) {

		Company company = companyRepository.findById(1);
		if(company == null) {
			company = new Company();
		}
		company.setActive(true);
		company.setAddress("test");
		company.setDbName("pddn");
		company.setDetails("test");
		company.setName("pddn");
		company.setUrlSlug("pddn");
		company.setUuid("265aec7d-6c3e-49c6-bca7-f8d9ccfffc0c");
		company.setVarify(true);
		companyRepository.save(company);
		
		User user1 = userRepository.findByEmail("vyasnaresh@gmail.com");
		if(user1 == null) {
			User user = new User();
			user.setActive(1);
			user.setClientActiveId(null);
			user.setCompany(company);
			user.setEmail("vyasnaresh@gmail.com");
			user.setFirstName("admin");
			user.setFileFolder(userService.getFinalFileFolder("admin", 0));
			user.setLastName("admin");
			user.setPassword("$2a$10$Vz0NuYgIgyGQT5MN4xzy9.3w.scogjs7CtBRboAUEDPor8PAyWYoi");
			user.setPhone("9876543210");
			user.setRole("ROLE_ADMIN");
			user.setUuid("265aec7d-6c3e-49c6-bca7-f8d9ccfffc0c");
			user.setWorkEmail("vyasnaresh@gmail.com");
			userRepository.save(user);
		}
		redirectAttributes.addFlashAttribute("success", "success");
		return "redirect:/login";
	}
}
