package com.hanmlet.myblog.shiro;

import java.util.List;

import javax.annotation.Resource;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import com.hanmlet.myblog.dto.UserDTO;
import com.hanmlet.myblog.dto.UserRoleDTO;
import com.hanmlet.myblog.service.IUserInfoService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyShiroRealm extends AuthorizingRealm {
	@Resource
	private IUserInfoService userInfoService;

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		log.info("目前不处理授权");
		SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

		UserDTO userDto = (UserDTO) principals.getPrimaryPrincipal();

		String userId = userDto.getUserId() + "";

		List<UserRoleDTO> dtos = userInfoService.listUserRoleDto(userId);
		for (UserRoleDTO roleDto : dtos) {
			authorizationInfo.addRole(roleDto.getRoleCode());
		}
		return authorizationInfo;
	}

	/* 主要是用来进行身份认证的，也就是说验证用户输入的账号和密码是否正确。 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		String username = (String) token.getPrincipal();
		log.debug("当前认证用户名：{}", username);
		// Shiro自己也是有时间间隔机制，2分钟内不会重复执行该方法
		UserDTO userInfo = userInfoService.findDtoByUsername(username);
		if (userInfo == null) {
			throw new UnknownAccountException();
		}
		SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(userInfo, // 用户名
				userInfo.getPassword(), // 密码
				ByteSource.Util.bytes(userInfo.getUsername()), // salt=username
				getName() // realm name
		);
		return authenticationInfo;
	}

}