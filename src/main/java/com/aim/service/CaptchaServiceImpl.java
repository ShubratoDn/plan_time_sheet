package com.aim.service;

import java.net.URI;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;

import com.aim.config.CaptchaSettings;
import com.aim.response.GoogleResponse;
import com.aim.model.InvalidReCaptchaException;
import com.aim.model.ReCaptchaInvalidException;

@Service
public class CaptchaServiceImpl implements CaptchaService{

	@Autowired
    private CaptchaSettings captchaSettings;

    @Autowired
    private RestOperations restTemplate;

    private static Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

    @Override
    public void processResponse(String response)throws InvalidReCaptchaException,ReCaptchaInvalidException{
    	if(StringUtils.isEmpty(response)) {
    		throw new InvalidReCaptchaException("Please check captcha");
    	}
        if(!responseSanityCheck(response)) {
            throw new InvalidReCaptchaException("Invalid captcha,Please try again");
        }

        URI verifyUri = URI.create(String.format(
          "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s",
          captchaSettings.getSecret(), response, captchaSettings.getSite()));

        GoogleResponse googleResponse = restTemplate.getForObject(verifyUri, GoogleResponse.class);

        if(!googleResponse.isSuccess()) {
            throw new ReCaptchaInvalidException("reCaptcha was not successfully validated");
        }
    }

    private boolean responseSanityCheck(String response) {
        return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
    }
}
