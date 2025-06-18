package com.aim.service;

import com.aim.model.InvalidReCaptchaException;
import com.aim.model.ReCaptchaInvalidException;

public interface CaptchaService {

	void processResponse(String response) throws InvalidReCaptchaException, ReCaptchaInvalidException;

}
