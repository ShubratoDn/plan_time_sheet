package com.aim.model;

public class ReCaptchaInvalidException extends Exception {

	public ReCaptchaInvalidException() {

	}

	public ReCaptchaInvalidException(String message) {
	    super(message);
	}
	
	public ReCaptchaInvalidException(String message, Throwable cause) {
	   super(message, cause);
	}

}
