package com.hanmlet.myblog.mapper;

import com.hanmlet.myblog.base.BaseMapper;
import com.hanmlet.myblog.po.SloganInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SloganInfoMapper extends BaseMapper<SloganInfo> {
    public SloganInfo selectTodaySlogan(String ymd);
}