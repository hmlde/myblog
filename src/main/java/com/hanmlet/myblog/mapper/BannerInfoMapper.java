package com.hanmlet.myblog.mapper;

import com.hanmlet.myblog.base.BaseMapper;
import com.hanmlet.myblog.po.BannerInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BannerInfoMapper extends BaseMapper<BannerInfo> {

    List<BannerInfo> queryAllBanners();
}