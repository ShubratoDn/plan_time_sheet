package com.aim.service.email;

import java.io.File;

import com.aim.entity.Company;
import com.aim.entity.User;
import com.aim.entity.UserCompany;

public interface EmailSenderService {

	void sendSuccessEmailWithPasswordLink(String email,String name,String key, String rootUrl);

	void sendForgotPasswordEmail(User userExists, String verifyEmailLink, String rootUrl);

	void sendMailToAdminUserSingup(String email, String string);

	void sendEmailToUserWithCC(String message, String email, String subject, String ccEmail, File file, String fromEmail);

	void sendCompanyAddEmail(UserCompany user, String verifyEmailLink, String tIMESHEET_SERVER_URL);

	void sendAddCompanyMailToSuperAdmin(UserCompany user1,User user);

	void sendMailToSuperAdminForCompanyActive(Company company,User user, String verifyEmailLink, String tIMESHEET_SERVER_URL);

	void sendUserInformCompanyIsActive(Company company, User user, String verifyEmailLink, String tIMESHEET_SERVER_URL);

}
