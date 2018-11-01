package com.hanmlet.myblog.base;

import org.springframework.beans.factory.annotation.Autowired;

import com.hanmlet.myblog.common.IdGenerator;

/**
 * 基础service类
 * 
 * @author hanml
 *
 */
public class BaseService {

	@Autowired
	private IdGenerator idGenerator;

	public long generateId() {
		return idGenerator.generateId();
	}
}
