package com.zct.quartz.springboot.controller.interceptor;

import com.zct.quartz.springboot.core.util.CookieUtil;
import com.zct.quartz.springboot.core.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.zct.quartz.springboot.controller.annotation.PermessionLimit;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;

/**
 * 权限拦截, 简易版
 * @author zct 2015-12-12 18:09:04
 */
@Component
public class PermissionInterceptor extends HandlerInterceptorAdapter {
	private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
	public static final String LOGIN_IDENTITY_KEY = "LOGIN_IDENTITY";
	public static String LOGIN_IDENTITY_TOKEN;

	@Value("${xxl.job.login.username}")
	private String username;

	@Value("${xxl.job.login.password}")
	private String password;


	@PostConstruct
	public void before(){
		String temp = username + "_" + password;
		LOGIN_IDENTITY_TOKEN = new BigInteger(1, temp.getBytes()).toString(16);
	}


	static {
	}

	public static boolean login(HttpServletResponse response, boolean ifRemember){
		CookieUtil.set(response, LOGIN_IDENTITY_KEY, LOGIN_IDENTITY_TOKEN, ifRemember);
		return true;
	}
	public static void logout(HttpServletRequest request, HttpServletResponse response){
		CookieUtil.remove(request, response, LOGIN_IDENTITY_KEY);
	}
	public static boolean ifLogin(HttpServletRequest request){
		String indentityInfo = CookieUtil.getValue(request, LOGIN_IDENTITY_KEY);
		if (indentityInfo==null || !LOGIN_IDENTITY_TOKEN.equals(indentityInfo.trim())) {
			return false;
		}
		return true;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		if (!(handler instanceof HandlerMethod)) {
			return super.preHandle(request, response, handler);
		}

		if (!ifLogin(request)) {
			HandlerMethod method = (HandlerMethod)handler;
			PermessionLimit permission = method.getMethodAnnotation(PermessionLimit.class);
			if (permission == null || permission.limit()) {
				logger.info("Interceptor：跳转到login页面！");
				response.sendRedirect(request.getContextPath() + "/toLogin");
				//request.getRequestDispatcher("/toLogin").forward(request, response);
				return false;
			}
		}

		return super.preHandle(request, response, handler);
	}

}
