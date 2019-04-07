package com.bravson.socialalert.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.bravson.socialalert.business.user.UserAccess;

public abstract class BaseFacade {
	
	@Autowired
	HttpServletRequest servletRequest;
	
	private static final String[] IP_HEADER_CANDIDATES = { 
			"X-Forwarded-For",
			"Proxy-Client-IP",
			"WL-Proxy-Client-IP",
			"HTTP_X_FORWARDED_FOR",
			"HTTP_X_FORWARDED",
			"HTTP_X_CLUSTER_CLIENT_IP",
			"HTTP_CLIENT_IP",
			"HTTP_FORWARDED_FOR",
			"HTTP_FORWARDED",
			"HTTP_VIA",
			"REMOTE_ADDR" };

	private String getClientIpAddress() {
		for (String header : IP_HEADER_CANDIDATES) {
			String ip = servletRequest.getHeader(header);
			if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
				return ip;
			}
		}
		return servletRequest.getRemoteAddr();
	}

	protected final UserAccess getUserAccess() {
		//return UserAccess.of(servletRequest.getRemoteUser(), getClientIpAddress());
		return UserAccess.of("test", getClientIpAddress());
	}
}
