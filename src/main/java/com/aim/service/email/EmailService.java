package com.aim.service.email;

import java.io.File;


public interface EmailService {

	public void sendHTMLMail(String from, String to, String subject, String msg, String ccEmail);

	void sendHTMLMailWithFile(String from, String to, String subject,String msg, File file, String ccEmail);

}