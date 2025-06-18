package com.aim.controller;

import java.io.File;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aim.entity.Company;
import com.aim.entity.PermissionPlan;
import com.aim.entity.User;
import com.aim.entity.UserCompany;
import com.aim.model.ResponseGenerator;
import com.aim.repository.CompanyRepository;
import com.aim.repository.PermissionPlanRepository;
import com.aim.repository.UserCompanyRepository;
import com.aim.request.CompanyRequest;
import com.aim.service.CompanyService;
import com.aim.utils.Response;

@Controller
@RequestMapping("/super-admin/")
public class SuperAdminController {

	@Autowired
	private CompanyRepository companyRepository;

	@Autowired
	private CompanyService companyService;

	@Autowired
	private UserCompanyRepository userCompanyRepository;

	@Autowired
	private PermissionPlanRepository permissionPlanRepository;

	@Value("${file.path}")
	private String FILE_PATH;

	@RequestMapping(value = "home", method = RequestMethod.GET)
	public String home(ModelMap modelMap) {

		return "new/superAdmin/home";
	}

	/**
	 * add Company
	 * 
	 * @param modelAttribute
	 * @return
	 */
	@RequestMapping(value = "add-company", method = RequestMethod.GET)
	public String addCompany(ModelMap modelMap) {

		CompanyRequest companyRequest = new CompanyRequest();
		User admin = new User();
		companyRequest.setAdmin(admin);
		modelMap.addAttribute("company", companyRequest);
		return "new/superAdmin/addCompany";
	}

	/**
	 * add Company
	 * 
	 * @param companyRequest
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value = "add-company", method = RequestMethod.POST)
	public String addCompany(@Valid CompanyRequest companyRequest, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, ModelMap modelMap) {

		Company company = companyRepository.findByName(companyRequest.getName());
		if (company != null) {
			redirectAttributes.addFlashAttribute("nameError",
					"There is already a company registered with the name provided");
			redirectAttributes.addFlashAttribute("company", companyRequest);

			return "redirect:/super-admin/add-company";
		}

		companyService.saveCompanyAndAdminUser(companyRequest);

		redirectAttributes.addFlashAttribute("success", "Company Details has been save successfully");
		return "redirect:/super-admin/add-company";
	}

	/**
	 * add Company
	 * 
	 * @param modelAttribute
	 * @return
	 */
	@RequestMapping(value = "company-list", method = RequestMethod.GET)
	public String getCompany(ModelMap modelMap) {

		Iterable<Company> companyList = companyRepository.findAll();
		modelMap.addAttribute("companyList", companyList);

		return "new/superAdmin/listCompany";
	}

	/**
	 * active Company
	 * 
	 * @param modelAttribute
	 * @return
	 */
	@RequestMapping(value = "company-active/{companyId}", method = RequestMethod.GET)
	public String getActiveCompany(@PathVariable("companyId") Integer companyId, ModelMap modelMap) {

		companyService.active(companyId, true);
		return "redirect:/super-admin/company-list";
	}

	/**
	 * de-active Company
	 * 
	 * @param modelAttribute
	 * @return
	 */
	@RequestMapping(value = "company-deactive/{companyId}", method = RequestMethod.GET)
	public String getDeactiveCompany(@PathVariable("companyId") Integer companyId, ModelMap modelMap) {

		companyService.active(companyId, false);
		return "redirect:/super-admin/company-list";
	}

	/**
	 * de-active Company
	 * 
	 * @param modelAttribute
	 * @return
	 */
	@RequestMapping(value = "company-detail/{companyId}", method = RequestMethod.GET)
	public String getDetailsCompany(@PathVariable("companyId") Integer companyId, ModelMap modelMap) {

		Company company = companyRepository.findById(companyId);
		if (company == null) {
			modelMap.addAttribute("company", company);
			return "new/superAdmin/companyDetail";
		}
		modelMap.addAttribute("company", company);
		List<UserCompany> userCompany = userCompanyRepository.findByCompany(company);
		modelMap.addAttribute("userCompanyList", userCompany);

		boolean folderExists = false;
		if (new File(FILE_PATH + company.getFileFolder()).exists())
			folderExists = true;

		modelMap.addAttribute("folderExists", folderExists);

		PermissionPlan permissionPlan = permissionPlanRepository.findByCompany(company);
		if (permissionPlan == null) {
			permissionPlan = new PermissionPlan();
			permissionPlan.setUserLimit(25);
			permissionPlan.setCompany(company);
			permissionPlanRepository.save(permissionPlan);
		}
		modelMap.addAttribute("permissionPlan", permissionPlan);
		return "new/superAdmin/companyDetail";
	}

	/**
	 * add Company
	 * 
	 * @param modelAttribute
	 * @return
	 */
	@RequestMapping(value = "edit-company", method = RequestMethod.POST)
	public String getEditCompany(Company company, ModelMap modelMap, RedirectAttributes redirectAttributes) {
		Company company1 = companyRepository.findByName(company.getName());
		if (company1 != null && company1.getId() != company.getId()) {
			redirectAttributes.addFlashAttribute("nameError",
					"There is already a company registered with the name provided");
			return "redirect:/super-admin/company-detail/" + company.getId();

		}
		Company company3 = companyRepository.findByFileFolder(company.getFileFolder());
		if (company3 != null && company3.getId() != company.getId()) {
			redirectAttributes.addFlashAttribute("fileNameError",
					"There is already a company with the file folder name provided");
			return "redirect:/super-admin/company-detail/" + company.getId();

		}
		companyService.edit(company);
		redirectAttributes.addFlashAttribute("success", "Company Details has been edit successfully");
		return "redirect:/super-admin/company-detail/" + company.getId();
	}

	/**
	 * 
	 * @param companyId
	 * @return
	 */
	@RequestMapping(value = "resend-varify-email-link/{companyId}", method = RequestMethod.GET)
	public ResponseEntity<Response> resendCompanyEmailVarify(@PathVariable("companyId") Integer companyId) {

		return companyService.resendCompanyEmailVarify(companyId);
	}

	/**
	 * 
	 * @param companyId
	 * @param permissionPlan
	 * @return
	 */
	@RequestMapping(value = "access-plan/{companyId}", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Response> accessPlan(@PathVariable("companyId") Integer companyId,
			PermissionPlan permissionPlan) {

		try {
			Company company = companyRepository.findById(companyId);
			if (company == null) {
				return ResponseGenerator.generateResponse(new Response("Please try again", null),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}

			companyService.changePermissionPlan(permissionPlan, company);
		} catch (Exception e) {
			return ResponseGenerator.generateResponse(new Response("Please try again", null),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return ResponseGenerator.generateResponse(new Response("successfully", null), HttpStatus.OK);
	}

	
	 @RequestMapping(value = "company-db-create/{companyId}", method = RequestMethod.GET)
		public String setDbCompany(@PathVariable("companyId") Integer companyId, ModelMap modelMap) {

			UserCompany userCompany = (UserCompany) companyService.getUserCompaniesByCompanyIdAndRole(companyId,"ROLE_ADMIN").get(0);
			
			companyService.varify(userCompany.getCompany().getUuid(),userCompany.getUser().getUuid());
			companyService.active(companyId,true);
			companyService.sendUserInformCompanyIsActive(userCompany.getCompany());
			return "redirect:/super-admin/company-detail/" + companyId;
		}
	 

}
