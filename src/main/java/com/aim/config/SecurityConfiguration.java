package com.aim.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private DataSource dataSource;
	
	@Value("${spring.queries.users-query}")
	private String usersQuery;
	
	@Value("${spring.queries.roles-query}")
	private String rolesQuery;

	@Override
	protected void configure(AuthenticationManagerBuilder auth)
			throws Exception {
		auth.
			jdbcAuthentication()
				.usersByUsernameQuery(usersQuery)
				.authoritiesByUsernameQuery(rolesQuery)
				.dataSource(dataSource)
				.passwordEncoder(bCryptPasswordEncoder);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.
			authorizeRequests()
				.antMatchers("/").permitAll()
				.antMatchers("/registration").permitAll()
				.antMatchers("/login").permitAll()
				.antMatchers("/company/active").permitAll()
				.antMatchers("/admin-signup").permitAll()
				.antMatchers("/user/**").hasAnyAuthority("ROLE_ADMIN","ROLE_SUPERVISOR","ROLE_USER")
				.antMatchers("/admin/mail/**").hasAnyAuthority("ROLE_ADMIN","ROLE_SUPERVISOR")
				.antMatchers("/admin/activity/**").hasAnyAuthority("ROLE_ADMIN","ROLE_SUPERVISOR")
				.antMatchers("/admin/add-user/**").hasAnyAuthority("ROLE_ADMIN","ROLE_SUPERVISOR")
				.antMatchers("/admin/user-list/**").hasAnyAuthority("ROLE_ADMIN","ROLE_SUPERVISOR")
				.antMatchers("/admin/internal-user/**").hasAnyAuthority("ROLE_ADMIN","ROLE_SUPERVISOR")
				.antMatchers("/admin/edit/**").hasAnyAuthority("ROLE_ADMIN","ROLE_SUPERVISOR")
				.antMatchers("/admin/deactive/**").hasAnyAuthority("ROLE_ADMIN","ROLE_SUPERVISOR")
				.antMatchers("/admin/active/**").hasAnyAuthority("ROLE_ADMIN","ROLE_SUPERVISOR")
				.antMatchers("/admin/template/**").hasAnyAuthority("ROLE_ADMIN","ROLE_SUPERVISOR")
				.antMatchers("/admin/user-detail/**").hasAnyAuthority("ROLE_ADMIN","ROLE_SUPERVISOR")
				.antMatchers("/admin/new-client-add/**").hasAnyAuthority("ROLE_ADMIN","ROLE_SUPERVISOR")
				.antMatchers("/admin/get-client/**").hasAnyAuthority("ROLE_ADMIN","ROLE_SUPERVISOR")
				.antMatchers("/admin/get-employer/**").hasAnyAuthority("ROLE_ADMIN","ROLE_SUPERVISOR")
				.antMatchers("/admin/get-vendor/**").hasAnyAuthority("ROLE_ADMIN","ROLE_SUPERVISOR")
				.antMatchers("/admin/set-internal-user/**").hasAnyAuthority("ROLE_ADMIN","ROLE_SUPERVISOR")
				.antMatchers("/admin/add-user-detail/**").hasAnyAuthority("ROLE_ADMIN","ROLE_SUPERVISOR")
				.antMatchers("/admin/basic-detail/**").hasAnyAuthority("ROLE_ADMIN","ROLE_SUPERVISOR")
				.antMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
				.antMatchers("/super-admin/**").hasAuthority("ROLE_SUPER_ADMIN")
				.antMatchers("/supervisor/**").hasAnyAuthority("ROLE_ADMIN","ROLE_SUPERVISOR")
				.antMatchers("/change-pass/**").permitAll()
				.antMatchers("/active").permitAll()
				.antMatchers("/active/new-company").permitAll()
				.antMatchers("/forgot-password/**").permitAll()
				.antMatchers("/file/**").permitAll()
				.anyRequest()
				.authenticated().and().csrf().disable().formLogin()
				.loginPage("/login").failureUrl("/login?error=true")
				.defaultSuccessUrl("/default",true)
				.usernameParameter("email")
				.passwordParameter("password")
				.and().logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.logoutSuccessUrl("/login").and().exceptionHandling()
				.accessDeniedPage("/access-denied");
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
	    web
	       .ignoring()
	       .antMatchers("/resources/**", "/static/**", "/css/**", "/js/**","/scss/**", "/images/**", "/assets/**", "/assets_new/**");
	}

}