package com.hanmlet.myblog.common;

import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalControllerAdvice {
	private static final String DEFAULT_LOGIN = "redirect:/login";
	private static final String DEFAULT_UNAUTH = "403";
	private static final String DEFAULT_500 = "500";

	@ExceptionHandler(Exception.class)
	public ModelAndView handleException(Exception e) {
		log.error("异常", e);
		ModelAndView mv = new ModelAndView();
		mv.setViewName(DEFAULT_500);
		return mv;
	}

	/**
	 * 未授权的异常
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(UnauthorizedException.class)
	public ModelAndView handleUnauthorizedException(UnauthorizedException e) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(DEFAULT_UNAUTH);
		return mv;
	}

	/**
	 * 未认证的异常
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(UnauthenticatedException.class)
	public ModelAndView handleShiroException(UnauthenticatedException e) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(DEFAULT_LOGIN);
		return mv;
	}
}
