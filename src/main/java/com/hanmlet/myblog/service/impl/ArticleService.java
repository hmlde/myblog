package com.hanmlet.myblog.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.github.pagehelper.Page;
import com.hanmlet.myblog.base.BaseService;
import com.hanmlet.myblog.common.util.DateUtil;
import com.hanmlet.myblog.dto.ArticleDTO;
import com.hanmlet.myblog.form.ArticleQueryForm;
import com.hanmlet.myblog.mapper.ArticleMapper;
import com.hanmlet.myblog.po.ArticlePO;
import com.hanmlet.myblog.po.UserInfoPO;
import com.hanmlet.myblog.service.IArticleService;
import com.hanmlet.myblog.service.IUserInfoService;

import lombok.extern.slf4j.Slf4j;

/**
 * 完全由es实现
 * 
 * @author hanml
 *
 */
@Slf4j
public class ArticleService extends BaseService implements IArticleService {

	@Autowired
	private IUserInfoService userInfoService;

	@Autowired
	private ArticleMapper articleMapper;

	@Override
	public Page<ArticleDTO> listPage(ArticleQueryForm queryForm) {
		Page<ArticlePO> list = articleMapper.listArticle(queryForm);
		Page<ArticleDTO> dtoList = new Page<>();
		// 处理作者信息
		for (ArticlePO po : list) {
			ArticleDTO dto = new ArticleDTO();
			BeanUtils.copyProperties(po, dto);
			String userId = po.getAuthor();
			if (!StringUtils.isEmpty(userId)) {
				UserInfoPO user = userInfoService.findByUserId(userId);
				BeanUtils.copyProperties(user, dto);
				dto.setUpdateDate(handerTime(dto.getUpdateDate()));
			}
			dtoList.add(dto);
		}
		return dtoList;
	}

	/**
	 * 处理时间
	 * 
	 * @param date
	 * @return
	 */
	private String handerTime(String date) {
		if (StringUtils.isEmpty(date)) {
			return "";
		}
		LocalDateTime dt = DateUtil.stringToLocalDateTime(date, DateUtil.TIME_PATTERN);
		String str = DateUtil.formatLocalDateTimeToString(dt, DateUtil.DATE_FMT_mdhm);
		return str;
	}

	public int countByAuthor(String author) {
		return articleMapper.countByAuthor(author);
	}

	@Override
	public int saveOrUpdate(ArticlePO articlePO) {
		if (articlePO.getArticleId() == null) {
			long id = this.generateId();
			articlePO.setArticleId(id);
			return articleMapper.insertSelective(articlePO);
		} else {
			return articleMapper.updateByPrimaryKeySelective(articlePO);
		}

	}

	@Override
	public int del(long id) {
		return articleMapper.deleteByPrimaryKey(id);
	}

	@Override
	public ArticlePO select(long id) {
		return articleMapper.selectByPrimaryKey(id);
	}

}
