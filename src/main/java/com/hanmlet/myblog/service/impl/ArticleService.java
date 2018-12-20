package com.hanmlet.myblog.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hanmlet.myblog.service.es.EsClientService;
import com.hanmlet.myblog.service.es.EsCommonResult;
import com.hanmlet.myblog.service.es.QueryBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.hanmlet.myblog.base.BaseService;
import com.hanmlet.myblog.common.util.DateUtil;
import com.hanmlet.myblog.dto.ArticleDTO;
import com.hanmlet.myblog.form.ArticleQueryForm;
import com.hanmlet.myblog.form.BasePageForm;
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
@Service
@Slf4j
public class ArticleService extends BaseService implements IArticleService {


	@Autowired
	private IUserInfoService userInfoService;

	@Autowired
	private ObjectMapper objectMapper;
	private RestClient restClient = null;

	@Autowired
	private ArticleMapper articleMapper;

	@Autowired
	private EsClientService esClientService;

	@Override
	public Page<ArticleDTO> listPage(ArticleQueryForm queryForm) {

		 Page<ArticleDTO> page = new Page<>();
		String[] queryCols = new String[]{
				"articleTitle^4", "articleKeyword^2","summary^2", "content"
		};
		//高亮显示列
		String[] highlightCols = new String[]{
				"solutionDesc", "solutionName"
		};
		Map<String, Object> query = QueryBuilder.builder().setSearchKey(queryForm.getSearchKey(), queryCols)
				.setPage(queryForm.getPageNo(), queryForm.getPageSize()).setCountFlag(false)
				.setHighlightColsName(highlightCols)
				.build();
		EsCommonResult commonResult = null;
		try {
			commonResult = esClientService.invokeQuery("/article/_search", query);
		} catch (IOException e) {
			throw  new RuntimeException(e);
		}
		//获取列数据
		List<Map<String, Object>> rows = commonResult.getRows();
		List<ArticleDTO> resultList = new ArrayList<>();
		rows.forEach(item -> {
			//json数据
			Map<String, Object> resource = (Map<String, Object>) item.get("_source");
			resultList.add(map2Dto(resource));
		});
		page.setTotal(commonResult.getTotal());
		page.addAll(resultList);

		return page;
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

	@Override
	public int countByAuthor(String author) {
		return articleMapper.countByAuthor(author);
	}

	@Override
	@Transactional
	public int saveOrUpdate(ArticlePO articlePO) {
		if (articlePO.getArticleId() == null) {
			long id = this.generateId();
			articlePO.setArticleId(id);
			articleMapper.insertSelective(articlePO);
		} else {
			articleMapper.updateByPrimaryKeySelective(articlePO);
		}
		articlePO = articleMapper.selectByPrimaryKey(articlePO.getArticleId());
		sync2Es(articlePO);
		return 1;
	}

	/**
	 * 同步到es
	 * @param po
	 */
	private void sync2Es(ArticlePO po){
		Map<String,Object> data = new HashMap<>();
		data.put("articleId",po.getArticleId());
		data.put("author", po.getAuthor());
		data.put("articleKeyword", po.getArticleKeyword());
		data.put("img", po.getImg());
		data.put("status", po.getStatus());
		data.put("content",po.getContent());
		data.put("articleTitle", po.getArticleTitle());
		data.put("readNum", null2Zero(po.getReadNum()));
		data.put("commentNum", null2Zero(po.getCommentNum()));
		data.put("favourNum",null2Zero( po.getFavourNum()));
		data.put("opposeNum", null2Zero(po.getOpposeNum()));
		data.put("updateDate", po.getUpdateDate());
		data.put("supportNum", null2Zero(po.getSupportNum()));
		data.put("summary",po.getSummary());
		data.put("createDate",po.getCreateDate());
		data.put("articleTag",po.getArticleTag());
		esClientService.invokeAdd(EsClientService.INDEX_ARTICLE, po.getArticleId(), data);
	}

	private Integer null2Zero(Integer num){
		return num==null?0:num;
	}

	@Override
	public int del(long id) {
		try {
			esClientService.invokeDel(EsClientService.INDEX_ARTICLE,String.valueOf(id));
		} catch (Exception e) {
			log.error("新增文章异常", e);
			return 0;
		}
		return 1;
	}

	@Override
	public ArticleDTO select(long id)  {
		try {
			Map<String,Object> result = esClientService.invokeSelect(EsClientService.INDEX_ARTICLE,String.valueOf(id));
			if(result!=null){
				Map<String, Object> resource = (Map<String, Object>) result.get("_source");
				return map2Dto(resource);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	/**
	 * map 转换为dto
	 * @param resource
	 * @return
	 */
	private ArticleDTO map2Dto(Map<String,Object> resource){
		resource.put("articleTag", "java学习");
		ArticleDTO dto = new ArticleDTO();
		BeanMap bm = BeanMap.create(dto);
		bm.putAll(resource);
		if(!StringUtils.isEmpty(dto.getAuthor())){
			UserInfoPO po = userInfoService.findByUserId(dto.getAuthor());
			dto.setDisplayName(po.getDisplayName());
		}

		return dto;
	}

}
