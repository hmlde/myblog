package com.hanmlet.myblog.service;

import java.util.List;

import com.hanmlet.myblog.po.UserFollowPO;

public interface IUserFollowService {

	int countByUserId(String userId);

	int countByFollowerId(String followerId);

	List<UserFollowPO> listByUserId(String userId);

	List<UserFollowPO> listByFollowerId(String followerId);

	int addFollow(String userId, String followerId);

	int delFollow(String userId, String followerId);

	UserFollowPO selectFollow(String userId, String followerId);

	boolean isFollowerOfUser(String userId, String followerId);

}
