package com.hanmlet.myblog.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.hanmlet.myblog.base.BaseMapper;
import com.hanmlet.myblog.po.StoreInfoPO;

@Repository
@Mapper
public interface StoreInfoMapper extends BaseMapper<StoreInfoPO> {

	StoreInfoPO selectByFileKey(@Param("fileKey") String fileKey);

}