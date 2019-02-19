package com.hanmlet.myblog.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.hanmlet.myblog.po.ArticleRecomment;
import com.hanmlet.myblog.po.BannerInfo;
import com.hanmlet.myblog.po.SloganInfo;
import com.hanmlet.myblog.service.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.pagehelper.Page;
import com.hanmlet.myblog.dto.ArticleDTO;
import com.hanmlet.myblog.form.ArticleQueryForm;
import com.hanmlet.myblog.form.RegisterForm;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class IndexController {

	@Autowired
	private IUserInfoService userInfoService;
	@Autowired
	private IArticleService articleService;
	@Autowired
	private IArticleRecommentService articleRecommentService;

	@Autowired
	private ISloganInfoService sloganInfoService;

	@Autowired
	private IBannerInfoService bannerInfoService;

	@RequestMapping("/")
	public String home(HttpSession session, Model model) {

		ArticleQueryForm queryForm = new ArticleQueryForm();
		Page<ArticleDTO> page = articleService.listPage(queryForm);
		model.addAttribute("articlePage", page);

		List<ArticleRecomment> recomments = articleRecommentService.queryAtricaleOfReco();
		model.addAttribute("recomments", recomments);

		List<BannerInfo> bannerInfos = bannerInfoService.queryAllBanners();
		model.addAttribute("banners", bannerInfos);
		return "index";
	}

	@RequestMapping("search")
	public String search(ArticleQueryForm queryForm, Model model) {
		Page<ArticleDTO> page = articleService.listPage(queryForm);
		model.addAttribute("articlePage", page);
		model.addAttribute("searchKey", queryForm.getSearchKey());
		return "home";
	}

	@GetMapping("todaySlogan")
	@ResponseBody
	public SloganInfo selectTodaySloganInfo(){
		 return sloganInfoService.selectTodaySlogan();
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
