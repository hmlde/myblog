package com.hanmlet.myblog.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import javax.activation.DataSource;
import javax.activation.FileDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import com.hanmlet.myblog.base.BaseService;
import com.hanmlet.myblog.common.util.DateUtil;
import com.hanmlet.myblog.dto.StoreDTO;
import com.hanmlet.myblog.mapper.StoreInfoMapper;
import com.hanmlet.myblog.po.StoreInfoPO;
import com.hanmlet.myblog.service.IStoreService;

@Service
public class LocalStoreService extends BaseService implements IStoreService {

	@Value("${store.dir}")
	private String baseDir;

	@Autowired
	private StoreInfoMapper storeInfoMapper;

	@Override
	public StoreDTO save(DataSource dataSource) throws IOException {
		String fileName = UUID.randomUUID().toString().replaceAll("-", "");
		String ymd = DateUtil.getCurrentLocalDateTimeString(DateUtil.DATE_yyyyMMdd);

		String filePath = this.baseDir + "/" + ymd;
		File path = new File(filePath);
		if (!path.exists()) {
			path.mkdirs();
		}

		String key = "/" + ymd + "/" + fileName;

		String f = this.baseDir + key;
		FileOutputStream fos = new FileOutputStream(f);
		FileCopyUtils.copy(dataSource.getInputStream(), fos);

		StoreDTO dto = new StoreDTO();
		dto.setKey(key);
		dto.setFileName(dataSource.getName());
		dto.setFileType(dataSource.getContentType());
		storeInfoMapper.insertSelective(dto2Po(dto));
		return dto;
	}

	private StoreInfoPO dto2Po(StoreDTO dto) {
		StoreInfoPO po = new StoreInfoPO();
		po.setFileKey(dto.getKey());
		po.setFileName(dto.getFileName());
		po.setFileType(dto.getFileType());
		po.setStoreId(this.generateId());
		return po;
	}

	@Override
	public StoreDTO get(String key) {
		StoreInfoPO po = storeInfoMapper.selectByFileKey(key);
		if (po == null) {
			return null;
		}
		String fileKey = po.getFileKey();
		StoreDTO dto = new StoreDTO();
		dto.setFileName(po.getFileName());
		dto.setFileType(po.getFileType());
		dto.setKey(fileKey);
		String filePath = this.baseDir + fileKey;
		dto.setDataSource(new FileDataSource(filePath));
		return dto;
	}

}
