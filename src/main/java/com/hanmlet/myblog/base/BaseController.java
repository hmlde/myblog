package com.hanmlet.myblog.base;

import org.apache.shiro.SecurityUtils;

import com.hanmlet.myblog.AppConstants;
import com.hanmlet.myblog.dto.BaseEntityDTO;
import com.hanmlet.myblog.dto.UserDTO;

/**
 * 基础控制层类
 * 
 * @author hanml
 *
 */
public class BaseController {
	/**
	 * 当前的用户
	 * 
	 * @return
	 */
	public UserDTO currentUser() {
		UserDTO user = (UserDTO) SecurityUtils.getSubject().getPrincipal();
		return user;
	}

	/**
	 * 构建成功返回
	 * 
	 * @param t
	 * @return
	 */
	public <T> BaseEntityDTO<T> ajaxSuccessReturn(T t) {
		BaseEntityDTO<T> dto = new BaseEntityDTO<>();
		dto.setCode(AppConstants.ReturnCode.SUCCESS);
		dto.setMsg("调用成功");
		dto.setData(t);
		return dto;
	}

}
