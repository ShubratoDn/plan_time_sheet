package com.aim.utils;

import java.io.Serializable;

public class Response implements Serializable {
	
	private static final long serialVersionUID = 3927656989682261161L;
	
	private String message;
	private Object data;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public Response() {
		super();
	}
	public Response(String message, Object data) {
		this.message = message;
		this.data = data;
	}
	
	public Response(String message) {
		this.message = message;
	}
}
