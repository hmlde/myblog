package com.hanmlet.myblog.service;

import com.hanmlet.myblog.po.BannerInfo;

import java.util.List;

/**
 * FileName : IBannerInfoService
 *
 * @author : hanml
 * @date : 2018/12/21 17:38
 * Description :
 * Copyright (C), 2015-2018, asiaInfo
 */
public interface IBannerInfoService {

    List<BannerInfo> queryAllBanners();
}
