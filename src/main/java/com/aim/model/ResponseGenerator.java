package com.aim.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.aim.utils.Response;

@Component
public class ResponseGenerator {

	public static ResponseEntity<Response> generateResponse(Response response, HttpStatus status) {
		return new ResponseEntity<Response>(response, status);
	}

}

