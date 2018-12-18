package com.hanmlet.myblog.service;

import com.hanmlet.myblog.po.SloganInfo;

/**
 * FileName : ISloganInfoService
 *
 * @author : hanml
 * @date : 2018/11/19 22:06
 * Description :
 * Copyright (C), 2015-2018, asiaInfo
 */
public interface ISloganInfoService  {

    SloganInfo selectTodaySlogan();
}
