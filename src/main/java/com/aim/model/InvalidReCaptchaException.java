package com.aim.model;

public class InvalidReCaptchaException extends Exception {

	public InvalidReCaptchaException() {

	}

	public InvalidReCaptchaException(String message) {
	    super(message);
	}
	
	public InvalidReCaptchaException(String message, Throwable cause) {
	   super(message, cause);
	}

}
