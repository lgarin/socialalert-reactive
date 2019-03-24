package com.bravson.socialalert.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.WebRequest;

import com.bravson.socialalert.business.user.UserAccess;

@Component
@Scope(scopeName = WebApplicationContext.SCOPE_REQUEST)
public class UserAccessFactory {

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

	@Autowired
	WebRequest webRequest;

	@Autowired
	HttpServletRequest servletRequest;

	public String getClientIpAddress() {
		for (String header : IP_HEADER_CANDIDATES) {
			String ip = webRequest.getHeader(header);
			if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
				return ip;
			}
		}
		return servletRequest.getRemoteAddr();
	}

	@Bean
	public UserAccess buildUserAccess() {
		return UserAccess.of("test", getClientIpAddress());
	}

}
