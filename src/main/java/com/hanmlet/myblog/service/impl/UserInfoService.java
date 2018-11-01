package com.hanmlet.myblog.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.activation.DataSource;
import javax.activation.FileDataSource;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;

import com.hanmlet.myblog.base.BaseService;
import com.hanmlet.myblog.common.PasswordEncoder;
import com.hanmlet.myblog.dto.StoreDTO;
import com.hanmlet.myblog.dto.UserDTO;
import com.hanmlet.myblog.dto.UserRoleDTO;
import com.hanmlet.myblog.form.RegisterForm;
import com.hanmlet.myblog.mapper.UserInfoMapper;
import com.hanmlet.myblog.mapper.UserRoleMapper;
import com.hanmlet.myblog.po.UserInfoPO;
import com.hanmlet.myblog.service.IStoreService;
import com.hanmlet.myblog.service.IUserInfoService;

import net.coobird.thumbnailator.Thumbnails;

@Service
public class UserInfoService extends BaseService implements IUserInfoService {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserInfoMapper userInfoMapper;

	@Autowired
	private IStoreService storeService;

	@Value("${store.dir}")
	private String storeDir;

	@Value("${avatar.dir}")
	private String avatarDir;

	@Value("${avatar.default.dir}")
	private String defaultAvatarDir;

	@Value("${avatar.default.name}")
	private String defaultAvatarName;

	@Autowired
	private UserRoleMapper userRoleMapper;

	@Override
	public StoreDTO getAvatar(String userId, int size) throws IOException {
		UserInfoPO userinfo = this.findByUserId(userId);
		String avatar = userinfo.getAvatar();
		if (StringUtils.isEmpty(avatar)) {
			// 返回默认头像
			return getDefaultAvatar(size);
		} else {
			return getUserAvatar(userId, avatar, size);
		}
	}

	@Override
	public void refreshAvatar(String userId) {
		String fileDir = this.storeDir + this.avatarDir + "/" + userId;
		File f = new File(fileDir);
		FileSystemUtils.deleteRecursively(f);
	}

	private StoreDTO getUserAvatar(String userId, String avatar, int size) throws IOException {
		String fileDir = this.storeDir + this.avatarDir + "/" + userId + "/" + size;
		String filePath = fileDir + "/" + this.defaultAvatarName;
		File f = new File(filePath);
		StoreDTO dto = new StoreDTO();
		dto.setFileName(this.defaultAvatarName);
		dto.setFileType("img");
		dto.setKey(filePath);
		if (!f.exists()) {
			// 如果不存在则创建
			StoreDTO storeDto = storeService.get(avatar);
			File fd = new File(fileDir);
			if (!fd.exists()) {
				fd.mkdirs();
			}
			Thumbnails.of(storeDto.getDataSource().getInputStream()).size(size, size).toFile(filePath);
		}
		DataSource ds = new FileDataSource(filePath);
		dto.setDataSource(ds);
		return dto;
	}

	private StoreDTO getDefaultAvatar(int size) throws IOException {
		String fileDir = this.storeDir + this.defaultAvatarDir + "/" + size;
		String filePath = fileDir + "/" + this.defaultAvatarName;
		File f = new File(filePath);
		StoreDTO dto = new StoreDTO();
		dto.setFileName(this.defaultAvatarName);
		dto.setFileType("img");
		dto.setKey(filePath);
		if (!f.exists()) {
			// 如果不存在则创建
			String prototypePath = this.storeDir + this.defaultAvatarDir + "/" + this.defaultAvatarName;
			File fd = new File(fileDir);
			if (!fd.exists()) {
				fd.mkdirs();
			}
			Thumbnails.of(prototypePath).size(size, size).toFile(filePath);
		}
		DataSource ds = new FileDataSource(filePath);
		dto.setDataSource(ds);
		return dto;
	}

	@Override
	public int updateUser(UserInfoPO po) {
		return userInfoMapper.updateByPrimaryKeySelective(po);
	}

	public UserInfoPO findByUsername(String username) {
		return userInfoMapper.findByUsername(username);
	}

	public int addUser(RegisterForm form) {
		UserInfoPO userinfo = new UserInfoPO();
		userinfo.setUsername(form.getUsername());
		userinfo.setEmail(form.getEmail());
		userinfo.setPassword(passwordEncoder.encoder(form.getPassword(), userinfo.getUsername()));
		userinfo.setUserId(this.generateId());
		return userInfoMapper.insertSelective(userinfo);
	}

	public UserDTO findDtoByUsername(String username) {
		UserInfoPO po = this.findByUsername(username);
		return this.po2Dto(po);
	}

	public UserInfoPO findByUserId(String userId) {
		return userInfoMapper.selectByPrimaryKey(Long.valueOf(userId));
	}

	public UserInfoPO findByUserId(Long userId) {
		return userInfoMapper.selectByPrimaryKey(userId);
	}

	private UserDTO po2Dto(UserInfoPO po) {
		UserDTO dto = new UserDTO();
		BeanUtils.copyProperties(po, dto);
		return dto;
	}

	@Override
	public List<UserRoleDTO> listUserRoleDto(String userId) {
		return userRoleMapper.listUserRoleDto(userId);
	}
}
