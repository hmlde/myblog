package com.hanmlet.myblog.base;

/**
 * 公共mapper类
 * 
 * @author hanml
 *
 */
public interface BaseMapper<T> {
	int deleteByPrimaryKey(Long id);

	int insert(T record);

	int insertSelective(T record);

	T selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(T record);

	int updateByPrimaryKey(T record);
}
