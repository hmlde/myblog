package com.hanmlet.myblog.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class ArticleService2 extends BaseService implements IArticleService {

	private final static String METHOD_PUT = "put";
	private final static String METHOD_POST = "POST";
	private final static String METHOD_GET = "GET";
	private final static String METHOD_DELETE = "DELETE";

	private final static String TYPE_NAME = "doc";

	private final static String INDEX_ARTICLE = "article";

	@Autowired
	private IUserInfoService userInfoService;

	@Autowired
	private ObjectMapper objectMapper;
	private RestClient restClient = null;

	@Autowired
	private ArticleMapper articleMapper;

	@Value("${es.hosts}")
	private String hosts;

	@Override
	public Page<ArticleDTO> listPage(ArticleQueryForm queryForm) {

		Page<ArticleDTO> list = this.listArticle(INDEX_ARTICLE, ArticleDTO.class, queryForm,
				new IQueryParamBuilder<ArticleQueryForm>() {
					@Override
					public Map<String, Object> buildQuery(ArticleQueryForm t) {
						return ArticleService2.this.buildQuery(t);
					}
				});
		// 处理作者信息
		for (ArticleDTO dto : list) {
			String userId = dto.getAuthor();
			if (!StringUtils.isEmpty(userId)) {
				UserInfoPO user = userInfoService.findByUserId(userId);
				BeanUtils.copyProperties(user, dto);
				dto.setUpdateDate(handerTime(dto.getUpdateDate()));
			}
		}
		return list;
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

	public RestClient getRestClient() {
		if (restClient == null) {
			if (StringUtils.isEmpty(hosts)) {
				hosts = "127.0.0.1:9200";
			}
			String[] hostArray = hosts.split(",");
			HttpHost[] httpHosts = new HttpHost[hostArray.length];
			for (int i = 0; i < hostArray.length; i++) {
				String[] hostIpPort = hostArray[i].split(":");
				HttpHost httpHost = new HttpHost(hostIpPort[0], Integer.valueOf(hostIpPort[1]), "http");
				httpHosts[i] = httpHost;
			}
			restClient = RestClient.builder(httpHosts).build();
		}
		return restClient;
	}

	@Override
	public int saveOrUpdate(ArticlePO articlePO) {

		ArticlePO dbPo = new ArticlePO();
		if (articlePO.getArticleId() == null) {
			long id = this.generateId();
			articlePO.setArticleId(id);
			BeanUtils.copyProperties(articlePO, dbPo);
			dbPo.setContent(null);
			articleMapper.insertSelective(dbPo);
		} else {
			BeanUtils.copyProperties(articlePO, dbPo);
			dbPo.setContent(null);
			articleMapper.updateByPrimaryKey(dbPo);
		}

		String endPoint = this.buildAddEndpoint(articlePO.getArticleId());
		try {
			Map<String, String> params = Collections.emptyMap();
			String jsonString = objectMapper.writeValueAsString(articlePO);
			HttpEntity entity = new NStringEntity(jsonString, ContentType.APPLICATION_JSON);
			Response response = getRestClient().performRequest(METHOD_PUT, endPoint, params, entity);
			String result = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			log.error("新增文章异常", e);
			return 0;
		}
		return 1;
	}

	private String buildAddEndpoint(long id) {
		String endPoint = "/" + INDEX_ARTICLE + "/" + TYPE_NAME + "/" + id;
		return endPoint;
	}

	private String buildDelEndpoint(long id) {
		String endPoint = "/" + INDEX_ARTICLE + "/" + TYPE_NAME + "/" + id;
		return endPoint;
	}

	private String buildGetEndpoint(long id) {
		String endPoint = "/" + INDEX_ARTICLE + "/" + TYPE_NAME + "/" + id;
		return endPoint;
	}

	private String buildQueryEndPoint() {
		String endPoint = "/" + INDEX_ARTICLE + "/" + TYPE_NAME + "/_search";
		return endPoint;
	}

	@Override
	public int del(long id) {
		String endPoint = this.buildDelEndpoint(id);
		try {
			Map<String, String> params = Collections.emptyMap();
			Response response = getRestClient().performRequest(METHOD_DELETE, endPoint, params);
			String result = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			log.error("新增文章异常", e);
			return 0;
		}
		return 1;
	}

	/**
	 * 通用的查询返回
	 * 
	 * @param resultPo
	 * @param queryForm
	 * @param queryBuilder
	 * @return
	 */
	private <E, T extends BasePageForm> Page<E> listArticle(String indexName, Class<E> resultPo, T t,
			IQueryParamBuilder<T> queryBuilder) {

		String queryEndPoint = this.buildQueryEndPoint();
		Map<String, String> params = Collections.emptyMap();
		try {
			// 查询消息封装
			Map<String, Object> queryBody = new HashMap<>();
			this.buildSortAndPage(queryBody, t);
			Map<String, Object> query = queryBuilder.buildQuery(t);
			if (query != null && !query.isEmpty()) {
				queryBody.put("query", query);
			}
			String jsonString = objectMapper.writeValueAsString(queryBody);
			log.debug("请求参数：{}", jsonString);
			HttpEntity entity = new NStringEntity(jsonString, ContentType.APPLICATION_JSON);
			Response response = getRestClient().performRequest(ArticleService2.METHOD_POST, queryEndPoint, params,
					entity);
			String resultJson = EntityUtils.toString(response.getEntity(), "utf-8");
			log.debug("返回参数,{}", resultJson);
			Map<String, Object> result = objectMapper.readValue(resultJson, Map.class);
			// 获取结果集
			Page<E> page = parseResult(resultPo, result);
			return page;
		} catch (Exception e) {
			log.error("es日志查询异常", e);
		}
		return null;
	}

	@Override
	public ArticlePO select(long id) {
		String endPoint = this.buildGetEndpoint(id);
		try {
			Map<String, String> params = Collections.emptyMap();
			Response response = getRestClient().performRequest(METHOD_GET, endPoint, params);
			String result = EntityUtils.toString(response.getEntity());
			// 解析
			Map<String, Object> resultMap = objectMapper.readValue(result, Map.class);
			if ((Boolean) resultMap.get("found")) {
				Map<String, Object> article = (Map<String, Object>) resultMap.get("_source");
				ArticlePO po = new ArticlePO();
				BeanMap beanMap = BeanMap.create(po);
				beanMap.putAll(article);
				return po;
			}
		} catch (Exception e) {
			log.error("新增文章异常", e);

		}
		return null;
	}

	/**
	 * 构建查询结构
	 * 
	 * @param t
	 * @return 没有条件 返回空
	 */
	private Map<String, Object> buildQuery(ArticleQueryForm queryForm) {
		// must结构
		List<Map<String, Object>> must = new ArrayList<>();
		// 模块名称
		buildMustMatch(must, "content", queryForm.getContent());
		buildMustMatch(must, "author", queryForm.getAuthor());
		return this.buildQuery(must);
	}

	/**
	 * 返回结构查询
	 * 
	 * @param must
	 * @return
	 */
	private Map<String, Object> buildQuery(List<Map<String, Object>> must) {
		// bool结构
		Map<String, Object> bool = new HashMap<>();
		if (must.size() == 0) {
			bool = null;
			return null;
		}
		bool.put("must", must);
		// 查询结构
		Map<String, Object> query = new HashMap<>();
		query.put("bool", bool);
		return query;
	}

	/**
	 * 构建math条件
	 * 
	 * @param must
	 * @param param
	 */
	private void buildMustMatch(List<Map<String, Object>> must, String paramName, String value) {
		if (!StringUtils.isEmpty(value)) {
			must.add(getMatch(paramName, value));
		}
	}

	/***
	 * 构建范围查询
	 * 
	 * @param must
	 * @param paramName
	 * @param startValue
	 * @param endValue
	 */
	private void buildMustRange(List<Map<String, Object>> must, String paramName, String startValue, String endValue) {
		if (!StringUtils.isEmpty(startValue) || !StringUtils.isEmpty(endValue)) {
			Map<String, Object> occurDate = new HashMap<>();
			if (!StringUtils.isEmpty(startValue)) {
				occurDate.put("gte", startValue);
			}
			if (!StringUtils.isEmpty(endValue)) {
				occurDate.put("lte", endValue);
			}
			must.add(getRange(paramName + ".keyword", occurDate));
		}
	}

	/**
	 * 构建排序和分页
	 * 
	 * @param body
	 * @param pageForm
	 */
	private void buildSortAndPage(Map<String, Object> body, BasePageForm pageForm) {
		// 分页数据
		/*
		 * body.put("from", pageForm.getOffset()); body.put("size",
		 * pageForm.getLimit()); // 排序 Map<String, Object> sort = new HashMap<>();
		 * sort.put("occurDate.keyword", "desc");
		 * 
		 * List<Map<String, Object>> sortList = Lists.newArrayList();
		 * sortList.add(sort); body.put("sort", sortList);
		 */

	}

	/**
	 * 解析结果集
	 * 
	 * @param t
	 * @param result
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private <T> Page<T> parseResult(Class<T> t, Map<String, Object> result)
			throws InstantiationException, IllegalAccessException {

		// 查询是否超时
		boolean timeOut = (Boolean) result.get("timed_out");
		Page<T> page = new Page<>();
		if (!timeOut) {
			// 获取记录数
			Map<String, Object> hits = (Map<String, Object>) result.get("hits");
			Integer total = (Integer) hits.get("total");
			page.setTotal(total);
			if (total > 0) {
				List list = (List) hits.get("hits");
				for (Object o : list) {
					Map<String, Object> item = (Map<String, Object>) o;
					// 获取原数据
					Map<String, Object> resource = (Map<String, Object>) item.get("_source");
					resource.put("articleTag", "java学习");
					T itemT = t.newInstance();
					BeanMap bm = BeanMap.create(itemT);
					bm.putAll(resource);
					page.add(itemT);
				}
			}
		}
		return page;
	}

	// 获取range结构
	private Map<String, Object> getRange(String key, Map<String, Object> range) {
		Map<String, Object> item = new HashMap<>();
		item.put(key, range);
		Map<String, Object> match = new HashMap<>();
		match.put("range", item);
		return match;
	}

	// 获取match结构
	private Map<String, Object> getMatch(String key, String value) {
		Map<String, String> item = new HashMap<>();
		item.put(key, value);
		Map<String, Object> match = new HashMap<>();
		match.put("match", item);
		return match;
	}

	/**
	 * 内部接口回调使用
	 * 
	 * @author hanml
	 *
	 */
	public interface IQueryParamBuilder<T> {
		Map<String, Object> buildQuery(T t);
	}

}
