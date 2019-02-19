package com.hanmlet.myblog.service.impl;

import com.hanmlet.myblog.mapper.BannerInfoMapper;
import com.hanmlet.myblog.po.BannerInfo;
import com.hanmlet.myblog.service.IBannerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * FileName : BannerInfoService
 *
 * @author : hanml
 * @date : 2018/12/21 17:42
 * Description :
 * Copyright (C), 2015-2018, asiaInfo
 */
@Service
public class BannerInfoService implements IBannerInfoService {

    @Autowired
    private BannerInfoMapper bannerInfoMapper;


    @Override
    public List<BannerInfo> queryAllBanners() {
        return bannerInfoMapper.queryAllBanners();
    }
}
