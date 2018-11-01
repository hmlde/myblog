package com.hanmlet.myblog.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hanmlet.myblog.base.BaseService;
import com.hanmlet.myblog.mapper.UserFollowMapper;
import com.hanmlet.myblog.po.UserFollowPO;
import com.hanmlet.myblog.service.IUserFollowService;

@Service
public class UserFollowService extends BaseService implements IUserFollowService {

	@Autowired
	private UserFollowMapper userFollowMapper;

	@Override
	public int countByUserId(String userId) {
		return userFollowMapper.countByUserId(userId);
	}

	@Override
	public int countByFollowerId(String followerId) {
		return userFollowMapper.countByFollowerId(followerId);
	}

	@Override
	public List<UserFollowPO> listByUserId(String userId) {
		return userFollowMapper.listByUserId(userId);
	}

	@Override
	public List<UserFollowPO> listByFollowerId(String followerId) {
		return userFollowMapper.listByFollowerId(followerId);
	}

	@Override
	public int addFollow(String userId, String followerId) {
		UserFollowPO po = new UserFollowPO();
		po.setFollowId(this.generateId());
		po.setUserId(userId);
		po.setFollowerId(followerId);
		return userFollowMapper.insertSelective(po);
	}

	@Override
	public int delFollow(String userId, String followerId) {
		UserFollowPO po = this.selectFollow(userId, followerId);
		if (po != null) {
			return userFollowMapper.deleteByPrimaryKey(po.getFollowId());
		}
		return 0;
	}

	@Override
	public UserFollowPO selectFollow(String userId, String followerId) {
		return userFollowMapper.selectFollow(userId, followerId);
	}

	@Override
	public boolean isFollowerOfUser(String userId, String followerId) {
		UserFollowPO po = this.selectFollow(userId, followerId);
		return po != null;
	}

}
