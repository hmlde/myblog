package com.hanmlet.myblog.service.es;

import java.util.List;
import java.util.Map;

/**
 * FileName : EsCommonResult.
 *
 * @author : hanml
 * @date : 2018/7/23 17:05
 *     Description :
 *     Copyright (C), 2015-2018, asiaInfo
 */
public class EsCommonResult {
  private int total;

  private List<Map<String, Object>> rows;

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public List<Map<String, Object>> getRows() {
    return rows;
  }

  public void setRows(List<Map<String, Object>> rows) {
    this.rows = rows;
  }

}
