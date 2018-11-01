package com.hanmlet.myblog.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.Page;
import com.hanmlet.myblog.base.BaseController;
import com.hanmlet.myblog.common.store.MultiFileDataSource;
import com.hanmlet.myblog.dto.ArticleDTO;
import com.hanmlet.myblog.dto.BaseEntityDTO;
import com.hanmlet.myblog.dto.StoreDTO;
import com.hanmlet.myblog.dto.UserDTO;
import com.hanmlet.myblog.form.ArticleQueryForm;
import com.hanmlet.myblog.po.UserInfoPO;
import com.hanmlet.myblog.service.IArticleService;
import com.hanmlet.myblog.service.IStoreService;
import com.hanmlet.myblog.service.IUserFollowService;
import com.hanmlet.myblog.service.IUserInfoService;

import lombok.extern.slf4j.Slf4j;

/**
 * 个人信息
 * 
 * @author hanml
 *
 */
@Controller
@RequestMapping("profile")
@Slf4j
public class ProfileController extends BaseController {

	@Autowired
	private IStoreService storeService;

	@Autowired
	private IUserInfoService userInfoService;

	@Autowired
	private IArticleService articleService;

	@Autowired
	private IUserFollowService userFollowService;

	@RequiresUser
	@PostMapping("followUser")
	@ResponseBody
	public BaseEntityDTO<Integer> followUser(String followedUser) {
		Long cuser = currentUser().getUserId();
		int i = userFollowService.addFollow(followedUser, String.valueOf(cuser));
		return ajaxSuccessReturn(i);
	}

	/**
	 * 获取头像
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("/avatar/{id}")
	public ResponseEntity<byte[]> avatar(@PathVariable("id") String id, int size) {

		ResponseEntity<byte[]> response = null;
		try {
			StoreDTO dto = userInfoService.getAvatar(id, size);
			HttpHeaders headers = new HttpHeaders();
			String filename = new String(dto.getFileName().getBytes("gbk"), "iso8859-1");
			headers.setContentType(MediaType.IMAGE_JPEG);
			headers.add("Content-Disposition", "attachment;filename=" + filename);
			response = new ResponseEntity<byte[]>(FileCopyUtils.copyToByteArray(dto.getDataSource().getInputStream()),
					headers, HttpStatus.OK);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

	@RequiresAuthentication
	@PostMapping("/avatar/upload")
	@ResponseBody
	public Map<String, String> upload(MultipartFile avatar) {
		Map<String, String> result = new HashMap<>();
		try {
			StoreDTO dto = storeService.save(new MultiFileDataSource(avatar));
			result.put("key", dto.getKey());
			UserInfoPO po = new UserInfoPO();
			po.setUserId(this.currentUser().getUserId());
			po.setAvatar(dto.getKey());
			userInfoService.updateUser(po);
			userInfoService.refreshAvatar(String.valueOf(this.currentUser().getUserId()));
		} catch (Exception e) {
			log.error("上传头像失败", e);
			result.put("key", "");
		}
		return result;
	}

	@RequiresAuthentication
	@GetMapping("/setting")
	public String setting(Model model) {
		long userId = currentUser().getUserId();
		UserInfoPO info = userInfoService.findByUserId(userId);
		model.addAttribute("user", info);
		return "setting";
	}

	@RequiresUser
	@PostMapping("update")
	@ResponseBody
	public BaseEntityDTO<Integer> saveUserInfo(UserInfoPO po) {
		Long userId = currentUser().getUserId();
		po.setUserId(userId);
		int i = userInfoService.updateUser(po);
		return ajaxSuccessReturn(i);
	}

	@GetMapping("/users/{id}")
	public String init(@PathVariable("id") String id, Model model) {

		// 判断当前访问的是否为自己的页面
		UserDTO userDto = this.currentUser();
		if (userDto == null) {
			model.addAttribute("isSelf", false);
			model.addAttribute("isFollowed", false);
		} else {
			Long cuserId = userDto.getUserId();
			if (id.equals(String.valueOf(cuserId))) {
				model.addAttribute("isSelf", true);
			} else {
				model.addAttribute("isSelf", false);
			}
			boolean isF = userFollowService.isFollowerOfUser(id, String.valueOf(cuserId));
			model.addAttribute("isFollowed", isF);
		}

		// 是否已关注

		// 查询该用户的文章
		ArticleQueryForm queryForm = new ArticleQueryForm();
		queryForm.setAuthor(id);
		Page<ArticleDTO> articleList = articleService.listPage(queryForm);
		model.addAttribute("articlePage", articleList);
		// 查询粉丝数量
		int followers = userFollowService.countByUserId(id);
		// 查询关注数量
		int following = userFollowService.countByFollowerId(id);
		// 文章数量
		int articleNum = articleService.countByAuthor(id);

		model.addAttribute("articleNum", articleNum);
		model.addAttribute("followers", followers);
		model.addAttribute("following", following);

		// 查询该用户信息
		UserInfoPO po = userInfoService.findByUserId(id);
		model.addAttribute("user", po);
		return "profile";
	}
}
