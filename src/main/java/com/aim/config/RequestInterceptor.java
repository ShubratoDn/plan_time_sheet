package com.aim.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.aim.entity.Company;
import com.aim.entity.User;
import com.aim.repository.CompanyRepository;
//https://medium.com/swlh/multi-tenancy-implementation-using-spring-boot-hibernate-6a8e3ecb251a
@Component
public class RequestInterceptor extends HandlerInterceptorAdapter {
	
	@Autowired HttpSession session;
	
	@Autowired
    private CompanyRepository companyRepository;
	
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object object) throws Exception {
    	
    	User user = (User) request.getSession().getAttribute("user");
    	Company company1 = (Company) request.getSession().getAttribute("company");
//    	String tenants = (String) session.getAttribute("tenants");
    	String tenants = null;
    	
    	if(user != null && company1 != null && !user.getRole().equals("ROLE_SUPER_ADMIN")) {
    		
	    	TenantContext.setCurrentTenant(null);
	        Company company = companyRepository.findByUrlSlug(company1.getUrlSlug());
	        TenantContext.clear();
	        
	    	if(company == null) {
	    		response.setStatus(400);
	    		return false;
	    	}
	    	
	    	tenants = company.getDbName();
    	}
    	
        TenantContext.setCurrentTenant(tenants);
        return true;
    }

    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
        TenantContext.clear();
    }

}