package com.aim.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@PropertySource(value="classpath:mail.properties")
public class MailPropertyConfig {

	public static String FROMEMAIL;
	public static String HOST;
	public static int PORT;
	public static String USERNAME;
	public static String PASSWORD;
	public static String AUTH;

	@Value("${mail.config.smtp.auth}")
	public void setAUTH(String aUTH) {
		AUTH = aUTH;
	}

	@Value("${mail.config.smtp.password}")
	public void setPASSWORD(String pASSWORD) {
		PASSWORD = pASSWORD;
	}

	@Value("${mail.config.smtp.username}")
	public void setUSERNAME(String uSERNAME) {
		USERNAME = uSERNAME;
	}

	@Value("${mail.config.smtp.port}")
	public void setPORT(int pORT) {
		PORT = pORT;
	}

	@Value("${mail.config.smtp.host}")
	public void setHOST(String hOST) {
		HOST = hOST;
	}

	@Value("${mail.config.fromemail}")
	public void setFROMEMAIL(String fROMEMAIL) {
		FROMEMAIL = fROMEMAIL;
	}

	@Bean
	public JavaMailSenderImpl javaMailSenderImpl(){
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(MailPropertyConfig.HOST);
		mailSender.setPort(MailPropertyConfig.PORT);
		mailSender.setUsername(MailPropertyConfig.USERNAME);
		mailSender.setPassword(MailPropertyConfig.PASSWORD);
		Properties prop = mailSender.getJavaMailProperties();
		prop.put("mail.smtp.auth", MailPropertyConfig.AUTH);
		prop.put("mail.smtp.ssl.enable", "true");
		return mailSender;
	}
	
}
