package com.hanmlet.myblog.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.pagehelper.Page;
import com.hanmlet.myblog.dto.ArticleDTO;
import com.hanmlet.myblog.form.ArticleQueryForm;
import com.hanmlet.myblog.form.RegisterForm;
import com.hanmlet.myblog.service.IArticleService;
import com.hanmlet.myblog.service.IUserInfoService;

@Controller
public class IndexController {

	@Autowired
	private IUserInfoService userInfoService;
	@Autowired
	private IArticleService articleService;

	@RequestMapping("/")
	public String home(HttpSession session, Model model) {

		ArticleQueryForm queryForm = new ArticleQueryForm();
		Page<ArticleDTO> page = articleService.listPage(queryForm);
		model.addAttribute("articlePage", page);
		return "index";
	}

	@RequestMapping("search")
	public String search(ArticleQueryForm queryForm, Model model) {
		Page<ArticleDTO> page = articleService.listPage(queryForm);
		model.addAttribute("articlePage", page);
		model.addAttribute("searchKey", queryForm.getSearchKey());
		return "home";
	}

	@RequestMapping("register")
	public String register() {
		return "register";
	}

	@RequestMapping("403")
	public String page403() {
		return "403";
	}

	@RequestMapping("login")
	public String login(HttpServletRequest req, Model model) {

		// 登出
		SecurityUtils.getSubject().logout();

		String exceptionClassName = (String) req.getAttribute("shiroLoginFailure");
		String error = null;
		if (UnknownAccountException.class.getName().equals(exceptionClassName)) {
			error = "用户名/密码错误";
		} else if (IncorrectCredentialsException.class.getName().equals(exceptionClassName)) {
			error = "用户名/密码错误";
		} else if (exceptionClassName != null) {
			error = "其他错误：" + exceptionClassName;
		}
		model.addAttribute("error", error);
		return "login";
	}

	@PostMapping("register")
	public String registerForm(RegisterForm form) {
		// 密码加密
		userInfoService.addUser(form);
		return "redirect:login";
	}

}
