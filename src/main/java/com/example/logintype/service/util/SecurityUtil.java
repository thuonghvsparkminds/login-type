package com.example.logintype.service.util;

import com.example.logintype.constant.Constants;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
public class SecurityUtil {

	private static HttpServletRequest getRequest() {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		return attributes != null ? attributes.getRequest() : null;
	}

	public static Long getCurrentUserId() {
		HttpServletRequest currentRequestContext = getRequest();
		return (Long) currentRequestContext.getAttribute(Constants.HEADER_USER_ID);
	}
}
