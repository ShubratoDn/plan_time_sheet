package com.aim.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import com.aim.service.UserCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aim.entity.Company;
import com.aim.entity.User;
import com.aim.entity.UserCompany;
import com.aim.repository.CompanyRepository;
import com.aim.repository.UserCompanyRepository;
import com.aim.repository.UserRepository;
import com.aim.service.PermissionService;
import com.aim.service.UserService;

@Controller
public class LoginController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	UserCompanyRepository userCompanyRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	CompanyRepository companyRepository;
	
	@Autowired
	PermissionService permissionService;

	@Autowired
	private UserCompanyService userCompanyService;
	/**
	 * Login
	 * @param principal
	 * @return
	 */
	@RequestMapping(value={"/", "/login"}, method = RequestMethod.GET)
	public String login(final Principal principal) {
		if (null == principal) 
			return "new/login"; 
		else 
			return "redirect:/default";
	}
	
	/**
	 * Once login throuh spring security it redirects to default PAGE
	 * @param request 
	 * @return
	 */
	@RequestMapping("/default")
	public String defaultAfterLogin(HttpServletRequest request, Principal principal,
			ModelMap modelMap, RedirectAttributes redirectAttributes) {
		
		User loginUser = userService.findUserByEmail(principal.getName());
		request.getSession().setAttribute("user",loginUser);
		
		if(!request.isUserInRole("ROLE_SUPER_ADMIN")) {
			Company company1 = (Company) request.getSession().getAttribute("company");
			if(company1 != null) {
				redirectAttributes.addAttribute("userCompany", company1.getId());
				return "redirect:/default-select-company"; 
			}
			List<UserCompany> userCompanies = userCompanyRepository.findByUserAndCompanyVarifyAndCompanyActive(loginUser, true, true);
			request.getSession().setAttribute("companyNumber",userCompanies.size());
			if(userCompanies.size()==1) {
				
				redirectAttributes.addAttribute("userCompany", userCompanies.get(0).getId());
				return "redirect:/default-select-company"; 
			} else if(userCompanies.size()>1){
				
				modelMap.addAttribute("userCompanies", userCompanies);
				return "new/selectCompany"; 
			} else {
				return "redirect:/logout"; 
			}
		 
		}else if(request.isUserInRole("ROLE_SUPER_ADMIN")) {
			request.getSession().setAttribute("companyNumber",0);
			return "redirect:/super-admin/home";
		} else {
			return "redirect:/logout"; 
		}
	}
	
	@RequestMapping("/default-select-company")
    public String defaultAfterselectCompany(@RequestParam("userCompany") Integer userCompanyId, HttpServletRequest request,
    		@RequestParam(name="login",defaultValue= "0",required = false ) Integer login,
    		Principal principal, RedirectAttributes redirectAttributes) {
		
		Company company1 = (Company) request.getSession().getAttribute("company");

		if(company1 == null) {
			UserCompany userCompany = userCompanyService.findOne(userCompanyId);

			User user = (User) request.getSession().getAttribute("user");
			user.setCompany(userCompany.getCompany());
			user.setRole(userCompany.getRole());
			
			if(userCompany.getLoginCount() != null) {
				userCompany.setLoginCount(userCompany.getLoginCount()+1);
				userCompanyRepository.save(userCompany);
				login = userCompany.getLoginCount()+1;
			}
			else {
				login = 1;
				userCompany.setLoginCount(1);
				userCompanyRepository.save(userCompany);
			}
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
			authorities.add(new SimpleGrantedAuthority(userCompany.getRole()));
			Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(),auth.getCredentials(),authorities);
			SecurityContextHolder.getContext().setAuthentication(newAuth);
			
			request.getSession().setAttribute("user",user);
			request.getSession().setAttribute("company",userCompany.getCompany());
			
			redirectAttributes.addAttribute("userCompany", userCompanyId);
			redirectAttributes.addAttribute("login", login);
			return "redirect:/default-select-company"; 
		} else {
			User user = (User) request.getSession().getAttribute("user");
			
			if(user.getRole().equals("ROLE_USER") && permissionService.getPermissionPlan().isUserCanLogin() == false) {
				return "redirect:/logout"; 
			}
			user = userRepository.findByEmail(user.getEmail());
			request.getSession().setAttribute("user",user);
		}
		
		
        if (request.isUserInRole("ROLE_ADMIN")) {
        	if( login == 1) {
        		return "redirect:/admin/admin-first-page";
        	}
            return "redirect:/admin/home";
        } else if(request.isUserInRole("ROLE_USER")) {
        	return "redirect:/user/home";
        } else if(request.isUserInRole("ROLE_SUPERVISOR")) {
        	return "redirect:/supervisor/home";
        } else if(request.isUserInRole("ROLE_SUPER_ADMIN")) {
        	return "redirect:/super-admin/home";
        } else {
        	return "redirect:/"; 
        }
    }
	
	/**
	 * change company
	 * @param principal
	 * @return
	 */
	@RequestMapping(value={"/change-company"}, method = RequestMethod.GET)
	public String changeCompony(final Principal principal, HttpServletRequest request) {
		request.getSession().setAttribute("company",null);
		return "redirect:/default"; 
	}
	
	@RequestMapping("/access-denied") 
	public String accessDenied(HttpServletRequest request) {
		return "pages-error-403";
	}
	
	@RequestMapping("/page-not-found")
	public String pageNotFound(HttpServletRequest request) {
		return "pages-error-404";
	}
	
//	@RequestMapping("/admin-signup")
//	public String adminsignUp() {
//		User user = new User();
//		user.setFirstName("admin");
//		user.setWorkEmail("parth@gmail.com");
//		user.setLastName("admin");
//		user.setEmail("parth@gmail.com");
//		user.setPassword("123456");
//		user.setRole("ROLE_ADMIN");
//		user.setPhone("8866986290");
//		userService.saveUser(user);
//		return "redirect:/";
//	}
}
