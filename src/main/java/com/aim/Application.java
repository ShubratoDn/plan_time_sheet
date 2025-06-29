package com.aim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Autowired;
import com.aim.entity.User;
import com.aim.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.UUID;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(appClass);
	}

	private static Class<Application> appClass = Application.class;

	@Bean
	public CommandLineRunner createDefaultSuperAdmin(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
		return args -> {
			String email = "managetsadm@gmail.com";
			String password = "Fiverr2025$";
			User existing = userRepository.findByEmail(email);
			if (existing == null) {
				User user = new User();
				user.setEmail(email);
				user.setWorkEmail(email);
				user.setPassword(passwordEncoder.encode(password));
				user.setFirstName("Super");
				user.setLastName("Admin");
				user.setPhone("0000000000");
				user.setActive(1);
				user.setRole("ROLE_SUPER_ADMIN");
				user.setUuid(UUID.randomUUID().toString());
				userRepository.save(user);
			} else if (existing.getActive() != 1) {
				existing.setActive(1);
				userRepository.save(existing);
			}
		};
	}
}