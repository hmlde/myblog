package com.hanmlet.myblog.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.hanmlet.myblog.base.BaseMapper;
import com.hanmlet.myblog.po.UserInfoPO;

/**
 * 用户管理
 * 
 * @author hanml
 *
 */
@Repository
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfoPO> {

	UserInfoPO findByUsername(@Param("username") String username);
}