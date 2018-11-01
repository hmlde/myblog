package com.hanmlet.myblog.common;

/**
 * 加密接口
 * 
 * @author hanml
 *
 */
public interface PasswordEncoder {

	String encoder(String password, String salt);
}
