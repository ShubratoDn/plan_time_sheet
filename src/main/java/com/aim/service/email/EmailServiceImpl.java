package com.aim.service.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {

	@Autowired
	private JavaMailSender mailSender;
	
	final static Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

	@Override
	public void sendHTMLMail(String from, String to, String subject, String msg, String ccEmail)  {
		this.sendHTMLMail(from, to, ccEmail, null, subject, msg,null);
	}

	public void sendHTMLMail(String from, String to, String cc, String bcc, String subject, String messageText,File file) {
		
		try{
			// Create a default MimeMessage object.
			MimeMessage mimemessage = mailSender.createMimeMessage();// MimeMessage(session);
			MimeMessageHelper message = new MimeMessageHelper(mimemessage, true);
			message.setFrom(from);
			
			List<String> myToList = new ArrayList<String>(Arrays.asList(to.split(",")));
			myToList.forEach(add-> {
				try {
					message.addTo(add);
				} catch (MessagingException e) {
					
					e.printStackTrace();
				}
			});
			
			if(cc != null && cc!= "") {
				List<String> myList = new ArrayList<String>(Arrays.asList(cc.split(",")));
				myList.forEach(add-> {
					try {
						message.addCc(add);
					} catch (MessagingException e) {
						
						e.printStackTrace();
					}
				});
			}
			message.setBcc(from);
			
			if(file != null)
				message.addAttachment(file.getName(), file);
			
			message.setSubject(subject);
			message.setText(messageText, true);
			message.setReplyTo(from);
			
			mailSender.send(mimemessage);
			logger.info("Email sent to : " + to);
			logger.info("Email sent from : " + from);
		} catch(Exception e) {
			logger.error("Error occured while send email : " + e);
		}
	}
	
	@Override
	public void sendHTMLMailWithFile(String from, String to, String subject, String msg,File file, String ccEmail)  {
		this.sendHTMLMail(from, to, ccEmail, null, subject, msg,file);
	}
	
}
