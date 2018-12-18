package com.hanmlet.myblog.service.es;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;


/**
 * FileName : QueryBuilder.
 *
 * @author : hanml
 * @date : 2018/7/26 16:54
 *     Description : 构建Es检索语句
 *     Copyright (C), 2015-2018, asiaInfo
 */
public class QueryBuilder {
  /**
   * 检索关键字.
   */
  private String searchKey;
  /**
   * 检索列名称.
   */
  private String[] searchColsName;
  /**
   * 高亮列名称.
   */
  private String[] highlightColsName;
  /**
   * 过滤字段.
   */
  private Map<String, Object> filter;
  /**
   * 排序.
   */
  private Map<String, String> sort;

  private int pageNo = 0;

  private int pageSize = 0;

  private Boolean countFlag;

  public static QueryBuilder builder() {
    return new QueryBuilder();
  }


  /**
   * build.
   */
  public Map<String, Object> build() {
    int offset = (pageNo - 1) * pageSize;
    //检索语句 根据name,desc,content字段进行全文检索
    Map<String, Object> dsl = new HashMap<>();
    if (countFlag != null) {
      if (countFlag) {
        dsl.put("size", 0);
      } else if (pageNo != 0) {
        dsl.put("from", offset);
        dsl.put("size", pageSize);
      }
    }
    //构建过滤结构
    List<Map<String, Object>> filters = this.buildFilter(filter);
    //全文检索结构
    Map<String, Object> searchMap = this.buildSearchKey(searchKey, searchColsName);
    Map<String, Object> queryMap = new HashMap<>();
    if (filters != null || searchMap != null) {
      Map<String, Object> bool = new HashMap<>();
      bool.put("must", searchMap);
      bool.put("filter", filters);
      queryMap.put("bool", bool);
    }
    if (!queryMap.isEmpty()) {
      dsl.put("query", queryMap);
    }
    List<Map<String, Object>> sortList = this.buildSort(sort);
    //非统计 检索条件不为空
    if (sortList != null && BooleanUtils.isFalse(countFlag)) {
      dsl.put("sort", sortList);
    }
    //高亮处理
    Map<String, Object> highlight = this.buildHighlight(highlightColsName);
    if (highlight != null) {
      dsl.put("highlight", highlight);
    }

    return dsl;
  }

  /**
 * setPage.
 * @param pageNo 页码
 * @param pageSize 数量
 * @return  QueryBuilder
 */
  public QueryBuilder setPage(int pageNo, int pageSize) {
    this.pageNo = pageNo;
    this.pageSize = pageSize;
    return this;
  }

  public String getSearchKey() {
    return searchKey;
  }

  /**
   * setSearchKey.
   * @param searchKey searchKey
   * @param searchColsName  searchColsName
   * @return QueryBuilder
   */
  public QueryBuilder setSearchKey(String searchKey, String[] searchColsName) {
    this.searchKey = searchKey;
    this.searchColsName = searchColsName == null ? null : searchColsName.clone();
    return this;
  }

  public String[] getSearchColsName() {
    return searchColsName == null ? null : searchColsName.clone();
  }

  public Map<String, Object> getFilter() {
    return filter;
  }

  public QueryBuilder setFilter(Map<String, Object> filter) {
    this.filter = filter;
    return this;
  }

  public Map<String, String> getSort() {
    return sort;
  }

  public QueryBuilder setSort(Map<String, String> sort) {
    this.sort = sort;
    return this;
  }

  public boolean isCountFlag() {
    return countFlag;
  }

  public QueryBuilder setCountFlag(boolean countFlag) {
    this.countFlag = countFlag;
    return this;
  }

  public String[] getHighlightColsName() {
    return highlightColsName == null ? null : highlightColsName.clone();
  }

  public QueryBuilder setHighlightColsName(String[] highlightColsName) {
    this.highlightColsName = highlightColsName == null ? null : highlightColsName.clone();
    return this;
  }

  /**
   * 构建高亮显示字段.
   *
   * @param cols cols
   * @return map
   */
  private Map<String, Object> buildHighlight(String[] cols) {

    if (cols == null || cols.length == 0) {
      return null;
    }
    Map<String, Object> highlight = new HashMap<>();
    highlight.put("pre_tags", "<span class='blue-text'>");
    highlight.put("post_tags", "</span>");
    List<Map<String, Object>> fields = new ArrayList<>();
    for (String col : cols) {
      Map<String, Object> item = new HashMap<>();
      item.put(col, Collections.emptyMap());
      fields.add(item);
    }
    highlight.put("fields", fields);

    return highlight;
  }

  /**
   * 构建排序结构.
   * 添加默认相关性排序
   * @param sort 排序
   * @return list
   */
  private List<Map<String, Object>> buildSort(Map<String, String> sort) {
    if (sort == null || sort.isEmpty()) {
      return null;
    }
    List<Map<String, Object>> list = new ArrayList<>();
    //第一个为默认相关性排序使检索的结果更准确
    Map<String, Object> defaultOrder = new HashMap<>();
    Map<String, Object> orderKv = new HashMap<>();
    orderKv.put("order", "desc");
    defaultOrder.put("_score",orderKv);
    list.add(defaultOrder);

    for (Map.Entry<String, String> item : sort.entrySet()) {
      String k = item.getKey();
      String v = item.getValue();
      Map<String, Object> order = new HashMap<>();
      order.put("order", v);
      Map<String, Object> sortItem = new HashMap<>();
      sortItem.put(k, order);
      list.add(sortItem);
    }
    return list;
  }

  /**
   * 构建检索语句.
   *
   * @param searchKey 关键词
   * @return map
   */
  private Map<String, Object> buildSearchKey(String searchKey, String[] cols) {
    if (StringUtils.isBlank(searchKey)) {
      return null;
    }
    Map<String, Object> must = new HashMap<>();
    Map<String, Object> multiMatch = new HashMap<>();
    multiMatch.put("query", searchKey);
    multiMatch.put("fields", cols);
    must.put("multi_match", multiMatch);
    return must;
  }

  /**
   * 构建过滤语句.
   *
   * @param filters filters
   * @return list
   */
  private List<Map<String, Object>> buildFilter(Map<String, Object> filters) {

    if (filters == null || filters.isEmpty()) {
      return null;
    }
    List<Map<String, Object>> cols = new ArrayList<>();
    for (Map.Entry<String, Object> item : filters.entrySet()) {
      String key = item.getKey();
      Object v = item.getValue();
      Map<String, Object> term = new HashMap<>();
      Map<String, Object> kv = new HashMap<>();
      kv.put(key, v);
      if (isArray(v)) {
        term.put("terms", kv);
      } else {
        term.put("term", kv);
      }
      cols.add(term);
    }
    return cols;
  }

  /**
   * main.
   * @param args args
   */
  public static void main(String[] args) {
    int[] s = {1, 2, 3};
    QueryBuilder qb = new QueryBuilder();
    System.out.println(qb.isArray(s));
  }

  /**
   * 判断对象是否为数组.
   *
   * @param o o
   */
  private boolean isArray(Object o) {
    if (o == null) {
      return false;
    }
    return o.getClass().isArray();
  }


}
