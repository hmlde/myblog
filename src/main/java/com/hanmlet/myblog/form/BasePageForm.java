package com.hanmlet.myblog.form;

import lombok.Getter;
import lombok.Setter;

/**
 * 分页数据
 * 
 * @author hanml
 *
 */
@Getter
@Setter
public class BasePageForm {
    private int pageSize = 10;
    private int pageNo = 1;
}
