package com.hanmlet.myblog.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.hanmlet.myblog.base.BaseMapper;
import com.hanmlet.myblog.po.UserFollowPO;

@Repository
@Mapper
public interface UserFollowMapper extends BaseMapper<UserFollowPO> {

	int countByUserId(@Param("userId") String userId);

	int countByFollowerId(@Param("followerId") String followerId);

	List<UserFollowPO> listByUserId(@Param("userId") String userId);

	List<UserFollowPO> listByFollowerId(@Param("followerId") String followerId);

	UserFollowPO selectFollow(@Param("userId") String userId, @Param("followerId") String followerId);
}