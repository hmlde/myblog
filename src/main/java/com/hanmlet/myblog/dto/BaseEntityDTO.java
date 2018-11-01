package com.hanmlet.myblog.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BaseEntityDTO<T> {

	private String code;

	private String msg;

	private T data;

}
