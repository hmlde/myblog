package com.hanmlet.myblog.service.impl;

import com.hanmlet.myblog.mapper.SloganInfoMapper;
import com.hanmlet.myblog.po.SloganInfo;
import com.hanmlet.myblog.service.ISloganInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * FileName : SloganInfoSevice
 *
 * @author : hanml
 * @date : 2018/11/19 22:06
 * Description :
 * Copyright (C), 2015-2018, asiaInfo
 */
@Service
public class SloganInfoSevice implements ISloganInfoService {

    @Autowired
    private SloganInfoMapper sloganInfoMapper;
    @Override
    public SloganInfo selectTodaySlogan() {
        SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMdd");
        String ymd=sdf.format(new Date());
        return sloganInfoMapper.selectTodaySlogan(ymd);
    }
}
