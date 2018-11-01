package com.hanmlet.myblog.dto;

import javax.activation.DataSource;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StoreDTO {

	private String key;
	private DataSource dataSource;
	private String fileName;// 原始的名称
	private String fileType;
}
