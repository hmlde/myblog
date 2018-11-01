package com.hanmlet.myblog.shiro;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.stereotype.Service;

import com.hanmlet.myblog.common.PasswordEncoder;

/**
 * 与密码校验时相同
 * 
 * @author hanml
 *
 */
@Service
public class ShiroPasswordEncoder implements PasswordEncoder {

	private String algorithmName = "MD5";

	private int hashIterations = 2;

	@Override
	public String encoder(String password, String salt) {
		String newPassword = new SimpleHash(algorithmName, password, ByteSource.Util.bytes(salt), hashIterations)
				.toHex();
		return newPassword;
	}

}
