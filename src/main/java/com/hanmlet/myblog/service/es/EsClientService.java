package com.hanmlet.myblog.service.es;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * FileName : EsClientFactory.
 *
 * @author : hanml
 * @date : 2018/7/23 16:53
 *     Description : Elasticsearch 客户端
 *     Copyright (C), 2015-2018, asiaInfo
 */
@Service
public class EsClientService {

  private static final Logger logger = LoggerFactory.getLogger(EsClientService.class);

  public static final String INDEX_NEWS = "news";
  public static final String INDEX_SOFTWARE = "software";
  public static final String INDEX_PRODUCT = "product";
  public static final String INDEX_SOLUTION = "solution";
  public static final String INDEX_SUPPLY_DEMAND = "supply_demand";
  public static final String INDEX_ARTICLE = "article";

  public static final String METHOD_GET = "GET";
  public static final String METHOD_POST = "POST";
  public static final String METHOD_DELETE = "DELETE";
  public static final String METHOD_PUT = "PUT";

  @Value("${es.hosts:127.0.0.1:9200}")
  public String hosts;

  /**
   * es调用客户端.
   */
  private RestClient restClient;

  @Autowired
  private ObjectMapper objectMapper;

  /**
   * 初始化调用客户端.
   */
  @PostConstruct
  public void init() {
    String[] host = this.hosts.split(",");
    List<HttpHost> list = new ArrayList<>();
    for (String h : host) {
      String[] ipPort = h.split(":");
      String ip = ipPort[0];
      int port = Integer.parseInt(ipPort[1]);
      HttpHost item = new HttpHost(ip, port, "http");
      list.add(item);
    }
    HttpHost[] httpHosts = new HttpHost[list.size()];
    RestClientBuilder builder = RestClient.builder(
                    list.toArray(httpHosts)
            );
    builder.setFailureListener(new RestClient.FailureListener() {
      @Override
      public void onFailure(HttpHost  host) {
        logger.error("es节点处理失败回调");
        if (host != null && host.getAddress() != null) {
          logger.error("es节点失败{}",host.getAddress());
        }
      }
    });
    restClient = builder.build();
  }

  /**
   * 删除文档.
   *
   * @param indexName 索引名称
   * @param id        文档id
   */
  public void invokeDel(String indexName, String id) {
    String url = indexName + "/_doc/" + id;
    try {
      restClient.performRequest(EsClientService.METHOD_DELETE, url);
    } catch (IOException e) {
      logger.error("删除索引文档异常", e);
    }
  }

  /**
   * 新建索引.
   */
  public void newIndex(String indexName) {
    if (StringUtils.isEmpty(indexName)) {
      return;
    }
    try {
      Map<String, String> params = Collections.emptyMap();
      try {
        //先删除原来的索引
        restClient.performRequest(EsClientService.METHOD_DELETE, "/" + indexName);
      } catch (Exception e) {
        //原来没有该索引删除时抛出异常
        System.out.println("当前时间： " + System.currentTimeMillis());
        logger.error("==================新建" + indexName + "索引异常====================");
      }
      //新建索引
      String indexJson = getEsFileJson("es/index_setting.json");
      HttpEntity indexEntity = new NStringEntity(indexJson, ContentType.APPLICATION_JSON);
      restClient.performRequest(EsClientService.METHOD_PUT, "/" + indexName, params, indexEntity);

      //配置mapping
      String mappingUrl = indexName + "/_mapping/_doc";
      String mappingJson = getEsFileJson("es/" + indexName + "_mapping.json");
      HttpEntity entity = new NStringEntity(mappingJson, ContentType.APPLICATION_JSON);
      restClient.performRequest(EsClientService.METHOD_PUT, mappingUrl, params, entity);

    } catch (IOException e) {
      String msg = "新建索引异常";
      logger.error(msg, e);
      throw new RuntimeException(msg, e);
    }
  }

  private String getEsFileJson(String path) throws IOException {

    InputStream in = EsClientService.class.getClassLoader().getResourceAsStream(path);
    String json = FileCopyUtils.copyToString(new InputStreamReader(in, "utf-8"));
    return json;
  }


  /**
   * 索引文档.
   *
   * @param indexName 索引名称
   * @param id 文档id
   * @param data data
   */
  public void invokeAdd(String indexName, Long id, Map<String, Object> data) {
    //Map<String, Object> updateDsl = new HashMap<>();
    String url = indexName + "/_doc/" + id;
    try {
      String queryJson = objectMapper.writeValueAsString(data);
      Map<String, String> params = Collections.emptyMap();
      HttpEntity entity = new NStringEntity(queryJson, ContentType.APPLICATION_JSON);
      logger.info("es add url:{} \n json:{}", url, queryJson);
      Response response = restClient.performRequest(EsClientService.METHOD_PUT,
              url, params, entity);
      String responseBody = EntityUtils.toString(response.getEntity());
    } catch (IOException e) {
      logger.error("es添加索引异常", e);
      throw new RuntimeException(e);
    }
  }


  /**
   * 根据脚本批量更新.
   *
   * @param query  query
   * @param script script
   */
  public void invokeUpdateWithScript(String indexName, Map<String, Object> query,
                                     Map<String, Object> script) {
    String url = indexName + "/_update_by_query?conflicts=proceed";
    Map<String, Object> dsl = QueryBuilder.builder().setFilter(query).build();
    dsl.put("script", script);
    try {
      String queryJson = objectMapper.writeValueAsString(dsl);
      Map<String, String> params = Collections.emptyMap();
      HttpEntity entity = new NStringEntity(queryJson, ContentType.APPLICATION_JSON);
      logger.info("es UpdateWithScript url:{} \n json:{}", url, queryJson);
      Response response = restClient.performRequest(EsClientService.METHOD_POST,
              url, params, entity);
      String responseBody = EntityUtils.toString(response.getEntity());
    } catch (IOException e) {
      String msg = "es更新数据异常";
      logger.error(msg, e);
      throw new RuntimeException(msg, e);
    }
  }


  /**
   * 更新数据.
   *
   * @param indexName indexName
   * @param id        id
   * @param data      data
   */
  public void invokeUpdate(String indexName, String id, Map<String, Object> data) {
    Map<String, Object> updateDsl = new HashMap<>();
    updateDsl.put("doc", data);
    String url = this.buildUpdateUrl(indexName, id);
    try {
      String queryJson = objectMapper.writeValueAsString(updateDsl);
      Map<String, String> params = Collections.emptyMap();
      HttpEntity entity = new NStringEntity(queryJson, ContentType.APPLICATION_JSON);
      logger.info("es update url:{} \n json:{}", url, queryJson);
      Response response = restClient.performRequest(EsClientService.METHOD_POST,
              url, params, entity);
      String responseBody = EntityUtils.toString(response.getEntity());
    } catch (IOException e) {
      String msg = "es更新数据异常";
      logger.error(msg, e);
      throw new RuntimeException(msg, e);
    }
  }

  private String buildUpdateUrl(String indexName, String id) {
    return indexName + "/_doc/" + id + "/_update";
  }

  /**
   * 根据id检索单条记录
   */
  public Map<String,Object> invokeSelect(String indexName,String id) throws IOException {
       String selectUrl="/"+indexName+"/_doc/"+id;
    Map<String, String> params = Collections.emptyMap();
    Response response = restClient.performRequest(EsClientService.METHOD_GET, selectUrl, params);
    String responseBody = EntityUtils.toString(response.getEntity());
    Map<String, Object> result = objectMapper.readValue(responseBody, Map.class);
    if((Boolean) result.get("found")){
        return result;
    }else{
        return null;
    }
  }

  /**
   * 请求调用.
   *
   * @param url   索引地址
   * @param query 请求json
   * @return EsCommonResult
   */
  public EsCommonResult invokeQuery(String url, Map<String, Object> query) throws IOException {
    String queryJson = objectMapper.writeValueAsString(query);
    Map<String, String> params = Collections.emptyMap();
    HttpEntity entity = new NStringEntity(queryJson, ContentType.APPLICATION_JSON);
    logger.info("es query url:{} \n json:{}", url, queryJson);
    Response response = restClient.performRequest(EsClientService.METHOD_POST, url, params, entity);
    String responseBody = EntityUtils.toString(response.getEntity());
    ///    EsCommonResult commonResult = handleResponse(responseBody);
    return handleResponse(responseBody);
  }

  /**
   * 解析查询结果.
   *
   * @param responseBody responseBody
   * @return EsCommonResult
   */
  private EsCommonResult handleResponse(String responseBody) throws IOException {
    Map<String, Object> result = objectMapper.readValue(responseBody, Map.class);

    Map<String, Object> hits = (Map<String, Object>) result.get("hits");
    //获取总条目数据
    Integer total = (Integer) hits.get("total");
    List<Map<String, Object>> list = Collections.emptyList();
    if (total > 0) {
      // 解析结果集
      list = (List<Map<String, Object>>) hits.get("hits");
    }
    EsCommonResult commonResult = new EsCommonResult();
    commonResult.setRows(list);
    commonResult.setTotal(total);
    return commonResult;
  }

  /**
   * getHighlightCol.
   * @param item item
   * @param colName  colName
   * @return Object
   */
  public Object getHighlightCol(Map<String, Object> item, String colName) {

    Map<String, Object> highlight = (Map<String, Object>) item.get("highlight");
    if (highlight != null) {
      List<Object> list = (List<Object>) highlight.get(colName);
      if (list != null && !list.isEmpty()) {
        return list.get(0);
      }
    }
    //如果为null 从 resource中获取
    Map<String, Object> resource = (Map<String, Object>) item.get("_source");
    return resource.get(colName);
  }


}
