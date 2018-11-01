package com.hanmlet.myblog.shiro.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import com.hanmlet.myblog.AppConstants;

public class CustomFormAuthenticationFilter extends FormAuthenticationFilter {

	@Override
	protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
			ServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		HttpServletRequest hsr = (HttpServletRequest) request;
		if (token.getPrincipal() != null) {
			Object p = subject.getPrincipal();
			hsr.getSession().setAttribute(AppConstants.SESSION_USER, p);
		}
		return super.onLoginSuccess(token, subject, request, response);
	}

}
