package com.hanmlet.myblog.service;

import java.io.IOException;
import java.util.List;

import com.hanmlet.myblog.dto.StoreDTO;
import com.hanmlet.myblog.dto.UserDTO;
import com.hanmlet.myblog.dto.UserRoleDTO;
import com.hanmlet.myblog.form.RegisterForm;
import com.hanmlet.myblog.po.UserInfoPO;

public interface IUserInfoService {

	UserInfoPO findByUsername(String username);

	int addUser(RegisterForm form);

	UserDTO findDtoByUsername(String username);

	UserInfoPO findByUserId(String userId);

	UserInfoPO findByUserId(Long userId);

	int updateUser(UserInfoPO po);

	StoreDTO getAvatar(String userId, int size) throws IOException;

	void refreshAvatar(String userId);

	List<UserRoleDTO> listUserRoleDto(String userId);
}
