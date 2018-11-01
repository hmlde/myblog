package com.hanmlet.myblog.service;

import java.io.IOException;

import javax.activation.DataSource;

import com.hanmlet.myblog.dto.StoreDTO;

/**
 * 文件存储service
 * 
 * @author hanml
 *
 */
public interface IStoreService {

	StoreDTO save(DataSource dataSource) throws IOException;

	StoreDTO get(String key) throws IOException;
}
