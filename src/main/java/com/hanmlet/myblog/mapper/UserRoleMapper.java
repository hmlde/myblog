package com.hanmlet.myblog.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.hanmlet.myblog.base.BaseMapper;
import com.hanmlet.myblog.dto.UserRoleDTO;
import com.hanmlet.myblog.po.UserRolePO;

@Repository
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRolePO> {
	List<UserRoleDTO> listUserRoleDto(@Param("userId") String userId);
}