package com.hanmlet.myblog.po;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFollowPO {
	private Long followId;

	private String userId;

	private String followerId;

}