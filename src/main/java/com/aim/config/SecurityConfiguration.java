package com.aim.config;

import com.aim.config.security.CustomUserDetailsServiceImpl;
import com.aim.config.security.CustomAuthenticationFailureHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	@Autowired
	private DataSource dataSource;

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/", "/registration", "/login", "/company/active", "/admin-signup", "/change-pass/**", "/active", "/active/new-company", "/forgot-password/**", "/file/**", "/resources/**", "/static/**", "/css/**", "/js/**", "/scss/**", "/images/**", "/assets/**", "/assets_new/**").permitAll()
				.requestMatchers("/user/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPERVISOR", "ROLE_USER")
				.requestMatchers("/admin/mail/**", "/admin/activity/**", "/admin/add-user/**", "/admin/user-list/**", "/admin/internal-user/**", "/admin/edit/**", "/admin/deactive/**", "/admin/active/**", "/admin/template/**", "/admin/user-detail/**", "/admin/new-client-add/**", "/admin/get-client/**", "/admin/get-employer/**", "/admin/get-vendor/**", "/admin/set-internal-user/**", "/admin/add-user-detail/**", "/admin/basic-detail/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPERVISOR")
				.requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
				.requestMatchers("/super-admin/**").hasAuthority("ROLE_SUPER_ADMIN")
				.requestMatchers("/supervisor/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPERVISOR")
				.anyRequest().authenticated()
			)
			.formLogin(form -> form
				.loginPage("/login").failureHandler(new CustomAuthenticationFailureHandler())
				.defaultSuccessUrl("/default", true)
				.usernameParameter("email")
				.passwordParameter("password")
				.permitAll()
			)
			.logout(logout -> logout
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.logoutSuccessUrl("/login")
				.permitAll()
			)
			.exceptionHandling(ex -> ex
				.accessDeniedPage("/access-denied")
			);

		http.authenticationProvider(authenticationProvider());
		return http.build();
	}

//	@Bean
//	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//		return authenticationConfiguration.getAuthenticationManager();
//	}
//
//	@Bean
//	public UserDetailsService userDetailsService(CustomUserDetailsServiceImpl customUserDetailsService) {
//		return customUserDetailsService;
//	}

	@Autowired
	private CustomUserDetailsServiceImpl userDetailsServiceImple;


	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(userDetailsServiceImple);
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		daoAuthenticationProvider.setHideUserNotFoundExceptions(false);

		return daoAuthenticationProvider;
	}


	// Configure and return authentication manager
	// Declare this Bean, it will needed for JWT Authentication while Login. Check Login auth controller
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}