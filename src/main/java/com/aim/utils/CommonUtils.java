package com.aim.utils;


import javax.servlet.http.HttpServletRequest;

public class CommonUtils {
	
	/**
	 * Get Root url
	 * 
	 * @param request
	 * @return
	 */
	public static String getRootURL(HttpServletRequest request) {
		String protocol = request.getScheme();
		int port = request.getServerPort();
		String host = request.getServerName();
		String rootURL = protocol+"://"+host+":"+port;
		return rootURL;
	}
}
