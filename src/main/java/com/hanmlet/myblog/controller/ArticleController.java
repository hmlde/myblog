package com.hanmlet.myblog.controller;

import java.util.List;

import com.hanmlet.myblog.dto.ArticleDTO;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hanmlet.myblog.base.BaseController;
import com.hanmlet.myblog.common.util.DateUtil;
import com.hanmlet.myblog.dto.BaseEntityDTO;
import com.hanmlet.myblog.dto.CommentDTO;
import com.hanmlet.myblog.dto.UserDTO;
import com.hanmlet.myblog.form.CommentQueryForm;
import com.hanmlet.myblog.po.ArticlePO;
import com.hanmlet.myblog.po.CommentPO;
import com.hanmlet.myblog.po.UserInfoPO;
import com.hanmlet.myblog.service.IArticleService;
import com.hanmlet.myblog.service.ICommentService;
import com.hanmlet.myblog.service.IUserFollowService;
import com.hanmlet.myblog.service.IUserInfoService;

/**
 * 文章管理
 * 
 * @author hanml
 *
 */
@RequestMapping("article")
@Controller
public class ArticleController extends BaseController {

	@Autowired
	private IArticleService articleService;

	@Autowired
	private IUserInfoService userInfoService;

	@Autowired
	private IUserFollowService userFollowService;

	@Autowired
	private ICommentService commentService;

	@RequiresAuthentication
	@RequestMapping("edit")
	public String edit() {
		return "articleEdit";
	}

	@RequiresUser
	@RequestMapping("addComment")
	@ResponseBody()
	public BaseEntityDTO<Integer> addComment(CommentPO comment) {
		UserDTO dto = this.currentUser();
		comment.setCreator(String.valueOf(dto.getUserId()));
		comment.setCreateDate(DateUtil.getCurrentLocalDateTimeString());
		int i = commentService.addComment(comment);
		return this.ajaxSuccessReturn(i);
	}

	@RequestMapping("{id}/comment")
	public String listArticleComment(@PathVariable("id") long articleId, Model model) {
		model.addAttribute("commentList", queryComment(articleId));
		return "comment";
	}

	private List<CommentDTO> queryComment(long articleId) {
		// 查询评论
		CommentQueryForm commentQuery = new CommentQueryForm();
		commentQuery.setArticleId(articleId);
		List<CommentDTO> commentList = commentService.listComment(commentQuery);
		for (CommentDTO dto : commentList) {
			Long commentId = dto.getCommentId();
			commentQuery.setRefId(commentId);
			List<CommentDTO> subList = commentService.listComment(commentQuery);
			dto.setSubCommentList(subList);
		}
		return commentList;
	}

	@RequiresAuthentication
	@PostMapping("save")
	public String save(ArticlePO po) {
		UserDTO user = currentUser();
		po.setAuthor(String.valueOf(user.getUserId()));
		po.setUpdateDate(DateUtil.getCurrentLocalDateTimeString());
		articleService.saveOrUpdate(po);
		return "redirect:detail/" + po.getArticleId();
	}
	@RequestMapping("detail/{id}")
	public String detail(@PathVariable("id") long id, Model model) {
		ArticleDTO po = articleService.select(id);
		String userId = po.getAuthor();
		UserInfoPO user = userInfoService.findByUserId(userId);
		model.addAttribute("article", po);
		model.addAttribute("author", user);
		// 查看当前用户是否关注了作者
		UserDTO userDto = currentUser();
		if (userDto != null) {
			Long cuserId = userDto.getUserId();
			boolean isF = userFollowService.isFollowerOfUser(userId, String.valueOf(cuserId));
			model.addAttribute("isFollowed", isF);
		} else {
			model.addAttribute("isFollowed", false);
		}
		// 记录当前阅读数目
		UserDTO cuser = currentUser();
		if (cuser == null || !String.valueOf(cuser.getUserId()).equals(userId)) {
			ArticlePO newpo = new ArticlePO();
			newpo.setArticleId(po.getArticleId());
			newpo.setReadNum(null2Zero(po.getReadNum()) + 1);
			articleService.saveOrUpdate(newpo);
		}

		// 查询粉丝数量
		int followers = userFollowService.countByUserId(userId);
		// 查询关注数量
		int following = userFollowService.countByFollowerId(userId);
		// 文章数量
		int articleNum = articleService.countByAuthor(userId);

		model.addAttribute("articleNum", articleNum);
		model.addAttribute("followers", followers);
		model.addAttribute("following", following);

		// 查询评论
		model.addAttribute("commentList", queryComment(id));
		return "content";
	}

	private int null2Zero(Integer num){
		return num==null?0:num;
	}

	@RequiresRoles({ "admin" })
	@GetMapping("manage")
	public String manage() {

		return "articleManage";
	}

}
